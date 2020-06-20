package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

import static net.roguelogix.biggerreactors.classic.reactor.ReactorState.INACTIVE;
import static net.roguelogix.biggerreactors.classic.reactor.ReactorState.REACTOR_STATE_MODEL_PROPERTY;

public class ReactorBaseTile extends RectangularMultiblockTile {

    ReactorMultiblockController reactor(){
        return (ReactorMultiblockController) controller;
    }

    public ReactorBaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    ReactorState reactorState = INACTIVE;

    @Override
    protected void appendModelData(ModelDataMap.Builder builder) {
        super.appendModelData(builder);
        builder.withInitial(REACTOR_STATE_MODEL_PROPERTY, reactorState);
    }

    @Override
    public final MultiblockController createController() {
        return new ReactorMultiblockController(world);
    }

    public void onActivated(PlayerEntity player) {
        if (controller != null) {
//            player.sendMessage(new StringTextComponent(world.getBlockState(getPos()).toString()));
            player.sendMessage(new StringTextComponent(controller.getInfo()));
        }
    }
}
