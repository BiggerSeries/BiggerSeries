package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorFuelRodTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_fuel_rod", tileEntityClass = ReactorFuelRodTile.class)
public class ReactorFuelRod extends ReactorBaseBlock {
    
    @RegisterBlock.Instance
    public static ReactorFuelRod INSTANCE;
    
    public ReactorFuelRod() {
        super(false);
        this.setDefaultState(this.getDefaultState().with(FUEL_HEIGHT_PROPERTY, 0).with(WASTE_HEIGHT_PROPERTY, 0));
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorFuelRodTile();
    }
    
    @OnlyIn(Dist.CLIENT)
    @RegisterBlock.RenderLayer
    RenderType renderLayer() {
        return RenderType.getCutout();
    }
    
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return 1.0F;
    }
    
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return true;
    }
    
    public static IntegerProperty FUEL_HEIGHT_PROPERTY = IntegerProperty.create("fuel_level", 0, 16);
    public static IntegerProperty WASTE_HEIGHT_PROPERTY = IntegerProperty.create("waste_level", 0, 16);
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FUEL_HEIGHT_PROPERTY);
        builder.add(WASTE_HEIGHT_PROPERTY);
    }
    
    @Override
    public boolean usesAssmeblyState() {
        return false;
    }
}
