package net.roguelogix.biggerreactors.classic.reactor.state;

import javax.annotation.Nonnull;

public enum RedstonePortSelector {
    INPUT_ACTIVITY(0),
    INPUT_CONTROL_ROD_INSERTION(1),
    INPUT_EJECT_WASTE(2),
    OUTPUT_FUEL_TEMP(3),
    OUTPUT_CASING_TEMP(4),
    OUTPUT_FUEL_ENRICHMENT(5),
    OUTPUT_FUEL_AMOUNT(6),
    OUTPUT_WASTE_AMOUNT(7),
    OUTPUT_ENERGY_AMOUNT(8);

    private final int value;

    RedstonePortSelector(int value) {
        this.value = value;
    }

    public static RedstonePortSelector valueOf(int value) {
        switch (value) {
            case 0:
                return INPUT_ACTIVITY;
            case 1:
                return INPUT_CONTROL_ROD_INSERTION;
            case 2:
                return INPUT_EJECT_WASTE;
            case 3:
                return OUTPUT_FUEL_TEMP;
            case 4:
                return OUTPUT_CASING_TEMP;
            case 5:
                return OUTPUT_FUEL_ENRICHMENT;
            case 6:
                return OUTPUT_FUEL_AMOUNT;
            case 7:
                return OUTPUT_WASTE_AMOUNT;
            case 8:
                return OUTPUT_ENERGY_AMOUNT;
        }
        throw new IndexOutOfBoundsException("Invalid index when determining redstone port selector");
    }

    public static int valueOf(@Nonnull RedstonePortSelector state) {
        return state.value;
    }
}
