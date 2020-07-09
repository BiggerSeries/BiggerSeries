package net.roguelogix.biggerreactors.classic.reactor;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.biggerreactors.classic.blocks.CyaniteReprocessor;
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

    this.populatePlayerInventory();
  }

  public int getEnergyStored() {
    return this.tileEntity.getEnergyStored();
  }

  public int getEnergyCapacity() {
    return this.tileEntity.getEnergyCapacity();
  }

  public int getCoreHeatStored() {
    return this.tileEntity.getCoreHeatStored();
  }

  public int getCoreHeatCapacity() {
    return this.tileEntity.getCoreHeatCapacity();
  }

  public int getCaseHeatStored() {
    return this.tileEntity.getCaseHeatStored();
  }

  public int getCaseHeatCapacity() {
    return this.tileEntity.getCaseHeatCapacity();
  }

  public int getFuelStored() {
    return this.tileEntity.getFuelStored();
  }

  public int getWasteStored() {
    return this.tileEntity.getWasteStored();
  }

  public int getFuelCapacity() {
    return this.tileEntity.getFuelCapacity();
  }

  private void populatePlayerInventory() {
    int guiOffset = 93;

    // Add player inventory;
    for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
      for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
        this.addSlot(new Slot(player.inventory, (columnIndex + rowIndex * 9 + 9),
            (8 + columnIndex * 18), (guiOffset + rowIndex * 18)));
      }
    }
    // Add player hotbar.
    for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
      this.addSlot(
          new Slot(player.inventory, columnIndex, (8 + columnIndex * 18), (guiOffset + 58)));
    }
  }

  @Override
  public boolean canInteractWith(@Nonnull PlayerEntity player) {
    assert tileEntity.getWorld() != null;
    return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
        player, CyaniteReprocessor.INSTANCE);
  }
}
