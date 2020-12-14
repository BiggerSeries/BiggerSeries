package net.roguelogix.biggerreactors.classic.turbine.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum TurbineActivity implements IStringSerializable {
    ACTIVE(1),
    INACTIVE(0);
    
    @SuppressWarnings("SpellCheckingInspection")
    public static final EnumProperty<TurbineActivity> TURBINE_STATE_ENUM_PROPERTY = EnumProperty.create("turbinestate", TurbineActivity.class);

    private final int state;

    TurbineActivity(int state) {
        this.state = state;
    }

    @Override
    @Nonnull
    public String getString() {
        return toString().toLowerCase();
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
     * Get a value from an integer state.
     *
     * @param state An integer usable with ROBN.
     * @return A value representing the state.
     */
    public static TurbineActivity fromInt(int state) {
        switch (state) {
            case 1:
                return TurbineActivity.ACTIVE;
            case 0:
                return TurbineActivity.INACTIVE;
        }
        throw new IndexOutOfBoundsException("Invalid index while deciphering turbine activity");
    }
}
