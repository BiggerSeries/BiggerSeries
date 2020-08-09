package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "turbine_terminal")
public class TurbineTerminalTile extends TurbineBaseTile {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineTerminalTile() {
        super(TYPE);
    }
    
    @Override
    public ActionResultType onBlockActivated(PlayerEntity player, Hand handIn) {
        if (player.isCrouching() && handIn == Hand.MAIN_HAND) {
            if (controller != null) {
                turbine().toggleActive();
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }
}
