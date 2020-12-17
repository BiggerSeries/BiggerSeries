package net.roguelogix.biggerreactors.classic.machine.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
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
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.api.IWorkHandler;
import net.roguelogix.biggerreactors.api.WorkHandler;
import net.roguelogix.biggerreactors.classic.machine.blocks.CyaniteReprocessor;
import net.roguelogix.biggerreactors.classic.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.machine.state.CyaniteReprocessorState;
import net.roguelogix.biggerreactors.classic.machine.tiles.impl.CyaniteReprocessorItemHandler;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.items.DebugTool;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@RegisterTileEntity(name = "cyanite_reprocessor")
public class CyaniteReprocessorTile extends LockableTileEntity implements INamedContainerProvider, ITickableTileEntity, IHasUpdatableState<CyaniteReprocessorState> {
    
    @RegisterTileEntity.Type
    public static TileEntityType<CyaniteReprocessorTile> INSTANCE;
    /**
     * The (mostly) current state of the machine.
     */
    public final CyaniteReprocessorState cyaniteReprocessorState = new CyaniteReprocessorState(this);
    /**
     * The work handler.
     */
    private WorkHandler workHandler;
    /**
     * Capability access to the work handler.
     */
    private final LazyOptional<IWorkHandler> WORK_HANDLER_CAPABILITY = LazyOptional.of(() -> this.workHandler);
    /**
     * The item handler.
     */
    private CyaniteReprocessorItemHandler itemHandler;
    /**
     * Capability access to the item handler.
     */
    private final LazyOptional<IItemHandler> ITEM_HANDLER_CAPABILITY = LazyOptional.of(() -> this.itemHandler.pipeHandler());
    /**
     * The energy storage.
     */
    private EnergyStorage energyStorage;
    /**
     * Capability access to the energy storage.
     */
    private final LazyOptional<IEnergyStorage> ENERGY_STORAGE_CAPABILITY = LazyOptional.of(() -> this.energyStorage);
    /**
     * The fluid tank.
     */
    private FluidTank fluidTank;
    /**
     * Capability access to the fluid tank.
     */
    private final LazyOptional<IFluidTank> FLUID_TANK_CAPABILITY = LazyOptional.of(() -> this.fluidTank);
    /**
     * "Anti-cheat" item stack, to ensure players don't swap items mid-process.
     *
     * @see CyaniteReprocessorTile#tick()
     */
    private ItemStack itemPresentLastTick = ItemStack.EMPTY;
    
    public CyaniteReprocessorTile() {
        super(CyaniteReprocessorTile.INSTANCE);
        this.clear();
        this.updateState();
    }
    
