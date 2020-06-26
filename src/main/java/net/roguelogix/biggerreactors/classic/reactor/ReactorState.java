package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.data.ModelProperty;

public enum ReactorState implements IStringSerializable {
    ACTIVE,
    INACTIVE;

    public static final EnumProperty<ReactorState> REACTOR_STATE_ENUM_PROPERTY = EnumProperty.create("reactorstate", ReactorState.class);
    public static final ModelProperty<ReactorState> REACTOR_STATE_MODEL_PROPERTY = new ModelProperty<>();

    // TODO: 6/25/20 mappings
    //    @Override
    public String getName() {
        return toString().toLowerCase();
    }

    @Override
    public String func_176610_l() {
        return getName();
    }
}
