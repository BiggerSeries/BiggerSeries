package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorPowerTapTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorPowerTap.ConnectionState.CONNECTION_STATE_ENUM_PROPERTY;

@RegisterBlock(name = "reactor_power_tap", tileEntityClass = ReactorPowerTapTile.class)
public class ReactorPowerTap extends ReactorBaseBlock{

    @RegisterBlock.Instance
    public static ReactorPowerTap INSTANCE;

    public ReactorPowerTap() {
        super();
        setDefaultState(getDefaultState().with(CONNECTION_STATE_ENUM_PROPERTY, ConnectionState.DISCONNECTED));
    }

    public enum ConnectionState implements IStringSerializable {
        CONNECTED,
        DISCONNECTED;

        public static final EnumProperty<ConnectionState> CONNECTION_STATE_ENUM_PROPERTY = EnumProperty.create("connectionstate", ConnectionState.class);
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
        builder.add(CONNECTION_STATE_ENUM_PROPERTY);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorPowerTapTile();
    }
}
