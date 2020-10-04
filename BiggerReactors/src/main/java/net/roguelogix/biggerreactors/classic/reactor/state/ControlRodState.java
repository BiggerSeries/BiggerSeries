package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorControlRodTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import java.util.HashMap;
import java.util.Map;

public class ControlRodState implements GuiSync.IGUIPacket {
    
    /**
     * The name of this control rod.
     */
    public String controlRodName;
    
    /**
     * How inserted the control rod is.
     */
    public int controlRodInsertion;
    
    /**
     * The tile whose information this belongs to.
     */
    ReactorControlRodTile controlRodTile;
    
    public ControlRodState(ReactorControlRodTile controlRodTile) {
        this.controlRodTile = controlRodTile;
    }
    
    @Override
    public void read(Map<?, ?> data) {
        // controlRodName = (String) data.get("controlRodName");
        controlRodInsertion = (Integer) data.get("controlRodInsertion");
    }
    
    @Override
    public Map<?, ?> write() {
        controlRodTile.updateState();
        HashMap<String, Object> data = new HashMap<>();
        
        //data.put("controlRodName", controlRodName);
        data.put("controlRodInsertion", controlRodInsertion);
        
        return data;
    }
}
