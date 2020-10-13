package net.roguelogix.biggerreactors.classic.turbine.tiles;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.roguelogix.biggerreactors.classic.turbine.deps.TurbinePeripheral;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "turbine_computer_port")
public class TurbineComputerPortTile extends TurbineBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineComputerPortTile() {
        super(TYPE);
    }
    
    @CapabilityInject(IPeripheral.class)
    public static Capability<IPeripheral> CAPABILITY_PERIPHERAL = null;
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CAPABILITY_PERIPHERAL) {
            return TurbinePeripheral.create(this::turbine).cast();
        }
        return LazyOptional.empty();
    }
    
}
