package net.roguelogix.biggerreactors.classic.blocks;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "cyanite_reprocessor")
public class CyaniteReprocessorTile extends LockableTileEntity implements ISidedInventory,
    ITickableTileEntity, INamedContainerProvider, INameable {

  @RegisterTileEntity.Type
  public static TileEntityType<CyaniteReprocessorTile> INSTANCE;

  // TODO: make these directions changeable.
  private final Direction SLOT_INPUT_DIRECTION = Direction.EAST;
  private final Direction SLOT_OUTPUT_DIRECTION = Direction.WEST;

  // Index numbers for valid slots.
  public static final int SLOT_INPUT = 0;
  public static final int SLOT_OUTPUT = 1;

  private int workTime;
  private int workTimeTotal;

  private final LazyOptional<EnergyStorage> energyStorageCapability = LazyOptional
      .of(() -> this.energyStorage);

  private EnergyStorage energyStorage = new EnergyStorage(Config.MachineEnergyTankCapacity);

  private final LazyOptional<FluidTank> fluidStorageCapability = LazyOptional
      .of(() -> this.fluidStorage);

  private FluidTank fluidStorage = new FluidTank(Config.MachineFluidTankCapacity,
      x -> x.getFluid() == Fluids.WATER);

  private final LazyOptional<ItemStackHandler> itemStorageCapability = LazyOptional
      .of(() -> this.itemStorage);

  private ItemStackHandler itemStorage = new ItemStackHandler(2) {
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

  public int getEnergyStored() {
    return this.energyStorage.getEnergyStored();
  }

  public int getEnergyCapacity() {
    return this.energyStorage.getMaxEnergyStored();
  }

  public int getFluidStored() {
    return this.fluidStorage.getFluidAmount();
  }

  public int getFluidCapacity() {
    return this.fluidStorage.getCapacity();
  }

  @Nonnull
  public ActionResultType onBlockActivated(@Nonnull BlockState blockState, World world,
      @Nonnull BlockPos blockPos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
      @Nonnull BlockRayTraceResult trace) {
    if (world.getTileEntity(blockPos) == this) {

      if (player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.WATER_BUCKET) {
        this.fluidStorage.fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
        player.setHeldItem(Hand.MAIN_HAND, Items.BUCKET.getDefaultInstance());
        return ActionResultType.SUCCESS;
      }

      if (!world.isRemote) {
        if (player.isCrouching()) {
          // This doesn't work and I have no idea why.
          //if(player.getHeldItemMainhand().getItem() == DebugTool.INSTANCE) {
          player.sendMessage(new StringTextComponent("Work Time: " + workTime));
          player.sendMessage(new StringTextComponent("Work Time Total: " + workTimeTotal));
          player.sendMessage(
              new StringTextComponent(
                  "Energy Stored: " + energyStorage.getEnergyStored() + " RF"));
          player.sendMessage(new StringTextComponent(
              "Energy Capacity: " + energyStorage.getMaxEnergyStored() + " RF"));
          player.sendMessage(new StringTextComponent(
              "Fluid Stored: " + getFluidStored() + " mB (" + fluidStorage.getFluid()
                  .getDisplayName().getFormattedText().toLowerCase() + ")"));
          player.sendMessage(
              new StringTextComponent("Fluid Capacity: " + fluidStorage.getCapacity() + " mB"));
          return ActionResultType.SUCCESS;
          //}
        }
        NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
        return ActionResultType.SUCCESS;
      }
    } else {
      throw new IllegalStateException("Container not found: biggerreactors:cyanite_reprocessor");
    }
    return ActionResultType.SUCCESS;
  }

  public boolean isActive() {
    return false;
    //return (this.machineData.get(WATER_STORED) > 0 && this.machineData.get(ENERGY_STORED) > 0);
  }

  @Override
  public void read(@Nonnull CompoundNBT compound) {
    super.read(compound);

    // Dear Forge, please standardize this. Seriously, WHY?
    // NBT method A: energy.
    this.energyStorage.receiveEnergy(compound.getInt("energyStored"), false);
    // NBT method B: fluids.
    this.fluidStorage = this.fluidStorage.readFromNBT(compound.getCompound("fluidStorage"));
    // NBT method C: items.
    this.itemStorage.deserializeNBT(compound.getCompound("inventory"));
    // Other stuff.
    this.workTime = compound.getInt("workTime");
    this.workTimeTotal = compound.getInt("workTimeTotal");
  }

  @Override
  public final CompoundNBT write(@Nonnull CompoundNBT compound) {
    super.write(compound);

    // Dear Forge, please standardize this. Seriously, WHY?
    // NBT method A: energy.
    compound.putInt("energyStored", this.energyStorage.getEnergyStored());
    compound.putInt("energyCapacity", this.energyStorage.getMaxEnergyStored());
    // NBT method B: fluid and items.
    compound.put("fluidStorage", fluidStorage.writeToNBT(new CompoundNBT()));
    compound.put("inventory", this.itemStorage.serializeNBT());

    // Other stuff.
    compound.putInt("workTime", this.workTime);
    compound.putInt("workTimeTotal", this.workTimeTotal);

    return compound;
  }

  @Nonnull
  @Override
  public int[] getSlotsForFace(@Nonnull Direction side) {
    if (side == SLOT_INPUT_DIRECTION) {
      return new int[]{SLOT_INPUT};
    }
    if (side == SLOT_OUTPUT_DIRECTION) {
      return new int[]{SLOT_OUTPUT};
    }
    return new int[]{};
  }

  @Override
  public boolean canInsertItem(int index, @Nonnull ItemStack itemStack,
      @Nullable Direction direction) {
    if (direction == SLOT_INPUT_DIRECTION && index == SLOT_INPUT) {
      return this.isItemValidForSlot(index, itemStack);
    }
    return false;
  }

  @Override
  public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull Direction direction) {
    if (direction == SLOT_INPUT_DIRECTION && index == SLOT_INPUT) {
      return true;
    }
    return direction == SLOT_OUTPUT_DIRECTION && index == SLOT_OUTPUT;
  }

  @Nonnull
  @Override
  protected ITextComponent getDefaultName() {
    return new TranslationTextComponent("block.biggerreactors.cyanite_reprocessor");
  }

  @Nonnull
  @Override
  protected Container createMenu(int windowId, PlayerInventory playerInventory) {
    return new CyaniteReprocessorContainer(windowId, this.pos, playerInventory.player);
  }

  @Override
  public int getSizeInventory() {
    return this.itemStorage.getSlots();
  }

  @Override
  public boolean isEmpty() {
    for (int index = 0; index < this.itemStorage.getSlots(); ++index) {
      if (!this.itemStorage.getStackInSlot(index).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int index) {
    return this.itemStorage.getStackInSlot(index);
  }

  @Nonnull
  @Override
  public ItemStack decrStackSize(int index, int count) {
    return this.itemStorage.getStackInSlot(index).split(count);
  }

  @Nonnull
  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack itemStack = this.itemStorage.getStackInSlot(index).copy();
    this.itemStorage.setStackInSlot(index, ItemStack.EMPTY);
    return itemStack;
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack itemStack) {

    ItemStack oldItemStack = this.itemStorage.getStackInSlot(index);
    boolean flag = !itemStack.isEmpty() && itemStack.isItemEqual(oldItemStack) && ItemStack
        .areItemStackTagsEqual(itemStack, oldItemStack);
    this.itemStorage.setStackInSlot(index, itemStack);
    if (itemStack.getCount() > this.getInventoryStackLimit()) {
      itemStack.setCount(this.getInventoryStackLimit());
    }

    if (index == 0 && !flag) {
      this.workTime = 0;
      this.workTimeTotal = Config.CyaniteReprocessorWorkTime;
      this.markDirty();
    }
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
    assert this.world != null;
    if (this.world.getTileEntity(this.pos) != this) {
      return false;
    } else {
      return
          player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
              (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
  }

  @Override
  public void clear() {
    for (int index = 0; index < this.itemStorage.getSlots(); ++index) {
      this.itemStorage.setStackInSlot(index, ItemStack.EMPTY);
    }
  }

  @Override
  public void tick() {
    if (this.isActive()) {
      // Consume power and water.
    }

    assert this.world != null;
    if (!this.world.isRemote()) {
      // Do processing here.
    }
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
      @Nullable Direction side) {
    if (capability.equals(CapabilityEnergy.ENERGY)) {
      return this.energyStorageCapability.cast();
    }

    if (capability.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
      return this.fluidStorageCapability.cast();
    }

    if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
      return this.itemStorageCapability.cast();
    }

    return Objects.requireNonNull(super.getCapability(capability, side));
  }
}