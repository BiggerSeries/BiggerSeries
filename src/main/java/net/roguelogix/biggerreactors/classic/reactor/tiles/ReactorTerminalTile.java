package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_terminal")
public class ReactorTerminalTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorTerminalTile() {
        super(TYPE);
    }
    
    
    @Override
    public ActionResultType onBlockActivated(PlayerEntity player, Hand handIn) {
        if (player.isCrouching()) {
            if(controller != null) {
                reactor().toggleActive();
            }
            return ActionResultType.PASS;
        }
        return super.onBlockActivated(player, handIn);
    }
}
