package net.roguelogix.biggerreactors.classic.reactor;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorTerminalTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import java.util.HashMap;
import java.util.Map;

// TODO: This will be replaced by ReactorState in due time.
@Deprecated
public class ReactorDatapack implements GuiSync.IGUIPacket {
    
    // True if online, false if offline.
    public boolean reactorStatus;
    // True if active, false if passive.
    public boolean reactorType;
    
    // Energy stored in reactor.
    public long energyStored;
    // Max energy capacity of reactor.
    public long energyCapacity;
    
    // Case heat stored in reactor.
    public double caseHeatStored;
    
    // Fuel heat stored in reactor.
    public double fuelHeatStored;
    
    // Waste stored in reactor.
    public long wasteStored;
    // Fuel stored in reactor.
    public long reactantStored;
    // Max fuel capacity of reactor.
    public long fuelCapacity;
    
    // Max coolant capacity of the reactor (active type).
    public long coolantCapacity;
    // Coolant stored in the reactor (active type).
    public long coolantStored;
    // Steam stored in the reactor (active type).
    public long steamStored;
    
    // Output rate of the reactor.
    public double reactorOutputRate;
    
    // Rate at which fuel is consumed (per tick).
    public double fuelUsageRate;
    // Rate at which reactions occur (per tick).
    public double reactivityRate;
    
    ReactorTerminalTile reactorTerminalTile;
    
    public ReactorDatapack(ReactorTerminalTile reactorTerminalTile) {
        this.reactorTerminalTile = reactorTerminalTile;
    }
    
    @Override
    public void read(Map<?, ?> data) {
        reactorStatus = (Boolean) data.get("reactorStatus");
        reactorType = (Boolean) data.get("reactorType");
        energyStored = (Long) data.get("energyStored");
        energyCapacity = (Long) data.get("energyCapacity");
        caseHeatStored = (Double) data.get("caseHeatStored");
        fuelHeatStored = (Double) data.get("fuelHeatStored");
        wasteStored = (Long) data.get("wasteStored");
        reactantStored = (Long) data.get("reactantStored");
        fuelCapacity = (Long) data.get("fuelCapacity");
        coolantCapacity = (Long) data.get("coolantCapacity");
        coolantStored = (Long) data.get("coolantStored");
        steamStored = (Long) data.get("steamStored");
        reactorOutputRate = (Double) data.get("reactorOutputRate");
        fuelUsageRate = (Double) data.get("fuelUsageRate");
        reactivityRate = (Double) data.get("reactivityRate");
    }
    
    @Override
    public Map<?, ?> write() {
        reactorTerminalTile.updateState();
        HashMap<String, Object> data = new HashMap<>();
        data.put("reactorStatus", reactorStatus);
        data.put("reactorType", reactorType);
        data.put("energyStored", energyStored);
        data.put("energyCapacity", energyCapacity);
        data.put("caseHeatStored", caseHeatStored);
        data.put("fuelHeatStored", fuelHeatStored);
        data.put("wasteStored", wasteStored);
        data.put("reactantStored", reactantStored);
        data.put("fuelCapacity", fuelCapacity);
        data.put("coolantCapacity", coolantCapacity);
        data.put("coolantStored", coolantStored);
        data.put("steamStored", steamStored);
        data.put("reactorOutputRate", reactorOutputRate);
        data.put("fuelUsageRate", fuelUsageRate);
        data.put("reactivityRate", reactivityRate);
        return data;
    }
}
