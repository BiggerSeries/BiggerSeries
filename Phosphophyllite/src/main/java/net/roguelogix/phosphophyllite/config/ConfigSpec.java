package net.roguelogix.phosphophyllite.config;

import mcp.MethodsReturnNonnullByDefault;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.parsers.Element;
import net.roguelogix.phosphophyllite.registry.OnModLoad;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.*;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigSpec {
    
    public static class DefinitionError extends RuntimeException {
        public DefinitionError(String message) {
            super(message);
        }
    }
    
    private static abstract class SpecNode {
        String comment;
        boolean advanced = false;
        boolean hidden = false;
    }
    
    private static class SpecClazzNode extends SpecNode {
        Class<?> clazz;
        Map<String, SpecClazzNode> clazzNodes;
        Map<String, SpecFieldNode> fieldNodes;
    }
    
    private static class SpecFieldNode extends SpecNode {
        Field field;
    }
    
    private static class SpecObjectNode extends SpecFieldNode {
        Class<?> clazz;
        Map<String, SpecFieldNode> subNodes;
    }
    
    private static class SpecMapNode extends SpecFieldNode {
        Class<?> elementClass;
        SpecFieldNode nodeType;
        Map<String, SpecFieldNode> defaultSubNodes;
    }
    
    private static class SpecListNode extends SpecFieldNode {
        Class<?> elementClass;
        SpecFieldNode subNodeType;
        List<SpecFieldNode> defaultSubNodes;
    }
    
    private static class SpecStringNode extends SpecFieldNode {
        String defaultString;
    }
    
    private static class SpecEnumNode extends SpecFieldNode {
        Class<?> enumClass;
        String defaultValue;
        String[] allowedValues;
    }
    
    private static class SpecNumberNode extends SpecFieldNode {
        boolean integral;
        boolean lowerInclusive;
        double lowerBound;
        boolean upperInclusive;
        double upperBound;
        double defaultValue;
    }
    
    private static class SpecBooleanNode extends SpecFieldNode {
        boolean defaultValue;
    }
    
    
    @OnModLoad
    private static void OML() {
        SpecClazzNode node = buildNodeForClazz(net.roguelogix.phosphophyllite.PhosphophylliteConfig.class);
    }
    
    final SpecClazzNode masterNode;
    
    ConfigSpec(Class<?> clazz) {
        masterNode = buildNodeForClazz(clazz);
    }
    
    Element generateElementTree(boolean enableAdvanced) {
        try {
            return generateElementForNode(masterNode, null, null, enableAdvanced);
        } catch (IllegalAccessException e) {
            ConfigManager.LOGGER.error("Unexpected error caught reading from config");
            ConfigManager.LOGGER.error(e.toString());
            throw new DefinitionError(e.getMessage());
        }
    }
    
    private Element generateElementForNode(SpecNode node, @Nullable Object object, @Nullable String name, boolean enableAdvanced) throws IllegalAccessException {
        if (node instanceof SpecClazzNode) {
            ArrayList<Element> subElements = new ArrayList<>();
            
            for (Map.Entry<String, SpecFieldNode> entry : ((SpecClazzNode) node).fieldNodes.entrySet()) {
                if ((enableAdvanced || !entry.getValue().advanced) && !entry.getValue().hidden) {
                    subElements.add(generateElementForNode(entry.getValue(), null, entry.getKey(), enableAdvanced));
                }
            }
            
            for (Map.Entry<String, SpecClazzNode> entry : ((SpecClazzNode) node).clazzNodes.entrySet()) {
                if ((enableAdvanced || !entry.getValue().advanced) && !entry.getValue().hidden) {
                    subElements.add(generateElementForNode(entry.getValue(), null, entry.getKey(), enableAdvanced));
                }
            }
            
            return new Element(Element.Type.Section, node.comment, name, subElements.toArray());
            
        } else if (node instanceof SpecObjectNode) {
            Object nodeObject = ((SpecObjectNode) node).field.get(object);
            
            ArrayList<Element> subElements = new ArrayList<>();
            
            for (Map.Entry<String, SpecFieldNode> entry : ((SpecObjectNode) node).subNodes.entrySet()) {
                if ((enableAdvanced || !entry.getValue().advanced) && !entry.getValue().hidden) {
                    subElements.add(generateElementForNode(entry.getValue(), nodeObject, entry.getKey(), enableAdvanced));
                }
            }
            
            return new Element(Element.Type.Section, node.comment, name, subElements.toArray());
            
        } else if (node instanceof SpecMapNode) {
            Object nodeObject = ((SpecFieldNode) node).field.get(object);
            assert nodeObject instanceof Map;
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) nodeObject;
            ArrayList<Element> subElements = new ArrayList<>();
            
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                subElements.add(generateElementForNode(((SpecMapNode) node).nodeType, entry.getValue(), entry.getKey(), enableAdvanced));
            }
            
            return new Element(Element.Type.Section, node.comment, name, subElements.toArray());
        } else if (node instanceof SpecListNode) {
            Object nodeObject = ((SpecFieldNode) node).field.get(object);
            SpecNode subNodeType = ((SpecListNode) node).subNodeType;
            ArrayList<Element> subElements = new ArrayList<>();
            
            if (nodeObject instanceof List) {
                List<?> list = (List<?>) nodeObject;
                for (Object o : list) {
                    subElements.add(generateElementForNode(subNodeType, o, null, enableAdvanced));
                }
            } else {
                assert nodeObject.getClass().isArray();
                int length = Array.getLength(nodeObject);
                for (int i = 0; i < length; i++) {
                    subElements.add(generateElementForNode(subNodeType, Array.get(nodeObject, i), null, enableAdvanced));
                }
            }
            
            return new Element(Element.Type.Array, node.comment, name, subElements.toArray());
        } else if (node instanceof SpecStringNode) {
            String val = ((SpecStringNode) node).field.get(object).toString();
            return new Element(Element.Type.String, node.comment, name, val);
        } else if (node instanceof SpecEnumNode) {
            String val = ((SpecEnumNode) node).field.get(object).toString();
            return new Element(Element.Type.String, node.comment, name, val);
        } else if (node instanceof SpecNumberNode) {
            Number num = (Number) ((SpecNumberNode) node).field.get(object);
            return new Element(Element.Type.Number, node.comment, name, String.valueOf(num.doubleValue()));
        } else if (node instanceof SpecBooleanNode) {
            Boolean bool = (Boolean) ((SpecBooleanNode) node).field.get(object);
            return new Element(Element.Type.Boolean, node.comment, name, bool.toString());
        }
        
        throw new DefinitionError("Attempting to generate element for unknown node type");
    }
    
    void writeElementTree(Element tree) {
        try {
            writeElementNode(tree, masterNode, null);
        } catch (IllegalAccessException e) {
            ConfigManager.LOGGER.error("Unexpected error caught reading from config");
            ConfigManager.LOGGER.error(e.toString());
            throw new DefinitionError(e.getMessage());
        }
    }
    
    private static void writeElementNode(Element element, SpecNode node, @Nullable Object object) throws IllegalAccessException {
        if (node instanceof SpecClazzNode) {
            if (element.type != Element.Type.Section) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a Class");
                return;
            }
            
            Element[] subElements = element.asArray();
            
            for (Element subElement : subElements) {
                SpecClazzNode clazzNode = ((SpecClazzNode) node).clazzNodes.get(subElement.name);
                SpecFieldNode fieldNode = ((SpecClazzNode) node).fieldNodes.get(subElement.name);
                if (clazzNode != null) {
                    writeElementNode(subElement, clazzNode, null);
                } else if (fieldNode != null) {
                    writeElementNode(subElement, fieldNode, null);
                } else {
                    Phosphophyllite.LOGGER.info("Unknown config option given: " + element.name);
                }
            }
            return;
        } else if (node instanceof SpecObjectNode) {
            if (element.type != Element.Type.Section) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to an Object");
                return;
            }
            
            Object nodeObject = createClassInstance(((SpecObjectNode) node).clazz);
            
            Element[] subElements = element.asArray();
            
            for (Element subElement : subElements) {
                SpecFieldNode subNode = ((SpecObjectNode) node).subNodes.get(subElement.name);
                if (subNode != null) {
                    writeElementNode(subElement, subNode, nodeObject);
                } else {
                    Phosphophyllite.LOGGER.info("Unknown config option given: " + element.name);
                }
            }
            
            ((SpecObjectNode) node).field.set(object, nodeObject);
            return;
        } else if (node instanceof SpecMapNode) {
            if (element.type != Element.Type.Section) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a Map");
                return;
            }
            
            @SuppressWarnings("rawtypes")
            Map map = (Map) createClassInstance(((SpecMapNode) node).field.getType());
            
            Element[] subElements = element.asArray();
            
            for (Element subElement : subElements) {
                Object newElementObject = createClassInstance(((SpecMapNode) node).elementClass);
                //noinspection unchecked
                map.put(subElement.name, newElementObject);
                writeElementNode(subElement, ((SpecMapNode) node).nodeType, newElementObject);
            }
            
            ((SpecMapNode) node).field.set(object, map);
            return;
        } else if (node instanceof SpecListNode) {
            if (element.type != Element.Type.Array) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to an Array");
                return;
            }
            
            Element[] subElements = element.asArray();
            
            if (((SpecListNode) node).field.getType().isArray()) {
                Object array = Array.newInstance(((SpecListNode) node).elementClass, subElements.length);
                for (int i = 0; i < subElements.length; i++) {
                    Object newElementObject = createClassInstance(((SpecListNode) node).elementClass);
                    
                    Element subElement = subElements[i];
                    Array.set(array, i, newElementObject);
                    
                    writeElementNode(subElement, ((SpecListNode) node).subNodeType, newElementObject);
                }
                
                ((SpecListNode) node).field.set(object, array);
            } else {
                
                @SuppressWarnings("rawtypes") List list = (List) createClassInstance(((SpecListNode) node).field.getType());
                
                for (Element subElement : subElements) {
                    Object newElementObject = createClassInstance(((SpecListNode) node).elementClass);
                    writeElementNode(subElement, ((SpecListNode) node).subNodeType, newElementObject);
                    //noinspection unchecked
                    list.add(newElementObject);
                }
                
                ((SpecListNode) node).field.set(object, list);
            }
            return;
        } else if (node instanceof SpecStringNode) {
            if (element.type != Element.Type.String && element.type != Element.Type.Number) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a String");
                return;
            }
            ((SpecStringNode) node).field.set(object, element.asString());
            return;
        } else if (node instanceof SpecEnumNode) {
            if (element.type != Element.Type.String) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a Enum");
                return;
            }
            //noinspection unchecked
            ((SpecEnumNode) node).field.set(object, Enum.valueOf((Class<? extends Enum>) ((SpecEnumNode) node).enumClass, element.asString()));
            return;
        } else if (node instanceof SpecNumberNode) {
            if (element.type != Element.Type.Number) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a Number");
                return;
            }
            double val = element.asDouble();
            if (isIntegral(((SpecNumberNode) node).field.getType())) {
                long realVal = Math.round(val);
                if (realVal < ((SpecNumberNode) node).lowerBound || realVal > ((SpecNumberNode) node).upperBound ||
                        (realVal <= ((SpecNumberNode) node).lowerBound && !(((SpecNumberNode) node).lowerInclusive)) ||
                        (realVal >= ((SpecNumberNode) node).upperBound && !((SpecNumberNode) node).upperInclusive)) {
                    ConfigManager.LOGGER.warn("Number value " + element.name + " given out of range value " + realVal + ". Valid range is " +
                            ((((SpecNumberNode) node).lowerInclusive ? "[" : "(" + ((((SpecNumberNode) node).lowerBound == Double.MIN_VALUE) ? "" : ((SpecNumberNode) node).lowerBound))) +
                            "," +
                            (((((SpecNumberNode) node).upperBound == Double.MAX_VALUE) ? "" : ((SpecNumberNode) node).upperBound) + (((SpecNumberNode) node).upperInclusive ? "]" : ")")) +
                            ". Clamping to range");
                    if (realVal <= ((SpecNumberNode) node).lowerBound) {
                        realVal = Math.round(((SpecNumberNode) node).lowerBound);
                        if (!((SpecNumberNode) node).lowerInclusive) {
                            realVal++;
                        }
                    } else if (realVal >= ((SpecNumberNode) node).upperBound) {
                        realVal = Math.round(((SpecNumberNode) node).upperBound);
                        if (!((SpecNumberNode) node).upperInclusive) {
                            realVal--;
                        }
                    }
                }
                val = realVal;
            } else {
                if (val < ((SpecNumberNode) node).lowerBound || val > ((SpecNumberNode) node).upperBound ||
                        (val <= ((SpecNumberNode) node).lowerBound && !(((SpecNumberNode) node).lowerInclusive)) ||
                        (val >= ((SpecNumberNode) node).upperBound && !((SpecNumberNode) node).upperInclusive)) {
                    ConfigManager.LOGGER.warn("Number value " + element.name + " given out of range value " + val + ". Valid range is " +
                            ((((SpecNumberNode) node).lowerInclusive ? "[" : "(" + ((((SpecNumberNode) node).lowerBound == Double.MIN_VALUE) ? "" : ((SpecNumberNode) node).lowerBound))) +
                            "," +
                            (((((SpecNumberNode) node).upperBound == Double.MAX_VALUE) ? "" : ((SpecNumberNode) node).upperBound) + (((SpecNumberNode) node).upperInclusive ? "]" : ")")) +
                            ". Clamping to range");
                    if (val <= ((SpecNumberNode) node).lowerBound) {
                        val = ((SpecNumberNode) node).lowerBound;
                        if (!((SpecNumberNode) node).lowerInclusive) {
                            val = Math.nextAfter(val, Double.POSITIVE_INFINITY);
                        }
                    } else if (val >= ((SpecNumberNode) node).upperBound) {
                        val = ((SpecNumberNode) node).upperBound;
                        if (!((SpecNumberNode) node).upperInclusive) {
                            val = Math.nextAfter(val, Double.NEGATIVE_INFINITY);
                        }
                    }
                }
            }
            setNumberField(((SpecNumberNode) node).field, object, val);
            return;
        } else if (node instanceof SpecBooleanNode) {
            if (element.type != Element.Type.String && element.type != Element.Type.Number && element.type != Element.Type.Boolean) {
                ConfigManager.LOGGER.info("Invalid config structure given");
                ConfigManager.LOGGER.info("Attempting to write " + element.type + " to a Boolean");
                return;
            }
            boolean newVal;
            if (element.type == Element.Type.String || element.type == Element.Type.Boolean) {
                String str = element.asString();
                newVal = Boolean.parseBoolean(str);
            } else {
                newVal = element.asDouble() != 0;
            }
            ((SpecBooleanNode) node).field.setBoolean(object, newVal);
            return;
        }
        
        ConfigManager.LOGGER.warn("Invalid config structure given");
        ConfigManager.LOGGER.warn("Attempting to write " + element.type + " to an unknown node type");
    }
    
    void writeDefaults() {
        try {
            defaultNode(masterNode, null);
        } catch (IllegalAccessException e) {
            Phosphophyllite.LOGGER.error("Error caught writing defaults to config");
            Phosphophyllite.LOGGER.error(e.toString());
        }
    }
    
    private static void defaultNode(SpecNode node, @Nullable Object object) throws IllegalAccessException {
        if (node instanceof SpecClazzNode) {
            for (Map.Entry<String, SpecClazzNode> entry : ((SpecClazzNode) node).clazzNodes.entrySet()) {
                defaultNode(entry.getValue(), null);
            }
            
            for (Map.Entry<String, SpecFieldNode> entry : ((SpecClazzNode) node).fieldNodes.entrySet()) {
                defaultNode(entry.getValue(), null);
            }
        } else if (node instanceof SpecObjectNode) {
            if (object == null) {
                Phosphophyllite.LOGGER.error("Error cannot write to null object");
                return;
            }
            Object newObject = createClassInstance(((SpecObjectNode) node).clazz);
            
            for (Map.Entry<String, SpecFieldNode> entry : ((SpecObjectNode) node).subNodes.entrySet()) {
                defaultNode(entry.getValue(), newObject);
            }
            
            ((SpecObjectNode) node).field.set(object, newObject);
        } else if (node instanceof SpecMapNode) {
            SpecMapNode mapNode = (SpecMapNode) node;
            
            HashMap<String, Object> newMap = new HashMap<>();
            
            for (Map.Entry<String, SpecFieldNode> entry : mapNode.defaultSubNodes.entrySet()) {
                Object obj = createClassInstance(mapNode.elementClass);
                defaultNode(entry.getValue(), obj);
                newMap.put(entry.getKey(), obj);
            }
            
            mapNode.field.set(object, newMap);
            
        } else if (node instanceof SpecListNode) {
            SpecListNode listNode = (SpecListNode) node;
            
            ArrayList<Object> newList = new ArrayList<>();
            
            for (SpecFieldNode defaultSubNode : listNode.defaultSubNodes) {
                Object obj = createClassInstance(listNode.elementClass);
                defaultNode(defaultSubNode, obj);
                newList.add(obj);
            }
            
            listNode.field.set(object, newList);
        } else if (node instanceof SpecStringNode) {
            ((SpecStringNode) node).field.set(object, ((SpecStringNode) node).defaultString);
        } else if (node instanceof SpecEnumNode) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object enumVal = Enum.valueOf((Class<? extends Enum>) ((SpecEnumNode) node).enumClass, ((SpecEnumNode) node).defaultValue);
            ((SpecEnumNode) node).field.set(object, enumVal);
        } else if (node instanceof SpecNumberNode) {
            setNumberField(((SpecNumberNode) node).field, object, ((SpecNumberNode) node).defaultValue);
        } else if (node instanceof SpecBooleanNode) {
            ((SpecBooleanNode) node).field.setBoolean(object, ((SpecBooleanNode) node).defaultValue);
        }
    }
    
    private static boolean isIntegral(Class<?> numberType) {
        return
                numberType == Byte.class || numberType == byte.class ||
                        numberType == Short.class || numberType == short.class ||
                        numberType == Integer.class || numberType == int.class ||
                        numberType == Long.class || numberType == long.class;
    }
    
    private static void setNumberField(Field field, @Nullable Object object, double value) throws IllegalAccessException {
        Object newVal = null;
        Class<?> numberType = field.getType();
        
        if (numberType == Byte.TYPE || numberType == byte.class) {
            newVal = (byte) value;
        } else if (numberType == Short.TYPE || numberType == short.class) {
            newVal = (short) value;
        } else if (numberType == Integer.TYPE || numberType == int.class) {
            newVal = (int) value;
        } else if (numberType == Long.TYPE || numberType == long.class) {
            newVal = (long) value;
        } else if (numberType == Float.TYPE || numberType == float.class) {
            newVal = (float) value;
        } else if (numberType == Double.TYPE || numberType == double.class) {
            newVal = value;
        }
        
        field.set(object, newVal);
    }
    
    @Nullable
    private static SpecClazzNode buildNodeForClazz(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(PhosphophylliteConfig.class)) {
            return null;
        }
        
        SpecClazzNode node = new SpecClazzNode();
        node.clazz = clazz;
        node.clazzNodes = new HashMap<>();
        node.fieldNodes = new HashMap<>();
        
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            SpecClazzNode subNode = buildNodeForClazz(subclass);
            if (subNode == null) {
                continue;
            }
            String name = subclass.getSimpleName();
            if (node.clazzNodes.containsKey(name)) {
                throw new DefinitionError("Duplicate config name: " + name);
            }
            node.clazzNodes.put(name, subNode);
        }
        
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            SpecFieldNode fieldNode = buildNodeForField(field, null);
            if (fieldNode == null) {
                continue;
            }
            String name = field.getName();
            if (node.clazzNodes.containsKey(name)) {
                throw new DefinitionError("Duplicate config name: " + name);
            }
            if (node.fieldNodes.containsKey(name)) {
                throw new DefinitionError("Duplicate config name: " + name);
            }
            node.fieldNodes.put(name, fieldNode);
        }
        
        return node;
    }
    
    private static SpecObjectNode buildNodeForObject(Class<?> clazz, Object object) {
        if (!clazz.isAnnotationPresent(PhosphophylliteConfig.class)) {
            throw new DefinitionError("Attempt to build node for invalid object class");
        }
        
        SpecObjectNode node = new SpecObjectNode();
        node.clazz = clazz;
        node.subNodes = new HashMap<>();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            SpecFieldNode fieldNode = buildNodeForField(field, object);
            if (fieldNode == null) {
                continue;
            }
            String name = field.getName();
            if (node.subNodes.containsKey(name)) {
                throw new DefinitionError("Duplicate config name: " + name);
            }
            node.subNodes.put(name, fieldNode);
        }
        return node;
    }
    
    @Nullable
    private static SpecFieldNode buildNodeForField(Field field, @Nullable Object object) {
        if (!field.isAnnotationPresent(PhosphophylliteConfig.Value.class)) {
            return null;
        }
        field.setAccessible(true);
        
        Object fieldObject = null;
        if (Modifier.isStatic(field.getModifiers()) == (object == null)) {
            try {
                fieldObject = field.get(object);
            } catch (IllegalAccessException e) {
                Phosphophyllite.LOGGER.warn("Illegal Access attempting to get field");
                Phosphophyllite.LOGGER.warn(e.getMessage());
                return null;
            }
        }
        
        Class<?> fieldClass = field.getType();
        
        if (fieldObject == null) {
            try {
                Constructor<?> constructor = fieldClass.getConstructor();
                constructor.setAccessible(true);
                fieldObject = constructor.newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Phosphophyllite.LOGGER.warn(e.getMessage());
                throw new DefinitionError("Unable to create default instance of field object");
            }
        }
        
        
        PhosphophylliteConfig.Value fieldAnnotation = field.getAnnotation(PhosphophylliteConfig.Value.class);
        
        StringBuilder comment = new StringBuilder(fieldAnnotation.comment());
        if (!fieldAnnotation.range().equals("(,)")) {
            if (comment.length() != 0) {
                comment.append('\n');
            }
            comment.append("Valid range: ").append(fieldAnnotation.range());
        }
        
        if (fieldAnnotation.commentDefaultValue() && !fieldClass.isArray() && !fieldClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            if (comment.length() != 0) {
                comment.append('\n');
            }
            comment.append("Default: ");
            comment.append(fieldObject.toString());
        }
        
        if (fieldClass.isEnum()) {
            String[] allowedValues = fieldAnnotation.allowedValues();
            if (allowedValues.length == 0) {
                Field[] enumFields = fieldClass.getFields();
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
        
        SpecFieldNode node = null;
        
        if (fieldClass.isArray() || List.class == fieldClass || ArrayList.class == fieldClass) {
            SpecListNode listNode = new SpecListNode();
            listNode.defaultSubNodes = new ArrayList<>();
            if (fieldClass.isArray()) {
                listNode.elementClass = fieldClass.getComponentType();
                for (int i = 0; i < Array.getLength(fieldObject); i++) {
                    Object element = Array.get(fieldObject, 0);
                    if (element != null) {
                        listNode.defaultSubNodes.add(buildNodeForObject(listNode.elementClass, element));
                    }
                }
            } else {
                
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                Type[] generics = type.getActualTypeArguments();
                Class<?> valueClass = (Class<?>) generics[0];
                listNode.elementClass = valueClass;
                
                if (!valueClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
                    throw new RuntimeException("list values must be config objects");
                }
                List<?> list = (List<?>) fieldObject;
                list.forEach(element -> {
                    listNode.defaultSubNodes.add(buildNodeForObject(listNode.elementClass, element));
                });
            }
            
            Object defaultObject = createClassInstance(listNode.elementClass);
            
            listNode.subNodeType = buildNodeForObject(listNode.elementClass, defaultObject);
            
            node = listNode;
        } else if (Map.class == fieldClass || HashMap.class == fieldClass) {
            SpecMapNode mapNode = new SpecMapNode();
            
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Type[] generics = type.getActualTypeArguments();
            
            if (generics[0] != String.class) {
                throw new RuntimeException("map keys must be strings");
            }
            
            Class<?> valueClass = (Class<?>) generics[1];
            if (!valueClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
                throw new RuntimeException("map values must be config objects");
            }
            
            mapNode.elementClass = valueClass;
            mapNode.defaultSubNodes = new HashMap<>();
            
            Object defaultObject = createClassInstance(mapNode.elementClass);
            
            mapNode.nodeType = buildNodeForObject(mapNode.elementClass, defaultObject);
            
            // its checked, see me check that generic type like 10 lines ago
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) fieldObject;
            map.forEach((string, element) -> {
                mapNode.defaultSubNodes.put(string, buildNodeForObject(mapNode.elementClass, element));
            });
            
            node = mapNode;
        } else if (String.class == fieldClass) {
            SpecStringNode stringNode = new SpecStringNode();
            stringNode.defaultString = (String) fieldObject;
            
            node = stringNode;
        } else if (fieldClass.isEnum()) {
            SpecEnumNode enumNode = new SpecEnumNode();
            enumNode.enumClass = fieldClass;
            enumNode.allowedValues = fieldAnnotation.allowedValues();
            
            enumNode.defaultValue = null;
            enumNode.defaultValue = fieldObject.toString();
            
            node = enumNode;
        } else if (fieldClass.isPrimitive() || Number.class.isAssignableFrom(fieldClass) || Boolean.class == fieldClass) {
            if (fieldClass == boolean.class || fieldClass == Boolean.class) {
                SpecBooleanNode booleanNode = new SpecBooleanNode();
                booleanNode.defaultValue = false;
                booleanNode.defaultValue = (Boolean) fieldObject;
                node = booleanNode;
            } else {
                // if it isn't a boolean, and it is a primitive, then its a number or its void
                // cant declare a void variable, so its a number
                
                SpecNumberNode numberNode = new SpecNumberNode();
                
                String range = fieldAnnotation.range();
                
                range = range.trim();
                if (range.length() < 3) {
                    throw new DefinitionError("Incomplete range given");
                }
                
                char lowerInclusiveChar = range.charAt(0);
                char higherInclusiveChar = range.charAt(range.length() - 1);
                boolean lowerInclusive;
                boolean higherInclusive;
                
                switch (lowerInclusiveChar) {
                    case '(': {
                        lowerInclusive = false;
                        break;
                    }
                    case '[': {
                        lowerInclusive = true;
                        break;
                    }
                    default: {
                        throw new DefinitionError("Unknown lower bound inclusivity");
                    }
                }
                switch (higherInclusiveChar) {
                    case ')': {
                        higherInclusive = false;
                        break;
                    }
                    case ']': {
                        higherInclusive = true;
                        break;
                    }
                    default: {
                        throw new DefinitionError("Unknown higher bound inclusivity");
                    }
                }
                
                range = range.substring(1, range.length() - 1).trim();
                String[] bounds = range.split(",");
                if (bounds.length > 2) {
                    throw new DefinitionError("Range cannot have more than two bounds");
                }
                String lowerBoundStr = "";
                String higherBoundStr = "";
                if (bounds.length == 2) {
                    lowerBoundStr = bounds[0].trim();
                    higherBoundStr = bounds[1].trim();
                } else {
                    if (range.length() == 0) {
                        throw new DefinitionError("Incomplete range given");
                    }
                    if (range.length() != 1) {
                        if (bounds.length != 1) {
                            throw new DefinitionError("Incomplete range given");
                        }
                        if (range.charAt(0) == ',') {
                            higherBoundStr = bounds[0];
                        } else if (range.charAt(range.length() - 1) == ',') {
                            lowerBoundStr = bounds[0];
                        } else {
                            throw new DefinitionError("Incomplete range given");
                        }
                    } else if (range.charAt(0) != ',') {
                        throw new DefinitionError("Incomplete range given");
                    }
                }
                double lowerBound = Double.MIN_VALUE;
                if (lowerBoundStr.length() != 0) {
                    lowerBound = Double.parseDouble(lowerBoundStr);
                }
                double higherBound = Double.MAX_VALUE;
                if (higherBoundStr.length() != 0) {
                    higherBound = Double.parseDouble(higherBoundStr);
                }
                if (lowerBound > higherBound) {
                    throw new DefinitionError("Higher bound must be greater or equal to lower bound");
                }
                
                numberNode.lowerInclusive = lowerInclusive;
                numberNode.upperInclusive = higherInclusive;
                numberNode.lowerBound = lowerBound;
                numberNode.upperBound = higherBound;
                
                Number fieldNum = (Number) fieldObject;
                numberNode.defaultValue = 0;
                numberNode.defaultValue = fieldNum.doubleValue();
                
                node = numberNode;
            }
        } else if (fieldClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
            node = buildNodeForObject(fieldClass, fieldObject);
        }
        
        if (node == null) {
            throw new DefinitionError("Cannot build config spec for invalid class");
        }
        
        node.field = field;
        node.comment = (comment.length() == 0 ? null : comment.toString());
        node.advanced = fieldAnnotation.advanced();
        node.hidden = fieldAnnotation.hidden();
        
        return node;
    }
    
    private static Object createClassInstance(Class<?> elementClass) {
        try {
            Constructor<?> constructor = elementClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Phosphophyllite.LOGGER.warn(e.getMessage());
            throw new DefinitionError("Unable to create default instance of object");
        }
    }
}
