package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorTerminalTile;
import net.roguelogix.phosphophyllite.registry.CreativeTabBlock;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@CreativeTabBlock
@RegisterBlock(name = "reactor_terminal", tileEntityClass = ReactorTerminalTile.class)
public class ReactorTerminal extends ReactorBaseBlock {
    
    @RegisterBlock.Instance
    public static ReactorTerminal INSTANCE;
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorTerminalTile();
    }
}
