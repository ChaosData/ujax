import java.util.*;
import java.lang.reflect.*;

class MTest {

  public static void dumpMethods(Class c) {
    Method[] ms = c.getDeclaredMethods();
    for (Method m : ms) {
      System.out.println(m);
    }
    System.out.println("======================");
  }

  public static void main(String[] argv) {
    dumpMethods(Object.class);
    dumpMethods(Class.class);
    dumpMethods(Method.class);
    dumpMethods(HashMap.class);
    dumpMethods(List.class);
  }

}
