package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCasingTile;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorRedstonePortTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_redstone_port", tileEntityClass = ReactorRedstonePortTile.class)
public class ReactorRedstonePort extends ReactorBaseBlock {
    @RegisterBlock.Instance
    public static ReactorRedstonePort INSTANCE;
    
    public ReactorRedstonePort() {
        super();
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorRedstonePortTile();
    }
}
