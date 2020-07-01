package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_control_rod")
public class ReactorControlRodTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorControlRodTile() {
        super(TYPE);
    }
    
    @Override
    public boolean doBlockStateUpdate() {
        return false;
    }
    
    public int getControlRodInsertion() {
        return 0;
    }
}
