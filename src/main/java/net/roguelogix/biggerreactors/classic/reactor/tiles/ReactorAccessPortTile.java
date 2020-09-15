package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.biggerreactors.items.ingots.YelloriumIngot;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.INLET;
import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort.PortDirection.PORT_DIRECTION_ENUM_PROPERTY;

@RegisterTileEntity(name = "reactor_access_port")
public class ReactorAccessPortTile extends ReactorBaseTile implements IItemHandler {
    
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
    protected void readNBT(CompoundNBT compound) {
        if (compound.contains("direction")) {
            direction = ReactorAccessPort.PortDirection.valueOf(compound.getString("direction"));
        }
    }
    
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        return NBT;
    }
    
    @Override
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
        if (slot == 1) {
            return ItemStack.EMPTY;
        } else {
            return new ItemStack(CyaniteIngot.INSTANCE, 64);
        }
    }
    
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isInlet() || controller == null) {
            return stack;
        }
        stack = stack.copy();
        if (stack.getItem() == YelloriumIngot.INSTANCE || stack.getItem() == BlutoniumIngot.INSTANCE) {
            long maxAcceptable = reactor().refuel(stack.getCount() * Config.Reactor.FuelMBPerIngot, true);
            long canAccept = maxAcceptable - (maxAcceptable % Config.Reactor.FuelMBPerIngot);
            reactor().refuel(canAccept, simulate);
            if (canAccept > 0) {
                stack.setCount(stack.getCount() - (int) (canAccept / Config.Reactor.FuelMBPerIngot));
            }
        }
        return stack;
    }
    
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isInlet() || controller == null) {
            return ItemStack.EMPTY;
        }
        
        long maxExtractable = reactor().extractWaste(amount * Config.Reactor.FuelMBPerIngot, true);
        long toExtracted = maxExtractable - (maxExtractable % Config.Reactor.FuelMBPerIngot);
        long extracted = reactor().extractWaste(toExtracted, simulate);
        
        return new ItemStack(CyaniteIngot.INSTANCE, (int) Math.min(amount, extracted / Config.Reactor.FuelMBPerIngot));
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == YelloriumIngot.INSTANCE || stack.getItem() == BlutoniumIngot.INSTANCE;
    }
    
    public int pushWaste(int waste, boolean simulated) {
        if (itemOutput.isPresent()) {
            IItemHandler output = itemOutput.orElse(null);
            waste /= Config.Reactor.FuelMBPerIngot;
            int wasteHandled = 0;
            for (int i = 0; i < output.getSlots(); i++) {
                if (waste == 0) {
                    break;
                }
                ItemStack toInsertStack = new ItemStack(CyaniteIngot.INSTANCE, (int) (waste));
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
    
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            itemOutputDirection = null;
        } else if (pos.getX() == controller.minX()) {
            itemOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxX()) {
            itemOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minY()) {
            itemOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxY()) {
            itemOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minZ()) {
            itemOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxZ()) {
            itemOutputDirection = Direction.SOUTH;
        }
        neighborChanged();
    }
    
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
}
