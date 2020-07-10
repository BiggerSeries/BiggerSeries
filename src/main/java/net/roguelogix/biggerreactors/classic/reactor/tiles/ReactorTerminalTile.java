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
import net.roguelogix.biggerreactors.classic.reactor.ReactorDatapack;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "reactor_terminal")
public class ReactorTerminalTile extends ReactorBaseTile {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorTerminalTile() {
        super(TYPE);
    }

    // TODO: Connect this to reactor logic.
    public ReactorDatapack getReactorData() {
        ReactorDatapack data = new ReactorDatapack();
        data.reactorStatus = false;
        data.reactorType = true;

        data.energyStored = 1000;
        data.energyCapacity = Config.MachineEnergyTankCapacity;

        data.caseHeatStored = 1000;
        data.caseHeatCapacity = 5000;

        data.coreHeatStored = 1000;
        data.coreHeatStored = 5000;

        data.fuelHeatStored = 1000;
        data.fuelHeatCapacity = 5000;

        data.wasteStored = 500;
        data.fuelStored = 500;
        data.fuelCapacity = 5000;

        data.reactorOutputRate = 500;
        data.fuelUsageRate = 250;
        data.reactivityRate = 250;

        return data;
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
            return ActionResultType.SUCCESS;
        }

        if(handIn == Hand.MAIN_HAND) {
            if (!world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
