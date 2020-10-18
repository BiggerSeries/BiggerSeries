package net.roguelogix.biggerreactors.classic.turbine.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineCoolantPort;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineCoolantPortTile;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

import javax.annotation.Nonnull;

@RegisterContainer(name = "turbine_coolant_port")
public class TurbineCoolantPortContainer extends Container implements GuiSync.IGUIPacketProvider {

    @RegisterContainer.Instance
    public static ContainerType<TurbineCoolantPortContainer> INSTANCE;

    private PlayerEntity player;
    private TurbineCoolantPortTile tileEntity;

    public TurbineCoolantPortContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
        super(INSTANCE, windowId);
        this.player = player;
        this.tileEntity = (TurbineCoolantPortTile) player.world.getTileEntity(blockPos);
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
                player, TurbineCoolantPort.INSTANCE);
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
