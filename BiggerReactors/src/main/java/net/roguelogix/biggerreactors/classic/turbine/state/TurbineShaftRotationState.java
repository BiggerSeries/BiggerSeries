package net.roguelogix.biggerreactors.classic.turbine.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public enum TurbineShaftRotationState implements IStringSerializable {
    X,
    Y,
    Z;
    
    @SuppressWarnings("SpellCheckingInspection")
    public static final EnumProperty<TurbineShaftRotationState> TURBINE_SHAFT_ROTATION_STATE_ENUM_PROPERTY = EnumProperty.create("turbineshaftrotation", TurbineShaftRotationState.class);
    
    @Override
    public String getString() {
        return toString().toLowerCase();
    }
}
