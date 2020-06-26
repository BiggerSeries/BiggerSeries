package net.roguelogix.biggerreactors.classic.reactor.blocks;

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
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.biggerreactors.items.tools.Wrench;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.*;


@RegisterBlock(name = "reactor_access_port", tileEntityClass = ReactorAccessPortTile.class)
public class ReactorAccessPort extends ReactorBaseBlock{

    @RegisterBlock.Instance
    public static ReactorAccessPort INSTANCE;

    public ReactorAccessPort(){
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
        // TODO: 6/25/20 mappings
        //    @Override
        public String getName() {
            return toString().toLowerCase();
        }

        @Override
        public String func_176610_l() {
            return getName();
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PORT_DIRECTION_ENUM_PROPERTY);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if(!worldIn.isRemote && handIn == Hand.MAIN_HAND){
            if(player.getHeldItemMainhand().getItem() == Wrench.INSTANCE){
                PortDirection direction = state.get(PORT_DIRECTION_ENUM_PROPERTY);
                direction = direction == INLET ? OUTLET : INLET;
                worldIn.setBlockState(pos, state.with(PORT_DIRECTION_ENUM_PROPERTY, direction));

                TileEntity te = worldIn.getTileEntity(pos);
                if(te instanceof ReactorAccessPortTile){
                    ((ReactorAccessPortTile) te).setDirection(direction);
                }

                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, p_225533_6_);
    }
}
