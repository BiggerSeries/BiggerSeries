package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorBaseTile extends RectangularMultiblockTile {
    
    @Nullable
    ReactorMultiblockController reactor() {
        if(controller instanceof ReactorMultiblockController) {
            return (ReactorMultiblockController) controller;
        }
        return null;
    }
    
    public ReactorBaseTile(@Nonnull TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    @Nonnull
    public final MultiblockController createController() {
        if(world == null){
            throw new IllegalStateException("Attempt to create controller with null world");
        }
        return new ReactorMultiblockController(world);
    }
    
    public void runRequest(String requestName, Object requestData) {
        ReactorMultiblockController reactor = reactor();
        if(reactor != null){
            reactor.runRequest(requestName, requestData);
        }
    }
    
    public boolean isCurrentController(@Nullable ReactorMultiblockController reactorMultiblockController) {
        return reactor() == reactorMultiblockController;
    }
}
