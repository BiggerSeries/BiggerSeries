package net.roguelogix.biggerreactors.classic.blocks;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "cyanite_reprocessor")
public class CyaniteReprocessorTile extends LockableTileEntity implements ISidedInventory, ITickableTileEntity, INamedContainerProvider, INameable {

  @RegisterTileEntity.Type
  public static TileEntityType<CyaniteReprocessorTile> INSTANCE;

  // TODO: make these directions changeable.
  private Direction SLOT_INPUT_DIRECTION = Direction.EAST;
  public static final int SLOT_INPUT = 0;
  private Direction SLOT_OUTPUT_DIRECTION = Direction.WEST;
  public static final int SLOT_OUTPUT = 1;
  //private NonNullList<ItemStack> machineInventory;
  private int workTime;
  private int workTimeTotal;
  private int water;
  private int energy;

  private final LazyOptional<ItemStackHandler> itemHandlerCapability = LazyOptional
      .of(() -> this.machineInventory);

  private final ItemStackHandler machineInventory = new ItemStackHandler(2) {
    @Override
    protected void onContentsChanged(int slot) {
      markDirty();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack itemStack) {
      switch (slot) {
        case 0:
          return itemStack.getItem() == CyaniteIngot.INSTANCE;
        case 1:
          return itemStack.getItem() == BlutoniumIngot.INSTANCE;
        default:
          return itemStack.getItem() == Items.AIR;
      }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack itemStack, boolean simulate) {
      if (isItemValid(slot, itemStack)) {
        return super.insertItem(slot, itemStack, simulate);
      }
      return itemStack;
    }
  };

  public CyaniteReprocessorTile() {
    super(INSTANCE);
  }

  public int getWorkTime() {
    return this.workTime;
  }

  public int getWorkTimeTotal() {
    return this.workTimeTotal;
  }

  public boolean isActive() {
    return (this.water > 0 && this.energy > 0);
  }

  @Override
  // TODO: Fix lost data on reload of world.
  // Read
  public void func_230337_a_(BlockState blockState, CompoundNBT compound) {
    super.func_230337_a_(blockState, compound);
    machineInventory.deserializeNBT(compound.getCompound("inventory"));
    this.workTime = compound.getInt("workTime");
    this.workTimeTotal = compound.getInt("workTimeTotal");
    this.energy = compound.getInt("energy");
    this.water = compound.getInt("water");
  }

  @Override
  // TODO: Fix lost data on reload of world.
  public final CompoundNBT write(CompoundNBT compound) {
    super.write(compound);
    compound.put("inventory", machineInventory.serializeNBT());
    compound.put("workTime", IntNBT.valueOf(this.workTime));
    compound.put("workTimeTotal", IntNBT.valueOf(this.workTimeTotal));
    compound.put("energy", IntNBT.valueOf(this.energy));
    compound.put("water", IntNBT.valueOf(this.water));
    return compound;
  }

  @Override
  public int[] getSlotsForFace(Direction side) {
    if(side == SLOT_INPUT_DIRECTION) return new int[]{SLOT_INPUT};
    if(side == SLOT_OUTPUT_DIRECTION) return new int[]{SLOT_OUTPUT};
    return new int[]{};
  }

  @Override
  public boolean canInsertItem(int index, ItemStack itemStack, @Nullable Direction direction) {
    if(direction == SLOT_INPUT_DIRECTION && index == SLOT_INPUT) return this.isItemValidForSlot(index, itemStack);
    return false;
  }

  @Override
  public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
    if(direction == SLOT_INPUT_DIRECTION && index == SLOT_INPUT) return true;
    if(direction == SLOT_OUTPUT_DIRECTION && index == SLOT_OUTPUT) return true;
    return false;
  }

  @Override
  protected ITextComponent getDefaultName() {
    return new TranslationTextComponent("block.biggerreactors.cyanite_reprocessor");
  }

  @Override
  protected Container createMenu(int windowId, PlayerInventory playerInventory) {
    return new CyaniteReprocessorContainer(windowId, this.pos, playerInventory.player);
  }

  @Override
  public int getSizeInventory() {
    return this.machineInventory.getSlots();
  }

  @Override
  public boolean isEmpty() {
    for (int index = 0; index < this.machineInventory.getSlots(); ++index) {
      if (!this.machineInventory.getStackInSlot(index).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return this.machineInventory.getStackInSlot(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    return this.machineInventory.getStackInSlot(index).split(count);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack itemStack = this.machineInventory.getStackInSlot(index).copy();
    this.machineInventory.setStackInSlot(index, ItemStack.EMPTY);
    return itemStack;
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack itemStack) {

    ItemStack oldItemStack = this.machineInventory.getStackInSlot(index);
    boolean flag = !itemStack.isEmpty() && itemStack.isItemEqual(oldItemStack) && ItemStack
        .areItemStackTagsEqual(itemStack, oldItemStack);
    this.machineInventory.setStackInSlot(index, itemStack);
    if (itemStack.getCount() > this.getInventoryStackLimit()) {
      itemStack.setCount(this.getInventoryStackLimit());
    }

    if (index == 0 && !flag) {
      this.workTimeTotal = 200;
      this.workTime = 0;
      this.markDirty();
    }
  }

  @Override
  public boolean isUsableByPlayer(PlayerEntity player) {
    if(this.world.getTileEntity(this.pos) != this) return false;
    else return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public void clear() {
    for(int index = 0; index < this.machineInventory.getSlots(); ++index) {
      this.machineInventory.setStackInSlot(index, ItemStack.EMPTY);
    }
  }

  @Override
  public void tick() {
    if(this.isActive()) {
      // Consume power and water.
    }

    if(!this.world.isRemote()) {
      // Do processing here.
    }
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
      @Nullable Direction side) {
    if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
      return this.itemHandlerCapability.cast();
    }

    return Objects.requireNonNull(super.getCapability(capability, side));
  }
}