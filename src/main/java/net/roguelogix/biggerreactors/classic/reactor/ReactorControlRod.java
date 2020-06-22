package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_control_rod", tileEntityClass = ReactorControlRodTile.class)
public class ReactorControlRod extends ReactorBaseBlock{

    @RegisterBlock.Instance
    public static ReactorControlRod INSTANCE;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorControlRodTile();
    }
}
