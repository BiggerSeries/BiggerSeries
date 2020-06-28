package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.biggerreactors.items.ingots.YelloriumIngot;
import net.roguelogix.biggerreactors.items.tools.DebugTool;
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

    ReactorAccessPort.PortDirection direction = INLET;

    public void setDirection(ReactorAccessPort.PortDirection direction){
        this.direction = direction;
        this.markDirty();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    protected void readNBT(CompoundNBT compound) {
        if(compound.contains("direction")){
            direction = ReactorAccessPort.PortDirection.valueOf(compound.getString("direction"));
        }
        if(compound.contains("inSlot")){
            inSlot.deserializeNBT(compound.getCompound("inSlot"));
        }
        if(compound.contains("outSlot")){
            outSlot.deserializeNBT(compound.getCompound("outSlot"));
        }
    }

    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        NBT.put("inSlot", inSlot.serializeNBT());
        NBT.put("outSlot", outSlot.serializeNBT());
        return NBT;
    }

    @Override
    public void onActivated(PlayerEntity player) {
        if (controller != null && player.getHeldItemMainhand().getItem() == DebugTool.INSTANCE) {
            player.sendMessage(new StringTextComponent(direction.toString() + "\n" +
                    "In: " + inSlot.toString() + "\n" +
                    "Out: " + outSlot.toString()), player.getUniqueID());
        }
    }

    @Override
    protected void onAssemblyAttempted() {
        world.setBlockState(pos, world.getBlockState(pos).with(PORT_DIRECTION_ENUM_PROPERTY, direction));
    }

    LazyOptional<IItemHandler> itemStackHandler = LazyOptional.of(()-> this);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return itemStackHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    ItemStack inSlot = new ItemStack(()-> YelloriumIngot.INSTANCE, 0);
    ItemStack outSlot = new ItemStack(()-> CyaniteIngot.INSTANCE, 0);

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
        stack = stack.copy();
        if(stack.getItem() == YelloriumIngot.INSTANCE){
            int canAccept = inSlot.getMaxStackSize() - inSlot.getCount();
            if(canAccept > 0){
                ItemStack accepted = stack.split(canAccept);
                if(!simulate) {
                    inSlot.setCount(inSlot.getCount() + accepted.getCount());
                }
            }
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
    
    public long refuel(long maxIngots) {
        return inSlot.split((int)maxIngots).getCount();
    }
}
