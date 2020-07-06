package net.roguelogix.phosphophyllite.blocks.whiteholes;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "fluid_white_hole", tileEntityClass = FluidWhiteHoleTile.class)
public class FluidWhiteHole extends Block {
    
    @RegisterBlock.Instance
    public static FluidWhiteHole INSTANCE;
    
    public FluidWhiteHole() {
        super(Properties.create(Material.IRON));
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidWhiteHoleTile();
    }
    
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        Item item = player.getHeldItemMainhand().getItem();
        if(item instanceof BucketItem){
            TileEntity te = worldIn.getTileEntity(pos);
            if(te instanceof FluidWhiteHoleTile){
                ((FluidWhiteHoleTile)te).setFluid(((BucketItem) item).getFluid());
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
