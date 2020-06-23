package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public final class FluidTankInfo
{
    public final FluidStack fluid;
    public final int capacity;

    public FluidTankInfo(FluidStack fluid, int capacity)
    {
        this.fluid = fluid;
        this.capacity = capacity;
    }

    public FluidTankInfo(IFluidTank tank)
    {
        this.fluid = tank.getFluid();
        this.capacity = tank.getCapacity();
    }
}
