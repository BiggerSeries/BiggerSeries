package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public enum ReactorActivity implements IStringSerializable {
    ACTIVE,
    INACTIVE;
    
    public static final EnumProperty<ReactorActivity> REACTOR_STATE_ENUM_PROPERTY = EnumProperty.create("reactorstate", ReactorActivity.class);
    
    @Override
    public String getName() {
        return toString().toLowerCase();
    }
}
