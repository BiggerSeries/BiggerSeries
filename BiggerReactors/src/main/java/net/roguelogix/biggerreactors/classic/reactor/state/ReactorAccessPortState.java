package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorAccessPortState implements GuiSync.IGUIPacket {

    /**
     * The input state of the port. True for input, false for output.
     */
    public boolean inputState = false;

    /**
     * Allows fuel or waste to be extracted. True for fuel extraction, false for waste extraction.
     */
    public boolean fuelMode = false;

    /**
     * The tile whose information this belongs to.
     */
    ReactorAccessPortTile ioPortTile;

    public ReactorAccessPortState(ReactorAccessPortTile ioPortTile) {
        this.ioPortTile = ioPortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        inputState = (Boolean) data.get("inputState");
        fuelMode = (Boolean) data.get("fuelMode");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        ioPortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("inputState", inputState);
        data.put("fuelMode", fuelMode);

        return data;
    }
}
