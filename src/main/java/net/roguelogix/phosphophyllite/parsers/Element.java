package net.roguelogix.phosphophyllite.parsers;

public class Element {

    public Element(Type type, String comment, String name, Object value) {
        this.type = type;
        this.comment = comment;
        this.name = name;
        this.value = value;
    }

    enum Type{
        Value,
        Array,
        Section
    }

    final Type type;

    final String comment;

    final String name;

    final Object value;

    Element[] asArray(){
        return (Element[]) value;
    }

    String asString() {
        return (String) value;
    }

    long asLong(){
        return Long.parseLong(asString());
   }

    double asDouble(){
        return Double.parseDouble(asString());
    }
}
