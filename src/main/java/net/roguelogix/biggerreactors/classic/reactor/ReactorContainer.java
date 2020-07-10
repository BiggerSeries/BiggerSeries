package net.roguelogix.biggerreactors.classic.reactor;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorTerminal;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorTerminalTile;
import net.roguelogix.phosphophyllite.registry.RegisterContainer;

@RegisterContainer(name = "reactor_terminal")
public class ReactorContainer extends Container {

  @RegisterContainer.Instance
  public static ContainerType<ReactorContainer> INSTANCE;

  private PlayerEntity player;
  private ReactorTerminalTile tileEntity;

  public ReactorContainer(int windowId, BlockPos blockPos, PlayerEntity player) {
    super(INSTANCE, windowId);
    this.player = player;
    this.tileEntity = (ReactorTerminalTile) player.world.getTileEntity(blockPos);
  }

  public ReactorDatapack getReactorData() {
    return this.tileEntity.getReactorData();
  }

  @Override
  public boolean canInteractWith(@Nonnull PlayerEntity player) {
    assert tileEntity.getWorld() != null;
    return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
        player, ReactorTerminal.INSTANCE);
  }
}
