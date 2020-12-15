package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorCoolantPortState implements GuiSync.IGUIPacket {

    /**
     * The direction of the port. True for input, false for output.
     */
    public boolean direction = false;

    /**
     * The tile whose information this belongs to.
     */
    ReactorCoolantPortTile reactorCoolantPortTile;

    public ReactorCoolantPortState(ReactorCoolantPortTile reactorCoolantPortTile) {
        this.reactorCoolantPortTile = reactorCoolantPortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        direction = (Boolean) data.get("direction");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        reactorCoolantPortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("direction", direction);

        return data;
    }
}
