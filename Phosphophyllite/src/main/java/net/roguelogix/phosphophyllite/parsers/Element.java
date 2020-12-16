package net.roguelogix.phosphophyllite.parsers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Element {
    
    public Element(@Nonnull Type type, @Nullable String comment, @Nullable String name, @Nonnull Object value) {
        this.type = type;
        this.comment = comment;
        this.name = name;
        this.value = value;
    }
    
    public enum Type {
        String,
        Number,
        Boolean,
        Array,
        Section
    }
    
    @Nonnull
    public final Type type;
    
    @Nullable
    public final String comment;
    
    @Nullable
    public final String name;
    
    @Nonnull
    public final Object value;
    
    @Nonnull
    public Element[] asArray() {
        Element[] newArray = new Element[((Object[]) value).length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = (Element) ((Object[]) value)[i];
        }
        return newArray;
    }
    
    @Nonnull
    public String asString() {
        return (String) value;
    }
    
    public long asLong() {
        return Long.parseLong(asString());
    }
    
    public double asDouble() {
        return Double.parseDouble(asString());
    }
}
