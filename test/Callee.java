class Callee {

  static void foo() {
    System.out.println("foo()");
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    System.out.println("- called from: " + callerClassName);
  }
}
