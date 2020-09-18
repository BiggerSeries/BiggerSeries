package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.multiblock.generic.client.MultiblockBakedModel;
import net.roguelogix.phosphophyllite.registry.Registry;

import javax.annotation.Nonnull;

public class MultiblockBlock extends Block {
    public MultiblockBlock(Properties properties) {
        super(properties);
    }
    
    protected MultiblockBakedModel model = null;
    
    @OnlyIn(Dist.CLIENT)
    public MultiblockBakedModel setupBakedModel(ResourceLocation defaultTexture) {
        model = new MultiblockBakedModel(defaultTexture);
        Registry.registerBakedModel(this, model);
        return model;
    }
    
    public boolean usesBlockState() {
        return true;
    }
    
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof MultiblockTile) {
            ActionResultType tileResult = ((MultiblockTile) te).onBlockActivated(player, handIn);
            if (tileResult != ActionResultType.PASS) {
                return tileResult;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
