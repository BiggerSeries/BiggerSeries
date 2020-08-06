package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort;
import net.roguelogix.biggerreactors.fluids.IrradiatedSteam;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort.PortDirection.*;

@RegisterTileEntity(name = "turbine_coolant_port")
public class TurbineCoolantPortTile extends TurbineBaseTile implements IFluidHandler {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineCoolantPortTile() {
        super(TYPE);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
    
    private final FluidStack water = new FluidStack(Fluids.WATER, 0);
    private final FluidStack steam = new FluidStack(IrradiatedSteam.INSTANCE, 0);
    
    @Override
    public int getTanks() {
        return 2;
    }
    
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank == 0) {
            return water;
        }
        if (tank == 1) {
            return steam;
        }
        return FluidStack.EMPTY;
    }
    
    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        if (tank == 1 && stack.getFluid() == Fluids.WATER) {
            return true;
        }
        return tank == 0 && stack.getFluid() == Fluids.WATER;
    }
    
    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (direction == OUTLET) {
            return 0;
        }
        if (resource.getFluid() != IrradiatedSteam.INSTANCE) {
            return 0;
        }
        if (controller != null) {
            return (int) turbine().addSteam(resource.getAmount(), action.simulate());
        }
        return 0;
    }
    
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.getFluid() == IrradiatedSteam.INSTANCE) {
            return drain(resource.getAmount(), action);
        }
        return FluidStack.EMPTY;
    }
    
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (direction == INLET) {
            return FluidStack.EMPTY;
        }
        water.setAmount((int) turbine().extractWater(maxDrain, action.simulate()));
        return water.copy();
    }
    
    public long pushSteam(long amount) {
        if (!connected || direction == INLET) {
            return 0;
        }
        steam.setAmount((int) amount);
        return steamOutput.orElse(EMPTY_TANK).fill(steam, IFluidHandler.FluidAction.EXECUTE);
    }
    
    
    private boolean connected = false;
    Direction steamOutputDirection = null;
    LazyOptional<IFluidHandler> steamOutput = null;
    FluidTank EMPTY_TANK = new FluidTank(0);
    private TurbineCoolantPort.PortDirection direction = INLET;
    
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            steamOutputDirection = null;
        }
        if (pos.getX() == controller.minX()) {
            steamOutputDirection = Direction.WEST;
            return;
        }
        if (pos.getX() == controller.maxX()) {
            steamOutputDirection = Direction.EAST;
            return;
        }
        if (pos.getY() == controller.minY()) {
            steamOutputDirection = Direction.DOWN;
            return;
        }
        if (pos.getY() == controller.maxY()) {
            steamOutputDirection = Direction.UP;
            return;
        }
        if (pos.getZ() == controller.minZ()) {
            steamOutputDirection = Direction.NORTH;
            return;
        }
        if (pos.getZ() == controller.maxZ()) {
            steamOutputDirection = Direction.SOUTH;
        }
        neighborChanged();
    }
    
    public void neighborChanged() {
        steamOutput = LazyOptional.empty();
        if (steamOutputDirection == null) {
            connected = false;
            return;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(steamOutputDirection));
        if (te == null) {
            connected = false;
            return;
        }
        steamOutput = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, steamOutputDirection.getOpposite());
        connected = false;
        IFluidHandler handler = steamOutput.orElse(EMPTY_TANK);
        for (int i = 0; i < handler.getTanks(); i++) {
            if (handler.isFluidValid(i, steam)) {
                connected = true;
                break;
            }
        }
        connected = connected && steamOutput.isPresent();
    }
    
    public void setDirection(TurbineCoolantPort.PortDirection direction) {
        this.direction = direction;
        this.markDirty();
    }
    
    @Override
    protected void readNBT(CompoundNBT compound) {
        if (compound.contains("direction")) {
            direction = TurbineCoolantPort.PortDirection.valueOf(compound.getString("direction"));
        }
    }
    
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putString("direction", String.valueOf(direction));
        return NBT;
    }
    
    @Override
    protected void onAssemblyAttempted() {
        assert world != null;
        world.setBlockState(pos, world.getBlockState(pos).with(PORT_DIRECTION_ENUM_PROPERTY, direction));
    }
}
