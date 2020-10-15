package net.roguelogix.biggerreactors.classic.turbine.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineCoolantPortTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Set;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort.PortDirection.*;

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
        public String getString() {
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
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        if (handIn == Hand.MAIN_HAND) {
            Set<ResourceLocation> tags = player.getHeldItemMainhand().getItem().getTags();
            if (tags.contains(new ResourceLocation("forge:tools/wrench")) || tags.contains(new ResourceLocation("forge:wrenches"))) {
                PortDirection direction = state.get(PORT_DIRECTION_ENUM_PROPERTY);
                direction = direction == INLET ? OUTLET : INLET;
                state = state.with(PORT_DIRECTION_ENUM_PROPERTY, direction);
                worldIn.setBlockState(pos, state);
                if (!worldIn.isRemote()) {
                    TileEntity te = worldIn.getTileEntity(pos);
                    if (te instanceof TurbineCoolantPortTile) {
                        ((TurbineCoolantPortTile) te).setDirection(direction);
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
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
