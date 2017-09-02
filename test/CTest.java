
class CTest {

  public class A {
    public A() {
      System.out.println("A(): " +this.getClass());
    }
    public void doStuffA() {
      System.out.println("doStuffA(): " + this.getClass());
    }
  }

  public class B extends A {
    public B() {
      super();
      System.out.println("B(): " +this.getClass());
    }
    public void doStuffB() {
      System.out.println("doStuffB(): " + this.getClass());
    }
  }

  public class C extends B {
    public C() {
      super();
      System.out.println("C(): " +this.getClass());
    }
    public void doStuffC() {
      System.out.println("doStuffC(): " + this.getClass());
    }
  }

  public static void main(String[] argv) {
    CTest ct = new CTest();
    ct.run();
  }

  public void doThingA(A _a) {
    System.out.println("doThingA: " + _a.getClass());
  }
  public void doThingB(B _b) {
    System.out.println("doThingB: " + _b.getClass());
  }
  public void doThingC(C _c) {
    System.out.println("doThingC: " + _c.getClass());
  }

  public void run() {
    A a = new A();
    a.doStuffA();
    doThingA(a);
    System.out.println("===============");
    B b = new B();
    b.doStuffA();
    b.doStuffB();
    doThingA(b);
    doThingB(b);
    System.out.println("===============");
    C c = new C();
    c.doStuffA();
    c.doStuffB();
    c.doStuffC();
    doThingA(c);
    doThingB(c);
    doThingC(c);

  }

}
