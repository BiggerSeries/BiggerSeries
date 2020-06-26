package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.biggerreactors.items.ingots.BlutoniumIngot;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "cyanite_reprocessor")
public class CyaniteReprocessorTile extends TileEntity implements ISidedInventory,
    ITickableTileEntity {

    @RegisterTileEntity.Type
    public static TileEntityType<?> INSTANCE;

    private final LazyOptional<IItemHandler> itemHandlerCapability = LazyOptional
        .of(() -> this.itemHandler);
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
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

    private int workTime;
    private int workTimeTotal;

    private static final int[] SLOT_UP = new int[]{0};
    private static final int[] SLOT_DOWN = new int[]{2, 1};
    private static final int[] SLOT_SIDE = new int[]{1};

    public CyaniteReprocessorTile() {
        super(INSTANCE);
    }


    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        if (world.isBlockPresent(this.getPos())) {
            if (world.getTileEntity(this.getPos()) instanceof CyaniteReprocessorTile) {
                world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(),
                    BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("workTime", IntNBT.valueOf(this.workTime));
        tag.put("workTimeTotal", IntNBT.valueOf(this.workTimeTotal));
        return new SUpdateTileEntityPacket(this.getPos(), -1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager network, SUpdateTileEntityPacket packet) {
        CompoundNBT tag = packet.getNbtCompound();
        itemHandler.deserializeNBT(tag);
        this.workTime = tag.getInt("workTime");
        this.workTimeTotal = tag.getInt("workTimeTotal");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
        @Nullable Direction side) {
        if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return this.itemHandlerCapability.cast();
        }

        return super.getCapability(capability, side);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOT_DOWN;
        } else {
            return side == Direction.UP ? SLOT_UP : SLOT_SIDE;
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.isItemValidForSlot(index, itemStack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack itemStack, Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            if (itemStack.getItem() == BlutoniumIngot.INSTANCE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSizeInventory() {
        return this.itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int index = 0; index < this.itemHandler.getSlots(); ++index) {
            if (!this.itemHandler.getStackInSlot(index).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.itemHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.itemHandler.getStackInSlot(index).split(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = itemHandler.getStackInSlot(index).copy();
        itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack itemStack) {

        ItemStack oldItemStack = this.itemHandler.getStackInSlot(index);
        boolean flag = !itemStack.isEmpty() && itemStack.isItemEqual(oldItemStack) && ItemStack
            .areItemStackTagsEqual(itemStack, oldItemStack);
        this.itemHandler.setStackInSlot(index, itemStack);
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
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        for(int index = 0; index < this.itemHandler.getSlots(); ++index) {
            this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        }
    }
}