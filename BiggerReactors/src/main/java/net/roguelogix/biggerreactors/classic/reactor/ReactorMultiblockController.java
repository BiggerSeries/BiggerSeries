package net.roguelogix.biggerreactors.classic.reactor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.blocks.*;
import net.roguelogix.biggerreactors.classic.reactor.simulation.ClassicReactorSimulation;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorType;
import net.roguelogix.biggerreactors.classic.reactor.tiles.*;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ReactorMultiblockController extends RectangularMultiblockController {
    
    public ReactorMultiblockController(@Nonnull World world) {
        super(world);
        
        minSize.set(3);
        maxSize.set(Config.Reactor.MaxLength, Config.Reactor.MaxHeight, Config.Reactor.MaxWidth);
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
                    block instanceof ReactorPowerTap ||
                    block instanceof ReactorRedstonePort ||
                    block instanceof ReactorComputerPort;
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
                throw new ValidationError("multiblock.error.biggerreactors.no_terminal");
            }
            if (controlRods.isEmpty()) {
                throw new ValidationError("multiblock.error.biggerreactors.no_rods");
            }
            if(!powerPorts.isEmpty() && !coolantPorts.isEmpty()){
                throw new ValidationError("multiblock.error.biggerreactors.coolant_and_power_ports");
            }
            for (ReactorControlRodTile controlRod : controlRods) {
                if (controlRod.getPos().getY() != maxCoord().y()) {
                    throw new ValidationError(new TranslationTextComponent("multiblock.error.biggerreactors.control_rod_not_on_top", controlRod.getPos().getX(), controlRod.getPos().getY(), controlRod.getPos().getZ()));
                }
                for (int i = 0; i < maxCoord().y() - minCoord().y() - 1; i++) {
                    if (!(world.getBlockState(controlRod.getPos().add(0, -1 - i, 0)).getBlock() instanceof ReactorFuelRod)) {
                        throw new ValidationError(new TranslationTextComponent("multiblock.error.biggerreactors.fuel_rod_gap", controlRod.getPos().getX(), -1 - i, controlRod.getPos().getZ()));
                    }
                }
            }
            
            for (ReactorFuelRodTile fuelRod : fuelRods) {
                if (!(world.getBlockState(new BlockPos(fuelRod.getPos().getX(), maxCoord().y(), fuelRod.getPos().getZ())).getBlock() instanceof ReactorControlRod)) {
                    throw new ValidationError(new TranslationTextComponent("multiblock.error.biggerreactors.no_control_rod_for_fuel_rod", fuelRod.getPos().getX(), fuelRod.getPos().getZ()));
                }
            }
            
            Util.chunkCachedBlockStateIteration(minCoord(), maxCoord(), world, (block, pos) -> {
                if (block.getBlock() instanceof ReactorBaseBlock) {
                    TileEntity te = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));
                    if (te instanceof ReactorBaseTile) {
                        if (!((ReactorBaseTile) te).isCurrentController(this)) {
                            throw new ValidationError(new TranslationTextComponent("multiblock.error.biggerreactors.dangling_internal_part", te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()));
                        }
                    }
                }
            });
            
            return true;
        });
    }
    
    private ReactorActivity reactorActivity = ReactorActivity.INACTIVE;
    
    private final Set<ReactorTerminalTile> terminals = new HashSet<>();
    private final List<ReactorControlRodTile> controlRods = new ArrayList<>();
    private final Set<ReactorFuelRodTile> fuelRods = new HashSet<>();
    private final ArrayList<Set<ReactorFuelRodTile>> fuelRodsByLevel = new ArrayList<>();
    private final Set<ReactorPowerTapTile> powerPorts = new HashSet<>();
    private final Set<ReactorAccessPortTile> accessPorts = new HashSet<>();
    private final Set<ReactorCoolantPortTile> coolantPorts = new HashSet<>();
    private final Set<ReactorRedstonePortTile> redstonePorts = new HashSet<>();
    
    @Override
    protected void onPartPlaced(@Nonnull MultiblockTile placed) {
        onPartAttached(placed);
    }
    
    
    @Override
    protected void onPartAttached(@Nonnull MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.add((ReactorTerminalTile) tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            synchronized (controlRods) {
                if (!controlRods.contains(tile)) {
                    controlRods.add((ReactorControlRodTile) tile);
                }
            }
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.add((ReactorFuelRodTile) tile);
        }
        if (tile instanceof ReactorPowerTapTile) {
            powerPorts.add((ReactorPowerTapTile) tile);
        }
        if (tile instanceof ReactorAccessPortTile) {
            synchronized (accessPorts) {
                accessPorts.add((ReactorAccessPortTile) tile);
            }
        }
        if (tile instanceof ReactorCoolantPortTile) {
            coolantPorts.add((ReactorCoolantPortTile) tile);
        }
        if (tile instanceof ReactorRedstonePortTile) {
            redstonePorts.add((ReactorRedstonePortTile) tile);
        }
    }
    
    @Override
    protected void onPartBroken(@Nonnull MultiblockTile broken) {
        onPartDetached(broken);
    }
    
    @Override
    protected void onPartDetached(@Nonnull MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.remove(tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            synchronized (controlRods) {
                controlRods.remove(tile);
            }
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.remove(tile);
        }
        if (tile instanceof ReactorPowerTapTile) {
            powerPorts.remove(tile);
        }
        if (tile instanceof ReactorAccessPortTile) {
            synchronized (accessPorts) {
                accessPorts.remove(tile);
            }
        }
        if (tile instanceof ReactorCoolantPortTile) {
            coolantPorts.remove(tile);
        }
        if (tile instanceof ReactorRedstonePortTile) {
            redstonePorts.remove(tile);
        }
    }
    
    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockState(terminal.getPos(), terminal.getBlockState().with(ReactorActivity.REACTOR_ACTIVITY_ENUM_PROPERTY, reactorActivity));
            terminal.markDirty();
        });
    }
    
    public synchronized void setActive(@Nonnull ReactorActivity newState) {
        if (reactorActivity != newState) {
            reactorActivity = newState;
            updateBlockStates();
        }
        simulation.setActive(reactorActivity == ReactorActivity.ACTIVE);
    }
    
    public void toggleActive() {
        setActive(reactorActivity == ReactorActivity.ACTIVE ? ReactorActivity.INACTIVE : ReactorActivity.ACTIVE);
    }
    
    protected void read(@Nonnull CompoundNBT compound) {
        if (compound.contains("reactorState")) {
            reactorActivity = ReactorActivity.valueOf(compound.getString("reactorState").toUpperCase());
            simulation.setActive(reactorActivity == ReactorActivity.ACTIVE);
        }
        
        if (compound.contains("simulationData")) {
            simulation.deserializeNBT(compound.getCompound("simulationData"));
        }
        
        if (compound.contains("storedPower")) {
            simulation.battery.extractPower(-compound.getLong("storedPower"));
        }
        
        updateBlockStates();
    }
    
    @Nonnull
    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        {
            compound.putString("reactorState", reactorActivity.toString());
            compound.put("simulationData", simulation.serializeNBT());
        }
        return compound;
    }
    
    @Override
    protected void onMerge(@Nonnull MultiblockController otherController) {
        setActive(ReactorActivity.INACTIVE);
        distributeFuel();
        assert otherController instanceof ReactorMultiblockController;
        ((ReactorMultiblockController) otherController).distributeFuel();
        simulation = new ClassicReactorSimulation();
    }
    
    @Override
    protected void onAssembled() {
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            coolantPort.updateOutputDirection();
        }
        for (ReactorAccessPortTile accessPort : accessPorts) {
            accessPort.updateOutputDirection();
        }
        for (ReactorRedstonePortTile redstonePort : redstonePorts) {
            redstonePort.updateOutputDirection();
        }
        simulation.resize(maxCoord().x() - minCoord().x() - 1, maxCoord().y() - minCoord().y() - 1, maxCoord().z() - minCoord().z() - 1);
        Vector3i start = new Vector3i(1).add(minCoord());
        Vector3i end = new Vector3i(-1).add(maxCoord());
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
        updateControlRodLevels();
        collectFuel();
        
        int levels = this.maxCoord().y() - this.minCoord().y() - 1;
        fuelRodsByLevel.clear();
        fuelRodsByLevel.ensureCapacity(levels);
        for (int i = 0; i < levels; i++) {
            fuelRodsByLevel.add(new HashSet<>());
        }
        
        fuelRods.forEach(rod -> {
            int rodLevel = rod.getPos().getY();
            rodLevel -= this.minCoord().y();
            rodLevel -= 1;
            fuelRodsByLevel.get(rodLevel).add(rod);
        });
        
        updateFuelRenderingLevel(true);
    }
    
    @Override
    protected void onUnpaused() {
        onAssembled();
    }
    
    @Override
    protected void onDisassembled() {
        distributeFuel();
        setActive(ReactorActivity.INACTIVE);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            powerPort.updateOutputDirection();
        }
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            coolantPort.updateOutputDirection();
        }
    }
    
    
    private ClassicReactorSimulation simulation = new ClassicReactorSimulation();
    
    public long addCoolant(long coolant, boolean simulated) {
        return simulation.coolantTank.insertWater(coolant, simulated);
    }
    
    public long extractSteam(long steam, boolean simulated) {
        return simulation.coolantTank.extractSteam(steam, simulated);
    }
    
    private boolean forceDirty = false;
    
    @Override
    public void tick() {
        
        simulation.tick();
        if (autoEjectWaste) {
            ejectWaste();
        }
        
        
        long totalPowerRequested = 0;
        final long startingPower = simulation.battery.storedPower();
        for (ReactorPowerTapTile powerPort : powerPorts) {
            totalPowerRequested += powerPort.distributePower(startingPower, true);
        }
        
        float distributionMultiplier = Math.min(1f, (float) startingPower / (float) totalPowerRequested);
        for (ReactorPowerTapTile powerPort : powerPorts) {
            long powerRequested = powerPort.distributePower(startingPower, true);
            powerRequested *= distributionMultiplier;
            powerRequested = Math.min(simulation.battery.storedPower(), powerRequested); // just in casei
            long powerAccepted = powerPort.distributePower(powerRequested, false);
            simulation.battery.extractPower(powerAccepted);
        }
        
        // i know this is just a hose out, not sure if it should be changed or not
        for (ReactorCoolantPortTile coolantPort : coolantPorts) {
            simulation.coolantTank.extractSteam(coolantPort.pushSteam(simulation.coolantTank.extractSteam(Integer.MAX_VALUE, true)), false);
        }
        
        updateFuelRenderingLevel();
        
        if(Phosphophyllite.tickNumber() % 2 == 0 || forceDirty){
            forceDirty = false;
            markDirty();
        }
    }
    
    long currentFuelRenderLevel = 0;
    long currentWasteRenderLevel = 0;
    
    private void updateFuelRenderingLevel() {
        updateFuelRenderingLevel(false);
    }
    
    private void updateFuelRenderingLevel(boolean forceFullUpdate) {
        
        if(simulation.fuelTank.getCapacity() == 0){
            return;
        }
        
        long rodPixels = fuelRodsByLevel.size() * 16L;
        long fuelPixels = (simulation.fuelTank.getTotalAmount() * rodPixels) / simulation.fuelTank.getCapacity();
        long wastePixels = (simulation.fuelTank.getWasteAmount() * rodPixels) / simulation.fuelTank.getCapacity();
        
        if (fuelPixels == currentFuelRenderLevel && wastePixels == currentWasteRenderLevel) {
            return;
        }
        
        long lowerFuelPixel = Math.min(currentFuelRenderLevel, fuelPixels);
        long upperFuelPixel = Math.max(currentFuelRenderLevel, fuelPixels);
        
        long lowerWastePixel = Math.min(currentWasteRenderLevel, wastePixels);
        long upperWastePixel = Math.max(currentWasteRenderLevel, wastePixels);
        
        if (forceFullUpdate) {
            lowerFuelPixel = lowerWastePixel = 0;
            upperFuelPixel = upperWastePixel = rodPixels;
        }
        
        long lowerFuelUpdateLevel = lowerFuelPixel / 16;
        long upperFuelUpdateLevel = upperFuelPixel / 16 + (((upperFuelPixel % 16) > 0) ? 1 : 0);
        
        long lowerWasteUpdateLevel = lowerWastePixel / 16;
        long upperWasteUpdateLevel = upperWastePixel / 16 + (((upperWastePixel % 16) > 0) ? 1 : 0);
        
        HashMap<BlockPos, BlockState> newStates = new HashMap<>();
        boolean[] updatedLevels = new boolean[(int) upperFuelUpdateLevel];
        
        if (lowerFuelPixel != upperFuelPixel) {
            for (long i = lowerFuelUpdateLevel; i < upperFuelUpdateLevel; i++) {
                long levelBasePixel = i * 16;
                int levelFuelPixel = (int) Math.max(Math.min(fuelPixels - levelBasePixel, 16), 0);
                
                for (ReactorFuelRodTile reactorFuelRodTile : fuelRodsByLevel.get((int) i)) {
                    BlockState state = newStates.computeIfAbsent(reactorFuelRodTile.getPos(), k -> reactorFuelRodTile.getBlockState());
                    BlockState newState = state.with(ReactorFuelRod.FUEL_HEIGHT_PROPERTY, levelFuelPixel);
                    if (newState != state) {
                        newStates.put(reactorFuelRodTile.getPos(), newState);
                        updatedLevels[(int) i] = true;
                    }
                }
                
            }
        }
        
        if (lowerWastePixel != upperWastePixel) {
            for (long i = lowerWasteUpdateLevel; i < upperWasteUpdateLevel; i++) {
                long levelBasePixel = i * 16;
                int levelWastePixel = (int) Math.max(Math.min(wastePixels - levelBasePixel, 16), 0);
                
                for (ReactorFuelRodTile reactorFuelRodTile : fuelRodsByLevel.get((int) i)) {
                    BlockState state = newStates.computeIfAbsent(reactorFuelRodTile.getPos(), k -> reactorFuelRodTile.getBlockState());
                    BlockState newState = state.with(ReactorFuelRod.WASTE_HEIGHT_PROPERTY, levelWastePixel);
                    if (newState != state) {
                        newStates.put(reactorFuelRodTile.getPos(), newState);
                        updatedLevels[(int) i] = true;
                    }
                }
                
            }
        }
        
        Util.setBlockStates(newStates, world);
        
        for (int i = 0; i < updatedLevels.length; i++) {
            if (!updatedLevels[i]) {
                continue;
            }
            for (ReactorFuelRodTile reactorFuelRodTile : fuelRodsByLevel.get(i)) {
                reactorFuelRodTile.updateContainingBlockInfo();
            }
        }
        
        currentFuelRenderLevel = fuelPixels;
        currentWasteRenderLevel = wastePixels;
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
            markDirty();
        }
    }
    
    private void collectFuel() {
        for (ReactorFuelRodTile fuelRod : fuelRods) {
            fuelRod.fuel -= simulation.fuelTank.insertFuel(fuelRod.fuel, false);
            fuelRod.waste -= simulation.fuelTank.insertWaste(fuelRod.waste, false);
            if (fuelRod.fuel != 0 || fuelRod.waste != 0) {
                BiggerReactors.LOGGER.warn("Reactor overfilled with fuel at " + fuelRod.getPos().toString());
                // for now, just void the fuel
                fuelRod.fuel = 0;
                fuelRod.waste = 0;
            }
        }
        markDirty();
    }
    
    private boolean autoEjectWaste = true;
    
    private void ejectWaste() {
        for (ReactorAccessPortTile accessPort : accessPorts) {
            if (accessPort.isInlet()) {
                continue;
            }
            long wastePushed = accessPort.pushWaste((int) simulation.fuelTank.getWasteAmount(), false);
            forceDirty = simulation.fuelTank.extractWaste(wastePushed, false) > 0;
            
        }
        
        // outlets have already taken as much as they can, now just hose it out the inlets too
        // this will only actually do anything with items, so, we only care if there is a full ingot or more
        // if/when fluid fueling is added, only oulets will output it
        if (simulation.fuelTank.getWasteAmount() > Config.Reactor.FuelMBPerIngot) {
            for (ReactorAccessPortTile accessPort : accessPorts) {
                long wastePushed = accessPort.pushWaste((int) simulation.fuelTank.getWasteAmount(), false);
                forceDirty = simulation.fuelTank.extractWaste(wastePushed, false) > 0;
            }
        }
    }
    
    public long extractWaste(long mb, boolean simulated) {
        if (assemblyState() != AssemblyState.ASSEMBLED) {
            return 0;
        }
        long wasteExtracted = simulation.fuelTank.extractWaste(mb, simulated);
        forceDirty = wasteExtracted > 0;
        return wasteExtracted;
    }
    
    public long extractFuel(long mb, boolean simulated) {
        if (assemblyState() != AssemblyState.ASSEMBLED) {
            return 0;
        }
        long fuelExtracted = simulation.fuelTank.extractFuel(mb, simulated);
        forceDirty = fuelExtracted > 0;
        return fuelExtracted;
    }
    
    public long refuel(long mb, boolean simulated) {
        if (assemblyState() != AssemblyState.ASSEMBLED) {
            return 0;
        }
        long fuelInserted = simulation.fuelTank.insertFuel(mb, simulated);
        forceDirty = fuelInserted > 0;
        return fuelInserted;
    }
    
    public void updateReactorState(@Nonnull ReactorState reactorState) {
        // TODO: These are mixed between the new enums and old booleans. Migrate them fully to enums.
        reactorState.reactorActivity = reactorActivity;
        reactorState.reactorType = simulation.isPassive() ? ReactorType.PASSIVE : ReactorType.ACTIVE;
        
        reactorState.doAutoEject = autoEjectWaste;
        
        reactorState.energyStored = simulation.battery.storedPower();
        reactorState.energyCapacity = simulation.battery.size();
        
        reactorState.wasteStored = simulation.fuelTank.getWasteAmount();
        reactorState.fuelStored = simulation.fuelTank.getFuelAmount();
        reactorState.fuelCapacity = simulation.fuelTank.getCapacity();
        
        reactorState.coolantStored = simulation.coolantTank.getWaterAmount();
        reactorState.coolantCapacity = simulation.coolantTank.getPerSideCapacity();
        
        reactorState.steamStored = simulation.coolantTank.getSteamAmount();
        reactorState.steamCapacity = simulation.coolantTank.getPerSideCapacity();
        
        reactorState.caseHeatStored = simulation.getReactorHeat();
        reactorState.fuelHeatStored = simulation.getFuelHeat();
        
        reactorState.reactivityRate = simulation.getFertility();
        reactorState.fuelUsageRate = simulation.getFuelConsumedLastTick();
        reactorState.reactorOutputRate = simulation.getFEProducedLastTick();
    }
    
    public void runRequest(@Nonnull String requestName, @Nullable Object requestData) {
        switch (requestName) {
            // Reactor.
            case "setActive": {
                boolean newState = (boolean) requestData;
                setActive(newState ? ReactorActivity.ACTIVE : ReactorActivity.INACTIVE);
                return;
            }
            case "setAutoEject": {
                autoEjectWaste = (boolean) requestData;
                return;
            }
            case "ejectWaste": {
                ejectWaste();
                return;
            }
            // Control rod.
            case "setRodInsertion": {
                Pair<Double, Boolean> newState = (Pair<Double, Boolean>) requestData;
                /* TODO: Wire to control rod logic. */
                // newState.getFirst() = Rod Insertion Level
                // newState.getSecond() = Apply Globally
            }
        }
    }
    
    @Override
    @Nonnull
    public String getDebugInfo() {
        return super.getDebugInfo() +
                "State: " + reactorActivity.toString() + "\n" +
                "StoredPower: " + simulation.battery.storedPower() + "\n" +
                "PowerProduction: " + simulation.getFEProducedLastTick() + "\n" +
                "FuelUsage: " + simulation.getFuelConsumedLastTick() + "\n" +
                "ReactantCapacity: " + simulation.fuelTank.getCapacity() + "\n" +
                "TotalReactant: " + simulation.fuelTank.getTotalAmount() + "\n" +
                "PercentFull: " + (float) simulation.fuelTank.getTotalAmount() * 100 / simulation.fuelTank.getCapacity() + "\n" +
                "Fuel: " + simulation.fuelTank.getFuelAmount() + "\n" +
                "Waste: " + simulation.fuelTank.getWasteAmount() + "\n" +
                "AutoEjectWaste: " + autoEjectWaste + "\n" +
                "Fertility: " + simulation.getFertility() + "\n" +
                "FuelHeat: " + simulation.getFuelHeat() + "\n" +
                "ReactorHeat: " + simulation.getReactorHeat() + "\n" +
                "CoolantTankSize: " + simulation.coolantTank.getPerSideCapacity() + "\n" +
                "Water: " + simulation.coolantTank.getWaterAmount() + "\n" +
                "Steam: " + simulation.coolantTank.getSteamAmount() + "\n" +
                "";
    }
    
    public void setAllControlRodLevels(double newLevel) {
        synchronized (controlRods) {
            controlRods.forEach(rod -> {
                rod.setInsertion(newLevel);
            });
            updateControlRodLevels();
        }
    }
    
    public void updateControlRodLevels() {
        controlRods.forEach(rod -> {
            BlockPos pos = rod.getPos();
            simulation.setControlRodInsertion(pos.getX() - minCoord().x() - 1, pos.getZ() - minCoord().z() - 1, rod.getInsertion());
        });
    }
    
    // -- Mekanism compat
    
    public long getSteamCapacity() {
        return simulation.coolantTank.getPerSideCapacity();
    }
    
    public long getSteamAmount() {
        return simulation.coolantTank.getSteamAmount();
    }
    
    // -- ComputerCraft API --
    
    public boolean CCgetConnected() {
        return state != MultiblockController.AssemblyState.DISASSEMBLED;
    }
    
    public boolean CCgetActive() {
        return reactorActivity == ReactorActivity.ACTIVE;
    }
    
    public int CCgetNumberOfControlRods() {
        return controlRods.size();
    }
    
    public long CCgetEnergyStored() {
        // backwards compatible with the old CC API, which requires this assumption
        return (simulation.battery.storedPower() * 10_000_000) / simulation.battery.size();
    }
    
    public long CCgetEnergyStoredUnscaled() {
        return simulation.battery.storedPower();
    }
    
    public long CCgetMaxEnergyStored() {
        return simulation.battery.size();
    }
    
    public double CCgetFuelTemperature() {
        return simulation.getFuelHeat();
    }
    
    public double CCgetCasingTemperature() {
        return simulation.getReactorHeat();
    }
    
    public long CCgetFuelAmount() {
        return simulation.fuelTank.getFuelAmount();
    }
    
    public long CCgetWasteAmount() {
        return simulation.fuelTank.getWasteAmount();
    }
    
    public long CCgetReactantAmount() {
        return simulation.fuelTank.getTotalAmount();
    }
    
    public long CCgetFuelAmountMax() {
        return simulation.fuelTank.getCapacity();
    }
    
    @Nonnull
    public String CCgetControlRodName(int index) {
        synchronized (controlRods) {
            if (index >= controlRods.size()) {
                throw new RuntimeException("control rod index out of bounds");
            }
            return controlRods.get(index).getName();
        }
    }
    
    public double CCgetControlRodLevel(int index) {
        synchronized (controlRods) {
            if (index >= controlRods.size()) {
                throw new RuntimeException("control rod index out of bounds");
            }
            return controlRods.get(index).getInsertion();
        }
    }
    
    public double CCgetEnergyProducedLastTick() {
        return simulation.FEProducedLastTick;
    }
    
    public double CCgetHotFluidProducedLastTick() {
        if (simulation.isPassive()) {
            return 0;
        }
        return simulation.FEProducedLastTick;
    }
    
    public double CCgetMaxHotFluidProducedLastTick() {
        if (simulation.isPassive()) {
            return 0;
        }
        return simulation.coolantTank.getMaxFluidVaporizedLastTick();
    }
    
    
    @Nullable
    public String CCgetCoolantType() {
        if (simulation.coolantTank.getWaterAmount() == 0) {
            return null;
        }
        return Objects.requireNonNull(Fluids.WATER.getRegistryName()).toString();
    }
    
    public long CCgetCoolantAmount() {
        return simulation.coolantTank.getWaterAmount();
    }
    
    @Nullable
    public String CCgetHotFluidType() {
        if (simulation.coolantTank.getSteamAmount() == 0) {
            return null;
        }
        return Objects.requireNonNull(FluidIrradiatedSteam.INSTANCE.getRegistryName()).toString();
    }
    
    public long CCgetHotFluidAmount() {
        return simulation.coolantTank.getSteamAmount();
    }
    
    public double CCgetFuelReactivity() {
        return simulation.getFertility() * 100;
    }
    
    public double CCgetFuelConsumedLastTick() {
        return simulation.getFuelConsumedLastTick();
    }
    
    public boolean CCisActivelyCooled() {
        return !simulation.isPassive();
    }
    
    public void CCsetActive(boolean active) {
        setActive(active ? ReactorActivity.ACTIVE : ReactorActivity.INACTIVE);
    }
    
    public void CCsetAllControlRodLevels(double insertion) {
        setAllControlRodLevels(insertion);
    }
    
    public void CCsetControlRodLevel(double insertion, int index) {
        synchronized (controlRods) {
            if (index >= controlRods.size()) {
                throw new RuntimeException("control rod index out of bounds");
            }
            controlRods.get(index).setInsertion(insertion);
            updateControlRodLevels();
        }
    }
    
    public void CCdoEjectWaste() {
        synchronized (accessPorts) {
            ejectWaste();
        }
    }
    
    public long CCgetCoolantAmountMax() {
        return simulation.coolantTank.getPerSideCapacity();
    }
    
    public long CCgetHotFluidAmountMax() {
        return simulation.coolantTank.getPerSideCapacity();
    }
}