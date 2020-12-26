package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;

public abstract class RectangularMultiblockTile<ControllerType extends RectangularMultiblockController<ControllerType, TileType>, TileType extends RectangularMultiblockTile<ControllerType, TileType>> extends MultiblockTile<ControllerType, TileType> {
    
    public RectangularMultiblockTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
}
