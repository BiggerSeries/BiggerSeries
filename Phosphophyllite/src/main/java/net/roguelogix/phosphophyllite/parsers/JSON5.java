package net.roguelogix.phosphophyllite.parsers;

import net.roguelogix.phosphophyllite.repack.tnjson.TnJson;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSON5 {
    public static Element parseString(String string) {
        return parseObject(TnJson.parse(string), null);
    }
    
    private static Element parseObject(Object obj, @Nullable String name) {
        if (obj instanceof Map) {
            final ArrayList<Element> subElements = new ArrayList<>();
            ((Map<?, ?>) obj).forEach((str, obj1) -> {
                subElements.add(parseObject(obj1, (String) str));
            });
            return new Element(Element.Type.Section, null, name, subElements.toArray());
        } else if (obj instanceof List) {
            final ArrayList<Element> subElements = new ArrayList<>();
            ((List<?>) obj).forEach(e -> {
                subElements.add(parseObject(e, null));
            });
            return new Element(Element.Type.Array, null, name, subElements.toArray());
        } else if (obj instanceof String) {
            return new Element(Element.Type.String, null, name, obj);
        } else {
            return new Element(Element.Type.Value, null, name, obj.toString());
        }
    }
    
    public static String parseElement(Element element) {
        StringBuilder builder = new StringBuilder();
        parseElement(new Element(element.type, element.comment, null, element.value), 0, builder, false);
        return builder.substring(1, builder.length() - 2);
    }
    
    private static void newLine(int indentLevel, StringBuilder builder) {
        builder.append("\n");
        for (int i = 0; i < indentLevel; i++) {
            builder.append("    ");
        }
    }
    
    private static void parseElement(Element element, int indentLevel, StringBuilder builder, boolean omitComments) {
        if (!omitComments && element.comment != null) {
            if (!element.comment.isEmpty()) {
                String[] commentLines = element.comment.split("\n");
                newLine(indentLevel, builder);
                builder.append("/* ");
                for (String commentLine : commentLines) {
                    newLine(indentLevel, builder);
                    builder.append(" * ");
                    builder.append(commentLine);
                }
                newLine(indentLevel, builder);
                builder.append(" */");
            }
        }
        newLine(indentLevel, builder);
        if (element.name != null && !element.name.isEmpty()) {
            builder.append(element.name);
            builder.append(": ");
        }
        switch (element.type) {
            case String: {
                String value = element.asString();
                value = value.replace("\n", "\\n");
                value = value.replace("\r", "\\r");
                value = value.replace("\"", "\\\"");
                builder.append("\"");
                builder.append(value);
                builder.append("\"");
                break;
            }
            case Value: {
                builder.append(element.asString());
                break;
            }
            case Array: {
                Element[] elements = element.asArray();
                builder.append("[");
                for (int i = 0; i < elements.length; i++) {
                    parseElement(elements[i], indentLevel + 1, builder, i > 0);
                }
                newLine(indentLevel, builder);
                builder.append("]");
                break;
            }
            case Section: {
                Element[] elements = element.asArray();
                builder.append("{");
                for (Element value : elements) {
                    parseElement(value, indentLevel + 1, builder, omitComments);
                }
                if(builder.charAt(builder.length() - 1) == ',') {
                    builder.setCharAt(builder.length() - 1, ' ');
                }
                newLine(indentLevel, builder);
                builder.append("}");
                break;
            }
        }
        builder.append(',');
        builder.append('\n');
    }
}
