package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum  ReactorRedstonePortTriggers implements IStringSerializable {
    PULSE_OR_ABOVE(false),
    SIGNAL_OR_BELOW(true);

    private final boolean state;

    ReactorRedstonePortTriggers(boolean state) {
        this.state = state;
    }

    @Override
    @Nonnull
    public String getString() {
        return toString().toLowerCase();
    }

    /**
     * Get a boolean state usable with ROBN.
     *
     * @return A boolean usable with ROBN.
     */
    public boolean toBool() {
        return this.state;
    }

    /**
     * Get a boolean from an integer state.
     *
     * @param state A boolean usable with ROBN.
     * @return A value representing the state.
     */
    public static ReactorRedstonePortTriggers fromBool(boolean state) {
        return (state) ? SIGNAL_OR_BELOW : PULSE_OR_ABOVE;
    }
}