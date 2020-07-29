package net.roguelogix.phosphophyllite.repack.tnjson;

/**
 * Exception on parse error
 */
public class ParseException extends RuntimeException {

  private int position;
  private String path;

  public ParseException(String s, int position, String path) {
    super(s);
    this.position = position;
    this.path = path;
  }

  /**
   * Position in json where occur error.
   * @return position of invalid symbol
   */
  public int getPosition() {
    return position;
  }
  /**
   * Path in json where occur error.
   * @return path where occur error
   */
  public String getPath() {
    return path;
  }
}
