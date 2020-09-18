package net.roguelogix.phosphophyllite.blocks.whiteholes;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT;

@RegisterTileEntity(name = "fluid_white_hole")
public class FluidWhiteHoleTile extends TileEntity implements IFluidHandler, ITickableTileEntity {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public FluidWhiteHoleTile() {
        super(TYPE);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
    
    FluidStack fluidStack = FluidStack.EMPTY;
    
    @Override
    public int getTanks() {
        return 1;
    }
    
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluidStack;
    }
    
    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }
    
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }
    
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.getFluid() == fluidStack.getFluid()) {
            return resource.copy();
        }
        return FluidStack.EMPTY;
    }
    
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return new FluidStack(fluidStack.getFluid(), maxDrain);
    }
    
    public void setFluid(Fluid fluid) {
        fluidStack = new FluidStack(fluid, Integer.MAX_VALUE);
    }
    
    @Override
    public void read(BlockState state, CompoundNBT compound) {
        fluidStack = loadFluidStackFromNBT(compound.getCompound("fluidstack"));
        super.read(state, compound);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("fluidstack", fluidStack.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }
    
    @Override
    public void tick() {
        assert world != null;
        for (Direction direction : Direction.values()) {
            TileEntity te = world.getTileEntity(pos.offset(direction));
            if (te != null) {
                te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(c -> c.fill(fluidStack.copy(), FluidAction.EXECUTE));
            }
        }
    }
}
