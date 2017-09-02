class Test {

  static {
    System.out.println("Test - static");
    Callee.foo();
  }

  static public void main(String[] argv) {
    System.out.println("main");
  }

}
