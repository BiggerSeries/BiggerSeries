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
import net.roguelogix.biggerreactors.classic.turbine.state.VentState;
import net.roguelogix.biggerreactors.classic.turbine.tiles.*;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector4i;
import net.roguelogix.phosphophyllite.util.Util;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

// ahh shit, here we go again
public class TurbineMultiblockController extends RectangularMultiblockController {
    public TurbineMultiblockController(World world) {
        super(world);
        minSize.set(5, 4, 5);
        maxSize.set(Config.Turbine.MaxLength, Config.Turbine.MaxHeight, Config.Turbine.MaxWidth);
        tileAttachValidator = tile -> {
            //noinspection CodeBlock2Expr
            return tile instanceof TurbineBaseTile;
        };
        frameValidator = block -> {
            //noinspection CodeBlock2Expr
            return block instanceof TurbineCasing;
        };
        exteriorValidator = Validator.or(frameValidator, block -> {
            //noinspection CodeBlock2Expr
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
            
            Iterator<TurbineRotorBearingTile> iterator = rotorBearings.iterator();
            TurbineRotorBearingTile primaryBearing = iterator.next();
            TurbineRotorBearingTile secondaryBearing = iterator.next();
            BlockPos bearingPosition = primaryBearing.getPos();
            Direction marchDirection;
            if (bearingPosition.getX() == minCoord().x()) {
                marchDirection = Direction.EAST;
            } else if (bearingPosition.getX() == maxCoord().x()) {
                marchDirection = Direction.WEST;
            } else if (bearingPosition.getY() == minCoord().y()) {
                marchDirection = Direction.UP;
            } else if (bearingPosition.getY() == maxCoord().y()) {
                marchDirection = Direction.DOWN;
            } else if (bearingPosition.getZ() == minCoord().z()) {
                marchDirection = Direction.SOUTH;
            } else if (bearingPosition.getZ() == maxCoord().z()) {
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
                    } else if (!(primaryBlock instanceof AirBlock) && !(primaryBlock instanceof TurbineBaseBlock)) {
                        isCoilShaft = true;
                        validCoilBlocks++;
                    }
                    
                    if (!(secondaryBlock instanceof AirBlock) && !(secondaryBlock instanceof TurbineBaseBlock)) {
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
                        primaryBearing.isRenderBearing = true;
                        secondaryBearing.isRenderBearing = false;
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
                        primaryBearing.isRenderBearing = false;
                        secondaryBearing.isRenderBearing = true;
                    }
                    inBlades = true;
                }
            }
            if (!switched) {
                primaryBearing.isRenderBearing = true;
                secondaryBearing.isRenderBearing = false;
            }
            
            int[] totalCoilBlocks = new int[]{0};
            
            Util.chunkCachedBlockStateIteration(new Vector3i(1).add(minCoord()), new Vector3i(-1).add(maxCoord()), world, (block, pos) -> {
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
    private long glassCount = 0;
    
    @Override
    protected void onPartPlaced(@Nonnull MultiblockTile placed) {
        onPartAttached(placed);
    }
    
    @Override
    protected void onPartAttached(@Nonnull MultiblockTile tile) {
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
        if (tile instanceof TurbineGlassTile) {
            glassCount++;
        }
    }
    
    @Override
    protected void onPartBroken(@Nonnull MultiblockTile broken) {
        onPartDetached(broken);
    }
    
    @Override
    protected void onPartDetached(@Nonnull MultiblockTile tile) {
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
        if (tile instanceof TurbineGlassTile) {
            glassCount--;
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
    private long flowRateLimit;
    private long tankSize;
    
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
        long toAdd = Math.min(amount, tankSize - steam);
        if (toAdd < 0) {
            toAdd = 0;
        }
        if (!simulate) {
            steam += toAdd;
        }
        return toAdd;
    }
    
    public final ArrayList<Vector4i> rotorConfiguration = new ArrayList<>();
    public net.minecraft.util.math.vector.Vector3i rotationAxis = new net.minecraft.util.math.vector.Vector3i(0, 0, 0);
    
    private long rotorMass;
    private long bladeSurfaceArea;
    private long coilSize;
    private double inductionEfficiency;
    private double inductorDragCoefficient;
    private double inductionEnergyExponentBonus;
    private double frictionDrag;
    private double bladeDrag;
    
    @Override
    protected void onAssembled() {
        rotorMass = 0;
        bladeSurfaceArea = 0;
        coilSize = 0;
        rotorEnergy = 0;
        onUnpaused();
        maxFlowRate = bladeSurfaceArea * Config.Turbine.FluidPerBlade;
    }
    
    @Override
    protected void onUnpaused() {
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
        
        Util.chunkCachedBlockStateIteration(new Vector3i(1).add(minCoord()), new Vector3i(-1).add(maxCoord()), world, (blockState, pos) -> {
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
            TurbineCoilRegistry.CoilData coilData = TurbineCoilRegistry.getCoilData(block);
            if (coilData != null) {
                inductionEfficiency += coilData.efficiency;
                inductorDragCoefficient += coilData.extractionRate;
                inductionEnergyExponentBonus += coilData.bonus;
                coilSize++;
            }
        });
        
        inductorDragCoefficient *= Config.Turbine.CoilDragMultiplier;
        
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
        
        flowRateLimit = bladeSurfaceArea * Config.Turbine.FluidPerBlade * Config.Turbine.BladeToFlowRateMultiplier;
        tankSize = flowRateLimit * Config.Turbine.FlowRateToTankSizeMultiplier;
        maxStoredPower = (coilSize + 1) * Config.Turbine.BatterySizePerCoilBlock;
        
        if (glassCount > 0) {
            for (TurbineRotorBearingTile rotorBearing : rotorBearings) {
                if (!rotorBearing.isRenderBearing) {
                    continue;
                }
                
                for (Direction value : Direction.values()) {
                    BlockPos possibleRotorPos = rotorBearing.getPos().offset(value);
                    if (world.getBlockState(possibleRotorPos).getBlock() == TurbineRotorShaft.INSTANCE) {
                        
                        rotationAxis = value.getDirectionVec();
                        
                        rotorConfiguration.clear();
                        
                        Direction.Axis shaftAxis = value.getAxis();
                        BlockPos currentRotorPosition = possibleRotorPos;
                        BlockPos currentBladePosition;
                        while (world.getBlockState(currentRotorPosition).getBlock() == TurbineRotorShaft.INSTANCE) {
                            Vector4i shaftSectionConfiguration = new Vector4i();
                            int i = 0;
                            for (Direction bladeDirection : Direction.values()) {
                                if (bladeDirection.getAxis() == shaftAxis) {
                                    continue;
                                }
                                
                                int bladeCount = 0;
                                
                                currentBladePosition = currentRotorPosition;
                                currentBladePosition = currentBladePosition.offset(bladeDirection);
                                while (world.getBlockState(currentBladePosition).getBlock() == TurbineRotorBlade.INSTANCE) {
                                    bladeCount++;
                                    currentBladePosition = currentBladePosition.offset(bladeDirection);
                                }
                                
                                shaftSectionConfiguration.setComponent(i, bladeCount);
                                
                                i++;
                            }
                            
                            rotorConfiguration.add(shaftSectionConfiguration);
                            currentRotorPosition = currentRotorPosition.offset(value);
                        }
                        
                        break;
                    }
                }
                
            }
        } else {
            for (TurbineRotorBearingTile rotorBearing : rotorBearings) {
                rotorBearing.isRenderBearing = false;
            }
        }
    }
    
    long storedPower = 0;
    long maxStoredPower = 0;
    
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
                long availableSpace = tankSize - water;
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
                
                // oh noes, there is a cap now, *no over speeding your fucking turbines*
                if (rotorSpeed > 2245) {
                    efficiency = -rotorSpeed / 4490;
                    efficiency += 1;
                }
                if (efficiency < 0) {
                    efficiency = 0;
                }
                
                energyToGenerate *= efficiency;
                
                energyGeneratedLastTick = energyToGenerate;
                
                energyToGenerate = Math.min(energyToGenerate, maxStoredPower - storedPower);
                
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
            if (water > tankSize) {
                water = tankSize;
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
        
        if (Phosphophyllite.tickNumber() % 10 == 0) {
            for (TurbineRotorBearingTile rotorBearing : rotorBearings) {
                world.notifyBlockUpdate(rotorBearing.getPos(), rotorBearing.getBlockState(), rotorBearing.getBlockState(), 0);
            }
        }
        
        if (Phosphophyllite.tickNumber() % 2 == 0) {
            markDirty();
        }
    }
    
    public void updateDataPacket(@Nonnull TurbineState turbineState) {
        turbineState.turbineActivity = turbineActivity;
        turbineState.ventState = ventState;
        turbineState.coilStatus = coilEngaged;
        
        turbineState.flowRate = maxFlowRate;
        turbineState.efficiencyRate = rotorEfficiencyLastTick;
        turbineState.turbineOutputRate = energyGeneratedLastTick;
        
        turbineState.currentRPM = (rotorBlades.size() > 0 && rotorMass > 0 ? rotorEnergy / (double) (rotorBlades.size() * rotorMass) : 0);
        turbineState.maxRPM = 2200.0;
        
        turbineState.intakeStored = steam;
        turbineState.intakeCapacity = tankSize;
        
        turbineState.exhaustStored = water;
        turbineState.exhaustCapacity = tankSize;
        
        turbineState.energyStored = storedPower;
        turbineState.energyCapacity = maxStoredPower;
    }
    
    @SuppressWarnings("UnnecessaryReturnStatement")
    public void runRequest(@Nonnull String requestName, @Nullable Object requestData) {
        switch (requestName) {
            // Set the turbine to ACTIVE or INACTIVE.
            case "setActive": {
                if (!(requestData instanceof Integer)) {
                    return;
                }
                setActive(TurbineActivity.fromInt((Integer) requestData));
                return;
            }
            // Change flow rate by value.
            case "changeFlowRate": {
                if (!(requestData instanceof Long)) {
                    return;
                }
                setMaxFlowRate(maxFlowRate + ((Long) requestData));
                return;
            }
            // Set coils to engaged or disengaged.
            case "setCoilEngaged": {
                if (!(requestData instanceof Integer)) {
                    return;
                }
                setCoilEngaged(((Integer) requestData != 0));
                return;
            }
            // Set vent state to OVERFLOW, ALL, or CLOSED.
            case "setVentState": {
                if (!(requestData instanceof Integer)) {
                    return;
                }
                setVentState(VentState.fromInt((int) requestData));
                return;
            }
        }
    }
    
    VentState ventState = VentState.OVERFLOW;
    
    private void setVentState(@Nonnull VentState newVentState) {
        ventState = newVentState;
    }
    
    long maxFlowRate = 0;
    
    private void setMaxFlowRate(long flowRate) {
        if (flowRate < 0) {
            flowRate = 0;
        }
        if (flowRate > flowRateLimit) {
            flowRate = flowRateLimit;
        }
        maxFlowRate = flowRate;
    }
    
    private boolean coilEngaged = true;
    
    private void setCoilEngaged(boolean engaged) {
        coilEngaged = engaged;
    }
    
    @Nonnull
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
    
    protected void read(@Nonnull CompoundNBT compound) {
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
            //ventState = VentState.toInt(compound.getString("ventState").toUpperCase());
            ventState = VentState.fromInt(compound.getInt(compound.getString("ventState")));
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
    @Nonnull
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
    
    // -- Mekanism compat
    
    public long getSteamCapacity() {
        return tankSize;
    }
    
    public long getSteamAmount() {
        return steam;
    }
    
    // -- ComputerCraft API --
    
    public boolean CCgetConnected() {
        return state != MultiblockController.AssemblyState.DISASSEMBLED;
    }
    
    public boolean CCgetActive() {
        return turbineActivity == TurbineActivity.ACTIVE;
    }
    
    public long CCgetEnergyStored() {
        // backwards compatible with the old CC API, which requires this assumption
        return (storedPower * 1_000_000) / maxStoredPower;
    }
    
    public long CCgetEnergyStoredUnscaled() {
        return storedPower;
    }
    
    public long CCgetMaxEnergyStored() {
        return maxStoredPower;
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
        return tankSize;
    }
    
    public long CCgetFluidFlowRate() {
        return fluidConsumedLastTick;
    }
    
    public long CCgetFluidFlowRateMax() {
        return maxFlowRate;
    }
    
    public long CCgetFluidFlowRateMaxMax() {
        return flowRateLimit;
    }
    
    public double CCgetEnergyProducedLastTick() {
        return energyGeneratedLastTick;
    }
    
    public long CCgetNumberOfBlades() {
        return bladeSurfaceArea;
    }
    
    public double CCgetBladeEfficiency() {
        return rotorEfficiencyLastTick;
    }
    
    public long CCgetRotorMass() {
        return rotorMass;
    }
    
    public boolean CCgetInductorEngaged() {
        return coilEngaged;
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetActive(boolean active) {
        setActive(active ? TurbineActivity.ACTIVE : TurbineActivity.INACTIVE);
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetFluidFlowRateMax(long maxFlowRate) {
        setMaxFlowRate(maxFlowRate);
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetVentNone() {
        setVentState(VentState.CLOSED);
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetVentOverflow() {
        setVentState(VentState.OVERFLOW);
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetVentAll() {
        setVentState(VentState.ALL);
    }
    
    @SuppressWarnings("SpellCheckingInspection")
    public void CCsetInductorEngaged(boolean engaged) {
        setCoilEngaged(engaged);
    }
}
