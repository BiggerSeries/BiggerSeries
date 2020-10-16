package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorControlRodTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ControlRodState implements GuiSync.IGUIPacket {
    
    /**
     * The name of this control rod.
     */
    public String name;
    
    /**
     * How inserted the control rod is.
     */
    public double insertionLevel;
    
    /**
     * The tile whose information this belongs to.
     */
    ReactorControlRodTile controlRodTile;
    
    public ControlRodState(ReactorControlRodTile controlRodTile) {
        this.controlRodTile = controlRodTile;
    }
    
    @Override
    public void read(@Nonnull Map<?, ?> data) {
        name = (String) data.get("name");
        insertionLevel = (Double) data.get("insertionLevel");
    }
    
    @Override
    @Nullable
    public Map<?, ?> write() {
        controlRodTile.updateState();
        HashMap<String, Object> data = new HashMap<>();
        
        data.put("name", name);
        data.put("insertionLevel", insertionLevel);
        
        return data;
    }
}
