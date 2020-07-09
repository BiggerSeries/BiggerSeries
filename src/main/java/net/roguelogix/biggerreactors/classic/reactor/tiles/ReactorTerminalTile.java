package net.roguelogix.biggerreactors.classic.reactor.tiles;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.ReactorContainer;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_terminal")
public class ReactorTerminalTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorTerminalTile() {
        super(TYPE);
    }

    // TODO: Connect lines 22-56 to logic.

    public int getEnergyStored() {
        return 1000;
    }

    public int getEnergyCapacity() {
        return Config.MachineEnergyTankCapacity;
    }

    public int getCoreHeatStored() {
        return 1000;
    }

    public int getCoreHeatCapacity() {
        return 5000;
    }

    public int getCaseHeatStored() {
        return 1000;
    }

    public int getCaseHeatCapacity() {
        return 5000;
    }

    public int getFuelStored() {
        return 1000;
    }

    public int getWasteStored() {
        return 1000;
    }

    public int getFuelCapacity() {
        return 5000;
    }

    @Nonnull
    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.biggerreactors.reactor_terminal");
    }

    @Nonnull
    @Override
    protected Container createMenu(int windowId, PlayerInventory playerInventory) {
        return new ReactorContainer(windowId, this.pos, playerInventory.player);
    }

    @Override
    public ActionResultType onBlockActivated(PlayerEntity player, Hand handIn) {
        if (player.isCrouching() && handIn == Hand.MAIN_HAND) {
            if(controller != null) {
                reactor().toggleActive();
            }
            return ActionResultType.PASS;
        }

        if(!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }
}
