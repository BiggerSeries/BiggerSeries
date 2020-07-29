package net.roguelogix.phosphophyllite.repack.tnjson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * MAP to JSON converter.<br>
 * Every value in the Map must be
 * <ul>
 *   <li>either a simple value: string, number, boolean, or other;</li>
 *   <li>either by a Map such as Map&lt;String, Object&gt;;</li>
 *   <li>either an array of values such as Collection&lt;Object&gt;.</li>
 * </ul>
 * <br>
 * Of course, you can use for json-object any class inheritor of the java.util.Map interface.<br>
 * For json-array you can use any class inheritor of the java.util.Collection interface.
 */
class JsonSerializer {

  private JsonSerializer() {
    //hide this
  }

  /**
   * Convert Map to JSON with builder
   * @param data Object to convert. It can be Map, Collection, array, or any other object
   * @param builder json generator tuning
   * @return JSON-string
   * @see TnJsonBuilder
   */
  public static String toJson(Object data, TnJsonBuilder builder) {
    StringBuilder b = new StringBuilder();
    addValue(data, b, builder, 0, "");
    String json = b.toString();
    return json;
  }

  private static void addValue(Object value, StringBuilder b, TnJsonBuilder builder, int level, String path) {

    if (value == null) {
      b.append("null");
    }
    else if (value instanceof Character) {
      addString(String.valueOf(value), b, builder);
    }
    else if (value instanceof CharSequence) {
      addString((CharSequence) value, b, builder);
    }
    else if (value instanceof Number) {
      addNum(value, b);
    }
    else if (value instanceof Boolean) {
      boolean v = (Boolean) value;
      b.append(v ? "true" : "false");
    }
    else if (value instanceof Date) {
      long v = ((Date)value).getTime();
      b.append(v);
    }
    else if (value instanceof LocalDate) {
      String strd = ((LocalDate)value).format(DateTimeFormatter.ISO_DATE);
      b.append(strd);
    }
    else if (value instanceof LocalTime) {
      String strt = ((LocalTime)value).format(DateTimeFormatter.ISO_TIME);
      b.append(strt);
    }
    else if (value instanceof LocalDateTime) {
      String strdt = ((LocalDateTime)value).format(DateTimeFormatter.ISO_DATE_TIME);
      b.append(strdt);
    }
    else if (value instanceof Map) {
      addMap((Map) value, b, builder, level, path);
    }
    else if (value instanceof Collection) {
      addList((Collection) value, b, builder, level, path);
    }
    else if (value.getClass().isArray()) {
      addArray(value, b, builder, level, path);
    }
    else {
      addObj(value, b, builder, level, path);
    }

  }

