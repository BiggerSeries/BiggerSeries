package net.roguelogix.phosphophyllite.robn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.*;

/**
 * This is a Java port of the C++ implementation
 */
public class ROBN {
    
    public static <T> ArrayList<Byte> toROBN(T t) {
        ArrayList<Byte> arrayList = new ArrayList<>();
        toROBN(t, arrayList);
        return arrayList;
    }
    
    public static Object fromROBN(ArrayList<Byte> buf) {
        return fromROBN(buf.iterator());
    }
    
    public enum Type {
        Undefined(0),
        
        String(1),
        Bool(2),
        
        Int8(4),
        Int16(5),
        Int32(6),
        Int64(7),
        Int128(18),
        BigInt(21),
        
        uInt8(8),
        uInt16(9),
        uInt32(10),
        uInt64(11),
        uInt128(19),
        
        Float(12),
        Double(13),
        LongDouble(20),
        BigFloat(22),
        
        Vector(15),
        Pair(16),
        Map(17),
        
        ;
        
        public final byte val;
        
        private Type(int i) {
            val = (byte) i;
        }
        
        public static <T> Type typeID(Class<T> tClass) {
            
            if (tClass == java.lang.String.class) {
                return String;
            }
            
            if (tClass == boolean.class || tClass == Boolean.class) {
                return Bool;
            }
            
            if (tClass == byte.class || tClass == Byte.class) {
                return Int8;
            }
            if (tClass == short.class || tClass == Short.class) {
                return Int16;
            }
            if (tClass == int.class || tClass == Integer.class) {
                return Int32;
            }
            if (tClass == long.class || tClass == Long.class) {
                return Int64;
            }
            
            if (tClass == BigDecimal.class) {
                return BigFloat;
            }
            if (tClass == BigInteger.class) {
                return BigInt;
            }
            
            // java doesnt have unsigned integers, *sooooooo*
            
            if (tClass == float.class || tClass == java.lang.Float.class) {
                return Float;
            }
            if (tClass == double.class || tClass == java.lang.Double.class) {
                return Double;
            }
            
            if (Collection.class.isAssignableFrom(tClass)) {
                return Vector;
            }
            
            if (java.util.Map.class.isAssignableFrom(tClass)) {
                return Map;
            }
            
            return Undefined;
        }
        
        static int primitiveTypeSize(Type type) {
            switch (type) {
                default:
                    return 0;
                case Bool:
                case Int8:
                case uInt8:
                    return 1;
                case Int16:
                case uInt16:
                    return 2;
                case Int32:
                case uInt32:
                case Float:
                    return 4;
                case Int64:
                case uInt64:
                case Double:
                    return 8;
                case Int128:
                case uInt128:
                    return 16;
            }
        }
        
        private static final HashMap<Byte, Type> idLookup = new HashMap<>();
        
        static {
            idLookup.put(Undefined.val, Undefined);
            idLookup.put(String.val, String);
            idLookup.put(Bool.val, Bool);
            idLookup.put(Int8.val, Int8);
            idLookup.put(Int16.val, Int16);
            idLookup.put(Int32.val, Int32);
            idLookup.put(Int64.val, Int64);
            idLookup.put(Int128.val, Int128);
            idLookup.put(BigInt.val, BigInt);
            idLookup.put(uInt8.val, uInt8);
            idLookup.put(uInt16.val, uInt16);
            idLookup.put(uInt32.val, uInt32);
            idLookup.put(uInt64.val, uInt64);
            idLookup.put(uInt128.val, uInt128);
            idLookup.put(Float.val, Float);
            idLookup.put(Double.val, Double);
            idLookup.put(LongDouble.val, LongDouble);
            idLookup.put(BigFloat.val, BigFloat);
            idLookup.put(Vector.val, Vector);
            idLookup.put(Pair.val, Pair);
            idLookup.put(Map.val, Map);
            
        }
        
        public static Type fromID(byte id) {
            id &= 0x7F;
            return idLookup.get(id);
        }
    }
    
    public enum Endianness {
        LITTLE(0),
        BIG(1 << 7),
        
        // im always going to encode to little endian from Java, its *probably* LE under the hood regardless
        // like, it almost always is, and BE/LE isn't going to be any faster/slow in Java
        NATIVE(ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? BIG.val : LITTLE.val);
        
        public final byte val;
        
        Endianness(int i) {
            val = (byte) i;
        }
        
        public static Endianness fromByte(byte b) {
            if ((b & (1 << 7)) != 0) {
                return BIG;
            } else {
                return LITTLE;
            }
        }
    }
    
    private static void requestBytes(ArrayList<Byte> buf, int requiredBytes) {
        buf.ensureCapacity(buf.size() + requiredBytes);
    }
    
