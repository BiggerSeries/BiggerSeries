package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.blocks.materials.BlutoniumBlock;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorAccessPortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorAccessPortState;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.biggerreactors.items.ingots.YelloriumIngot;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.IAssemblyAttemptedTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.*;

@RegisterTileEntity(name = "reactor_access_port")
public class ReactorAccessPortTile extends ReactorBaseTile implements IItemHandler, INamedContainerProvider, IHasUpdatableState<ReactorAccessPortState>, IAssemblyAttemptedTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    private static final ResourceLocation uraniumIngotTag = new ResourceLocation("forge:ingots/uranium");
    private static final ResourceLocation uraniumBlockTag = new ResourceLocation("forge:storage_blocks/uranium");
    private static final ResourceLocation yelloriumIngotTag = new ResourceLocation("forge:ingots/yellorium");
    private static final ResourceLocation yelloriumBlockTag = new ResourceLocation("forge:storage_blocks/yellorium");
    
    public static final int FUEL_SLOT = 0;
    public static final int WASTE_SLOT = 1;
    public static final int FUEL_INSERT_SLOT = 2;
    
    public ReactorAccessPortTile() {
        super(TYPE);
    }
    
    private ReactorAccessPort.PortDirection direction = INLET;
    private boolean fuelMode = false;
    
    public boolean isInlet() {
        return direction == INLET;
    }
    
    public void setDirection(ReactorAccessPort.PortDirection direction) {
        this.direction = direction;
        this.markDirty();
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
    }
    
    @Override
    protected void readNBT(@Nonnull CompoundNBT compound) {
        if (compound.contains("direction")) {
            direction = ReactorAccessPort.PortDirection.valueOf(compound.getString("direction"));
        }
        if (compound.contains("fuelMode")) {
            fuelMode = compound.getBoolean("fuelMode");
        }
    }
    
    @Override
    @Nonnull
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        NBT.putBoolean("fuelMode", fuelMode);
        return NBT;
    }
    
    @Override
    @Nonnull
    protected String getDebugInfo() {
        return direction.toString();
    }
    
    @Override
    public void onAssemblyAttempted() {
        assert world != null;
        world.setBlockState(pos, world.getBlockState(pos).with(PORT_DIRECTION_ENUM_PROPERTY, direction));
    }
    
    LazyOptional<IItemHandler> itemStackHandler = LazyOptional.of(() -> this);
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemStackHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public int getSlots() {
        return 3;
    }
    
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (controller == null) {
            return ItemStack.EMPTY;
        } else if (slot == WASTE_SLOT) {
            long availableIngots = controller.CCgetWasteAmount() / Config.Reactor.FuelMBPerIngot;
            return new ItemStack(CyaniteIngot.INSTANCE, (int) availableIngots);
        } else if (slot == FUEL_SLOT) {
            long availableIngots = controller.CCgetFuelAmount() / Config.Reactor.FuelMBPerIngot;
            return new ItemStack(YelloriumIngot.INSTANCE, (int) availableIngots);
        } else {
            return ItemStack.EMPTY;
        }
    }
    
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isInlet() || controller == null || slot != FUEL_INSERT_SLOT) {
            return stack;
        }
        stack = stack.copy();
        if (stack.getItem().getTags().contains(uraniumIngotTag) || stack.getItem().getTags().contains(yelloriumIngotTag) || stack.getItem() == BlutoniumIngot.INSTANCE) {
            long maxAcceptable = controller.refuel(stack.getCount() * Config.Reactor.FuelMBPerIngot, true);
            long canAccept = maxAcceptable - (maxAcceptable % Config.Reactor.FuelMBPerIngot);
            controller.refuel(canAccept, simulate);
            if (canAccept > 0) {
                stack.setCount(stack.getCount() - (int) (canAccept / Config.Reactor.FuelMBPerIngot));
            }
        }
        if (stack.getItem().getTags().contains(uraniumBlockTag) || stack.getItem().getTags().contains(yelloriumBlockTag) || stack.getItem() == BlutoniumBlock.INSTANCE.asItem()) {
            long maxAcceptable = controller.refuel(stack.getCount() * (Config.Reactor.FuelMBPerIngot * 9), true);
            long canAccept = maxAcceptable - (maxAcceptable % (Config.Reactor.FuelMBPerIngot * 9));
            controller.refuel(canAccept, simulate);
            if (canAccept > 0) {
                stack.setCount(stack.getCount() - (int) (canAccept / (Config.Reactor.FuelMBPerIngot * 9)));
            }
        }
        return stack;
    }
    
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isInlet() || controller == null || slot == FUEL_INSERT_SLOT) {
            return ItemStack.EMPTY;
        }
        
        if (slot == WASTE_SLOT && !fuelMode) {
            long maxExtractable = controller.extractWaste(amount * Config.Reactor.FuelMBPerIngot, true);
            long toExtracted = maxExtractable - (maxExtractable % Config.Reactor.FuelMBPerIngot);
            long extracted = controller.extractWaste(toExtracted, simulate);
            
            return new ItemStack(CyaniteIngot.INSTANCE, (int) Math.min(amount, extracted / Config.Reactor.FuelMBPerIngot));
        } else if (slot == FUEL_SLOT && fuelMode) {
            long maxExtractable = controller.extractFuel(amount * Config.Reactor.FuelMBPerIngot, true);
            long toExtracted = maxExtractable - (maxExtractable % Config.Reactor.FuelMBPerIngot);
            long extracted = controller.extractFuel(toExtracted, simulate);
            
            return new ItemStack(YelloriumIngot.INSTANCE, (int) Math.min(amount, extracted / Config.Reactor.FuelMBPerIngot));
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public int getSlotLimit(int slot) {
        if(controller == null){
            return 0;
        }
        return (int) (controller.CCgetFuelAmountMax() / Config.Reactor.FuelMBPerIngot);
    }
    
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot == FUEL_INSERT_SLOT) {
            return stack.getItem().getTags().contains(uraniumIngotTag) || stack.getItem().getTags().contains(yelloriumIngotTag) || stack.getItem() == BlutoniumIngot.INSTANCE
                    || stack.getItem().getTags().contains(uraniumBlockTag) || stack.getItem().getTags().contains(yelloriumBlockTag) || stack.getItem() == BlutoniumBlock.INSTANCE.asItem();
        } else if (slot == FUEL_SLOT) {
            return stack.getItem() == YelloriumIngot.INSTANCE;
        } else {
            return stack.getItem() == CyaniteIngot.INSTANCE;
        }
    }
    
    public int pushWaste(int waste, boolean simulated) {
        if (itemOutput.isPresent()) {
            IItemHandler output = itemOutput.orElse(EmptyHandler.INSTANCE);
            waste /= Config.Reactor.FuelMBPerIngot;
            int wasteHandled = 0;
            for (int i = 0; i < output.getSlots(); i++) {
                if (waste == 0) {
                    break;
                }
                ItemStack toInsertStack = new ItemStack(CyaniteIngot.INSTANCE, waste);
                ItemStack remainingStack = output.insertItem(i, toInsertStack, simulated);
                wasteHandled += toInsertStack.getCount() - remainingStack.getCount();
                waste -= toInsertStack.getCount() - remainingStack.getCount();
            }
            return (int) (wasteHandled * Config.Reactor.FuelMBPerIngot);
        }
        return 0;
    }
    
    public void ejectWaste() {
        controller.extractWaste(pushWaste((int) controller.extractWaste(Integer.MAX_VALUE, true), false), false);
    }
    
    public int pushFuel(int fuel, boolean simulated) {
        if (itemOutput.isPresent()) {
            IItemHandler output = itemOutput.orElse(EmptyHandler.INSTANCE);
            fuel /= Config.Reactor.FuelMBPerIngot;
            int fuelHandled = 0;
            for (int i = 0; i < output.getSlots(); i++) {
                if (fuel == 0) {
                    break;
                }
                ItemStack toInsertStack = new ItemStack(YelloriumIngot.INSTANCE, fuel);
                ItemStack remainingStack = output.insertItem(i, toInsertStack, simulated);
                fuelHandled += toInsertStack.getCount() - remainingStack.getCount();
                fuel -= toInsertStack.getCount() - remainingStack.getCount();
            }
            return (int) (fuelHandled * Config.Reactor.FuelMBPerIngot);
        }
        return 0;
    }
    
    public void ejectFuel() {
        controller.extractFuel(pushFuel((int) controller.extractFuel(Integer.MAX_VALUE, true), false), false);
    }
    
    Direction itemOutputDirection;
    boolean connected;
    LazyOptional<IItemHandler> itemOutput = LazyOptional.empty();
    public final ReactorAccessPortState reactorAccessPortState = new ReactorAccessPortState(this);
    
    
    @SuppressWarnings("DuplicatedCode")
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            itemOutputDirection = null;
        } else if (pos.getX() == controller.minCoord().x()) {
            itemOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxCoord().x()) {
            itemOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minCoord().y()) {
            itemOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxCoord().y()) {
            itemOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minCoord().z()) {
            itemOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxCoord().z()) {
            itemOutputDirection = Direction.SOUTH;
        }
        neighborChanged();
    }
    
    @SuppressWarnings("DuplicatedCode")
    public void neighborChanged() {
        itemOutput = LazyOptional.empty();
        if (itemOutputDirection == null) {
            connected = false;
            return;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(itemOutputDirection));
        if (te == null) {
            connected = false;
            return;
        }
        itemOutput = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemOutputDirection.getOpposite());
        connected = itemOutput.isPresent();
    }
    
    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert world != null;
        if (world.getBlockState(pos).get(MultiblockBlock.ASSEMBLED)) {
            if (!world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void runRequest(String requestName, Object requestData) {
        if (controller == null) {
            return;
        }
        
        // Change IO direction.
        if (requestName.equals("setDirection")) {
            this.setDirection(((Integer) requestData != 0) ? OUTLET : INLET);
            world.setBlockState(this.pos, this.getBlockState().with(PORT_DIRECTION_ENUM_PROPERTY, direction));
            return;
        }
        
        // Change fuel/waste ejection.
        if (requestName.equals("setFuelMode")) {
            this.fuelMode = ((Integer) requestData != 0);
            return;
        }
        
        if (requestName.equals("ejectWaste")) {
            if (fuelMode) {
                ejectFuel();
            } else {
                ejectWaste();
            }
            return;
        }
        
        super.runRequest(requestName, requestData);
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ReactorAccessPort.INSTANCE.getTranslationKey());
    }
    
    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new ReactorAccessPortContainer(windowId, this.pos, player);
    }
    
    @Nullable
    @Override
    public ReactorAccessPortState getState() {
        this.updateState();
        return this.reactorAccessPortState;
    }
    
    @Override
    public void updateState() {
        reactorAccessPortState.direction = (this.direction == INLET);
        reactorAccessPortState.fuelMode = this.fuelMode;
    }
}
