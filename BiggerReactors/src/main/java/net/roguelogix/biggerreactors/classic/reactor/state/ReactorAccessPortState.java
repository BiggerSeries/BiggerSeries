package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorAccessPortState implements GuiSync.IGUIPacket {

    /**
     * The direction of the port. True for input, false for output.
     */
    public boolean direction = false;

    /**
     * Allows fuel or waste to be extracted. True for fuel extraction, false for waste extraction.
     */
    public boolean fuelMode = false;

    /**
     * The tile whose information this belongs to.
     */
    ReactorAccessPortTile reactorAccessPortTile;

    public ReactorAccessPortState(ReactorAccessPortTile reactorAccessPortTile) {
        this.reactorAccessPortTile = reactorAccessPortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        direction = (Boolean) data.get("direction");
        fuelMode = (Boolean) data.get("fuelMode");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        reactorAccessPortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("direction", direction);
        data.put("fuelMode", fuelMode);

        return data;
    }
}
