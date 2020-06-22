package net.roguelogix.biggerreactors.classic.blocks.reactor;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.data.ModelProperty;

enum ReactorState implements IStringSerializable {
    ACTIVE,
    INACTIVE;

    public static final EnumProperty<ReactorState> REACTOR_STATE_ENUM_PROPERTY = EnumProperty.create("reactorstate", ReactorState.class);
    public static final ModelProperty<ReactorState> REACTOR_STATE_MODEL_PROPERTY = new ModelProperty<>();

    @Override
    public String getName() {
        return toString().toLowerCase();
    }
}
