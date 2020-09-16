package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.data.ModelProperty;

public enum RectangularMultiblockPositions implements IStringSerializable {
    DISASSEMBLED("disassembled"),
    CORNER("corner"),
    FRAME_X("frame_x"),
    FRAME_Y("frame_y"),
    FRAME_Z("frame_z"),
    FACE("face"),
    INTERIOR("interior");
    
    private final String name;
    
    RectangularMultiblockPositions(String name) {
        this.name = name;
    }
    
    @Override
    public String getString() {
        return toString().toLowerCase();
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static final EnumProperty<RectangularMultiblockPositions> POSITIONS_ENUM_PROPERTY = EnumProperty.create("direction", RectangularMultiblockPositions.class);
    public static final ModelProperty<RectangularMultiblockPositions> POSITIONS_MODEL_PROPERTY = new ModelProperty<>();
    
}
