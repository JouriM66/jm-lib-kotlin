package jm.lib;

class jHelper {
  public static boolean CmpByRef( Object first, Object second ) {
    return (first == null) ? (second == null) : (second != null && first == second);
  }
}