package net.roguelogix.biggerreactors.classic.turbine.state;

import net.roguelogix.biggerreactors.classic.turbine.VentState;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineTerminalTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import java.util.HashMap;
import java.util.Map;

public class TurbineState implements GuiSync.IGUIPacket {
    
    /**
     * The activity status for the turbine.
     */
    public TurbineActivity turbineActivity;
    /**
     * The current vent state.
     */
    public VentState ventState;
    /**
     * Status of the induction coils.
     */
    public boolean coilStatus;
    
    /**
     * The current max flow rate.
     */
    public long flowRate;
    
    /**
     * The current energy efficiency of the turbine.
     */
    public double efficiencyRate;
    /**
     * Output rate of the turbine.
     */
    public double turbineOutputRate;
    
    /**
     * The current RPM of the turbine.
     */
    public double currentRPM;
    /**
     * The max RPM the turbine can handle.
     */
    public double maxRPM;
    
    /**
     * The amount of intake stored in t he turbine.
     */
    public long intakeStored;
    /**
     * The max intake capacity of the turbine.
     */
    public long intakeCapacity;
    
    /**
     * The amount of exhaust stored in the turbine.
     */
    public long exhaustStored;
    /**
     * The max exhaust capacity of the turbine.
     */
    public long exhaustCapacity;
    
    /**
     * The amount of energy stored in the turbine.
     */
    public long energyStored;
    /**
     * The max energy capacity of the turbine.
     */
    public long energyCapacity;
    
    /**
     * The tile whose information this belongs to.
     */
    TurbineTerminalTile turbineTerminalTile;
    
    public TurbineState(TurbineTerminalTile turbineTerminalTile) {
        this.turbineTerminalTile = turbineTerminalTile;
    }
    
    @Override
    public void read(Map<?, ?> data) {
        turbineActivity = ((Boolean) data.get("turbineActivity")) ? TurbineActivity.ACTIVE : TurbineActivity.INACTIVE;
        ventState = VentState.valueOf((Integer) data.get("ventState"));
        coilStatus = (Boolean) data.get("coilStatus");
        
        flowRate = (Long) data.get("flowRate");
        
        efficiencyRate = (Double) data.get("efficiencyRate");
        turbineOutputRate = (Double) data.get("turbineOutputRate");
        
        currentRPM = (Double) data.get("currentRPM");
        maxRPM = (Double) data.get("maxRPM");
        
        intakeStored = (Long) data.get("intakeStored");
        intakeCapacity = (Long) data.get("intakeCapacity");
        
        exhaustStored = (Long) data.get("exhaustStored");
        exhaustCapacity = (Long) data.get("exhaustCapacity");
        
        energyStored = (Long) data.get("energyStored");
        energyCapacity = (Long) data.get("energyCapacity");
    }
    
    @Override
    public Map<?, ?> write() {
        turbineTerminalTile.updateState();
        HashMap<String, Object> data = new HashMap<>();
        
        data.put("turbineActivity", turbineActivity == TurbineActivity.ACTIVE);
        data.put("ventState", VentState.valueOf(ventState));
        data.put("coilStatus", coilStatus);
        
        data.put("flowRate", flowRate);
        
        data.put("efficiencyRate", efficiencyRate);
        data.put("turbineOutputRate", turbineOutputRate);
        
        data.put("currentRPM", currentRPM);
        data.put("maxRPM", maxRPM);
        
        data.put("intakeStored", intakeStored);
        data.put("intakeCapacity", intakeCapacity);
        
        data.put("exhaustStored", exhaustStored);
        data.put("exhaustCapacity", exhaustCapacity);
        
        data.put("energyStored", energyStored);
        data.put("energyCapacity", energyCapacity);
        
        return data;
    }
}
