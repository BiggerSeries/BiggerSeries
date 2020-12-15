package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum ReactorType implements IStringSerializable {
    ACTIVE(1),
    PASSIVE(0);
    
    public static final EnumProperty<ReactorType> REACTOR_TYPE_ENUM_PROPERTY = EnumProperty.create("reactortype", ReactorType.class);

    private final int state;

    ReactorType(int state) {
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
    public static ReactorType fromInt(int state) {
        switch (state) {
            case 1:
                return ReactorType.ACTIVE;
            case 0:
                return ReactorType.PASSIVE;
        }
        throw new IndexOutOfBoundsException("Invalid index while deciphering reactor type");
    }
}
