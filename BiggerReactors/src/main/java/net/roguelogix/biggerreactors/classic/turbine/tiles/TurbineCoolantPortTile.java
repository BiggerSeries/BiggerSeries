package net.roguelogix.biggerreactors.classic.turbine.tiles;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorCoolantPort;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorCoolantPortState;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineCoolantPortContainer;
import net.roguelogix.biggerreactors.classic.turbine.deps.TurbineGasHandler;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineCoolantPortState;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort.PortDirection.*;

@RegisterTileEntity(name = "turbine_coolant_port")
public class TurbineCoolantPortTile extends TurbineBaseTile implements IFluidHandler, INamedContainerProvider, IHasUpdatableState<TurbineCoolantPortState> {
    
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
    public final TurbineCoolantPortState coolantPortState = new TurbineCoolantPortState(this);
    
    @SuppressWarnings("DuplicatedCode")
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            waterOutputDirection = null;
        } else if (pos.getX() == controller.minCoord().x()) {
            waterOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxCoord().x()) {
            waterOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minCoord().y()) {
            waterOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxCoord().y()) {
            waterOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minCoord().z()) {
            waterOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxCoord().z()) {
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

    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert world != null;
        if (world.getBlockState(pos).get(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY) != RectangularMultiblockPositions.DISASSEMBLED) {
            if (!world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void runRequest(String requestName, Object requestData) {
        TurbineMultiblockController turbine = turbine();
        if(turbine == null){
            return;
        }

        if (requestName.equals("setInputState")) {
            boolean state = (Boolean) requestData;
            this.setDirection(state ? INLET : OUTLET);
            world.setBlockState(this.pos, this.getBlockState().with(PORT_DIRECTION_ENUM_PROPERTY, direction));

        }
        super.runRequest(requestName, requestData);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(TurbineCoolantPort.INSTANCE.getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new TurbineCoolantPortContainer(windowId, this.pos, player);
    }

    @Nullable
    @Override
    public TurbineCoolantPortState getState() {
        this.updateState();
        return this.coolantPortState;
    }

    @Override
    public void updateState() {
        coolantPortState.inputState = (this.direction == INLET);
    }
}
