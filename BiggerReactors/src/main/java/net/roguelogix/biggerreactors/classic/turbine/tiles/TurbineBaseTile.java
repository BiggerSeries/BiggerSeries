package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;

import javax.annotation.Nonnull;

public class TurbineBaseTile extends RectangularMultiblockTile<TurbineMultiblockController, TurbineBaseTile> {
    public TurbineBaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Nonnull
    @Override
    public final TurbineMultiblockController createController() {
        return new TurbineMultiblockController(world);
    }
    
    public void runRequest(String requestName, Object requestData) {
        if (this.controller != null) {
            controller.runRequest(requestName, requestData);
        }
    }
    
    public boolean isCurrentController(TurbineMultiblockController turbineMultiblockController) {
        return controller == turbineMultiblockController;
    }
}
