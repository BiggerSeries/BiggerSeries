package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorRedstonePortTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_redstone_port", tileEntityClass = ReactorRedstonePortTile.class)
public class ReactorRedstonePort extends ReactorBaseBlock {
    @RegisterBlock.Instance
    public static ReactorRedstonePort INSTANCE;
    
    public ReactorRedstonePort() {
        super();
        setDefaultState(getDefaultState().with(IS_LIT_BOOLEAN_PROPERTY, false));
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorRedstonePortTile();
    }
    
    @Override
    public boolean canProvidePower(@Nonnull BlockState stateate) {
        return true;
    }
    
    @Override
    public int getWeakPower(@Nonnull BlockState blockState, @Nonnull IBlockReader blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        if (tile instanceof ReactorRedstonePortTile) {
            return ((ReactorRedstonePortTile) tile).isEmitting() ? 15 : 0;
        }
        return super.getWeakPower(blockState, blockAccess, pos, side);
    }
    
    @Override
    public int getStrongPower(@Nonnull BlockState blockState, @Nonnull IBlockReader blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        if (tile instanceof ReactorRedstonePortTile) {
            return ((ReactorRedstonePortTile) tile).isEmitting() ? 15 : 0;
        }
        return super.getWeakPower(blockState, blockAccess, pos, side);
    }
    
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        boolean powered = worldIn.getRedstonePowerFromNeighbors(pos) > 0;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof ReactorRedstonePortTile) {
            ((ReactorRedstonePortTile) tile).setPowered(powered);
        }
    }
    
    
    public static BooleanProperty IS_LIT_BOOLEAN_PROPERTY = BooleanProperty.create("is_lit");
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_LIT_BOOLEAN_PROPERTY);
        super.fillStateContainer(builder);
    }
}
