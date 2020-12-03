package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum ReactorActivity implements IStringSerializable {
    ACTIVE(1),
    INACTIVE(0);
    
    public static final EnumProperty<ReactorActivity> REACTOR_ACTIVITY_ENUM_PROPERTY = EnumProperty.create("reactoractivity", ReactorActivity.class);

    private final int state;

    ReactorActivity(int state) {
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
    public static ReactorActivity fromInt(int state) {
        switch (state) {
            case 1:
                return ReactorActivity.ACTIVE;
            case 0:
                return ReactorActivity.INACTIVE;
        }
        throw new IndexOutOfBoundsException("Invalid index while deciphering reactor activity");
    }
}
