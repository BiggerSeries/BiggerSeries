package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorControlRodTile;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorRedstonePortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class RedstonePortState implements GuiSync.IGUIPacket {

    /**
     * What selector is currently active.
     */
    public int settingId = 0;

    /**
     * Pulse (false) or signal (true).
     * Used by:
     * - Input Reactor Activity: toggles state if pulse (false), or activates reactor only during redstone input when signal (true).
     * - Input Control Rod Insertion:
     * - Input Waste Eject: always pulse (false).
     */
    public boolean triggerPulseOrSignal = false;

    /**
     * Active above (false) or below (true);
     * Used by:
     * - Output Fuel Temp: temperature to trigger on.
     * - Output Casing Temp: temperature to trigger on.
     * - Output Fuel Enrichment: percentage to trigger on.
     * - Output Fuel Amount: amount to trigger on.
     * - Output Waste Amount: amount to trigger on.
     * - Output Energy Amount: percentage to trigger on.
     */
    public boolean triggerAboveOrBelow = false;

    /**
     * Insert by (0), retract by (1), or set to (2).
     * Used by:
     * - Input Control Rod Insertion: when pulseOrSignal is pulse (false).
     */
    public int mode = 2;

    /**
     * Text box for elements.
     * Used by:
     * - Input Control Rod Insertion: when triggerPulseOrSignal is signal (true), used for "while on" percent. Otherwise, used for "set to"/"retract by"/"insert by" percent.
     * - Output Fuel Temp: temperature to trigger on.
     * - Output Casing Temp: temperature to trigger on.
     * - Output Fuel Enrichment: percentage to trigger on.
     * - Output Fuel Amount: amount to trigger on.
     * - Output Waste Amount: amount to trigger on.
     * - Output Energy Amount: percentage to trigger on.
     */
    public String mainBuffer = "";

    /**
     * Text box for elements.
     * Used by:
     * - Input Control Rod Insertion: when pulseOrSignal is signal (true), used for "while off" percent.
     */
    public String secondBuffer = "";

    /**
     * The tile whose information this belongs to.
     */
    ReactorRedstonePortTile redstonePortTile;

    public RedstonePortState(ReactorRedstonePortTile redstonePortTile) {
        this.redstonePortTile = redstonePortTile;
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        settingId = (Integer) data.get("settingId");
        triggerPulseOrSignal = (Boolean) data.get("triggerPulseOrSignal");
        triggerAboveOrBelow = (Boolean) data.get("triggerAboveOrBelow");
        mode = (Integer) data.get("mode");
        mainBuffer = (String) data.get("mainBuffer");
        secondBuffer = (String) data.get("secondBuffer");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        redstonePortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("settingId", settingId);
        data.put("triggerPulseOrSignal", triggerPulseOrSignal);
        data.put("triggerAboveOrBelow", triggerAboveOrBelow);
        data.put("mode", mode);
        data.put("mainBuffer", mainBuffer);
        data.put("secondBuffer", secondBuffer);

        return data;
    }
}