package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

@RegisterContainer(name = "cyanite_reprocessor")
public class CyaniteReprocessorContainer extends Container {

  @RegisterContainer.Instance
  public static ContainerType<CyaniteReprocessorContainer> INSTANCE;

  private PlayerEntity player;
  private CyaniteReprocessorTile tileEntity;

  public CyaniteReprocessorContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
    super(INSTANCE, windowId);
    this.player = player;
    this.tileEntity = (CyaniteReprocessorTile) player.world.getTileEntity(blockPos);

    if (this.tileEntity != null) {
      tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
        // Add input slot.
        this.addSlot(new SlotItemHandler(handler, CyaniteReprocessorTile.SLOT_INPUT, 44, 41));
        // Add output slot.
        this.addSlot(new SlotItemHandler(handler, CyaniteReprocessorTile.SLOT_OUTPUT, 116, 41));
      });
    }

    this.populatePlayerInventory();
  }

  public int getEnergyStored() {
    //return this.tileEntity.getEnergyStored();
    return this.tileEntity.getCapability(CapabilityEnergy.ENERGY)
        .map(IEnergyStorage::getEnergyStored).orElse(0);
  }

  public int getEnergyCapacity() {
    //return this.tileEntity.getEnergyCapacity();
    return this.tileEntity.getCapability(CapabilityEnergy.ENERGY)
        .map(IEnergyStorage::getMaxEnergyStored).orElse(
            Config.MachineEnergyTankCapacity);
  }

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

  @Override
  public boolean canInteractWith(@Nonnull PlayerEntity player) {
    assert tileEntity.getWorld() != null;
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
}