  private static void addString(CharSequence str, StringBuilder b, TnJsonBuilder builder) {
    if (str == null) {
      b.append("null");
    }
    else {
      b.append(builder.quoteSymbol);

      int len = str.length();
      for (int p = 0; p < len; p++) {
        char c = str.charAt(p);
        if (Character.isLetterOrDigit(c)) {
          b.append(c);
        }
        else if (c == '\n') {
          if (builder.isAllowMultiRowString) {
            b.append("/\n");
          }
          else if (builder.isStayReadable) {
            b.append(charToReadable(c));
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
        else if (c == builder.quoteSymbol) {
          if (builder.isStayReadable) {
            b.append(charToReadable(c));
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
        else {
          if (builder.isStayReadable) {
            if (c == '\'' || c == '"') {
              b.append(c);
            }
            else {
              b.append(charToReadable(c));
            }
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
      }

      b.append(builder.quoteSymbol);
    }
  }
  private static String charToReadable(char c) {
    switch (c) {
      case ' ': return String.valueOf(c);
      case '`': return String.valueOf(c);
      case '~': return String.valueOf(c);
      case '!': return String.valueOf(c);
      case '@': return String.valueOf(c);
      case '#': return String.valueOf(c);
      case '$': return String.valueOf(c);
      case '%': return String.valueOf(c);
      case '^': return String.valueOf(c);
      case '&': return String.valueOf(c);
      case '*': return String.valueOf(c);
      case '(': return String.valueOf(c);
      case ')': return String.valueOf(c);
      case '-': return String.valueOf(c);
      case '_': return String.valueOf(c);
      case '=': return String.valueOf(c);
      case '+': return String.valueOf(c);
      case '[': return String.valueOf(c);
      case ']': return String.valueOf(c);
      case '{': return String.valueOf(c);
      case '}': return String.valueOf(c);
      case ';': return String.valueOf(c);
      case ':': return String.valueOf(c);
      case '"': return "\\\"";
      case '\'': return "\\'";
      case '\\': return "\\\\";
      case '|': return String.valueOf(c);
      case '/': return "\\/";
      case ',': return String.valueOf(c);
      case '.': return String.valueOf(c);
      case '?': return String.valueOf(c);
      case '<': return String.valueOf(c);
      case '>': return String.valueOf(c);
      case '\b': return "\\b";
      case '\f': return "\\f";
      case '\n': return "\\n";
      case '\r': return "\\r";
      case '\t': return "\\t";
      default: return unicodeEscaped(c);
    }
  }
  private static String unicodeEscaped(char ch) {
    if (ch < 0x10) {
      return "\\u000" + Integer.toHexString(ch);
    }
    else if (ch < 0x100) {
      return "\\u00" + Integer.toHexString(ch);
    }
    else if (ch < 0x1000) {
      return "\\u0" + Integer.toHexString(ch);
    }
    return "\\u" + Integer.toHexString(ch);
  }


  private static void addNum(Object num, StringBuilder b) {
    String v  = String.valueOf(num);
    b.append(v);
  }

  private static void addMap(Map map, StringBuilder b, TnJsonBuilder builder, int level, String path) {
    int valuelevel = level + 1;

    b.append("{");
    endLine(b, builder);

    boolean hasEntry = false;
    for(Object keyObj : map.keySet()) {
      String key = String.valueOf(keyObj);
      Object value = map.get(keyObj);
      String valuePath = path+"."+key;

      if (builder.pathHandler != null) {
        value = builder.pathHandler.handlePath(valuePath, value);
      }
      if (value == null && !builder.isKeepNull) {
        continue;
      }
      if (value != null && builder.typeHandler != null) {
        value = builder.typeHandler.handleType(value);
      }

      key = codeKey(key, builder);

      if (hasEntry) {
        b.append(",");
        endLine(b, builder);
      }
      else {
        hasEntry = true;
      }

      startLine(b, builder, valuelevel);
      b.append(key);

      b.append(":");
      if (builder.isFormated) {
        b.append(" ");
      }

      addValue(value, b, builder, valuelevel, valuePath);
    }

    endLine(b, builder);
    startLine(b, builder, level);
    b.append("}");

  }

  private static String codeKey(CharSequence key, TnJsonBuilder builder) {
    StringBuilder b = new StringBuilder();
    int len = key.length();
    boolean validIdenty = true;
    for (int i=0; i < len; i++) {
      char c = key.charAt(i);
      if (Character.isLetterOrDigit(c)) {
        b.append(c);
      }
      else {
        b.append(unicodeEscaped(c));
        validIdenty = false;
      }


    }

    if (!builder.isKeyQuoted && validIdenty && Character.isLetter(key.charAt(0))) {
      return b.toString();
    }
    else {
      return builder.quoteSymbol + b.toString() + builder.quoteSymbol;
    }
  }

  private static void addList(Collection list, StringBuilder b, TnJsonBuilder builder, int level, String path) {
    int itemlevel = level + 1;

    b.append("[");

    endLine(b, builder);
    startLine(b, builder, itemlevel);

    boolean hasEntry = false;
    for (Object value : list) {

      if (builder.pathHandler != null) {
        value = builder.pathHandler.handlePath(path, value);
      }
      if (value == null && !builder.isKeepNull) {
        continue;
      }

      if (hasEntry) {
        b.append(",");
        endLine(b, builder);
        startLine(b, builder, itemlevel);
      }
      else {
        hasEntry = true;
      }


      if (value != null && builder.typeHandler != null) {
        value = builder.typeHandler.handleType(value);
      }

      addValue(value, b, builder, itemlevel, path);
    }
    endLine(b, builder);
    startLine(b, builder, level);
    b.append("]");

  }

  private static void addArray(Object array, StringBuilder b, TnJsonBuilder builder, int level, String path) {
    int itemlevel = level + 1;

    b.append("[");

    endLine(b, builder);
    startLine(b, builder, itemlevel);

    boolean hasEntry = false;
    int length = Array.getLength(array);
    for (int i = 0; i < length; i ++) {
      Object value = Array.get(array, i);
      if (builder.pathHandler != null) {
        value = builder.pathHandler.handlePath(path, value);
      }
      if (value == null && !builder.isKeepNull) {
        continue;
      }

      if (hasEntry) {
        b.append(",");
        endLine(b, builder);
        startLine(b, builder, itemlevel);
      }
      else {
        hasEntry = true;
      }

      if (value != null && builder.typeHandler != null) {
        value = builder.typeHandler.handleType(value);
      }

      addValue(value, b, builder, itemlevel, path);
    }

    endLine(b, builder);
    startLine(b, builder, level);
    b.append("]");

  }

  private static void addObj(Object obj, StringBuilder b, TnJsonBuilder builder, int level, String path) {

    Class<?> cls = obj.getClass();

    try {
      Method toJsonMtd = cls.getDeclaredMethod("toJson");
      toJsonMtd.setAccessible(true);
      String string = (String) toJsonMtd.invoke(obj);
      b.append(string);
      return;
    }
    catch (NoSuchMethodException e) {
      //no method, no problem
    }
    catch (InvocationTargetException | IllegalAccessException e) {
      throw new SerializeException("Error on invoke method toJson by class: "+cls.getName()+", object: " + String.valueOf(obj)+".", e);
    }
    int valuelevel = level + 1;

    b.append("{");
    endLine(b, builder);

    boolean hasEntry = false;
    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields) {
      int modifiers = field.getModifiers();
      boolean allow = !field.isSynthetic() && !Modifier.isPrivate(modifiers) && !Modifier.isTransient(modifiers);
      if (! allow) {
        continue;
      }

      Object value = getObjValue(field, obj);

      if (value == null && !builder.isKeepNull) {
        continue;
      }
      if (value != null && builder.typeHandler != null) {
        value = builder.typeHandler.handleType(value);
      }

      if (hasEntry) {
        b.append(",");
        endLine(b, builder);
      }
      else {
        hasEntry = true;
      }

      startLine(b, builder, valuelevel);

      b.append(field.getName());
      b.append(":");

      if (builder.isFormated) {
        b.append(" ");
      }

      addValue(value, b, builder, level, path);
    }
    endLine(b, builder);
    startLine(b, builder, level);
    b.append("}");
  }
  private static Object getObjValue(Field field, Object fromObj) {
    boolean accessible = field.isAccessible();
    field.setAccessible(true);
    try {
      Object value = field.get(fromObj);
      return value;
    }
    catch (Throwable t) {
      throw new SerializeException("Error on extract value from object " + fromObj.getClass().getName() + " ("+String.valueOf(fromObj) + ") from field " + field.getName(), t);
    }
    finally {
      field.setAccessible(accessible);
    }
  }



  private static void endLine(StringBuilder b, TnJsonBuilder builder) {
    if (builder.isFormated) {
      b.append("\n");
    }
  }
  private static void startLine(StringBuilder b, TnJsonBuilder builder, int level) {
    if (!builder.isFormated) {
      return;
    }

    for(int i=0; i<level; i++) {
      b.append("  ");
    }
  }

}
