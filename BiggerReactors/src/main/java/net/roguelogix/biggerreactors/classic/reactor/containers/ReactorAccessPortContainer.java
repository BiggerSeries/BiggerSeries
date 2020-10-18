package net.roguelogix.biggerreactors.classic.reactor.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorAccessPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

import javax.annotation.Nonnull;

@RegisterContainer(name = "reactor_access_port")
public class ReactorAccessPortContainer extends Container implements GuiSync.IGUIPacketProvider {

    @RegisterContainer.Instance
    public static ContainerType<ReactorAccessPortContainer> INSTANCE;

    private PlayerEntity player;
    private ReactorAccessPortTile tileEntity;

    public ReactorAccessPortContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
        super(INSTANCE, windowId);
        this.player = player;
        this.tileEntity = (ReactorAccessPortTile) player.world.getTileEntity(blockPos);
        this.getGuiPacket();
    }

    /**
     * @return The current state of the machine.
     */
    @Override
    public GuiSync.IGUIPacket getGuiPacket() {
        return this.tileEntity.accessPortState;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        assert tileEntity.getWorld() != null;
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
                player, ReactorAccessPort.INSTANCE);
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
