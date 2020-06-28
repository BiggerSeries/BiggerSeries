package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.blocks.*;
import net.roguelogix.biggerreactors.classic.reactor.simulation.ClassicReactorSimulation;
import net.roguelogix.biggerreactors.classic.reactor.tiles.*;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.util.Util;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;


/* TODO

assembly errors

 */
public class ReactorMultiblockController extends RectangularMultiblockController {

    public ReactorMultiblockController(World world) {
        super(world);
        minWidth = minHeight = minLength = 3;
        maxLength = Config.Reactor.MaxLength;
        maxWidth = Config.Reactor.MaxWidth;
        maxHeight = Config.Reactor.MaxHeight;
        tileAttachValidator = tile -> {
            return tile instanceof ReactorBaseTile;
        };
        frameValidator = block -> {
            return block instanceof ReactorCasing;
        };
        cornerValidator = frameValidator;
        exteriorValidator = Validator.or(frameValidator, block -> {
            return block instanceof ReactorTerminal ||
                    block instanceof ReactorControlRod ||
                    block instanceof ReactorGlass ||
                    block instanceof ReactorAccessPort ||
                    block instanceof ReactorPowerTap;
        });
        interiorValidator = block -> {
            if(block instanceof ReactorFuelRod){
                return true;
            }
            if(!ReactorModeratorRegistry.isBlockAllowed(block)){
                return false;
            }
            if(exteriorValidator.validate(block)){
                return false;
            }
            return true;
        };
        setAssemblyValidator(genericController -> {
            ReactorMultiblockController reactorController = (ReactorMultiblockController) genericController;
            if (terminals.isEmpty()) {
                return false;
            }
            if (controlRods.isEmpty()) {
                return false;
            }
            for (ReactorControlRodTile controlRod : controlRods) {
                if (controlRod.getPos().getY() != maxY()) {
                    return false;
                }
                for (int i = 0; i < maxY() - minY() - 1; i++) {
                    if (!(world.getBlockState(controlRod.getPos().add(0, -1 - i, 0)).getBlock() instanceof ReactorFuelRod)) {
                        return false;
                    }
                }
            }

            for (ReactorFuelRodTile fuelRod : fuelRods) {
                if (!(world.getBlockState(new BlockPos(fuelRod.getPos().getX(), maxY(), fuelRod.getPos().getZ())).getBlock() instanceof ReactorControlRod)) {
                    return false;
                }
            }

            return true;
        });
    }

    private ReactorState reactorState = ReactorState.INACTIVE;
    
    private final Set<ReactorTerminalTile> terminals = new HashSet<>();
    private final Set<ReactorControlRodTile> controlRods = new HashSet<>();
    private final Set<ReactorFuelRodTile> fuelRods = new HashSet<>();
    private final Set<ReactorPowerTapTile> powerPorts = new HashSet<>();
    private final Set<ReactorAccessPortTile> accessPorts = new HashSet<>();

    @Override
    protected void onPartAdded(MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.add((ReactorTerminalTile) tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            controlRods.add((ReactorControlRodTile) tile);
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.add((ReactorFuelRodTile) tile);
        }
        if (tile instanceof ReactorPowerTapTile) {
            powerPorts.add((ReactorPowerTapTile) tile);
        }
        if (tile instanceof ReactorAccessPortTile) {
            accessPorts.add((ReactorAccessPortTile) tile);
        }
    }

