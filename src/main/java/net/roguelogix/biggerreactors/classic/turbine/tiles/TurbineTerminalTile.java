package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "turbine_terminal")
public class TurbineTerminalTile extends TurbineBaseTile {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineTerminalTile() {
        super(TYPE);
    }
}
