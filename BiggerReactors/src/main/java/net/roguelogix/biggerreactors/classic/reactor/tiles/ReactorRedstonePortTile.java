package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_redstone_port")
public class ReactorRedstonePortTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorRedstonePortTile() {
        super(TYPE);
    }
}
