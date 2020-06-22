package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

@RegisterTileEntity(name = "cyanite_reprocessor")
public class CyaniteReprocessorTile extends LockableTileEntity implements ISidedInventory,
    IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    private static final int[] SLOT_UP = new int[] {0};
    private static final int[] SLOT_DOWN = new int[] {2, 1};
    private static final int[] SLOT_SIDE = new int[] {1};
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public CyaniteReprocessorTile() {
        super(TYPE);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return this.getDefaultName();
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if(side == Direction.DOWN) {
            return SLOT_DOWN;
        } else {
            return (side == Direction.UP) ? SLOT_UP : SLOT_SIDE;
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        // TODO: item extraction configuration
        return false;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        /*
      for(ItemStack itemstack : this.items) {
        if (!itemstack.isEmpty()) {
           return false;
        }
      }
      return true;
        */
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if(this.world.getTileEntity(this.pos) != this) { return false; }
        return player.getDistanceSq(player) <= 64.0D;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        for(ItemStack stack : this.inventory) {
            helper.accountStack(stack);
        }
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
    }

    @Nullable
    @Override
    public IRecipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void tick() {

    }
}
