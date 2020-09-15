package net.roguelogix.biggerreactors.classic.turbine.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineRotorBladeTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterBlock(name = "turbine_rotor_blade", tileEntityClass = TurbineRotorBladeTile.class)
public class TurbineRotorBlade extends TurbineBaseBlock {
    
    @RegisterBlock.Instance
    public static TurbineRotorBlade INSTANCE;
    
    public TurbineRotorBlade() {
        super(false);
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurbineRotorBladeTile();
    }
    
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return 1.0F;
    }
    
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return true;
    }
}
