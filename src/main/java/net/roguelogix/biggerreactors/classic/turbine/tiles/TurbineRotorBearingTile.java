package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "turbine_rotor_bearing")
public class TurbineRotorBearingTile extends TurbineBaseTile{
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineRotorBearingTile() {
        super(TYPE);
    }
}
