package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

public class ReactorBaseTile extends RectangularMultiblockTile {
    
    ReactorMultiblockController reactor() {
        return (ReactorMultiblockController) controller;
    }
    
    public ReactorBaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public final MultiblockController createController() {
        return new ReactorMultiblockController(world);
    }
    
    public void runRequest(String requestName, Object requestData) {
        if(this.controller != null){
            reactor().runRequest(requestName, requestData);
        }
    }
    
    public boolean isCurrentController(ReactorMultiblockController reactorMultiblockController) {
        return reactor() == reactorMultiblockController;
    }
}