    private static <T> void toROBN(T t, ArrayList<Byte> buf) {
        if (t instanceof Boolean) {
            buf.add(Type.Bool.val);
            buf.add((byte) ((Boolean) t ? 1 : 0));
            return;
        }
        if (t instanceof String) {
            stringToROBN((String) t, buf);
            return;
        }
        if (t instanceof Number) {
            numberToROBN((Number) t, buf);
            return;
        }
        if (t instanceof Collection) {
            vectorToROBN((Collection<?>) t, buf);
            return;
        }
        if (t instanceof Map) {
            mapToROBN((Map<?, ?>) t, buf);
            return;
        }
        throw new IllegalArgumentException("Unknown object type");
    }
    
    private static Object fromROBN(Iterator<Byte> iterator) {
        if (iterator.hasNext()) {
            return fromROBN(iterator, Type.fromID(iterator.next()));
        }
        throw new IllegalArgumentException("Malformed Binary");
    }
    
    private static Object fromROBN(Iterator<Byte> iterator, Type type) {
        switch (type) {
            
            case String:
                return stringFromROBN(iterator);
            case Bool:
            case Int8:
            case Int16:
            case Int32:
            case Int64:
            case Int128:
            case BigInt:
            case uInt8:
            case uInt16:
            case uInt32:
            case uInt64:
            case uInt128:
            case Float:
            case Double:
            case LongDouble:
            case BigFloat: {
                return primitiveFromROBN(iterator, type);
            }
            case Vector:
                return vectorFromROBN(iterator);
            case Map:
                return mapFromROBN(iterator);
            case Pair:
                break;
            case Undefined:
                throw new IllegalArgumentException("Malformed Binary");
        }
        throw new IllegalArgumentException("Incompatible Binary");
    }
    
