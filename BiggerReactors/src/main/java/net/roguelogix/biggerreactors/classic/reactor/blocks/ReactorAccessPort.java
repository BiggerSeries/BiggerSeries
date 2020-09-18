package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCoolantPortTile;
import net.roguelogix.biggerreactors.items.tools.Wrench;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.*;


@RegisterBlock(name = "reactor_access_port", tileEntityClass = ReactorAccessPortTile.class)
public class ReactorAccessPort extends ReactorBaseBlock {
    
    @RegisterBlock.Instance
    public static ReactorAccessPort INSTANCE;
    
    public ReactorAccessPort() {
        super();
        setDefaultState(getDefaultState().with(PORT_DIRECTION_ENUM_PROPERTY, INLET));
        
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorAccessPortTile();
    }
    
    public enum PortDirection implements IStringSerializable {
        INLET,
        OUTLET;
        
        public static final EnumProperty<PortDirection> PORT_DIRECTION_ENUM_PROPERTY = EnumProperty.create("portdirection", PortDirection.class);
        
        @Override
        public String getString() {
            return toString().toLowerCase();
        }
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PORT_DIRECTION_ENUM_PROPERTY);
    }
    
    
    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rotationDirection) {
        ReactorAccessPort.PortDirection direction = state.get(PORT_DIRECTION_ENUM_PROPERTY);
        direction = direction == INLET ? OUTLET : INLET;
        state = state.with(PORT_DIRECTION_ENUM_PROPERTY, direction);
        if (!world.isRemote()) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ReactorAccessPortTile) {
                ((ReactorAccessPortTile) te).setDirection(direction);
            }
        }
        return state;
    }
    
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof ReactorAccessPortTile) {
            ((ReactorAccessPortTile) te).neighborChanged();
        }
    }
}
