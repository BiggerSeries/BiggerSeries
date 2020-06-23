package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_access_port")
public class ReactorAccessPortTile extends ReactorBaseTile{

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorAccessPortTile() {
        super(TYPE);
    }


}
