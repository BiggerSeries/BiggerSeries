package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class StaticUtils {

	public static final Direction[] CardinalDirections = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

	public static final Direction neighborsBySide[][] = new Direction[][] {
		{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST},
		{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST},
		{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},
		{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST},
		{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH},
		{Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH}
	};

	public static class Inventory {
		/**
		 * Consume a single item from a stack of items
		 * @param stack The stack from which to consume
		 * @return The remainder of the stack, or null if the stack was fully consumed.
		 */
		public static ItemStack consumeItem(ItemStack stack)
		{
			return consumeItem(stack, 1);
		}

		/**
		 * Consume some amount of items from a stack of items. Assumes you've already validated
		 * the consumption. If you try to consume more than the stack has, it will simply destroy
		 * the stack, as if you'd consumed all of it.
		 * @param stack The stack from which to consume
		 * @return The remainder of the stack, or null if the stack was fully consumed.
		 */
		public static ItemStack consumeItem(ItemStack stack, int amount)
		{
			if(stack == null) { return null; }

			if(stack.getCount() <= amount)
			{
				if(stack.getItem().hasContainerItem(stack))
				{
					return stack.getItem().getContainerItem(stack);
				}
				else
				{
					return null;
				}
			}
			else
			{
				stack.split(amount);
				return stack;
			}
		}

		/**
		 * Is this player holding a goddamn wrench?
		 * @return True if the player is holding a goddamn wrench. BC only, screw you.
		 */
		//TODO: add a wrench, and check for TE's wrench, etc
//		public static boolean isPlayerHoldingWrench(PlayerEntity player) {
//			if(player.inventory.getCurrentItem() == null) {
//				return false;
//			}
//			Item currentItem = player.inventory.getCurrentItem().getItem();
//			return (ModHelperBase.useCofh && currentItem instanceof IToolHammer) ||
//					(ModHelperBase.useBuildcraftTools && currentItem instanceof IToolWrench);
//		}

		/**
		 * Check to see if two stacks are equal. NBT-sensitive.
		 * Lifted from PowerCrystalsCore.
		 * @param s1 First stack to compare
		 * @param s2 Second stack to compare
		 * @return True if stacks are equal, false otherwise.
		 */
        public static boolean areStacksEqual(ItemStack s1, ItemStack s2)
        {
                return areStacksEqual(s1, s2, true);
        }

        /**
		 * Check to see if two stacks are equal. Optionally NBT-sensitive.
		 * Lifted from PowerCrystalsCore.
		 * @param s1 First stack to compare
		 * @param s2 Second stack to compare
         * @param nbtSensitive True if stacks' NBT tags should be checked for equality.
         * @return True if stacks are equal, false otherwise.
         */
        public static boolean areStacksEqual(ItemStack s1, ItemStack s2, boolean nbtSensitive)
        {
                if(s1 == null || s2 == null) return false;
                if(!s1.isItemEqual(s2)) return false;

                if(nbtSensitive)
                {
                        if(s1.getTag() == null && s2.getTag() == null) return true;
                        if(s1.getTag() == null || s2.getTag() == null) return false;
                        return s1.getTag().equals(s2.getTag());
                }

                return true;
        }

        private static final Direction[] chestDirections = new Direction[] { Direction.NORTH,
        																				Direction.SOUTH,
        																				Direction.EAST,
        																				Direction.WEST};
// TODO: 6/22/20 fix?
//		public static IInventory checkForDoubleChest(World worldObj, IInventory te, int x, int y, int z) {
//			for(Direction dir : chestDirections) {
//				if(worldObj.getBlockState(new BlockPos(x, y, z).offset(dir)).getBlock() == Blocks.CHEST) {
//					TileEntity otherTe = worldObj.getTileEntity(new BlockPos(x, y, z).offset(dir));
//					if(otherTe instanceof IInventory) {
//						return new Inventory("Large Chest", te, (IInventory)otherTe);
//					}
//				}
//			}
//
//			// Not a large chest, so just return the single chest.
//			return te;
//		}
	}

    // TODO: 6/22/20 fix?
//	public static class Fluids {
//		/* Below stolen from COFHLib because COFHLib itself still relies on cofh.core */
//		public static boolean fillTankWithContainer(World world, IFluidHandler handler, PlayerEntity player) {
//
//	        ItemStack container = player.getHeldItemMainhand();
//	        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);
//
//	        if (fluid != null) {
//	                if (handler.fill(Direction.UNKNOWN, fluid, false) == fluid.amount || player.capabilities.isCreativeMode) {
//	                        if (world.isRemote) {
//	                                return true;
//	                        }
//	                        handler.fill(Direction.UNKNOWN, fluid, true);
//
//	                        if (!player.capabilities.isCreativeMode) {
//	                                player.inventory.setInventorySlotContents(player.inventory.currentItem, Inventory.consumeItem(container));
//	                        }
//	                        return true;
//	                }
//	        }
//	        return false;
//		}
//
//		public static boolean fillContainerFromTank(World world, IFluidHandler handler, EntityPlayer player, FluidStack tankFluid) {
//			ItemStack container = player.getCurrentEquippedItem();
//
//			if (FluidContainerRegistry.isEmptyContainer(container)) {
//			        ItemStack returnStack = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
//			        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(returnStack);
//
//			        if (fluid == null || returnStack == null) {
//			                return false;
//			        }
//			        if (!player.capabilities.isCreativeMode) {
//			                if (container.stackSize == 1) {
//			                        container = container.copy();
//			                        player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
//			                } else if (!player.inventory.addItemStackToInventory(returnStack)) {
//			                        return false;
//			                }
//			                handler.drain(Direction.UNKNOWN, fluid.amount, true);
//			                container.stackSize--;
//
//			                if (container.stackSize <= 0) {
//			                        container = null;
//			                }
//			        } else {
//			                handler.drain(Direction.UNKNOWN, fluid.amount, true);
//			        }
//			        return true;
//			}
//			return false;
//		}
//	}
//
	public static class ExtraMath {
		/**
		 * Linear interpolate between two numbers.
		 * @param from
		 * @param to
		 * @param modifier
		 * @return
		 */
		public static float Lerp(float from, float to, float modifier)
		{
			modifier = Math.min(1f, Math.max(0f, modifier));
		    return from + modifier * (to - from);
		}

		/**
		 * Calculate the volume of the cube defined by two coordinates.
		 * @param minimum Minimum coordinate.
		 * @param maximum Maximum coordinate.
		 * @return The cube's volume, in blocks.
		 */
		public static int Volume(BlockPos minimum, BlockPos maximum) {
			if(minimum == null || maximum == null) { return 0; }

			int xsize = Math.abs(maximum.getX() - minimum.getX()) + 1;
			int ysize = Math.abs(maximum.getY() - minimum.getY()) + 1;
			int zsize = Math.abs(maximum.getZ() - minimum.getZ()) + 1;
			return xsize * ysize * zsize;
		}
	}

	public static class Energy {
		public static float RFPerCentigradePerUnitVolume = 10f;

		public static float getRFFromVolumeAndTemp(int volume, float temperature) {
			return temperature * (float)volume * RFPerCentigradePerUnitVolume;
		}

		public static float getTempFromVolumeAndRF(int volume, float rf) {
			return rf / ((float)volume * RFPerCentigradePerUnitVolume);
		}
	}

	public static class Strings {
		public static String[] sizePrefixes = {"", "Ki", "Me", "Gi", "Te", "Pe", "Ex", "Ze", "Yo", "Ho"};
		// Ho = Hojillion

		public static String formatRF(float number) {
			String prefix = "";
			if(number < 0f) {
				prefix = "-";
				number *= -1;
			}

			if(number <= 0.00001f) { return "0.00 RF"; }

			int power = (int)Math.floor(Math.log10(number));

			int decimalPoints = 2 - (power % 3);
			int letterIdx = Math.max(0, Math.min(sizePrefixes.length, power / 3));
			double divisor = Math.pow(1000f, letterIdx);

			if(divisor > 0) {
				return String.format("%s%." + Integer.toString(decimalPoints) + "f %sRF", prefix, number/divisor, sizePrefixes[letterIdx]);
			}
			else {
				return String.format("%s%." + Integer.toString(decimalPoints) + "f RF", prefix, number);
			}
		}

		public static String formatMillibuckets(float number) {
			String prefix = "";
			if(number < 0f) {
				prefix = "-";
				number *= -1;
			}

			if(number <= 0.00001f) { return "0.000 mB"; }
			int power = (int)Math.floor(Math.log10(number));
			if(power < 1) {
				return String.format("%.3f mB", number);
			}
			else if(power < 2) {
				return String.format("%.2f mB", number);
			}
			else if(power < 3) {
				return String.format("%.1f mB", number);
			}
			else if(power < 4) {
				return String.format("%.0f mB", number);
			}
			else {
				number /= 1000f; // Re-render into buckets
				if(power < 5) {
					return String.format("%.2f B", number);
				}
				else if(power < 6) {
					return String.format("%.1f B", number);
				}
				else {
					return String.format("%.0f B", number);
				}
			}
		}
	}

	// Mob = Mobile = Entity
	public static class Mob {
		/**
		 * @param entity The entity whose facing you wish to query.
		 * @return The Direction which entity is facing (north/south/east/west)
		 */
		protected Direction getFacingDirection(Entity entity) {
			int facingAngle = ((int)Math.floor((entity.rotationYaw * 4F) / 360F + 0.5D) & 3);
			switch(facingAngle) {
			case 1:
				return Direction.EAST;
			case 2:
				return Direction.SOUTH;
			case 3:
				return Direction.WEST;
			default:
				return Direction.NORTH;
			}
		}
	}

//	public static class TE {
//		public static TileEntity getTileEntityUnsafe(IWorldReader iba, int x, int y, int z) {
//			TileEntity te = null;
//
//			if(iba instanceof World) {
//				// We don't want to trigger tile entity loads in this method
//				te = getTileEntityUnsafe((World)iba, x, y, z);
//			}
//			else {
//				// Should never happen, generally
//				te = iba.getTileEntity(new BlockPos(x, y, z));
//			}
//
//			return te;
//		}
//
//		public static TileEntity getTileEntityUnsafe(World world, int x, int y, int z) {
//			TileEntity te = null;
//
//			Chunk chunk = world.getChunkFromBlockCoords(x, z);
//			if(chunk != null) {
//				te = chunk.getTileEntityUnsafe(x & 0x0F, y, z & 0x0F);
//			}
//
//			return te;
//		}
//	}

//	public static class WorldGen {
//		/**
//		 * Check if a Big Reactors world generator should even bother to run
//		 * in a given dimension.
//		 * @param dimensionId The dimension being queried for WorldGen.
//		 * @return True if world generation should proceed, false if it should be skipped altogether.
//		 */
//		public static boolean shouldGenerateInDimension(int dimensionId) {
//			return dimensionId >= 0 || BigReactors.enableWorldGenInNegativeDimensions ||
//					BigReactors.dimensionWhitelist.contains(dimensionId);
//		}
//	}
}
