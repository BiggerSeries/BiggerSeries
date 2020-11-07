package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorGlassTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
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
    
    @OnlyIn(Dist.CLIENT)
    @RegisterBlock.RenderLayer
    public RenderType renderLayer() {
        return RenderType.getCutout();
    }
    
    @SuppressWarnings("deprecation")
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return 1.0F;
    }
    
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return true;
    }
    
    @Override
    public boolean connectedTexture() {
        return true;
    }
}
