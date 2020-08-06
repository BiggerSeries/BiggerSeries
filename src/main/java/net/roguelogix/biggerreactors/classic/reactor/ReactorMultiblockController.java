package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.blocks.*;
import net.roguelogix.biggerreactors.classic.reactor.simulation.ClassicReactorSimulation;
import net.roguelogix.biggerreactors.classic.reactor.tiles.*;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineBaseBlock;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineBaseTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.util.Util;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;


public class ReactorMultiblockController extends RectangularMultiblockController {
    
    public ReactorMultiblockController(World world) {
        super(world);
        minX = minY = minZ = 3;
        maxX = Config.Reactor.MaxLength;
        maxZ = Config.Reactor.MaxWidth;
        maxY = Config.Reactor.MaxHeight;
        tileAttachValidator = tile -> {
            return tile instanceof ReactorBaseTile;
        };
        frameValidator = block -> {
            return block instanceof ReactorCasing;
        };
        exteriorValidator = Validator.or(frameValidator, block -> {
            return block instanceof ReactorTerminal ||
                    block instanceof ReactorControlRod ||
                    block instanceof ReactorGlass ||
                    block instanceof ReactorAccessPort ||
                    block instanceof ReactorCoolantPort ||
                    block instanceof ReactorPowerTap;
        });
        interiorValidator = block -> {
            if (block instanceof ReactorFuelRod) {
                return true;
            }
            if (!ReactorModeratorRegistry.isBlockAllowed(block)) {
                return false;
            }
            return !exteriorValidator.validate(block);
        };
        setAssemblyValidator(genericController -> {
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
            
            Util.chunkCachedBlockStateIteration(new Vector3i(minX(), minY(), minZ()), new Vector3i(maxX(), maxY(), maxZ()), world, (block, pos) -> {
                if (block.getBlock() instanceof ReactorBaseBlock) {
                    TileEntity te = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));
                    if (te instanceof ReactorBaseTile) {
                        if (!((ReactorBaseTile) te).isCurrentController(this)) {
                            throw new ValidationError("multiblock.error.biggerreactors.dangling_internal_part");
                        }
                    }
                }
            });
            
            return true;
        });
    }
    
    private ReactorState reactorState = ReactorState.INACTIVE;
    
    private final Set<ReactorTerminalTile> terminals = new HashSet<>();
    private final Set<ReactorControlRodTile> controlRods = new HashSet<>();
    private final Set<ReactorFuelRodTile> fuelRods = new HashSet<>();
    private final Set<ReactorPowerTapTile> powerPorts = new HashSet<>();
    private final Set<ReactorAccessPortTile> accessPorts = new HashSet<>();
    private final Set<ReactorCoolantPortTile> coolantPorts = new HashSet<>();
    
    @Override
    protected void onPartAdded(MultiblockTile tile) {
        distributeFuel();
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
        if (tile instanceof ReactorCoolantPortTile) {
            coolantPorts.add((ReactorCoolantPortTile) tile);
        }
    }
    
    @Override
    protected void onPartRemoved(MultiblockTile tile) {
        distributeFuel();
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
        if (tile instanceof ReactorCoolantPortTile) {
            coolantPorts.remove(tile);
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
        
        if (compound.contains("simulationData")) {
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
    protected void onMerge(MultiblockController otherController) {
        setActive(ReactorState.INACTIVE);
        distributeFuel();
        assert otherController instanceof ReactorMultiblockController;
        ((ReactorMultiblockController) otherController).distributeFuel();
        storedPower = 0;
        simulation = new ClassicReactorSimulation();
    }
    
    @Override
    protected void onAssembly() {
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            coolantPort.updateOutputDirection();
        }
        for (ReactorAccessPortTile accessPort : accessPorts) {
            accessPort.updateOutputDirection();
        }
        simulation.resize(maxX() - minX() - 1, maxY() - minY() - 1, maxZ() - minZ() - 1);
        Vector3i start = new Vector3i(minX() + 1, minY() + 1, minZ() + 1);
        Vector3i end = new Vector3i(maxX() - 1, maxY() - 1, maxZ() - 1);
        Util.chunkCachedBlockStateIteration(start, end, world, (state, pos) -> {
            pos.sub(start);
            if (state.getBlock() != ReactorFuelRod.INSTANCE) {
                simulation.setModeratorProperties(pos.x, pos.y, pos.z, ReactorModeratorRegistry.blockModeratorProperties(state.getBlock()));
            }
        });
        for (ReactorControlRodTile controlRod : controlRods) {
            BlockPos rodPos = controlRod.getPos();
            simulation.setControlRod(rodPos.getX() - start.x, rodPos.getZ() - start.z);
        }
        simulation.setPassivelyCooled(coolantPorts.isEmpty());
        simulation.updateInternalValues();
        collectFuel();
    }
    
    @Override
    protected void onDisassembly() {
        setActive(ReactorState.INACTIVE);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            coolantPort.updateOutputDirection();
        }
    }
    
    
    private ClassicReactorSimulation simulation = new ClassicReactorSimulation();
    
    private long storedPower = 0;
    
    public long addCoolant(long coolant, boolean simulated) {
        return simulation.coolantTank.insertWater(coolant, simulated);
    }
    
    public long extractSteam(long steam, boolean simulated) {
        return simulation.coolantTank.extractSteam(steam, simulated);
    }
    
    @Override
    public void tick() {
        
        simulation.tick();
        if (!Double.isNaN(simulation.FEProducedLastTick) && simulation.isPassive()) {
            storedPower += simulation.FEProducedLastTick;
            if (storedPower > Config.Reactor.PassiveBatterySize) {
                storedPower = Config.Reactor.PassiveBatterySize;
            }
        }
        
        for (ReactorAccessPortTile accessPort : accessPorts) {
            // todo, output to inputs if there aren't any outputs left
            if (accessPort.isInlet()) {
                continue;
            }
            long wasteSpaceAvailable = accessPort.wasteSpaceAvailable();
            simulation.fuelTank.extractWaste(accessPort.dumpWaste(simulation.fuelTank.extractWaste(wasteSpaceAvailable, true)), false);
            
        }
        
        if (simulation.fuelTank.spaceAvailable() > 0) {
            for (ReactorAccessPortTile accessPort : accessPorts) {
                long ingots = accessPort.refuel(simulation.fuelTank.spaceAvailable());
                simulation.fuelTank.insertFuel(ingots, false);
            }
        }
        
        
        long totalPowerRequested = 0;
        for (ReactorPowerTapTile powerPort : powerPorts) {
            totalPowerRequested += powerPort.distributePower(storedPower, true);
        }
        long startingPower = storedPower;
        
        float distributionMultiplier = Math.min(1f, (float) storedPower / (float) totalPowerRequested);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            long powerRequested = powerPort.distributePower(startingPower, true);
            powerRequested *= distributionMultiplier;
            powerRequested = Math.min(storedPower, powerRequested); // just in case
            storedPower -= powerPort.distributePower(powerRequested, false);
        }
        
        // i know this is just a hose out, not sure if it should be changed or not
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            simulation.coolantTank.extractSteam(coolantPort.pushSteam(simulation.coolantTank.extractSteam(Integer.MAX_VALUE, true)), false);
        }
        
        for (ReactorAccessPortTile accessPort : accessPorts) {
            accessPort.pushWaste();
        }
    }
    
    private void distributeFuel() {
        if (simulation.fuelTank.getTotalAmount() > 0 && !fuelRods.isEmpty()) {
            long fuelToDistribute = simulation.fuelTank.getFuelAmount();
            long wasteToDistribute = simulation.fuelTank.getWasteAmount();
            fuelToDistribute /= fuelRods.size();
            wasteToDistribute /= fuelRods.size();
            for (ReactorFuelRodTile fuelRod : fuelRods) {
                fuelRod.fuel += simulation.fuelTank.extractFuel(fuelToDistribute, false);
                fuelRod.waste += simulation.fuelTank.extractWaste(wasteToDistribute, false);
            }
            for (ReactorFuelRodTile fuelRod : fuelRods) {
                fuelRod.fuel += simulation.fuelTank.extractFuel(Long.MAX_VALUE, false);
                fuelRod.waste += simulation.fuelTank.extractWaste(Long.MAX_VALUE, false);
            }
        }
    }
    
    private void collectFuel() {
        for (ReactorFuelRodTile fuelRod : fuelRods) {
            fuelRod.fuel -= simulation.fuelTank.insertFuel(fuelRod.fuel, false);
            fuelRod.waste -= simulation.fuelTank.insertWaste(fuelRod.waste, false);
            if (fuelRod.fuel != 0 || fuelRod.waste != 0) {
                // TODO: 7/9/20 log this shit, shouldn't happen
                // for now, just void the fuel
                fuelRod.fuel = 0;
                fuelRod.waste = 0;
            }
        }
    }
    
    public void updateDataPacket(ReactorDatapack data) {
        data.reactorStatus = reactorState == ReactorState.ACTIVE;
        data.reactorType = !simulation.isPassive();
        
        data.energyStored = storedPower;
        data.energyCapacity = Config.Reactor.PassiveBatterySize;
        
        data.caseHeatStored = simulation.getReactorHeat();
        data.fuelHeatStored = simulation.getFuelHeat();
        
        data.wasteStored = simulation.fuelTank.getWasteAmount();
        data.reactantStored = simulation.fuelTank.getTotalAmount();
        data.fuelCapacity = simulation.fuelTank.getCapacity();
        
        data.coolantCapacity = simulation.coolantTank.getPerSideCapacity();
        data.coolantStored = simulation.coolantTank.getWaterAmount();
        data.steamStored = simulation.coolantTank.getSteamAmount();
        
        data.reactorOutputRate = simulation.getFEProducedLastTick();
        data.fuelUsageRate = simulation.getFuelConsumedLastTick();
        data.reactivityRate = simulation.getFertility();
    }
    
    public void runRequest(String requestName, Object requestData) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (requestName) {
            case "setActive": {
                Boolean newState = (Boolean) requestData;
                setActive(newState ? ReactorState.ACTIVE : ReactorState.INACTIVE);
            }
        }
    }
    
    @Override
    public String getDebugInfo() {
        return super.getDebugInfo() +
                "State: " + reactorState.toString() + "\n" +
                "StoredPower: " + storedPower + "\n" +
                "PowerProduction: " + simulation.getFEProducedLastTick() + "\n" +
                "FuelUsage: " + simulation.getFuelConsumedLastTick() + "\n" +
                "ReactantCapacity: " + simulation.fuelTank.getCapacity() + "\n" +
                "TotalReactant: " + simulation.fuelTank.getTotalAmount() + "\n" +
                "PercentFull: " + (float) simulation.fuelTank.getTotalAmount() * 100 / simulation.fuelTank.getCapacity() + "\n" +
                "Fuel: " + simulation.fuelTank.getFuelAmount() + "\n" +
                "Waste: " + simulation.fuelTank.getWasteAmount() + "\n" +
                "Fertility: " + simulation.getFertility() + "\n" +
                "FuelHeat: " + simulation.getFuelHeat() + "\n" +
                "ReactorHeat: " + simulation.getReactorHeat() + "\n" +
                "CoolantTankSize: " + simulation.coolantTank.getPerSideCapacity() + "\n" +
                "Water: " + simulation.coolantTank.getWaterAmount() + "\n" +
                "Steam: " + simulation.coolantTank.getSteamAmount() + "\n" +
                "";
    }
}