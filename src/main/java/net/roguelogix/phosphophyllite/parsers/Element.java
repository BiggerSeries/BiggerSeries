package net.roguelogix.phosphophyllite.parsers;

public class Element {
    
    public Element(Type type, String comment, String name, Object value) {
        this.type = type;
        this.comment = comment;
        this.name = name;
        this.value = value;
    }
    
    public enum Type {
        String,
        Value,
        Array,
        Section
    }
    
    public final Type type;
    
    public final String comment;
    
    public final String name;
    
    public final Object value;
    
    public Element[] asArray() {
        Element[] newArray = new Element[((Object[]) value).length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = (Element) ((Object[]) value)[i];
        }
        return newArray;
    }
    
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
