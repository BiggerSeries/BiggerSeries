package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_fuel_rod")
public class ReactorFuelRodTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorFuelRodTile() {
        super(TYPE);
    }
    
    @Override
    public boolean doBlockStateUpdate() {
        return false;
    }
}
