package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_terminal")
public class ReactorTerminalTile extends ReactorBaseTile {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorTerminalTile() {
        super(TYPE);
    }

    @Override
    public void onActivated(PlayerEntity player) {
        if(controller == null){
            return;
        }
        if(player.isCrouching()){
            reactor().toggleActive();
        }else{
            super.onActivated(player);
        }
    }
}
