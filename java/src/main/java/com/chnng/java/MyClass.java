package com.chnng.java;

public class MyClass {
  static {
    System.out.println("main in static");
  }

  public static void main(String[] args) {
    TestA a = new TestA();
  }
}
