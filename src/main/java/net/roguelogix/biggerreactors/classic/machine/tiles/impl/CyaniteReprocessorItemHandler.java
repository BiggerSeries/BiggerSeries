package net.roguelogix.biggerreactors.classic.machine.tiles.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;

import javax.annotation.Nonnull;

public class CyaniteReprocessorItemHandler extends ItemStackHandler {
    
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int OUTPUT_SLOT_INDEX = 1;
    
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.isItemValid(slot, stack)) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }
    
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
}
