package net.roguelogix.biggerreactors.classic.reactor.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorCoolantPort;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

import javax.annotation.Nonnull;

@RegisterContainer(name = "reactor_coolant_port")
public class ReactorCoolantPortContainer extends Container implements GuiSync.IGUIPacketProvider {

    @RegisterContainer.Instance
    public static ContainerType<ReactorCoolantPortContainer> INSTANCE;

    private PlayerEntity player;
    private ReactorCoolantPortTile tileEntity;

    public ReactorCoolantPortContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
        super(INSTANCE, windowId);
        this.player = player;
        this.tileEntity = (ReactorCoolantPortTile) player.world.getTileEntity(blockPos);
        this.getGuiPacket();
    }

    /**
     * @return The current state of the machine.
     */
    @Override
    public GuiSync.IGUIPacket getGuiPacket() {
        return this.tileEntity.coolantPortState;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        assert tileEntity.getWorld() != null;
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
                player, ReactorCoolantPort.INSTANCE);
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
