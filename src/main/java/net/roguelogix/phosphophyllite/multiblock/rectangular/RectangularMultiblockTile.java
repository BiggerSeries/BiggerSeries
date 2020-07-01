package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;

import static net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions.POSITIONS_MODEL_PROPERTY;

public abstract class RectangularMultiblockTile extends MultiblockTile {
    
    public RectangularMultiblockTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    RectangularMultiblockPositions position = RectangularMultiblockPositions.DISASSEMBLED;
    
    @Override
    public CompoundNBT getBakedModelState() {
        CompoundNBT nbt = super.getBakedModelState();
        nbt.putString("position", position.toString());
        return nbt;
    }
    
    @Override
    public void updateBakedModelState(CompoundNBT nbt) {
        if (nbt.contains("position")) {
            position = RectangularMultiblockPositions.valueOf(nbt.getString("position").toUpperCase());
            super.updateBakedModelState(nbt);
        }
    }
    
    @Override
    protected void appendModelData(ModelDataMap.Builder builder) {
        super.appendModelData(builder);
        builder.withInitial(POSITIONS_MODEL_PROPERTY, position);
    }
}
