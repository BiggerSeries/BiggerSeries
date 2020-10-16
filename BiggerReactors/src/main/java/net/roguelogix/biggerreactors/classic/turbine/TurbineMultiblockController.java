package net.roguelogix.biggerreactors.classic.turbine;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.turbine.blocks.*;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineActivity;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineState;
import net.roguelogix.biggerreactors.classic.turbine.tiles.*;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.util.Util;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// ahh shit, here we go again
public class TurbineMultiblockController extends RectangularMultiblockController {
    public TurbineMultiblockController(World world) {
        super(world);
        minX = minZ = 5;
        minY = 4;
        maxX = Config.Turbine.MaxLength;
        maxZ = Config.Turbine.MaxWidth;
        maxY = Config.Turbine.MaxHeight;
        tileAttachValidator = tile -> {
            return tile instanceof TurbineBaseTile;
        };
        frameValidator = block -> {
            return block instanceof TurbineCasing;
        };
        exteriorValidator = Validator.or(frameValidator, block -> {
            return block instanceof TurbineTerminal
                    || block instanceof TurbineCoolantPort
                    || block instanceof TurbineRotorBearing
                    || block instanceof TurbinePowerTap
                    || block instanceof TurbineComputerPort
                    || block instanceof TurbineGlass;
        });
        interiorValidator = block -> {
            if (TurbineCoilRegistry.isBlockAllowed(block)) {
                return true;
            }
            return block instanceof TurbineRotorShaft
                    || block instanceof TurbineRotorBlade
                    || block instanceof AirBlock;
        };
        setAssemblyValidator(multiblockController -> {
            if (rotorBearings.size() != 2) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_bearing_count");
            }
            
            TurbineRotorBearingTile bearing = rotorBearings.iterator().next();
            BlockPos bearingPosition = bearing.getPos();
            Direction marchDirection;
            if (bearingPosition.getX() == minX()) {
                marchDirection = Direction.EAST;
            } else if (bearingPosition.getX() == maxX()) {
                marchDirection = Direction.WEST;
            } else if (bearingPosition.getY() == minY()) {
                marchDirection = Direction.UP;
            } else if (bearingPosition.getY() == maxY()) {
                marchDirection = Direction.DOWN;
            } else if (bearingPosition.getZ() == minZ()) {
                marchDirection = Direction.SOUTH;
            } else if (bearingPosition.getZ() == maxZ()) {
                marchDirection = Direction.NORTH;
            } else {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_bearing_position_undefined");
            }
            int marchedBlocks = 0;
            BlockPos currentPos = bearingPosition.offset(marchDirection);
            while (world.getBlockState(currentPos).getBlock() instanceof TurbineRotorShaft) {
                currentPos = currentPos.offset(marchDirection);
                marchedBlocks++;
            }
            if (!(world.getBlockState(currentPos).getBlock() instanceof TurbineRotorBearing)) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_shaft_bearing_ends");
            }
            
            if (rotorShafts.size() != marchedBlocks) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_shaft_off_shaft");
            }
            
            marchedBlocks = 0;
            
            for (TurbineRotorShaftTile rotorShaft : rotorShafts) {
                for (Direction value : Direction.values()) {
                    BlockPos pos = rotorShaft.getPos();
                    while (true) {
                        pos = pos.offset(value);
                        Block block = world.getBlockState(pos).getBlock();
                        if (!(block instanceof TurbineRotorBlade)) {
                            break;
                        }
                        marchedBlocks++;
                    }
                }
            }
            
