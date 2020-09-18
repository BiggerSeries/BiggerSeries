package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.nbt.CompoundNBT;
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
    
    public long fuel = 0;
    public long waste = 0;
    
    @Override
    protected void readNBT(CompoundNBT compound) {
        super.readNBT(compound);
        fuel = compound.getLong("fuel");
        waste = compound.getLong("waste");
    }
    
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT compound = super.writeNBT();
        compound.putLong("fuel", fuel);
        compound.putLong("waste", waste);
        return compound;
    }
}
