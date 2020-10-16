package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum ReactorActivity implements IStringSerializable {
    ACTIVE,
    INACTIVE;
    
    public static final EnumProperty<ReactorActivity> REACTOR_ACTIVITY_ENUM_PROPERTY = EnumProperty.create("reactoractivity", ReactorActivity.class);
    
    @Override
    @Nonnull
    public String getString() {
        return toString().toLowerCase();
    }
}
