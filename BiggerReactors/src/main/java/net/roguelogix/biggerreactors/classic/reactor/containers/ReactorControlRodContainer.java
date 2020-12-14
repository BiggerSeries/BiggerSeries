package net.roguelogix.biggerreactors.classic.reactor.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorControlRod;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorControlRodState;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorControlRodTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

import javax.annotation.Nonnull;

@RegisterContainer(name = "reactor_control_rod")
public class ReactorControlRodContainer extends Container implements GuiSync.IGUIPacketProvider {
    
    @RegisterContainer.Instance
    public static ContainerType<ReactorControlRodContainer> INSTANCE;
    
    private PlayerEntity player;
    private ReactorControlRodTile tileEntity;
    
    public ReactorControlRodContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
        super(INSTANCE, windowId);
        this.player = player;
        this.tileEntity = (ReactorControlRodTile) player.world.getTileEntity(blockPos);
        this.getGuiPacket();
    }
    
    /**
     * @return The current state of the machine.
     */
    @Override
    public GuiSync.IGUIPacket getGuiPacket() {
        return this.tileEntity.reactorControlRodState;
    }
    
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        assert tileEntity.getWorld() != null;
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
                player, ReactorControlRod.INSTANCE);
    }
    
    @Override
    public void executeRequest(String requestName, Object requestData) {
        assert tileEntity.getWorld() != null;
        if (tileEntity.getWorld().isRemote) {
            runRequest(requestName, requestData);
            return;
        }
        
        tileEntity.runRequest(requestName, requestData);
    }
}
