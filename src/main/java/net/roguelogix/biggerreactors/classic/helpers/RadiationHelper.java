package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorControlRodTile;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorFuelRodTile;
import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;


/**
 * Helper for reactor radiation game logic
 * @author Erogenous Beef
 */
public class RadiationHelper {

	// Game Balance Values
	public static final float fuelPerRadiationUnit = 0.0007f; // fuel units used per fission event
	public static final float rfPerRadiationUnit = 10f; // RF generated per fission event
	public static final float fissionEventsPerFuelUnit = 0.01f; // 1 fission event per 100 mB

	public static final ReactorModeratorRegistry.ModeratorProperties airData = new ReactorModeratorRegistry.ModeratorProperties(0.1f, 0.25f, 1.1f, 0.05F);
	public static final ReactorModeratorRegistry.ModeratorProperties waterData = new ReactorModeratorRegistry.ModeratorProperties(0.33f, 0.5f, 1.33f, 0.1f);

	private float fertility;
	
	public RadiationHelper() {
		fertility = 1f;
	}

	public RadiationData radiate(World world, FuelContainer fuelContainer, ReactorFuelRodTile source, ReactorControlRodTile controlRod, float fuelHeat, float environmentHeat, int numControlRods) {
		// No fuel? No radiation!
		if(fuelContainer.getFuelAmount() <= 0) { return null; }

		// Determine radiation amount & intensity, heat amount, determine fuel usage
		RadiationData data = new RadiationData();
		data.fuelAbsorbedRadiation = 0f;

		// Base value for radiation production penalties. 0-1, caps at about 3000C;
		double radiationPenaltyBase = Math.exp(-15*Math.exp(-0.0025*fuelHeat));

		// Raw amount - what's actually in the tanks
		// Effective amount - how 
		int baseFuelAmount = fuelContainer.getFuelAmount() + (fuelContainer.getWasteAmount() / 100);
		float fuelReactivity = fuelContainer.getFuelReactivity();
		
		// Intensity = how strong the radiation is, hardness = how energetic the radiation is (penetration)
		float rawRadIntensity = (float)baseFuelAmount * fissionEventsPerFuelUnit;
		
		// Scale up the "effective" intensity of radiation, to provide an incentive for bigger reactors in general.
		float scaledRadIntensity = (float) Math.pow((rawRadIntensity), fuelReactivity);

		// Scale up a second time based on scaled amount in each fuel rod. Provides an incentive for making reactors that aren't just pancakes.
		scaledRadIntensity = (float) Math.pow((scaledRadIntensity/numControlRods), fuelReactivity) * numControlRods;

		// Apply control rod moderation of radiation to the quantity of produced radiation. 100% insertion = 100% reduction.
		float controlRodModifier = (float)(100-controlRod.getControlRodInsertion()) / 100f;
		scaledRadIntensity = scaledRadIntensity * controlRodModifier;
		rawRadIntensity = rawRadIntensity * controlRodModifier;

		// Now nerf actual radiation production based on heat.
		float effectiveRadIntensity = scaledRadIntensity * (1f + (float)(-0.95f*Math.exp(-10f*Math.exp(-0.0012f*fuelHeat))));

		// Radiation hardness starts at 20% and asymptotically approaches 100% as heat rises.
		// This will make radiation harder and harder to capture.
		float radHardness = 0.2f + (float)(0.8 * radiationPenaltyBase);

		// Calculate based on propagation-to-self
		float rawFuelUsage = (fuelPerRadiationUnit * rawRadIntensity / getFertilityModifier()) * Config.fuelUsageMultiplier; // Not a typo. Fuel usage is thus penalized at high heats.
		data.fuelRfChange = rfPerRadiationUnit * effectiveRadIntensity;
		data.environmentRfChange = 0f;

		// Propagate radiation to others
		BlockPos originCoord = source.getPos();
		BlockPos currentCoord;
		
		effectiveRadIntensity *= 0.25f; // We're going to do this four times, no need to repeat
		RadiationPacket radPacket = new RadiationPacket();

		for(Direction dir : StaticUtils.CardinalDirections) {
			radPacket.hardness = radHardness;
			radPacket.intensity = effectiveRadIntensity;
			int ttl = 4;
			currentCoord = new BlockPos(originCoord.getX(), originCoord.getY(), originCoord.getZ());

			while(ttl > 0 && radPacket.intensity > 0.0001f) {
				ttl--;
				currentCoord.offset(dir);
				performIrradiation(world, data, radPacket, currentCoord.getX(), currentCoord.getY(), currentCoord.getZ());
			}
		}

		// Apply changes
		fertility += data.fuelAbsorbedRadiation;
		data.fuelAbsorbedRadiation = 0f;
		
		// Inform fuelContainer
		fuelContainer.onRadiationUsesFuel(rawFuelUsage);
		data.fuelUsage = rawFuelUsage;
		
		return data;
	}
	
