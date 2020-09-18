package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "turbine_casing")
public class TurbineCasingTile extends TurbineBaseTile{
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineCasingTile() {
        super(TYPE);
    }
}
