package net.roguelogix.biggerreactors.classic.reactor.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public enum ReactorType implements IStringSerializable {
    ACTIVE,
    PASSIVE;
    
    public static final EnumProperty<ReactorType> REACTOR_TYPE_ENUM_PROPERTY = EnumProperty.create("reactortype", ReactorType.class);
    
    @Override
    public String getString() {
        return toString().toLowerCase();
    }
}
