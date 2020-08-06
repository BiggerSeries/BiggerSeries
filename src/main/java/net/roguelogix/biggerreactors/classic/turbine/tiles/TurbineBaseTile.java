package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

public class TurbineBaseTile extends RectangularMultiblockTile {
    TurbineMultiblockController turbine() {
        return (TurbineMultiblockController) controller;
    }
    
    public TurbineBaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public final MultiblockController createController() {
        return new TurbineMultiblockController(world);
    }
    
    public void runRequest(String requestName, Object requestData) {
        if(this.controller != null){
            turbine().runRequest(requestName, requestData);
        }
    }
    
    public boolean isCurrentController(TurbineMultiblockController turbineMultiblockController) {
        return turbine() == turbineMultiblockController;
    }
}
