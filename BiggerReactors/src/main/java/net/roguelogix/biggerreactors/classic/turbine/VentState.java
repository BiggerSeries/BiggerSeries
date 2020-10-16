package net.roguelogix.biggerreactors.classic.turbine;

import javax.annotation.Nonnull;

public enum VentState {
    OVERFLOW(0),
    ALL(1),
    CLOSED(2);
    
    private final int value;
    
    VentState(int value) {
        this.value = value;
    }
    
    public static VentState valueOf(int value) {
        switch (value) {
            case 0:
                return OVERFLOW;
            case 1:
                return ALL;
            case 2:
                return CLOSED;
        }
        throw new IndexOutOfBoundsException("Invalid index when converting to vent state");
    }
    
    public static int valueOf(@Nonnull VentState state) {
        return state.value;
    }
}