    @Override
    protected void onPartRemoved(MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.remove(tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            controlRods.remove(tile);
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.remove(tile);
        }
        if (tile instanceof ReactorPowerTapTile) {
            powerPorts.remove(tile);
        }
        if (tile instanceof ReactorAccessPortTile) {
            accessPorts.remove(tile);
        }
    }

    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockState(terminal.getPos(), terminal.getBlockState().with(ReactorState.REACTOR_STATE_ENUM_PROPERTY, reactorState));
            terminal.markDirty();
        });
    }

    public void setActive(ReactorState newState) {
        if (reactorState != newState) {
            reactorState = newState;
            updateBlockStates();
        }
        simulation.setActive(reactorState == ReactorState.ACTIVE);
    }

    public void toggleActive() {
        setActive(reactorState == ReactorState.ACTIVE ? ReactorState.INACTIVE : ReactorState.ACTIVE);
    }

    protected void read(CompoundNBT compound) {
        if (compound.contains("reactorState")) {
            reactorState = ReactorState.valueOf(compound.getString("reactorState").toUpperCase());
            simulation.setActive(reactorState == ReactorState.ACTIVE);
        }
        
        if(compound.contains("simulationData")){
            simulation.deserializeNBT(compound.getCompound("simulationData"));
        }
        
        updateBlockStates();
    }

    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        {
            compound.putString("reactorState", reactorState.toString());
            compound.put("simulationData", simulation.serializeNBT());
        }
        return compound;
    }

    @Override
    protected void onAssembly() {
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
        simulation.resize(maxX() - minX() - 1, maxY() - minY() - 1, maxZ() - minZ() - 1);
        Vector3i start = new Vector3i(minX() + 1, minY() + 1, minZ() + 1);
        Vector3i end = new Vector3i(maxX() - 1, maxY() - 1, maxZ() - 1);
        Util.chunkCachedBlockStateIteration(start, end, world, (state, pos) -> {
            pos.sub(start);
            if (state.getBlock() != ReactorFuelRod.INSTANCE) {
                simulation.setModeratorProperties(pos.x, pos.y, pos.z, ReactorModeratorRegistry.blockModeratorProperties(state.getBlock()));
            }
            return true;
        });
        for (ReactorControlRodTile controlRod : controlRods) {
            BlockPos rodPos = controlRod.getPos();
            simulation.setControlRod(rodPos.getX() - start.x, rodPos.getZ() - start.z);
        }
        simulation.updateInternalValues();
    }
    
    @Override
    protected void onDisassembly() {
        setActive(ReactorState.INACTIVE);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
    }
    
    
    private ClassicReactorSimulation simulation = new ClassicReactorSimulation();
    
    private long storedPower = 0;
    
    @Override
    public void tick() {
        
        simulation.tick();
        if (!Float.isNaN(simulation.FEProducedLastTick)) {
            storedPower += simulation.FEProducedLastTick;
            if (storedPower > Config.Reactor.PassiveBatterySize) {
                storedPower = Config.Reactor.PassiveBatterySize;
            }
        }
        
        if (simulation.fuelTank.spaceAvailable() >= 1000) {
            // for now, its ingots only
            for (ReactorAccessPortTile accessPort : accessPorts) {
                long maxIngots = simulation.fuelTank.spaceAvailable() / 1000;
                if (maxIngots == 0) {
                    break;
                }
                long ingots = accessPort.refuel(maxIngots);
                simulation.fuelTank.insertFuel(ingots * 1000, false);
            }
        }
        
        
        long totalPowerRequested = 0;
        for (ReactorPowerTapTile powerPort : powerPorts) {
            totalPowerRequested += powerPort.distributePower(storedPower, true);
        }
    
        float distributionMultiplier = Math.min(1f, (float)storedPower/(float)totalPowerRequested);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            long powerRequested = powerPort.distributePower(storedPower, true);
            powerRequested *= distributionMultiplier;
            powerRequested = Math.min(storedPower, powerRequested); // just in case
            storedPower -= powerPort.distributePower(powerRequested, false);
        }
    }
    
    @Override
    public String getInfo() {
        return super.getInfo() +
                "State: " + reactorState.toString() + "\n" +
                "StoredPower: " + storedPower + "\n" +
                "PowerProduction: " + simulation.getFEProducedLastTick() + "\n" +
                "FuelUsage: " + simulation.getFuelConsumedLastTick() + "\n" +
                "ReactantCapacity: " + simulation.fuelTank.getCapacity() + "\n" +
                "TotalReactant: " + simulation.fuelTank.getTotalAmount() + "\n" +
                "Fuel: " + simulation.fuelTank.getFuelAmount() + "\n" +
                "Waste: " + simulation.fuelTank.getWasteAmount() + "\n" +
                "Fertility: " + simulation.getFertility() + "\n" +
                "FuelHeat: " + simulation.getFuelHeat() + "\n" +
                "ReactorHeat: " + simulation.getReactorHeat() + "\n" +
                "";
    }
}