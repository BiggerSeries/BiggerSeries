package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_glass")
public class ReactorGlassTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorGlassTile() {
        super(TYPE);
    }
    
    @Override
    public boolean doBlockStateUpdate() {
        return false;
    }
}
