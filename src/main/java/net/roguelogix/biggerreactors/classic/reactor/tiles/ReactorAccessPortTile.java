package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.INLET;
import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.PORT_DIRECTION_ENUM_PROPERTY;

@RegisterTileEntity(name = "reactor_access_port")
public class ReactorAccessPortTile extends ReactorBaseTile{

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorAccessPortTile() {
        super(TYPE);
    }

    ReactorAccessPort.PortDirection direction = INLET;

    public void setDirection(ReactorAccessPort.PortDirection direction){
        this.direction = direction;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        assert world != null;
        BlockState state = world.getBlockState(pos);
        direction = state.get(PORT_DIRECTION_ENUM_PROPERTY);
    }
}