    private static void numberToROBN(Number number, ArrayList<Byte> buf) {
        if (number instanceof Byte) {
            buf.add(Type.Int8.val);
            buf.add(number.byteValue());
            return;
        } else if (number instanceof Short) {
            short val = number.shortValue();
            buf.add((byte) (Type.Int16.val | Endianness.NATIVE.val));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 1 : 0))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 0 : 1))) & 0xFF));
            return;
        } else if (number instanceof Integer) {
            int val = number.intValue();
            buf.add((byte) (Type.Int32.val | Endianness.NATIVE.val));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 3 : 0))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 2 : 1))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 1 : 2))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 0 : 3))) & 0xFF));
            return;
        } else if (number instanceof Long) {
            long val = number.longValue();
            buf.add((byte) (Type.Int64.val | Endianness.NATIVE.val));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 7 : 0))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 6 : 1))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 5 : 2))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 4 : 3))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 3 : 4))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 2 : 5))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 1 : 6))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 0 : 7))) & 0xFF));
            return;
        } else if (number instanceof Float) {
            int val = Float.floatToIntBits(number.floatValue());
            buf.add((byte) (Type.Float.val | Endianness.NATIVE.val));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 3 : 0))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 2 : 1))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 1 : 2))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 0 : 3))) & 0xFF));
            return;
        } else if (number instanceof Double) {
            long val = Double.doubleToLongBits(number.doubleValue());
            buf.add((byte) (Type.Double.val | Endianness.NATIVE.val));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 7 : 0))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 6 : 1))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 5 : 2))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 4 : 3))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 3 : 4))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 2 : 5))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 1 : 6))) & 0xFF));
            buf.add((byte) ((val >> (8 * (Endianness.NATIVE.val == Endianness.BIG.val ? 0 : 7))) & 0xFF));
            return;
        } else if (number instanceof BigDecimal) {
            // TODO: 7/24/20  BigDecimal
        } else if (number instanceof BigInteger) {
            // TODO: 7/24/20  BigInteger
        }
        throw new IllegalArgumentException("Unsupported number type");
    }
    
    private static Object primitiveFromROBN(Iterator<Byte> iterator, Type type) {
        switch (type) {
            case Bool: {
                if (iterator.hasNext()) {
                    return iterator.next() != 0;
                }
                break;
            }
            case Int8: {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                break;
            }
            case Int16: {
                short val = 0;
                for (int i = 0; i < 2; i++) {
                    if (iterator.hasNext()) {
                        val |= (((short) iterator.next() & 0xFF) << (8 * i));
                    } else {
                        break;
                    }
                }
                if (Endianness.NATIVE.val == Endianness.BIG.val) {
                    val = Short.reverseBytes(val);
                }
                return val;
            }
            case Int32: {
                int val = 0;
                for (int i = 0; i < 4; i++) {
                    if (iterator.hasNext()) {
                        val |= (((int) iterator.next() & 0xFF) << (8 * i));
                    } else {
                        break;
                    }
                }
                if (Endianness.NATIVE.val == Endianness.BIG.val) {
                    val = Integer.reverseBytes(val);
                }
                return val;
            }
            case Int64: {
                long val = 0;
                for (int i = 0; i < 8; i++) {
                    if (iterator.hasNext()) {
                        val |= (((long) iterator.next() & 0xFF) << (8 * i));
                    } else {
                        break;
                    }
                }
                if (Endianness.NATIVE.val == Endianness.BIG.val) {
                    val = Long.reverseBytes(val);
                }
                return val;
            }
            
            case uInt8:
                return ((byte) primitiveFromROBN(iterator, Type.Int8)) & 0x7F;
            case uInt16:
                return ((short) primitiveFromROBN(iterator, Type.Int16)) & 0x7FFF;
            case uInt32:
                return ((int) primitiveFromROBN(iterator, Type.Int32)) & 0x7FFFFFFF;
            case uInt64:
                return ((long) primitiveFromROBN(iterator, Type.Int64)) & 0x7FFFFFFFFFFFFFFFL;
            
            case Int128:
            case uInt128:
                throw new IllegalArgumentException("Incompatible Binary");
            
            case Float:
                return Float.intBitsToFloat((Integer) primitiveFromROBN(iterator, Type.Int32));
            case Double:
                return Double.longBitsToDouble((Long) primitiveFromROBN(iterator, Type.Int64));
        }
        throw new IllegalArgumentException("Malformed Binary");
    }
    
    private static void vectorToROBN(Collection<?> collection, ArrayList<Byte> buf) {
        
        requestBytes(buf, 11);
        int elementCount = collection.size();
        buf.add(Type.Vector.val);
        toROBN(elementCount, buf);
        
        Type elementType = Type.Undefined;
        Iterator<?> iterator = collection.iterator();
        if (iterator.hasNext()) {
            Object o = iterator.next();
            elementType = Type.typeID(o.getClass());
        }
        buf.add((byte) (elementType.val | Endianness.NATIVE.val));
        
        if (Type.primitiveTypeSize(elementType) != 0) {
            requestBytes(buf, Type.primitiveTypeSize(elementType) * elementCount);
        }
        
        ArrayList<Byte> tmpBuffer = new ArrayList<>();
        for (Object o : collection) {
            tmpBuffer.clear();
            toROBN(o, tmpBuffer);
            requestBytes(buf, tmpBuffer.size() - 1);
            for (int i = 1; i < tmpBuffer.size(); i++) {
                buf.add(tmpBuffer.get(i));
            }
        }
        
    }
    
    private static Object vectorFromROBN(Iterator<Byte> iterator) {
        Object vectorLengthObj = fromROBN(iterator);
        if (!(vectorLengthObj instanceof Number)) {
            throw new IllegalArgumentException("Malformed Binary");
        }
        long vectorLength = ((Number) vectorLengthObj).longValue();
        if (vectorLength > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Incompatible Binary");
        }
        
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.ensureCapacity((int) vectorLength);
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Malformed Binary");
        }
        Type elementType = Type.fromID(iterator.next());
        
        for (long i = 0; i < vectorLength; i++) {
            arrayList.add(fromROBN(iterator, elementType));
        }
        return arrayList;
    }
    
    private static void mapToROBN(Map<?, ?> map, ArrayList<Byte> buf) {
        // because java doesnt have native std::pair support, i get to encode them right here
        // no being lazy like in C++, *fuck*
        
        buf.add(Type.Map.val);
        toROBN(map.size(), buf);
        
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            buf.add(Type.Pair.val);
            buf.addAll(toROBN(entry.getKey()));
            buf.addAll(toROBN(entry.getValue()));
        }
        
        // that was easy
    }
    
    private static Object mapFromROBN(Iterator<Byte> iterator) {
        Object mapLengthOBJ = fromROBN(iterator);
        if (!(mapLengthOBJ instanceof Number)) {
            throw new IllegalArgumentException("Malformed Binary");
        }
        long mapLength = ((Number) mapLengthOBJ).longValue();
        if (mapLength > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Incompatible Binary");
        }
        
        HashMap<Object, Object> map = new HashMap<>();
        
        for (int i = 0; i < mapLength; i++) {
            if (!iterator.hasNext() || iterator.next() != Type.Pair.val) {
                throw new IllegalArgumentException("Malformed Binary");
            }
            Object key = fromROBN(iterator);
            Object value = fromROBN(iterator);
            map.put(key, value);
        }
        return map;
    }
    
    private static void stringToROBN(String str, ArrayList<Byte> buf) {
        buf.ensureCapacity(buf.size() + str.length() + 2);
        buf.add(Type.String.val);
        for (int i = 0; i < str.length(); i++) {
            buf.add((byte) str.charAt(i));
        }
        buf.add((byte) 0);
    }
    
    private static Object stringFromROBN(Iterator<Byte> iterator) {
        StringBuilder builder = new StringBuilder();
        while (true) {
            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("Malformed Binary");
            }
            byte next = iterator.next();
            if (next == 0) {
                break;
            }
            builder.append((char) next);
        }
        return builder.toString();
    }
}