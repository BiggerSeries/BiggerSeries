package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.phosphophyllite.multiblock.generic.ConnectedTextureStates.*;

public class MultiblockBlock extends Block {
    
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");
    
    public MultiblockBlock(Properties properties) {
        super(properties);
        BlockState defaultState = this.getDefaultState();
        defaultState = defaultState.with(ASSEMBLED, false);
        if (connectedTexture()) {
            defaultState = defaultState.with(TOP_CONNECTED_PROPERTY, false);
            defaultState = defaultState.with(BOTTOM_CONNECTED_PROPERTY, false);
            defaultState = defaultState.with(NORTH_CONNECTED_PROPERTY, false);
            defaultState = defaultState.with(SOUTH_CONNECTED_PROPERTY, false);
            defaultState = defaultState.with(EAST_CONNECTED_PROPERTY, false);
            defaultState = defaultState.with(WEST_CONNECTED_PROPERTY, false);
        }
        this.setDefaultState(defaultState);
    }
    
    public boolean usesBlockState() {
        return true;
    }
    
    public boolean connectedTexture() {
        return false;
    }
    
    @SuppressWarnings("deprecation")
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
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ASSEMBLED);
        if (connectedTexture()) {
            builder.add(TOP_CONNECTED_PROPERTY);
            builder.add(BOTTOM_CONNECTED_PROPERTY);
            builder.add(NORTH_CONNECTED_PROPERTY);
            builder.add(SOUTH_CONNECTED_PROPERTY);
            builder.add(EAST_CONNECTED_PROPERTY);
            builder.add(WEST_CONNECTED_PROPERTY);
        }
    }
    
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (connectedTexture()) {
            updateConnectedTextureState(worldIn, pos, this.getDefaultState());
        }
    }
    
    @Override
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (connectedTexture()) {
            updateConnectedTextureState(worldIn, pos, this.getDefaultState());
        }
    }
    
    private void updateConnectedTextureState(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        state = state.with(TOP_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.UP)).getBlock() == this);
        state = state.with(BOTTOM_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.DOWN)).getBlock() == this);
        state = state.with(NORTH_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.NORTH)).getBlock() == this);
        state = state.with(SOUTH_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.SOUTH)).getBlock() == this);
        state = state.with(EAST_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.EAST)).getBlock() == this);
        state = state.with(WEST_CONNECTED_PROPERTY, worldIn.getBlockState(pos.offset(Direction.WEST)).getBlock() == this);
        worldIn.setBlockState(pos, state, 2);
    }
}
