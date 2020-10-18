package net.roguelogix.biggerreactors.classic.turbine.state;

import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TurbineCoolantPortState implements GuiSync.IGUIPacket {

    /**
     * The input state of the port. True for input, false for output.
     */
    public boolean inputState;

    /**
     * The tile whose information this belongs to.
     */
    TurbineCoolantPortTile ioPortTile;

    public TurbineCoolantPortState(TurbineCoolantPortTile ioPortTile) {
        this.ioPortTile = ioPortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        inputState = (Boolean) data.get("inputState");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        ioPortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("inputState", inputState);

        return data;
    }
}