	public void tick(boolean active) {
		float denominator = 20f;
		if(!active) { denominator *= 200f; } // Much slower decay when off
		
		// Fertility decay, at least 0.1 rad/t, otherwise halve it every 10 ticks
		fertility = Math.max(0f, fertility - Math.max(0.1f, fertility/denominator));
	}
	
	private void performIrradiation(World world, RadiationData data, RadiationPacket radiation, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IRadiationModerator) {
			((IRadiationModerator)te).moderateRadiation(data, radiation);
		}
		else if (world.isAirBlock(pos)) {
			moderateByAir(data, radiation);
		}
		else {
			Block block = world.getBlockState(pos).getBlock();
			if(block != null) {
				
				if(block instanceof IFluidBlock) {
					moderateByFluid(data, radiation, ((IFluidBlock)block).getFluid());
				}
				else {
					// Go by block
					moderateByBlock(data, radiation, block);
				}
			}
			else {
				// Weird-ass problem. Assume it's air.
				moderateByAir(data, radiation);
			}
			// Do it based on fluid?
		}
	}
	
	private void moderateByAir(RadiationData data, RadiationPacket radiation) {
		applyModerationFactors(data, radiation, airData);
	}
	
	private void moderateByBlock(RadiationData data, RadiationPacket radiation, Block block) {
		ReactorModeratorRegistry.ModeratorProperties moderatorData = null;

		if(block == Blocks.IRON_BLOCK) {
			moderatorData = ReactorModeratorRegistry.blockModeratorProperties(block);
		}
		else if(block == Blocks.GOLD_BLOCK) {
			moderatorData = ReactorModeratorRegistry.blockModeratorProperties(block);
		}
		else if(block == Blocks.DIAMOND_BLOCK) {
			moderatorData = ReactorModeratorRegistry.blockModeratorProperties(block);
		}
		else if(block == Blocks.EMERALD_BLOCK) {
			moderatorData = ReactorModeratorRegistry.blockModeratorProperties(block);
		}
		else {
			// Check the ore dictionary.
			// TODO: 6/22/20 "ore dict" check
//			moderatorData = ReactorModeratorRegistry.blockModeratorProperties(ItemHelper.oreProxy.getOreName(new ItemStack(block, 1)));
		}
		
		if(moderatorData == null) {
			moderatorData = airData;
		}

		applyModerationFactors(data, radiation, moderatorData);
	}
	
	private void moderateByFluid(RadiationData data, RadiationPacket radiation, Fluid fluid) {
		
		float absorption, heatEfficiency, moderation;

		ReactorModeratorRegistry.ModeratorProperties moderatorData = ReactorModeratorRegistry.fluidModeratorProperties(fluid);
		
		if(moderatorData == null) {
			moderatorData = waterData;
		}

		applyModerationFactors(data, radiation, moderatorData);
	}
	
	private static void applyModerationFactors(RadiationData data, RadiationPacket radiation, ReactorModeratorRegistry.ModeratorProperties moderatorData) {
		float radiationAbsorbed = radiation.intensity * moderatorData.absorption * (1f - radiation.hardness);
		radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
		radiation.hardness /= moderatorData.moderation;
		data.environmentRfChange += moderatorData.heatEfficiency * radiationAbsorbed * rfPerRadiationUnit;
	}
	
	// Data Access
	public float getFertility() { return fertility; }

	public float getFertilityModifier() {
		if(fertility <= 1f) { return 1f; }
		else {
			return (float)(Math.log10(fertility) + 1);
		}
	}

	public void setFertility(float newFertility) {
		if(Float.isNaN(newFertility) || Float.isInfinite(newFertility)) {
			fertility = 1f;
		}
		else if(newFertility < 0f) {
			fertility = 0f;
		}
		else {
			fertility = newFertility;
		}
	}

	// Save/Load
	public void readFromNBT(CompoundNBT data) {
		if(data.hasUniqueId("fertility")) {
			setFertility(data.getFloat("fertility"));
		}
	}
	
	public CompoundNBT writeToNBT(CompoundNBT data) {
		data.putFloat("fertility", fertility);
		return data;
	}
	
	public void merge(RadiationHelper other) {
		fertility = Math.max(fertility, other.fertility);
	}

}
