package net.roguelogix.biggerreactors.classic.blocks.reactor;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_glass", tileEntityClass = ReactorGlassTile.class)
public class ReactorGlass extends ReactorBaseBlock {

    @RegisterBlock.Instance
    public static ReactorGlass INSTANCE;

    public ReactorGlass() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorGlassTile();
    }

    @RegisterBlock.RenderLayer
    public RenderType renderLayer() {
        return RenderType.getCutout();
    }
}
