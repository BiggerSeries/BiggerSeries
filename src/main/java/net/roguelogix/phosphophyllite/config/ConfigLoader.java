package net.roguelogix.phosphophyllite.config;

import net.roguelogix.phosphophyllite.parsers.Element;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigLoader {
    
    public static Element buildCurrentElementTree(Class<?> configClass) {
        try {
            return buildCurrentElementTree(configClass, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
    private static Element buildCurrentElementTree(Class<?> configClass, Object curentobject) throws IllegalAccessException {
        if (!configClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            // these get ignored
            return null;
        }
        
        ArrayList<Element> subElements = new ArrayList<>();
        
        Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(PhosphophylliteConfig.Value.class)) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers()) != (curentobject == null)) {
                continue;
            }
            field.setAccessible(true);
            
            Object fieldObject = field.get(curentobject);
            
            PhosphophylliteConfig.Value fieldAnnotation = field.getAnnotation(PhosphophylliteConfig.Value.class);
            StringBuilder comment = new StringBuilder(fieldAnnotation.comment());
            if (fieldAnnotation.min() != Long.MIN_VALUE) {
                if (comment.length() != 0) {
                    comment.append('\n');
                }
                comment.append("Min: ").append(fieldAnnotation.min());
            }
            if (fieldAnnotation.max() != Long.MAX_VALUE) {
                if (comment.length() != 0) {
                    comment.append('\n');
                }
                comment.append("Max: ").append(fieldAnnotation.max());
            }
            
            if (fieldAnnotation.commentDefaultValue() && !fieldObject.getClass().isArray() && !fieldObject.getClass().isAnnotationPresent(PhosphophylliteConfig.class)) {
                if (comment.length() != 0) {
                    comment.append('\n');
                }
                comment.append("Default: ").append(fieldObject.toString());
            }
            
            if (fieldObject.getClass().isEnum()) {
                String[] allowedValues = fieldAnnotation.allowedValues();
                if (allowedValues.length == 0) {
                    Field[] enumFields = fieldObject.getClass().getFields();
                    allowedValues = new String[enumFields.length];
                    for (int i = 0; i < enumFields.length; i++) {
                        allowedValues[i] = enumFields[i].getName();
                    }
                }
                if (comment.length() != 0) {
                    comment.append('\n');
                }
                comment.append("Allowed Values: ");
                for (String allowedValue : allowedValues) {
                    comment.append(allowedValue);
                    comment.append(", ");
                }
            }
            subElements.add(buildElementForObject(fieldObject, field.getName(), comment.length() == 0 ? null : comment.toString()));
        }
        
        if (curentobject == null) {
            Class<?>[] classes = configClass.getDeclaredClasses();
            for (Class<?> subclass : classes) {
                subElements.add(buildCurrentElementTree(subclass, null));
            }
        }
        
        subElements.removeIf(Objects::isNull);
        
//        if (subElements.isEmpty()) {
//            return null;
//        }
        
        PhosphophylliteConfig classAnnotation = configClass.getAnnotation(PhosphophylliteConfig.class);
        String comment = classAnnotation.comment();
        comment = comment.equals("") ? null : comment;
        String name = classAnnotation.name();
        name = name.equals("") ? configClass.getSimpleName() : name;
        return new Element(Element.Type.Section, comment, name, subElements.toArray());
    }
    
    private static Element buildElementForObject(Object object, String name, String comment) throws IllegalAccessException {
        Class<?> objectClass = object.getClass();
        if (objectClass == Double.class) {
            double value = (double) object;
            return new Element(Element.Type.Value, comment, name, Double.toString(value));
        } else if (objectClass == Float.class) {
            float value = (float) object;
            return new Element(Element.Type.Value, comment, name, Float.toString(value));
        } else if (objectClass == Long.class) {
            long value = (long) object;
            return new Element(Element.Type.Value, comment, name, Long.toString(value));
        } else if (objectClass == Integer.class) {
            int value = (int) object;
            return new Element(Element.Type.Value, comment, name, Integer.toString(value));
        } else if (objectClass == Short.class) {
            short value = (short) object;
            return new Element(Element.Type.Value, comment, name, Short.toString(value));
        } else if (objectClass == Character.class) {
            char value = (char) object;
            return new Element(Element.Type.Value, comment, name, Character.toString(value));
        } else if (objectClass == Byte.class) {
            byte value = (byte) object;
            return new Element(Element.Type.Value, comment, name, Byte.toString(value));
        } else if (objectClass == Boolean.class) {
            boolean value = (boolean) object;
            return new Element(Element.Type.Value, comment, name, Boolean.toString(value));
        } else if (objectClass == String.class) {
            return new Element(Element.Type.String, comment, name, object);
        } else if (objectClass.isEnum()) {
            return new Element(Element.Type.String, comment, name, object.toString());
        } else if (objectClass.isArray()) {
            Object[] arrayValues = (Object[]) object;
            ArrayList<Element> arrayElements = new ArrayList<>();
            for (Object arrayValue : arrayValues) {
                Element e = buildElementForObject(arrayValue, null, null);
                if (e != null) {
                    arrayElements.add(new Element(e.type, null, null, e.value));
                }
            }
            return new Element(Element.Type.Array, comment, name, arrayElements.toArray());
            
        } else if (objectClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            return buildCurrentElementTree(objectClass, object);
        }
        throw new RuntimeException();
    }
    
    public static void writeElementTree(Element tree, Class<?> configClass) {
        try {
            writeElementTree(tree, configClass, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
    
    static Field modifiersField;
    
    static {
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        modifiersField.setAccessible(true);
    }
    
    private static void writeElementTree(Element tree, Class<?> configClass, Object currentObject) throws IllegalAccessException {
        if (!configClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            // these get ignored
            return;
        }
        
        if (tree.type != Element.Type.Section) {
            throw new IllegalArgumentException();
        }
        
        Element[] elements = tree.asArray();
        for (Element element : elements) {
            Field field;
            try {
                field = configClass.getDeclaredField(element.name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers()) != (currentObject == null)) {
                continue;
            }
            field.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL); // yes i know this is evil
            Object newValue = buildConfigObject(element, field.getType());
            field.set(currentObject, newValue);
        }
        for (Element element : elements) {
            for (Class<?> declaredClass : configClass.getDeclaredClasses()) {
                if (declaredClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
                    PhosphophylliteConfig annotation = declaredClass.getAnnotation(PhosphophylliteConfig.class);
                    String name = annotation.name();
                    name = name.equals("") ? declaredClass.getSimpleName() : name;
                    if (element.name.equals(name)) {
                        writeElementTree(element, declaredClass, null);
                        break;
                    }
                }
            }
        }
    }
    
    private static Object buildConfigObject(Element tree, Class<?> objectClass) throws IllegalAccessException {
        if (objectClass == Double.class || objectClass == double.class) {
            return tree.asDouble();
        } else if (objectClass == Float.class || objectClass == float.class) {
            return (float) tree.asDouble();
        } else if (objectClass == Long.class || objectClass == long.class) {
            return tree.asLong();
        } else if (objectClass == Integer.class || objectClass == int.class) {
            return (int) tree.asLong();
        } else if (objectClass == Short.class || objectClass == short.class) {
            return (short) tree.asLong();
        } else if (objectClass == Character.class || objectClass == char.class) {
            return tree.asString().charAt(0);
        } else if (objectClass == Byte.class || objectClass == byte.class) {
            return (byte) tree.asLong();
        } else if (objectClass == Boolean.class || objectClass == boolean.class) {
            return Boolean.valueOf(tree.asString());
        } else if (objectClass == String.class) {
            return tree.asString();
        } else if (objectClass.isEnum()) {
            for (Object enumConstant : objectClass.getEnumConstants()) {
                if (enumConstant.toString().toLowerCase().equals(tree.value.toString().toLowerCase())) {
                    return enumConstant;
                }
            }
        } else if (objectClass.isArray()) {
            if (tree.type != Element.Type.Array) {
                throw new RuntimeException();
            }
            Class<?> elementClass = objectClass.getComponentType();
            Constructor<?> constructor;
            try {
                constructor = elementClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException();
            }
            constructor.setAccessible(true);
            
            Element[] elements = tree.asArray();
            
            Object[] objects = (Object[]) Array.newInstance(elementClass, elements.length);
            
            for (int i = 0; i < elements.length; i++) {
                try {
                    objects[i] = constructor.newInstance();
                } catch (InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
                writeElementTree(elements[i], elementClass, objects[i]);
            }
            
            return objects;
        } else if (objectClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            Constructor<?> constructor;
            try {
                constructor = objectClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException();
            }
            constructor.setAccessible(true);
            Object obj;
            try {
                obj = constructor.newInstance();
            } catch (InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
            writeElementTree(tree, objectClass, obj);
            return obj;
        }
        throw new RuntimeException();
    }
}
