package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public enum RectangularMultiblockPositions implements IStringSerializable {
    DISASSEMBLED("disassembled"),
    CORNER("corner"),
    FRAME_X("frame_x"),
    FRAME_Y("frame_y"),
    FRAME_Z("frame_z"),
    FACE("face"),
    INTERIOR("interior");
    
    private final String name;
    
    RectangularMultiblockPositions(@Nonnull String name) {
        this.name = name;
    }
    
    @Override
    @Nonnull
    public String getString() {
        return toString().toLowerCase();
    }
    
    @Override
    @Nonnull
    public String toString() {
        return name;
    }
    
    public static final EnumProperty<RectangularMultiblockPositions> POSITIONS_ENUM_PROPERTY = EnumProperty.create("direction", RectangularMultiblockPositions.class);
    
}
