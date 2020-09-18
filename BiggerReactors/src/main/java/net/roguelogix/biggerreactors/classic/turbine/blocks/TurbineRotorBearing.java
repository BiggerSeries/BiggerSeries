package net.roguelogix.biggerreactors.classic.turbine.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineRotorBearingTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "turbine_rotor_bearing", tileEntityClass = TurbineRotorBearingTile.class)
public class TurbineRotorBearing extends TurbineBaseBlock {
    
    @RegisterBlock.Instance
    public static TurbineRotorBearing INSTANCE;
    
    public TurbineRotorBearing() {
        super();
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurbineRotorBearingTile();
    }
}
