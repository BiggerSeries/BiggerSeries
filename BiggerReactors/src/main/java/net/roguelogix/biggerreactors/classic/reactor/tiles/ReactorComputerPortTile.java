package net.roguelogix.biggerreactors.classic.reactor.tiles;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.roguelogix.biggerreactors.classic.reactor.deps.ReactorPeripheral;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_computer_port")
public class ReactorComputerPortTile extends ReactorBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorComputerPortTile() {
        super(TYPE);
    }
    
    @CapabilityInject(IPeripheral.class)
    public static Capability<IPeripheral> CAPABILITY_PERIPHERAL = null;
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if(controller != null) {
                return ReactorPeripheral.create(reactor()).cast();
            }
        }
        return LazyOptional.empty();
    }
    
    
}
