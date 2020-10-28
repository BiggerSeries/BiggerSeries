package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorCoolantPortState implements GuiSync.IGUIPacket {

    /**
     * The input state of the port. True for input, false for output.
     */
    public boolean inputState = false;

    /**
     * The tile whose information this belongs to.
     */
    ReactorCoolantPortTile ioPortTile;

    public ReactorCoolantPortState(ReactorCoolantPortTile ioPortTile) {
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
