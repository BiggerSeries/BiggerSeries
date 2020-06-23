package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_access_port", tileEntityClass = ReactorAccessPortTile.class)
public class ReactorAccessPort extends ReactorBaseBlock{

    @RegisterBlock.Instance
    public static ReactorAccessPort INSTANCE;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorAccessPortTile();
    }

}
