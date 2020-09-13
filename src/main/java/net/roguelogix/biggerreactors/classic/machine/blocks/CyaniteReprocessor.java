package net.roguelogix.biggerreactors.classic.machine.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.roguelogix.biggerreactors.classic.machine.tiles.CyaniteReprocessorTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterBlock(name = "cyanite_reprocessor", tileEntityClass = CyaniteReprocessorTile.class)
public class CyaniteReprocessor extends ContainerBlock {
    
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    
    @RegisterBlock.Instance
    public static CyaniteReprocessor INSTANCE;
    
    public CyaniteReprocessor() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.STONE)
                .hardnessAndResistance(1.0F)
                .harvestTool(ToolType.PICKAXE));
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(FACING, Direction.NORTH)
                .with(ENABLED, Boolean.FALSE));
    }
    
    public static Direction getFacingFromEntity(BlockPos clickedBlockPos, LivingEntity entity) {
        return Direction.getFacingFromVector((float) (entity.getPosX() - clickedBlockPos.getX()), 0.0F, (float) (entity.getPosZ() - clickedBlockPos.getZ()));
    }
    
    @Nonnull
    @Override
    public BlockRenderType getRenderType(@Nonnull BlockState blockState) {
        return BlockRenderType.MODEL;
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new CyaniteReprocessorTile();
    }
    
    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(ENABLED);
    }
    
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState blockState, World world, @Nonnull BlockPos blockPos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult trace) {
        TileEntity tile = world.getTileEntity(blockPos);
        if (tile instanceof CyaniteReprocessorTile) {
            return ((CyaniteReprocessorTile) tile).onBlockActivated(blockState, world, blockPos, player, hand, trace);
        }
        return ActionResultType.FAIL;
    }
    
    @Override
    public void onReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState newBlockState, boolean isMoving) {
        if (blockState.getBlock() != newBlockState.getBlock()) {
            TileEntity tile = world.getTileEntity(blockPos);
            if (tile instanceof CyaniteReprocessorTile) {
                ((CyaniteReprocessorTile) tile).onReplaced(blockState, world, blockPos, newBlockState, isMoving);
            }
            super.onReplaced(blockState, world, blockPos, newBlockState, isMoving);
        }
    }
    
    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState, @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
        if (entity != null) {
            world.setBlockState(blockPos, blockState
                    .with(FACING, getFacingFromEntity(blockPos, entity))
                    .with(ENABLED, false), 2);
        }
    }
}
