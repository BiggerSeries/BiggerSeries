package net.roguelogix.biggerreactors.classic.turbine;

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
    
    public static int valueOf(VentState state) {
        switch (state) {
            case OVERFLOW:
                return 0;
            case ALL:
                return 1;
            case CLOSED:
                return 2;
        }
        throw new IndexOutOfBoundsException("Invalid vent state when converting to index");
    }
}
