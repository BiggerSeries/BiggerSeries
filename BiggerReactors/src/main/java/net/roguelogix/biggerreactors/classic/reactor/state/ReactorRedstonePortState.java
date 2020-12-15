package net.roguelogix.biggerreactors.classic.reactor.state;

import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorRedstonePortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorRedstonePortState implements GuiSync.IGUIPacket {

    /**
     * What selector/tab is currently active.
     */
    public ReactorRedstonePortSelection selectedTab = ReactorRedstonePortSelection.INPUT_ACTIVITY;

    /**
     * Trigger on pulse or signal.
     * Used by:
     * - (Input) Reactor Activity: toggles state on pulse, or turns reactor on while signal is high.
     * - (Input) Control Rod Insertion: Change insertion on high/low signal, or move rod by amount on pulse.
     * - (Input) Eject Waste: always set to pulse.
     */
    public ReactorRedstonePortTriggers triggerPS = ReactorRedstonePortTriggers.PULSE_OR_ABOVE;

    /**
     * Output when above or below condition amount..
     * Used by:
     * - (Output) Fuel Temp: temperature to trigger on.
     * - (Output) Casing Temp: temperature to trigger on.
     * - (Output) Fuel Enrichment: percentage to trigger on.
     * - (Output) Fuel Amount: amount to trigger on.
     * - (Output) Waste Amount: amount to trigger on.
     * - (Output) Energy Amount: percentage to trigger on.
     */
    public ReactorRedstonePortTriggers triggerAB = ReactorRedstonePortTriggers.PULSE_OR_ABOVE;

    /**
     * Insert by (0), retract by (1), or set to (2).
     * Used by:
     * - (Input) Control Rod Insertion: when triggerPS is pulse, change between insert by, retract by, or set to.
     */
    public int triggerMode = 2;

    /**
     * Text box for elements.
     * Used by:
     * - (Input) Control Rod Insertion: percentage to change rod insertion by.
     * - (Output) Fuel Temp: temperature to trigger on.
     * - (Output) Casing Temp: temperature to trigger on.
     * - (Output) Fuel Enrichment: percentage to trigger on.
     * - (Output) Fuel Amount: amount to trigger on.
     * - (Output) Waste Amount: amount to trigger on.
     * - (Output) Energy Amount: percentage to trigger on.
     */
    public String textBufferA = "";

    /**
     * Text box for elements.
     * Used by:
     * - (Input) Control Rod Insertion: percentage to change rod insertion by when no redstone signal is active and triggerPS is signal.
     */
    public String textBufferB = "";

    /**
     * The tile whose information this belongs to.
     */
    ReactorRedstonePortTile reactorRedstonePortTile;

    public ReactorRedstonePortState(ReactorRedstonePortTile reactorRedstonePortTile) {
        this.reactorRedstonePortTile = reactorRedstonePortTile;
    }

    /**
     * Check if the state is set as an input.
     *
     * @return Whether the state is input.
     */
    public boolean isInput() {
        return (this.selectedTab.toInt() < 3);
    }

    @Override
    public void read(@Nonnull Map<?, ?> data) {
        selectedTab = ReactorRedstonePortSelection.fromInt((Integer) data.get("selectedTab"));
        triggerPS = ReactorRedstonePortTriggers.fromBool((Boolean) data.get("triggerPS"));
        triggerAB = ReactorRedstonePortTriggers.fromBool((Boolean) data.get("triggerAB"));
        triggerMode = (Integer) data.get("triggerMode");
        textBufferA = (String) data.get("textBufferA");
        textBufferB = (String) data.get("textBufferB");
    }

    @Override
    @Nullable
    public Map<?, ?> write() {
        reactorRedstonePortTile.updateState();
        HashMap<String, Object> data = new HashMap<>();

        data.put("selectedTab", selectedTab.toInt());
        data.put("triggerPS", triggerPS.toBool());
        data.put("triggerAB", triggerAB.toBool());
        data.put("triggerMode", triggerMode);
        data.put("textBufferA", textBufferA);
        data.put("textBufferB", textBufferB);

        return data;
    }
}