            if (marchedBlocks != rotorBlades.size()) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_blade_off_blade");
            }
            
            boolean inCoil = false;
            boolean inBlades = false;
            boolean switched = false;
            
            int validCoilBlocks = 0;
            
            currentPos = bearingPosition;
            while (true) {
                currentPos = currentPos.offset(marchDirection);
                TileEntity te = world.getTileEntity(currentPos);
                if (!(te instanceof TurbineRotorShaftTile)) {
                    break;
                }
                TurbineRotorShaftTile rotorShaft = (TurbineRotorShaftTile) te;
                boolean isCoilShaft = false;
                boolean isBladeShaft = false;
                for (Direction value : Direction.values()) {
                    if (value == marchDirection || value == marchDirection.getOpposite()) {
                        continue;
                    }
                    BlockPos offsetPos = rotorShaft.getPos().offset(value);
                    net.minecraft.util.math.vector.Vector3i secondaryOffset = value.getDirectionVec().crossProduct(marchDirection.getDirectionVec());
                    Direction secondaryDirection = Direction.getFacingFromVector(secondaryOffset.getX(), secondaryOffset.getY(), secondaryOffset.getZ());
                    BlockPos secondaryOffsetPos = offsetPos.offset(secondaryDirection);
                    
                    Block primaryBlock = world.getBlockState(offsetPos).getBlock();
                    Block secondaryBlock = world.getBlockState(secondaryOffsetPos).getBlock();
                    if (primaryBlock instanceof TurbineRotorBlade) {
                        isBladeShaft = true;
                    } else if (!(primaryBlock instanceof AirBlock)) {
                        isCoilShaft = true;
                        validCoilBlocks++;
                    }
                    
                    if (!(secondaryBlock instanceof AirBlock)) {
                        isCoilShaft = true;
                        validCoilBlocks++;
                    }
                    
                    if (isCoilShaft && isBladeShaft) {
                        throw new ValidationError("multiblock.error.biggerreactors.turbine.mixed_blades_and_coil");
                    }
                }
                if (isCoilShaft) {
                    if (inBlades) {
                        if (switched) {
                            throw new ValidationError("multiblock.error.biggerreactors.turbine.multiple_groups");
                        }
                        inBlades = false;
                        switched = true;
                    }
                    inCoil = true;
                }
                if (isBladeShaft) {
                    if (inCoil) {
                        if (switched) {
                            throw new ValidationError("multiblock.error.biggerreactors.turbine.multiple_groups");
                        }
                        inCoil = false;
                        switched = true;
                    }
                    inBlades = true;
                }
            }
            
            int[] totalCoilBlocks = new int[]{0};
            
            Util.chunkCachedBlockStateIteration(new Vector3i(minX(), minY(), minZ()), new Vector3i(maxX(), maxY(), maxZ()), world, (block, pos) -> {
                if (block.getBlock() instanceof TurbineBaseBlock) {
                    TileEntity te = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));
                    if (te instanceof TurbineBaseTile) {
                        if (!((TurbineBaseTile) te).isCurrentController(this)) {
                            throw new ValidationError("multiblock.error.biggerreactors.dangling_internal_part");
                        }
                    }
                    return;
                }
                if (block.getBlock() instanceof AirBlock) {
                    return;
                }
                totalCoilBlocks[0]++;
            });
            
            if (totalCoilBlocks[0] != validCoilBlocks) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.dangling_coil");
            }
            
            return true;
        });
    }
    
    private TurbineActivity turbineActivity = TurbineActivity.INACTIVE;
    
    private final Set<TurbineTerminalTile> terminals = new HashSet<>();
    private final Set<TurbineCoolantPortTile> coolantPorts = new HashSet<>();
    private final Set<TurbineRotorBearingTile> rotorBearings = new HashSet<>();
    private final Set<TurbineRotorShaftTile> rotorShafts = new HashSet<>();
    private final Set<TurbineRotorBladeTile> rotorBlades = new HashSet<>();
    private final Set<TurbinePowerTapTile> powerTaps = new HashSet<>();
    
    @Override
    protected void onPartAdded(MultiblockTile tile) {
        if (tile instanceof TurbineTerminalTile) {
            terminals.add((TurbineTerminalTile) tile);
        }
        if (tile instanceof TurbineCoolantPortTile) {
            coolantPorts.add((TurbineCoolantPortTile) tile);
        }
        if (tile instanceof TurbineRotorBearingTile) {
            rotorBearings.add((TurbineRotorBearingTile) tile);
        }
        if (tile instanceof TurbineRotorShaftTile) {
            rotorShafts.add((TurbineRotorShaftTile) tile);
        }
        if (tile instanceof TurbineRotorBladeTile) {
            rotorBlades.add((TurbineRotorBladeTile) tile);
        }
        if (tile instanceof TurbinePowerTapTile) {
            powerTaps.add((TurbinePowerTapTile) tile);
        }
    }
    
    @Override
    protected void onPartRemoved(MultiblockTile tile) {
        if (tile instanceof TurbineTerminalTile) {
            terminals.remove(tile);
        }
        if (tile instanceof TurbineCoolantPortTile) {
            coolantPorts.remove(tile);
        }
        if (tile instanceof TurbineRotorBearingTile) {
            rotorBearings.remove(tile);
        }
        if (tile instanceof TurbineRotorShaftTile) {
            rotorShafts.remove(tile);
        }
        if (tile instanceof TurbineRotorBladeTile) {
            rotorBlades.remove(tile);
        }
        if (tile instanceof TurbinePowerTapTile) {
            powerTaps.remove(tile);
        }
    }
    
    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockState(terminal.getPos(), terminal.getBlockState().with(TurbineActivity.TURBINE_STATE_ENUM_PROPERTY, turbineActivity));
            terminal.markDirty();
        });
    }
    
    private long steam;
    private long water;
    
    public long extractWater(long maxDrain, boolean simulate) {
        long toExtract = Math.min(maxDrain, water);
        if (toExtract < 0) {
            toExtract = 0;
        }
        if (!simulate) {
            water -= toExtract;
        }
        return toExtract;
    }
    
    public long addSteam(long amount, boolean simulate) {
        long toAdd = Math.min(amount, Config.Turbine.TankSize - steam);
        if (toAdd < 0) {
            toAdd = 0;
        }
        if (!simulate) {
            steam += toAdd;
        }
        return toAdd;
    }
    
    private long rotorMass;
    private long bladeSurfaceArea;
    private long coilSize;
    private double inductionEfficiency;
    private double inductorDragCoefficient;
    private double inductionEnergyExponentBonus;
    private double frictionDrag;
    private double bladeDrag;
    
    @Override
    protected void onAssembly() {
        
        for (TurbinePowerTapTile powerPort : powerTaps) {
            powerPort.updateOutputDirection();
        }
        for (TurbineCoolantPortTile coolantPort : coolantPorts) {
            coolantPort.updateOutputDirection();
        }
        
        rotorMass = 0;
        bladeSurfaceArea = 0;
        coilSize = 0;
        inductionEfficiency = 0;
        inductorDragCoefficient = 0;
        inductionEnergyExponentBonus = 0;
        
        Util.chunkCachedBlockStateIteration(new Vector3i(minX() + 1, minY() + 1, minZ() + 1), new Vector3i(maxX() - 1, maxY() - 1, maxZ() - 1), world, (blockState, pos) -> {
            Block block = blockState.getBlock();
            if (block instanceof AirBlock) {
                return;
            }
            if (block instanceof TurbineRotorBlade) {
                bladeSurfaceArea++;
            }
            if (block instanceof TurbineRotorBlade || block instanceof TurbineRotorShaft) {
                rotorMass += Config.Turbine.RotorMassPerPart;
            }
            TurbineCoilRegistry.TurbineCoilData coilData = TurbineCoilRegistry.getCoilData(block);
            if (coilData != null) {
                inductionEfficiency += coilData.efficiency;
                inductorDragCoefficient += coilData.extractionRate;
                inductionEnergyExponentBonus += coilData.bonus;
                coilSize++;
            }
        });
        
        frictionDrag = rotorMass * Config.Turbine.MassDragMultiplier;
        bladeDrag = Config.Turbine.BladeDragMultiplier * bladeSurfaceArea;
        
        if (coilSize <= 0) {
            inductionEfficiency = 0;
            inductorDragCoefficient = 0;
            inductionEnergyExponentBonus = 0;
        } else {
            // TODO: 8/8/20 config that 1b
            inductionEfficiency = (inductionEfficiency * 1d) / coilSize;
            inductionEnergyExponentBonus = Math.max(1f, (inductionEnergyExponentBonus / coilSize));
            inductorDragCoefficient = (inductorDragCoefficient / coilSize);
        }
    }
    
    @Override
    protected void onDisassembly() {
        rotorMass = 0;
        bladeSurfaceArea = 0;
        coilSize = 0;
        rotorEnergy = 0;
    }
    
    long storedPower = 0;
    
    private double energyGeneratedLastTick;
    private long fluidConsumedLastTick;
    private double rotorEfficiencyLastTick;
    
    private double rotorEnergy = 0;
    
    @Override
    public void tick() {
        
        energyGeneratedLastTick = 0;
        fluidConsumedLastTick = 0;
        rotorEfficiencyLastTick = 0;
        
        long steamIn = 0;
        
        if (turbineActivity == TurbineActivity.ACTIVE) {
            steamIn = Math.min(maxFlowRate, steam);
            
            if (ventState == VentState.CLOSED) {
                long availableSpace = Config.Turbine.TankSize - water;
                steamIn = Math.min(steamIn, availableSpace);
            }
        }
        
        if (steamIn > 0 || rotorEnergy > 0) {
            
            double rotorSpeed = 0;
            if (rotorBlades.size() > 0 && rotorMass > 0) {
                rotorSpeed = rotorEnergy / (double) (rotorBlades.size() * rotorMass);
            }
            
            double aeroDragTorque = rotorSpeed * bladeDrag;
            
            double liftTorque = 0;
            if (steamIn > 0) {
                long steamToProcess = bladeSurfaceArea * Config.Turbine.FluidPerBlade;
                steamToProcess = Math.min(steamToProcess, steamIn);
                liftTorque = steamToProcess * Config.Turbine.SteamCondensationEnergy;
                
                if (steamToProcess < steamIn) {
                    steamToProcess = steamIn - steamToProcess;
                    double neededBlades = steamIn / (double) Config.Turbine.FluidPerBlade;
                    double missingBlades = neededBlades - bladeSurfaceArea;
                    double bladeEfficiency = 1.0 - missingBlades / neededBlades;
                    liftTorque += steamToProcess * bladeEfficiency;
                    
                }
                rotorEfficiencyLastTick = liftTorque / (steamIn * Config.Turbine.SteamCondensationEnergy);
            }
            
            double inductionTorque = coilEngaged ? rotorSpeed * inductorDragCoefficient * coilSize : 0f;
            double energyToGenerate = Math.pow(inductionTorque, inductionEnergyExponentBonus) * inductionEfficiency;
            if (energyToGenerate > 0) {
                // TODO: 8/7/20 this works at multiples of 900 over 1800 RPM, it probably shouldn't
                // TODO: 8/7/20 make RPM range configurable, its not exactly the easiest thing to do
                double efficiency = 0.25 * Math.cos(rotorSpeed / (45.5 * Math.PI)) + 0.75;
                // yes this is slightly different, this matches what the equation actually looks like better
                // go on, graph it
                if (rotorSpeed < 450) {
                    efficiency = Math.min(0.5, efficiency);
                }
                
                // oh noes, there is a cap now, *no overspeeding your fucking turbines*
                if (rotorSpeed > 2245) {
                    efficiency = -rotorSpeed / 4490;
                    efficiency += 1;
                }
                if (efficiency < 0) {
                    efficiency = 0;
                }
                
                energyToGenerate *= efficiency;
                
                energyGeneratedLastTick = energyToGenerate;
                
                energyToGenerate = Math.min(energyToGenerate, Config.Turbine.BatterySize - storedPower);
                
                if (energyToGenerate > 0) {
                    storedPower += energyToGenerate;
                }
            }
            
            rotorEnergy += liftTorque;
            rotorEnergy -= inductionTorque;
            rotorEnergy -= aeroDragTorque;
            rotorEnergy -= frictionDrag;
            if (rotorEnergy < 0) {
                rotorEnergy = 0;
            }
            
            if (steamIn > 0) {
                fluidConsumedLastTick = steamIn;
                steam -= steamIn;
                
                if (ventState != VentState.ALL) {
                    water += steamIn;
                }
            }
            if (water > Config.Turbine.TankSize) {
                water = Config.Turbine.TankSize;
            }
        }
        
        long totalPowerRequested = 0;
        for (TurbinePowerTapTile powerPort : powerTaps) {
            totalPowerRequested += powerPort.distributePower(storedPower, true);
        }
        long startingPower = storedPower;
        
        double distributionMultiplier = Math.min(1f, (double) storedPower / (double) totalPowerRequested);
        for (TurbinePowerTapTile powerPort : powerTaps) {
            long powerRequested = powerPort.distributePower(startingPower, true);
            powerRequested *= distributionMultiplier;
            powerRequested = Math.min(storedPower, powerRequested); // just in case
            storedPower -= powerPort.distributePower(powerRequested, false);
        }
        
        for (TurbineCoolantPortTile coolantPort : coolantPorts) {
            if (water < 0) {
                break;
            }
            water -= coolantPort.pushWater(water);
        }
        
        markDirty();
    }
    
    public void updateDataPacket(TurbineState turbineState) {
        turbineState.turbineActivity = turbineActivity;
        turbineState.ventState = ventState;
        turbineState.coilStatus = coilEngaged;
        
        turbineState.flowRate = maxFlowRate;
        turbineState.efficiencyRate = rotorEfficiencyLastTick;
        turbineState.turbineOutputRate = energyGeneratedLastTick;
        
        turbineState.currentRPM = (rotorBlades.size() > 0 && rotorMass > 0 ? rotorEnergy / (double) (rotorBlades.size() * rotorMass) : 0);
        turbineState.maxRPM = 2200.0;
        
        turbineState.intakeStored = steam;
        turbineState.intakeCapacity = Config.Turbine.TankSize;
        
        turbineState.exhaustStored = water;
        turbineState.exhaustCapacity = Config.Turbine.TankSize;
        
        turbineState.energyStored = storedPower;
        turbineState.energyCapacity = Config.Turbine.BatterySize;
    }
    
    public void runRequest(String requestName, Object requestData) {
        switch (requestName) {
            case "setActive": {
                boolean newState = (boolean) requestData;
                setActive(newState ? TurbineActivity.ACTIVE : TurbineActivity.INACTIVE);
                return;
            }
            case "changeFlowRate": {
                long newRate = maxFlowRate + ((long) requestData);
                setMaxFlowRate(newRate);
                return;
            }
            case "setCoilEngaged": {
                boolean newState = (boolean) requestData;
                setCoilEngaged(newState);
                return;
            }
            case "setVentState": {
                VentState newState = VentState.valueOf((int) requestData);
                setVentState(newState);
                return;
            }
        }
    }
    
    VentState ventState = VentState.OVERFLOW;
    
    private void setVentState(VentState newVentState) {
        ventState = newVentState;
    }
    
    long maxFlowRate = Config.Turbine.MaxFlow;
    
    private void setMaxFlowRate(long flowRate) {
        if (flowRate < 0) {
            flowRate = 0;
        }
        if (flowRate > Config.Turbine.MaxFlow) {
            flowRate = Config.Turbine.MaxFlow;
        }
        maxFlowRate = flowRate;
    }
    
    private boolean coilEngaged = true;
    
    private void setCoilEngaged(boolean engaged) {
        coilEngaged = engaged;
    }
    
    
    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        {
            compound.putLong("steam", steam);
            compound.putLong("water", water);
            compound.putString("turbineState", turbineActivity.toString());
            compound.putDouble("storedPower", storedPower);
            compound.putString("ventState", ventState.toString());
            compound.putDouble("rotorEnergy", rotorEnergy);
            compound.putLong("maxFloatRate", maxFlowRate);
            compound.putBoolean("coilEngaged", coilEngaged);
        }
        return compound;
    }
    
    protected void read(CompoundNBT compound) {
        if (compound.contains("steam")) {
            steam = compound.getLong("steam");
        }
        if (compound.contains("water")) {
            water = compound.getLong("water");
        }
        if (compound.contains("turbineState")) {
            turbineActivity = TurbineActivity.valueOf(compound.getString("turbineState").toUpperCase());
        }
        if (compound.contains("storedPower")) {
            storedPower = compound.getLong("storedPower");
        }
        if (compound.contains("ventState")) {
            ventState = VentState.valueOf(compound.getString("ventState").toUpperCase());
        }
        if (compound.contains("rotorEnergy")) {
            rotorEnergy = compound.getDouble("rotorEnergy");
        }
        if (compound.contains("maxFloatRate")) {
            maxFlowRate = compound.getLong("maxFloatRate");
        }
        if (compound.contains("coilEngaged")) {
            coilEngaged = compound.getBoolean("coilEngaged");
        }
        
        updateBlockStates();
    }
    
    public void toggleActive() {
        setActive(turbineActivity == TurbineActivity.ACTIVE ? TurbineActivity.INACTIVE : TurbineActivity.ACTIVE);
    }
    
    public void setActive(TurbineActivity newState) {
        if (turbineActivity != newState) {
            turbineActivity = newState;
            updateBlockStates();
        }
    }
    
    @Override
    public String getDebugInfo() {
        return super.getDebugInfo() + "\n" +
                "rotorMass: " + rotorMass + "\n" +
                "bladeSurfaceArea: " + bladeSurfaceArea + "\n" +
                "coilSize: " + coilSize + "\n" +
                "inductionEfficiency: " + inductionEfficiency + "\n" +
                "inductorDragCoefficient: " + inductorDragCoefficient + "\n" +
                "inductionEnergyExponentBonus: " + inductionEnergyExponentBonus + "\n" +
                "frictionDrag: " + frictionDrag + "\n" +
                "bladeDrag: " + bladeDrag + "\n" +
                "CoilEngaged:" + coilEngaged + " \n" +
                "VentState:" + ventState + " \n" +
                "State:" + turbineActivity.toString() + " \n" +
                "StoredPower: " + storedPower + "\n" +
                "CoilEngaged: " + coilEngaged + " \n" +
                "PowerProduction: " + energyGeneratedLastTick + "\n" +
                "CoilEfficiency: " + rotorEfficiencyLastTick + "\n" +
                "Steam: " + steam + "\n" +
                "Water: " + water + "\n" +
                "Flow: " + fluidConsumedLastTick + "\n" +
                "RotorEfficiency: " + rotorEfficiencyLastTick + "\n" +
                "MaxFlow: " + maxFlowRate + "\n" +
                "RotorRPM: " + (rotorBlades.size() > 0 && rotorMass > 0 ? rotorEnergy / (double) (rotorBlades.size() * rotorMass) : 0) + "\n" +
                "";
    }
    
    // -- ComputerCraft API --
    
    public boolean CCgetConnected() {
        return state != MultiblockController.AssemblyState.DISASSEMBLED;
    }
    
    public boolean CCgetActive() {
        return turbineActivity == TurbineActivity.ACTIVE;
    }
    
    public long CCgetEnergyStored() {
        return storedPower;
    }
    
    public double CCgetRotorSpeed() {
        return (rotorBlades.size() > 0 && rotorMass > 0 ? rotorEnergy / (double) (rotorBlades.size() * rotorMass) : 0);
    }
    
    public long CCgetInputAmount() {
        return steam;
    }
    
    public String CCgetInputType() {
        if (steam > 0) {
            return Objects.requireNonNull(FluidIrradiatedSteam.INSTANCE.getRegistryName()).toString();
        }
        return null;
    }
    
    public long CCgetOutputAmount() {
        return water;
    }
    
    public String CCgetOutputType() {
        if (water > 0) {
            return Objects.requireNonNull(Fluids.WATER.getRegistryName()).toString();
        }
        return null;
    }
    
    public long CCgetFluidAmountMax() {
        return Config.Turbine.TankSize;
    }
    
    public long CCgetFluidFlowRate() {
        return fluidConsumedLastTick;
    }
    
    public long CCgetFluidFlowRateMax() {
        return maxFlowRate;
    }
    
    public long CCgetFluidFlowRateMaxMax() {
        return Config.Turbine.MaxFlow;
    }
    
    public double CCgetEnergyProducedLastTick() {
        return energyGeneratedLastTick;
    }
    
    public boolean CCgetInductorEngaged() {
        return coilEngaged;
    }
    
    public void CCsetActive(boolean active) {
        setActive(active ? TurbineActivity.ACTIVE : TurbineActivity.INACTIVE);
    }
    
    public void CCsetFluidFlowRateMax(long maxFlowRate) {
        setMaxFlowRate(maxFlowRate);
    }
    
    public void CCsetVentNone() {
        setVentState(VentState.CLOSED);
    }
    
    public void CCsetVentOverflow() {
        setVentState(VentState.OVERFLOW);
    }
    
    public void CCsetVentAll() {
        setVentState(VentState.ALL);
    }
    
    public void CCsetInductorEngaged(boolean engaged){
        setCoilEngaged(engaged);
    }
}
