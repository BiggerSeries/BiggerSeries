package net.roguelogix.biggerreactors.classic.turbine;

import javax.annotation.Nonnull;

public enum VentState {
    OVERFLOW(0),
    ALL(1),
    CLOSED(2);
    
    private final int state;
    
    VentState(int state) {
        this.state = state;
    }

    /**
     * Get an integer state usable with ROBN.
     *
     * @return An integer usable with ROBN.
     */
    public int toInt() {
        return this.state;
    }

    /**
     * Get an integer state usable with ROBN.
     *
     * @param state An integer usable with ROBN.
     * @return An integer usable with ROBN.
     */
    @Deprecated
    public static int toInt(@Nonnull VentState state) {
        return state.state;
    }

    /**
     * Get a value from an integer state.
     *
     * @param state An integer usable with ROBN.
     * @return A value representing the state.
     */
    public static VentState fromInt(int state) {
        switch (state) {
            case 0:
                return OVERFLOW;
            case 1:
                return ALL;
            case 2:
                return CLOSED;
        }
        throw new IndexOutOfBoundsException("Invalid index while deciphering vent state");
    }
}
