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
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorAccessPortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorAccessPortState;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.*;

@RegisterTileEntity(name = "reactor_access_port")
public class ReactorAccessPortTile extends ReactorBaseTile implements IItemHandler, INamedContainerProvider, IHasUpdatableState<ReactorAccessPortState> {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorAccessPortTile() {
        super(TYPE);
    }

    private ReactorAccessPort.PortDirection direction = INLET;

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
    }

    @Override
    @Nonnull
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        return NBT;
    }

    @Override
    @Nonnull
    protected String getDebugInfo() {
        return direction.toString();
    }

    @Override
    protected void onAssemblyAttempted() {
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
        return 2;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            return ItemStack.EMPTY;
        } else {
            return new ItemStack(CyaniteIngot.INSTANCE, 64);
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ReactorMultiblockController reactor = reactor();

        if (!isInlet() || reactor == null || slot == 1) {
            return stack;
        }
        stack = stack.copy();
        if (stack.getItem().getTags().contains(new ResourceLocation("forge:ingots/uranium")) || stack.getItem() == BlutoniumIngot.INSTANCE) {
            long maxAcceptable = reactor.refuel(stack.getCount() * Config.Reactor.FuelMBPerIngot, true);
            long canAccept = maxAcceptable - (maxAcceptable % Config.Reactor.FuelMBPerIngot);
            reactor.refuel(canAccept, simulate);
            if (canAccept > 0) {
                stack.setCount(stack.getCount() - (int) (canAccept / Config.Reactor.FuelMBPerIngot));
            }
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ReactorMultiblockController reactor = reactor();

        if (isInlet() || reactor == null || slot == 0) {
            return ItemStack.EMPTY;
        }

        long maxExtractable = reactor.extractWaste(amount * Config.Reactor.FuelMBPerIngot, true);
        long toExtracted = maxExtractable - (maxExtractable % Config.Reactor.FuelMBPerIngot);
        long extracted = reactor.extractWaste(toExtracted, simulate);

        return new ItemStack(CyaniteIngot.INSTANCE, (int) Math.min(amount, extracted / Config.Reactor.FuelMBPerIngot));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if(slot == 0) {
            return stack.getItem().getTags().contains(new ResourceLocation("forge:ingots/uranium")) || stack.getItem() == BlutoniumIngot.INSTANCE;
        }else{
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

    Direction itemOutputDirection;
    boolean connected;
    LazyOptional<IItemHandler> itemOutput = LazyOptional.empty();
    public final ReactorAccessPortState accessPortState = new ReactorAccessPortState(this);


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
        if (world.getBlockState(pos).get(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY) != RectangularMultiblockPositions.DISASSEMBLED) {
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
        ReactorMultiblockController reactor = reactor();
        if (reactor == null) {
            return;
        }

        if (requestName.equals("setInputState")) {
            boolean state = (Boolean) requestData;
            this.setDirection(state ? INLET : OUTLET);
            world.setBlockState(this.pos, this.getBlockState().with(PORT_DIRECTION_ENUM_PROPERTY, direction));

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
        return this.accessPortState;
    }

    @Override
    public void updateState() {
        accessPortState.inputState = (this.direction == INLET);
    }
}
