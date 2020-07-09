package net.roguelogix.biggerreactors.classic.reactor.tiles;

import static net.roguelogix.biggerreactors.classic.reactor.ReactorState.INACTIVE;
import static net.roguelogix.biggerreactors.classic.reactor.ReactorState.REACTOR_STATE_MODEL_PROPERTY;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.ReactorState;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

public class ReactorBaseTile extends RectangularMultiblockTile {
    
    ReactorMultiblockController reactor() {
        return (ReactorMultiblockController) controller;
    }
    
    public ReactorBaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return null;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }


    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

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
}
