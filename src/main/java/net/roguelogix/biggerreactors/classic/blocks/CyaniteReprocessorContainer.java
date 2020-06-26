package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

@RegisterContainer(name = "cyanite_reprocessor")
public class CyaniteReprocessorContainer extends Container {

  private World world;
  private PlayerInventory playerInventory;
  private TileEntity tileEntity;
  private int size;

  @RegisterContainer.Instance
  public static ContainerType<CyaniteReprocessorContainer> INSTANCE;

  public CyaniteReprocessorContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
    super(INSTANCE, windowId);
    this.world = player.world;
    this.playerInventory = player.inventory;
    this.tileEntity = this.world.getTileEntity(blockPos);
    this.size = 2;

    if(tileEntity != null) {
      tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handle -> {
        this.addSlot(new SlotItemHandler(handle, 0, 55, 35));  // INPUT
        this.addSlot(new SlotItemHandler(handle, 1, 115, 35)); // OUTPUT
      });
    }
    this.addPlayerInventorySlots();
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

    if (slot != null && slot.getHasStack()) {
      ItemStack itemStackB = slot.getStack();
      itemStackA = itemStackB.copy();

      if (index <  size) {
        if (!this.mergeItemStack(itemStackB, size, this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      }
      else if (!this.mergeItemStack(itemStackB, 0, size, false)) {
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

  public void addPlayerInventorySlots() {
    for(int rowIndex = 0; rowIndex < 3; ++rowIndex) {
      for(int columnIndex = 0; columnIndex < 9; ++columnIndex) {
        this.addSlot(new Slot(this.playerInventory, columnIndex + rowIndex * 9 + 9, 8 + columnIndex * 18, 84 + rowIndex * 18));
      }
    }

    for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
      this.addSlot(new Slot(this.playerInventory, hotbarIndex, 8 + hotbarIndex * 18, 142));
    }
  }

  /*
  // [0] Input, [1] Output
  private final IInventory machineInventory;
  // [0] runTime, [1] runTimeTotal, [2] powerStored, [3] waterStored
  private final IIntArray machineData;
  private final World world;

  public CyaniteReprocessorContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory) {
    this(containerType, windowId, playerInventory, new Inventory(2), new IntArray(4));
  }

  public CyaniteReprocessorContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, IInventory machineInventory, IIntArray machineData) {
    super(containerType, windowId);
    assertInventorySize(machineInventory, 2);
    assertIntArraySize(machineData, 4);
    this.machineInventory = machineInventory;
    this.machineData = machineData;
    this.world = playerInventory.player.world;


  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return this.;
  }

  @Override
  public void onContainerClosed(PlayerEntity player) {
    super.onContainerClosed(player);
  }
  */
}
