package net.roguelogix.phosphophyllite.blocks.whiteholes;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "item_white_hole")
public class ItemWhiteHoleTile extends TileEntity implements IItemHandler, ITickableTileEntity {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ItemWhiteHoleTile() {
        super(TYPE);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
    
    Item item = null;
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    @Override
    public void read(BlockState state, CompoundNBT compound) {
        if (compound.contains("item")) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("item")));
        }
        super.read(state, compound);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (item != null) {
            compound.putString("item", item.getRegistryName().toString());
        }
        return super.write(compound);
    }
    
    @Override
    public void tick() {
        if (item != null) {
            assert world != null;
            for (Direction direction : Direction.values()) {
                TileEntity te = world.getTileEntity(pos.offset(direction));
                if (te != null) {
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(c -> {
                        for (int i = 0; i < c.getSlots(); i++) {
                            c.insertItem(i, new ItemStack(item, item.getMaxStackSize()), false);
                        }
                    });
                }
            }
        }
    }
    
    @Override
    public int getSlots() {
        return 128;
    }
    
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return new ItemStack(item, item.getMaxStackSize());
    }
    
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }
    
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return new ItemStack(item, amount);
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }
}
