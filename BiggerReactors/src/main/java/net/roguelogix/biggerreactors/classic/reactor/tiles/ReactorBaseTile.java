package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorBaseTile extends RectangularMultiblockTile<ReactorMultiblockController, ReactorBaseTile> {
    
    public ReactorBaseTile(@Nonnull TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    @Nonnull
    public final ReactorMultiblockController createController() {
        if (world == null) {
            throw new IllegalStateException("Attempt to create controller with null world");
        }
        return new ReactorMultiblockController(world);
    }
    
    public void runRequest(String requestName, Object requestData) {
        if (this.controller != null) {
            controller.runRequest(requestName, requestData);
        }
    }
    
    public boolean isCurrentController(@Nullable ReactorMultiblockController reactorMultiblockController) {
        return controller == reactorMultiblockController;
    }
}