    /**
     * Do right-click stuff.
     */
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull BlockState blockState, World world, @Nonnull BlockPos blockPos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult trace) {
        // Check for client-side.
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        
        // Print tile data.
        if (ItemStack.areItemsEqual(player.getHeldItemMainhand(), new ItemStack(DebugTool.INSTANCE))) {
            player.sendMessage(new StringTextComponent(String.format("[%s] Progress: %s/%s", BiggerReactors.modid, this.cyaniteReprocessorState.workTime, this.cyaniteReprocessorState.workTimeTotal)), player.getUniqueID());
            player.sendMessage(new StringTextComponent(String.format("[%s] Energy: %s/%s RF", BiggerReactors.modid, this.cyaniteReprocessorState.energyStored, this.cyaniteReprocessorState.energyCapacity)), player.getUniqueID());
            player.sendMessage(new StringTextComponent(String.format("[%s] Fluid Tank: %s/%s mB", BiggerReactors.modid, this.cyaniteReprocessorState.waterStored, this.cyaniteReprocessorState.waterCapacity)), player.getUniqueID());
            return ActionResultType.SUCCESS;
        }
        
        // Do water bucket check.
        if (ItemStack.areItemsEqual(player.getHeldItemMainhand(), new ItemStack(Items.WATER_BUCKET))) {
            if (this.fluidTank.getFluidAmount() <= (Config.CyaniteReprocessor.WaterTankCapacity - 1000)) {
                this.fluidTank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
            }
            return ActionResultType.SUCCESS;
        }
        
        // Get container and open GUI.
        NetworkHooks.openGui((ServerPlayerEntity) player, this, blockPos);
        return ActionResultType.SUCCESS;
    }
    
    /**
     * Drop items on destruction.
     */
    public void onReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState newBlockState, boolean isMoving) {
        ItemStack inputStack = this.itemHandler.getStackInSlot(CyaniteReprocessorItemHandler.INPUT_SLOT_INDEX);
        if (!inputStack.isEmpty()) {
            InventoryHelper.dropInventoryItems(world, blockPos, new Inventory(inputStack));
        }
        
        ItemStack outputStack = this.itemHandler.getStackInSlot(CyaniteReprocessorItemHandler.OUTPUT_SLOT_INDEX);
        if (!outputStack.isEmpty()) {
            InventoryHelper.dropInventoryItems(world, blockPos, new Inventory(outputStack));
        }
    }
    
    /**
     * @see CyaniteReprocessorTile#getDefaultName()
     */
    @Override
    public ITextComponent getDisplayName() {
        return this.getDefaultName();
    }
    
    /**
     * @return The localized default name for the tile.
     */
    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.biggerreactors.cyanite_reprocessor");
    }
    
    /**
     * Create a GUI container for the tile.
     *
     * @param windowId        The window ID to use.
     * @param playerInventory The player's inventory.
     * @return A GUI container to render.
     */
    @Override
    protected Container createMenu(int windowId, PlayerInventory playerInventory) {
        return new CyaniteReprocessorContainer(windowId, this.getPos(), playerInventory.player);
    }
    
    /**
     * @return The current state of the tile.
     */
    @Override
    @Nonnull
    public CyaniteReprocessorState getState() {
        this.updateState();
        return this.cyaniteReprocessorState;
    }
    
    /**
     * Call for an update to the current state information.
     */
    @Override
    public void updateState() {
        this.cyaniteReprocessorState.workTime = this.workHandler.getProgress();
        this.cyaniteReprocessorState.workTimeTotal = this.workHandler.getGoal();
        this.cyaniteReprocessorState.energyStored = this.energyStorage.getEnergyStored();
        this.cyaniteReprocessorState.energyCapacity = this.energyStorage.getMaxEnergyStored();
        this.cyaniteReprocessorState.waterStored = this.fluidTank.getFluidAmount();
        this.cyaniteReprocessorState.waterCapacity = this.fluidTank.getCapacity();
    }
    
    /**
     * @return How large this tile's inventory is.
     */
    @Override
    public int getSizeInventory() {
        return this.itemHandler.getSlots();
    }
    
    /**
     * @return Whether or not the inventory is empty.
     */
    @Override
    public boolean isEmpty() {
        for (int index = 0; index < this.itemHandler.getSlots(); ++index) {
            if (!this.itemHandler.getStackInSlot(index).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the item held in the specified slot.
     *
     * @param index The slot index to fetch from.
     * @return Any items held in that slot.
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.itemHandler.getStackInSlot(index);
    }
    
    /**
     * Removes the specified number of items from the specified slot.
     *
     * @param index The slot index to remove from.
     * @param count The number of items to remove.
     * @return The items that were removed.
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.itemHandler.getStackInSlot(index).split(count);
    }
    
    /**
     * Removes an entire stack fromm the specified slot.
     *
     * @param index The slot index to remove from.
     * @return The items that were removed.
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = this.itemHandler.getStackInSlot(index).copy();
        this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        return itemStack;
    }
    
    /**
     * Updates the stored items in the specified slot.
     *
     * @param index The slot index to update.
     * @param stack The items to update with.
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack oldStack = this.itemHandler.getStackInSlot(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(oldStack) && ItemStack
                .areItemStackTagsEqual(stack, oldStack);
        this.itemHandler.setStackInSlot(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        
        if (index == 0 && !flag) {
            this.workHandler.clear();
            this.markDirty();
        }
    }
    
    /**
     * Clears all data and inventory for this tile.
     */
    @Override
    public void clear() {
        // Reset work.
        this.workHandler = new WorkHandler(Config.CyaniteReprocessor.TotalWorkTime);
        
        // Reset items.
        this.itemHandler = new CyaniteReprocessorItemHandler();
        this.itemHandler.setSize(2);
        
        // Reset energy.
        this.energyStorage = new EnergyStorage(Config.CyaniteReprocessor.EnergyTankCapacity);
        
        // Reset fluids.
        this.fluidTank = new FluidTank(Config.CyaniteReprocessor.WaterTankCapacity, fluid -> fluid.getFluid() == Fluids.WATER);
    }
    
    /**
     * Checks if the player can currently use this tile.
     *
     * @param player The player to check.
     * @return True if usable, false otherwise.
     */
    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        assert this.world != null;
        if (this.world.getTileEntity(this.getPos()) != this) {
            return false;
        } else {
            return player.getDistanceSq(
                    (double) this.getPos().getX() + 0.5D,
                    (double) this.getPos().getY() + 0.5D,
                    (double) this.getPos().getZ() + 0.5D) <= 64.0D;
        }
    }
    
    /**
     * Read NBT data from the world.
     *
     * @param parentCompound The parent compound to read from.
     */
    @Override
    public void read(BlockState state, @Nonnull CompoundNBT parentCompound) {
        super.read(state, parentCompound);
        CompoundNBT childCompound = parentCompound.getCompound("cyaniteReprocessorState");
        
        // Read work.
        this.workHandler = new WorkHandler(childCompound.getInt("workTimeTotal"), childCompound.getInt("workTime"));
        // Read items.
        this.itemHandler.deserializeNBT(childCompound.getCompound("inventory"));
        // Read energy.
        this.energyStorage = new EnergyStorage(childCompound.getInt("energyCapacity"),
                Config.CyaniteReprocessor.TransferRate,
                Config.CyaniteReprocessor.TransferRate,
                childCompound.getInt("energyStored"));
        // Read fluids.
        this.fluidTank = this.fluidTank.readFromNBT(childCompound.getCompound("fluidStorage"));
    }
    
    /**
     * Save NBT data to the world.
     *
     * @param parentCompound The parent compound to append onto.
     * @return The updated compound.
     */
    @Override
    public final CompoundNBT write(@Nonnull CompoundNBT parentCompound) {
        super.write(parentCompound);
        CompoundNBT childCompound = new CompoundNBT();
        
        // Write work.
        childCompound.putInt("workTime", this.workHandler.getProgress());
        childCompound.putInt("workTimeTotal", this.workHandler.getGoal());
        // Write items.
        childCompound.put("inventory", this.itemHandler.serializeNBT());
        // Write energy.
        childCompound.putInt("energyStored", this.energyStorage.getEnergyStored());
        childCompound.putInt("energyCapacity", this.energyStorage.getMaxEnergyStored());
        // Write fluids.
        childCompound.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
        
        parentCompound.put("cyaniteReprocessorState", childCompound);
        
        return parentCompound;
    }
    
    /**
     * @return Whether or not the machine can perform work.
     * @see CyaniteReprocessorTile#tick()
     */
    private boolean canWork() {
        // If the output slot is full, we cannot work.
        if (this.getStackInSlot(CyaniteReprocessorItemHandler.OUTPUT_SLOT_INDEX).getCount() >= 64) {
            return false;
        }
        return (this.energyStorage.getEnergyStored() >= Config.CyaniteReprocessor.EnergyConsumptionPerTick
                && this.fluidTank.getFluidAmount() >= Config.CyaniteReprocessor.WaterConsumptionPerTick);
    }
    
    /**
     * Do work (if possible).
     */
    @Override
    public void tick() {
        // Check for client-side.
        assert world != null;
        if (world.isRemote()) {
            return;
        }
        
        boolean doUpdate = false;
        boolean isActive = false;
        ItemStack inputStack = this.itemHandler.getStackInSlot(CyaniteReprocessorItemHandler.INPUT_SLOT_INDEX);
        
        // Check to make sure the player doesn't try to pull a fast one.
        if (!ItemStack.areItemsEqual(this.itemPresentLastTick, inputStack)) {
            this.workHandler.clear();
        }
        this.itemPresentLastTick = inputStack.copy();
        
        if (inputStack.getCount() >= 2) {
            // Can we continue?
            if (canWork()) {
                isActive = true;
                doUpdate = true;
                this.workHandler.increment(1);
                this.energyStorage.extractEnergy(Config.CyaniteReprocessor.EnergyConsumptionPerTick, false);
                this.fluidTank.drain(Config.CyaniteReprocessor.WaterConsumptionPerTick, IFluidHandler.FluidAction.EXECUTE);
                // We've run out of resources, halt.
            } else if (this.workHandler.getProgress() > 0) {
                this.workHandler.decrement(2);
            }
            
            // Item is done.
            if (this.workHandler.isFinished()) {
                this.itemHandler.extractItem(CyaniteReprocessorItemHandler.INPUT_SLOT_INDEX, 2, false);
                this.itemHandler.insertItem(CyaniteReprocessorItemHandler.OUTPUT_SLOT_INDEX, new ItemStack(BlutoniumIngot.INSTANCE, 1), false);
                this.workHandler.clear();
            }
        }
        
        BlockState currentBlockState = world.getBlockState(this.getPos());
        BlockState newBlockState = currentBlockState.with(CyaniteReprocessor.ENABLED, isActive);
        if (!newBlockState.equals(currentBlockState)) {
            this.world.setBlockState(this.getPos(), newBlockState);
            doUpdate = true;
        }
        
        if (doUpdate) {
            markDirty();
        }
        
        // Update the current machine state.
        this.updateState();
    }
    
    /**
     * Check if the tile holds a certain capability.
     *
     * @param capability The capability to check for.
     * @param side       Which side this capability should belong to.
     * @param <T>        The type class of the capability.
     * @return The handler for the capability, if present.
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                             @Nullable Direction side) {
        // Check for work.
        // TODO: While this capability is exclusive to the Reprocessor for now, it may not always be.
        //  The capability is technically implemented, but no registration is done.
        //  That oughta be fixed, so it can be checked for here.
        //  But I'm lazy, so I'll do that some other time.
        //if (capability.equals(CapabilityWorkHandler.WORK_HANDLER_CAPABILITY)) {
        //    return this.WORK_HANDLER_CAPABILITY.cast();
        //}
        
        // Check for items.
        if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return this.ITEM_HANDLER_CAPABILITY.cast();
        }
        
        // Check for energy.
        if (capability.equals(CapabilityEnergy.ENERGY)) {
            return this.ENERGY_STORAGE_CAPABILITY.cast();
        }
        
        // Check for water.
        if (capability.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
            return this.FLUID_TANK_CAPABILITY.cast();
        }
        
        return Objects.requireNonNull(super.getCapability(capability, side));
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.itemHandler.isItemValid(index, stack);
    }
    
    public CyaniteReprocessorItemHandler getItemHandler() {
        return itemHandler;
    }
}
