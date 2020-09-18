package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.turbine.blocks.TurbinePowerTap.ConnectionState.*;


@RegisterTileEntity(name = "turbine_power_tap")
public class TurbinePowerTapTile extends TurbineBaseTile implements IEnergyStorage {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbinePowerTapTile() {
        super(TYPE);
    }
    
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
    
    private boolean connected = false;
    Direction powerOutputDirection = null;
    
    private static final EnergyStorage ENERGY_ZERO = new EnergyStorage(0);
    
    private void setConnected(boolean newState) {
        if (newState != connected) {
            connected = newState;
            assert world != null;
            world.setBlockState(pos, getBlockState().with(CONNECTION_STATE_ENUM_PROPERTY, connected ? CONNECTED : DISCONNECTED));
        }
    }
    
    LazyOptional<IEnergyStorage> energyOutput = LazyOptional.empty();
    
    public void neighborChanged() {
        energyOutput = LazyOptional.empty();
        if (powerOutputDirection == null) {
            setConnected(false);
            return;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(powerOutputDirection));
        if (te == null) {
            setConnected(false);
            return;
        }
        energyOutput = te.getCapability(CapabilityEnergy.ENERGY, powerOutputDirection.getOpposite());
        setConnected(energyOutput.isPresent());
    }
    
    public long distributePower(long toDistribute, boolean simulate) {
        IEnergyStorage e = energyOutput.orElse(ENERGY_ZERO);
        if (e.canReceive()) {
            return e.receiveEnergy((int) toDistribute, simulate);
        }
        return 0;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }
    
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }
    
    @Override
    public int getEnergyStored() {
        return 0;
    }
    
    @Override
    public int getMaxEnergyStored() {
        return 0;
    }
    
    @Override
    public boolean canExtract() {
        return false;
    }
    
    @Override
    public boolean canReceive() {
        return false;
    }
    
    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            powerOutputDirection = null;
        } else if (pos.getX() == controller.minX()) {
            powerOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxX()) {
            powerOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minY()) {
            powerOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxY()) {
            powerOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minZ()) {
            powerOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxZ()) {
            powerOutputDirection = Direction.SOUTH;
        }
        neighborChanged();
    }
}
