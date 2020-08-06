package net.roguelogix.biggerreactors.classic.turbine;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.roguelogix.biggerreactors.classic.reactor.ReactorState;

public enum TurbineState implements IStringSerializable {
    ACTIVE,
    INACTIVE;
    
    public static final EnumProperty<TurbineState> TURBINE_STATE_ENUM_PROPERTY = EnumProperty.create("turbinestate", TurbineState.class);
    
    @Override
    public String getName() {
        return toString().toLowerCase();
    }
}
