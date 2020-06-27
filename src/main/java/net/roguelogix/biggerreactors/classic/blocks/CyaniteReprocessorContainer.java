package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

@RegisterContainer(name = "cyanite_reprocessor")
public class CyaniteReprocessorContainer extends Container {
  // Slot 0 = Input, Slot 1 = Output
  private IInventory machineInventory;
  // Data 0 = workTime, Data 2 = workTimeTotal, Data 3 = energy, Data 4 = water
  private IIntArray machineData;
  private CyaniteReprocessorTile tileEntity;
  private static final int GUI_OFFSET = 93;

  @RegisterContainer.Instance
  public static ContainerType<CyaniteReprocessorContainer> INSTANCE;

  public CyaniteReprocessorContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
    this(windowId, blockPos, player, new Inventory(2), new IntArray(4));
  }
  public CyaniteReprocessorContainer(int windowId, BlockPos blockPos, PlayerEntity player, IInventory machineInventory, IIntArray machineData) {
    super(INSTANCE, windowId);
    assertInventorySize(machineInventory, 2);
    this.machineInventory = machineInventory;
    assertIntArraySize(machineData, 4);
    this.machineData = machineData;
    this.tileEntity = (CyaniteReprocessorTile) player.world.getTileEntity(blockPos);

    if(this.tileEntity != null) {
      tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
        // Add input slot.
        this.addSlot(new SlotItemHandler(handler, 0, 44, 41));
        // Add output slot.
        this.addSlot(new SlotItemHandler(handler, 1, 116, 41));
      });
    }

    // Add player inventory;
    for(int rowIndex = 0; rowIndex < 3; rowIndex++) {
      for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
        this.addSlot(new Slot(player.inventory, (columnIndex + rowIndex * 9 + 9),
            (8 + columnIndex * 18), (GUI_OFFSET + rowIndex * 18)));
      }
    }
    // Add player hotbar.
    for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
      this.addSlot(new Slot(player.inventory, columnIndex, (8 + columnIndex * 18), (GUI_OFFSET + 58)));
    }

    this.trackIntArray(machineData);
  }

  @Override
  public boolean canInteractWith(PlayerEntity player) {
    assert tileEntity.getWorld() != null;
    return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), player, CyaniteReprocessor.INSTANCE);
  }

  @Nonnull
  @Override
  public ItemStack transferStackInSlot(PlayerEntity player, int index) {
    ItemStack itemStackA = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    int inventorySize = this.machineInventory.getSizeInventory();

    if (slot != null && slot.getHasStack()) {
      ItemStack itemStackB = slot.getStack();
      itemStackA = itemStackB.copy();

      if (index < inventorySize) {
        if (!this.mergeItemStack(itemStackB, inventorySize, this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      }
      else if (!this.mergeItemStack(itemStackB, 0, inventorySize, false)) {
        return ItemStack.EMPTY;
      }

      if (itemStackB.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      }
      else {
        slot.onSlotChanged();
      }
    }

    return itemStackA;
  }
}
