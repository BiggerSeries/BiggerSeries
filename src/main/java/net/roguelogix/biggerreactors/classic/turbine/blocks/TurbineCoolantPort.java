package net.roguelogix.biggerreactors.classic.turbine.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineCoolantPortTile;
import net.roguelogix.biggerreactors.items.tools.Wrench;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort.PortDirection.INLET;
import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort.PortDirection.PORT_DIRECTION_ENUM_PROPERTY;

@RegisterBlock(name = "turbine_coolant_port", tileEntityClass = TurbineCoolantPortTile.class)
public class TurbineCoolantPort extends TurbineBaseBlock {
    @RegisterBlock.Instance
    public static TurbineCoolantPort INSTANCE;
    
    public TurbineCoolantPort() {
        super();
        setDefaultState(getDefaultState().with(PORT_DIRECTION_ENUM_PROPERTY, INLET));
    }
    
    public enum PortDirection implements IStringSerializable {
        INLET,
        OUTLET;
        
        public static final EnumProperty<PortDirection> PORT_DIRECTION_ENUM_PROPERTY = EnumProperty.create("portdirection", PortDirection.class);
        
        @Override
        public String getName() {
            return toString().toLowerCase();
        }
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurbineCoolantPortTile();
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PORT_DIRECTION_ENUM_PROPERTY);
    }
    
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult p_225533_6_) {
        if (handIn == Hand.MAIN_HAND && player.getHeldItemMainhand().getItem() == Wrench.INSTANCE) {
            if (!worldIn.isRemote) {
                PortDirection direction = state.get(PORT_DIRECTION_ENUM_PROPERTY);
                direction = direction == INLET ? PortDirection.OUTLET : INLET;
                worldIn.setBlockState(pos, state.with(PORT_DIRECTION_ENUM_PROPERTY, direction));
                
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof TurbineCoolantPortTile) {
                    ((TurbineCoolantPortTile) te).setDirection(direction);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, p_225533_6_);
    }
    
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TurbineCoolantPortTile) {
            ((TurbineCoolantPortTile) te).neighborChanged();
        }
    }
}
