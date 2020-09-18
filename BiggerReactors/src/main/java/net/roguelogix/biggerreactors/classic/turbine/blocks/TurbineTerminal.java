package net.roguelogix.biggerreactors.classic.turbine.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineTerminalTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "turbine_terminal", tileEntityClass = TurbineTerminalTile.class)
public class TurbineTerminal extends TurbineBaseBlock {
    
    @RegisterBlock.Instance
    public static TurbineTerminal INSTANCE;
    
    public TurbineTerminal() {
        super();
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurbineTerminalTile();
    }
}
