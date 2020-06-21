package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_power_port", tileEntityClass = ReactorPowerPortTile.class)
public class ReactorPowerPort extends ReactorBaseBlock{

    @RegisterBlock.Instance
    public static ReactorPowerPort INSTANCE;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorPowerPortTile();
    }
}
