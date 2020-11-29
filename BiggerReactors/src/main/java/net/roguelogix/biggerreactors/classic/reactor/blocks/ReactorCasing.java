package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCasingTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_casing", tileEntityClass = ReactorCasingTile.class)
public class ReactorCasing extends ReactorBaseBlock {
    
    @RegisterBlock.Instance
    public static ReactorCasing INSTANCE;
    
    public ReactorCasing() {
        super();
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorCasingTile();
    }
    
    @Override
    public boolean usesAxisPositions() {
        return true;
    }
}
