package net.roguelogix.biggerreactors.classic.turbine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbinePowerTapTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbinePowerTap.ConnectionState.CONNECTION_STATE_ENUM_PROPERTY;

@RegisterBlock(name = "turbine_power_tap", tileEntityClass = TurbinePowerTapTile.class)
public class TurbinePowerTap extends TurbineBaseBlock {
    
    @RegisterBlock.Instance
    public static TurbinePowerTap INSTANCE;
    
    public TurbinePowerTap() {
        super();
        setDefaultState(getDefaultState().with(CONNECTION_STATE_ENUM_PROPERTY, ConnectionState.DISCONNECTED));
    }
    
    public enum ConnectionState implements IStringSerializable {
        CONNECTED,
        DISCONNECTED;
        
        @SuppressWarnings("SpellCheckingInspection")
        public static final EnumProperty<ConnectionState> CONNECTION_STATE_ENUM_PROPERTY = EnumProperty.create("connectionstate", ConnectionState.class);
        
        @Override
        public String getString() {
            return toString().toLowerCase();
        }
        
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(CONNECTION_STATE_ENUM_PROPERTY);
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurbinePowerTapTile();
    }
    
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TurbinePowerTapTile) {
            ((TurbinePowerTapTile) te).neighborChanged();
        }
    }
}
