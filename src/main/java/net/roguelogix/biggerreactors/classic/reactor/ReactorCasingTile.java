package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_casing")
public class ReactorCasingTile extends ReactorBaseTile {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorCasingTile() {
        super(TYPE);
    }
    @Override
    public boolean doBlockStateUpdate() {
        return false;
    }
}
