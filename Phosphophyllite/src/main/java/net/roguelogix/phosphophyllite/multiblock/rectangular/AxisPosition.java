package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum AxisPosition implements IStringSerializable {
    LOWER("lower"),
    MIDDLE("middle"),
    UPPER("upper");
    
    private final String name;
    
    AxisPosition(@Nonnull String name) {
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
    
    public static final EnumProperty<AxisPosition> X_AXIS_POSITION = EnumProperty.create("x_axis", AxisPosition.class);
    public static final EnumProperty<AxisPosition> Y_AXIS_POSITION = EnumProperty.create("y_axis", AxisPosition.class);
    public static final EnumProperty<AxisPosition> Z_AXIS_POSITION = EnumProperty.create("z_axis", AxisPosition.class);
    
}
