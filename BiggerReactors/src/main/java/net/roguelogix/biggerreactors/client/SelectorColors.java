package net.roguelogix.biggerreactors.client;

public enum  SelectorColors {
    RED(0, 106, 0, 118),
    CYAN(14, 106, 14, 118),
    GREEN(28, 106, 28, 118),
    YELLOW(42, 106, 42, 118),
    BLUE(56, 106, 56, 118),
    BLACK(70, 106, 70, 118);

    /**
     * The u position of the inactive/non-hovered button.
     */
    public final int uI;

    /**
     * The v position of the inactive/non-hovered button.
     */
    public final int vI;

    /**
     * The u position of the active/hovered button.
     */
    public final int uA;

    /**
     * The v position of the active/hovered button.
     */
    public final int vA;

    SelectorColors(int uI, int vI, int uA, int vA) {
        this.uI = uI;
        this.vI = vI;
        this.uA = uA;
        this.vA = vA;
    }
}
