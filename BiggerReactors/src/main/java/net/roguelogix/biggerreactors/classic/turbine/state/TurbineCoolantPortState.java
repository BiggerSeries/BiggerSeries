package net.roguelogix.biggerreactors.classic.turbine.state;

import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TurbineCoolantPortState implements GuiSync.IGUIPacket {

    /**
     * The direction of the port. True for input, false for output.
     */
    public boolean direction = false;

    /**
     * The tile whose information this belongs to.
     */
    TurbineCoolantPortTile turbineCoolantPortTile;

    public TurbineCoolantPortState(TurbineCoolantPortTile turbineCoolantPortTile) {
        this.turbineCoolantPortTile = turbineCoolantPortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        direction = (Boolean) data.get("direction");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        turbineCoolantPortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("direction", direction);

        return data;
    }
}
