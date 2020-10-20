package net.roguelogix.biggerreactors.classic.machine.tiles.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;

import javax.annotation.Nonnull;

public class CyaniteReprocessorItemHandler extends ItemStackHandler {
    
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int OUTPUT_SLOT_INDEX = 1;
    
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        switch (slot) {
            case INPUT_SLOT_INDEX:
                return stack.getItem() == CyaniteIngot.INSTANCE;
            case OUTPUT_SLOT_INDEX:
                return stack.getItem() == BlutoniumIngot.INSTANCE;
        }
        throw new IndexOutOfBoundsException();
    }
    
    public IItemHandler pipeHandler(){
        final CyaniteReprocessorItemHandler realHandler = this;
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return realHandler.getSlots();
            }
    
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return realHandler.getStackInSlot(slot);
            }
    
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot != INPUT_SLOT_INDEX) {
                    return ItemStack.EMPTY;
                }
                return realHandler.insertItem(slot, stack, simulate);
            }
    
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot != OUTPUT_SLOT_INDEX) {
                    return ItemStack.EMPTY;
                }
                return realHandler.extractItem(slot, amount, simulate);
            }
    
            @Override
            public int getSlotLimit(int slot) {
                return realHandler.getSlotLimit(slot);
            }
    
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return realHandler.isItemValid(slot, stack);
            }
        };
    }
}
