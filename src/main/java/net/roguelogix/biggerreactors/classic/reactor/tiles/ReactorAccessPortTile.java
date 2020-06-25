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
    }

    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        return NBT;
    }

    @Override
    public void onActivated(PlayerEntity player) {
        player.sendMessage(new StringTextComponent(direction.toString()));
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

    ItemStack inSlot;
    ItemStack outSlot;

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
}
