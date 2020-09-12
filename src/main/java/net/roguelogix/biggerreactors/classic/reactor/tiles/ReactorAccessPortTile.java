package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.item.Item;
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
        if (compound.contains("fuel")) {
            fuel = compound.getInt("fuel");
        }
        if (compound.contains("waste")) {
            waste = compound.getInt("waste");
        }
    }
    
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        NBT.putInt("fuel", fuel);
        NBT.putInt("waste", waste);
        return NBT;
    }
    
    @Override
    protected String getDebugInfo() {
        return direction.toString() + "\n" +
                "Fuel: " + fuel + "\n" +
                "Waste: " + waste;
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
    
    int fuel = 0;
    int waste = 0;
    
    @Override
    public int getSlots() {
        return 1;
    }
    
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }
    
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isInlet()) {
            return stack;
        }
        stack = stack.copy();
        if (stack.getItem() == YelloriumIngot.INSTANCE || stack.getItem() == BlutoniumIngot.INSTANCE) {
            int canAccept = 64 - fuel;
            if (canAccept > 0) {
                ItemStack accepted = stack.split(canAccept);
                if (!simulate) {
                    fuel += accepted.getCount();
                }
            }
        }
        return stack;
    }
    
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isInlet()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(CyaniteIngot.INSTANCE, Math.min(amount, waste));
        if (!simulate) {
            waste -= stack.getCount();
        }
        return stack;
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
    
    public long refuel(long maxAmount) {
        long ingots = maxAmount / Config.Reactor.FuelMBPerIngot;
        ingots = Math.min(ingots, fuel);
        fuel -= ingots;
        return ingots * Config.Reactor.FuelMBPerIngot;
    }
    
    public long wasteSpaceAvailable() {
        return (64 - waste) * Config.Reactor.FuelMBPerIngot;
    }
    
    public long dumpWaste(long wasteMB) {
        long ingots = wasteMB / Config.Reactor.FuelMBPerIngot;
        waste += ingots;
        return (ingots * Config.Reactor.FuelMBPerIngot);
    }
    
    public void pushWaste() {
        itemOutput.ifPresent(output -> {
            for (int i = 0; i < output.getSlots(); i++) {
                if (waste == 0) {
                    return;
                }
                waste = output.insertItem(i, new ItemStack(CyaniteIngot.INSTANCE, waste), false).getCount();
            }
        });
    }
    
    Direction itemOutputDirection;
    boolean connected;
    LazyOptional<IItemHandler> itemOutput = LazyOptional.empty();
    
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            itemOutputDirection = null;
        }
        if (pos.getX() == controller.minX()) {
            itemOutputDirection = Direction.WEST;
            return;
        }
        if (pos.getX() == controller.maxX()) {
            itemOutputDirection = Direction.EAST;
            return;
        }
        if (pos.getY() == controller.minY()) {
            itemOutputDirection = Direction.DOWN;
            return;
        }
        if (pos.getY() == controller.maxY()) {
            itemOutputDirection = Direction.UP;
            return;
        }
        if (pos.getZ() == controller.minZ()) {
            itemOutputDirection = Direction.NORTH;
            return;
        }
        if (pos.getZ() == controller.maxZ()) {
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
