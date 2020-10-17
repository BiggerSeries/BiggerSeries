package net.roguelogix.biggerreactors.classic.machine.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.roguelogix.biggerreactors.classic.machine.blocks.CyaniteReprocessor;
import net.roguelogix.biggerreactors.classic.machine.tiles.CyaniteReprocessorTile;
import net.roguelogix.biggerreactors.classic.machine.tiles.impl.CyaniteReprocessorItemHandler;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterContainer(name = "cyanite_reprocessor")
public class CyaniteReprocessorContainer extends Container implements GuiSync.IGUIPacketProvider {
    
    @RegisterContainer.Instance
    public static ContainerType<CyaniteReprocessorContainer> INSTANCE;
    
    private PlayerEntity player;
    private CyaniteReprocessorTile tileEntity;
    
    public CyaniteReprocessorContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
        super(CyaniteReprocessorContainer.INSTANCE, windowId);
        this.player = player;
        this.tileEntity = (CyaniteReprocessorTile) player.world.getTileEntity(blockPos);
        this.getGuiPacket();
        
        // Populate machine slots.
        if (this.tileEntity != null) {
            IItemHandler handler = tileEntity.getItemHandler();
            // Add input slot.
            this.addSlot(new SlotItemHandler(handler, CyaniteReprocessorItemHandler.INPUT_SLOT_INDEX, 44, 41));
            // Add output slot.
            this.addSlot(new SlotItemHandler(handler, CyaniteReprocessorItemHandler.OUTPUT_SLOT_INDEX, 116, 41));
        }
        
        // Populate player inventory.
        this.populatePlayerInventory();
    }
    
    /**
     * @return The current state of the machine.
     */
    @Nullable
    @Override
    public GuiSync.IGUIPacket getGuiPacket() {
        return this.tileEntity.cyaniteReprocessorState;
    }
    
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        assert this.tileEntity.getWorld() != null;
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
                player, CyaniteReprocessor.INSTANCE);
    }
    
    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int index) {
        ItemStack itemStackA = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        int inventorySize = this.tileEntity.getSizeInventory();
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStackB = slot.getStack();
            itemStackA = itemStackB.copy();
            
            if (index < inventorySize) {
                if (!this.mergeItemStack(itemStackB, inventorySize, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStackB, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStackB.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        
        return itemStackA;
    }
    
    /**
     * Draw and initialize the player's inventory.
     */
    private void populatePlayerInventory() {
        int guiOffset = 93;
        
        // Add player inventory;
        for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
                this.addSlot(new Slot(player.inventory, (columnIndex + rowIndex * 9 + 9),
                        (8 + columnIndex * 18), (guiOffset + rowIndex * 18)));
            }
        }
        // Add player hotbar.
        for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
            this.addSlot(
                    new Slot(player.inventory, columnIndex, (8 + columnIndex * 18), (guiOffset + 58)));
        }
    }
}
