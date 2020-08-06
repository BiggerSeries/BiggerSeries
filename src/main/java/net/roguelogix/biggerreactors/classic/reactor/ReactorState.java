package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.data.ModelProperty;

public enum ReactorState implements IStringSerializable {
    ACTIVE,
    INACTIVE;
    
    public static final EnumProperty<ReactorState> REACTOR_STATE_ENUM_PROPERTY = EnumProperty.create("reactorstate", ReactorState.class);
    
    @Override
    public String getName() {
        return toString().toLowerCase();
    }
}
