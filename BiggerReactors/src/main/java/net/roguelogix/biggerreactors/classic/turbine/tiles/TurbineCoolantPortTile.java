package net.roguelogix.biggerreactors.classic.turbine.tiles;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort;
import net.roguelogix.biggerreactors.classic.turbine.deps.TurbineGasHandler;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
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
    
    @CapabilityInject(IGasHandler.class)
    public static Capability<IGasHandler> GAS_HANDLER_CAPABILITY = null;
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        if (cap == GAS_HANDLER_CAPABILITY) {
            return TurbineGasHandler.create(this::turbine).cast();
        }
        return super.getCapability(cap, side);
    }
    
    private static final ResourceLocation steamTagLocation = new ResourceLocation("forge:steam");
    
    private final FluidStack water = new FluidStack(Fluids.WATER, 0);
    private final FluidStack steam = new FluidStack(FluidIrradiatedSteam.INSTANCE, 0);
    
    @Override
    public int getTanks() {
        return 2;
    }
    
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank == 0) {
            return steam;
        }
        if (tank == 1) {
            return water;
        }
        return FluidStack.EMPTY;
    }
    
    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        if (tank == 1 && stack.getRawFluid() == Fluids.WATER) {
            return true;
        }
        return tank == 0 && stack.getRawFluid().getTags().contains(steamTagLocation);
    }
    
    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (direction == OUTLET) {
            return 0;
        }
        if (!resource.getFluid().getTags().contains(steamTagLocation)) {
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
        if (resource.getFluid() == FluidIrradiatedSteam.INSTANCE) {
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
    
    public long pushWater(long amount) {
        if (!connected || direction == INLET) {
            return 0;
        }
        water.setAmount((int) amount);
        return waterOutput.orElse(EMPTY_TANK).fill(water, IFluidHandler.FluidAction.EXECUTE);
    }
    
    
    private boolean connected = false;
    Direction waterOutputDirection = null;
    LazyOptional<IFluidHandler> waterOutput = null;
    FluidTank EMPTY_TANK = new FluidTank(0);
    private TurbineCoolantPort.PortDirection direction = INLET;
    
    @SuppressWarnings("DuplicatedCode")
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            waterOutputDirection = null;
        } else if (pos.getX() == controller.minX()) {
            waterOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxX()) {
            waterOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minY()) {
            waterOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxY()) {
            waterOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minZ()) {
            waterOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxZ()) {
            waterOutputDirection = Direction.SOUTH;
        }
        neighborChanged();
    }
    
    @SuppressWarnings("DuplicatedCode")
    public void neighborChanged() {
        waterOutput = LazyOptional.empty();
        if (waterOutputDirection == null) {
            connected = false;
            return;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(waterOutputDirection));
        if (te == null) {
            connected = false;
            return;
        }
        waterOutput = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, waterOutputDirection.getOpposite());
        connected = false;
        IFluidHandler handler = waterOutput.orElse(EMPTY_TANK);
        for (int i = 0; i < handler.getTanks(); i++) {
            if (handler.isFluidValid(i, water)) {
                connected = true;
                break;
            }
        }
        connected = connected && waterOutput.isPresent();
    }
    
    public void setDirection(TurbineCoolantPort.PortDirection direction) {
        this.direction = direction;
        this.markDirty();
    }
    
    @Override
    protected void readNBT(@Nonnull CompoundNBT compound) {
        if (compound.contains("direction")) {
            direction = TurbineCoolantPort.PortDirection.valueOf(compound.getString("direction"));
        }
    }
    
    @Nonnull
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
