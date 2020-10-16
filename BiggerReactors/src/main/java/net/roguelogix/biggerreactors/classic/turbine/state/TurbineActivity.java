package net.roguelogix.biggerreactors.classic.turbine.state;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public enum TurbineActivity implements IStringSerializable {
    ACTIVE,
    INACTIVE;
    
    @SuppressWarnings("SpellCheckingInspection")
    public static final EnumProperty<TurbineActivity> TURBINE_STATE_ENUM_PROPERTY = EnumProperty.create("turbinestate", TurbineActivity.class);
    
    @Override
    public String getString() {
        return toString().toLowerCase();
    }
}
