/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class TestRunner {

  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(MkTestSuite.class);
    for (Failure failure : result.getFailures()) {
      System.out.println("###Test number ");
      System.out.println(failure.toString());
    }
    System.out.println(result.wasSuccessful());
    System.out.println("###Has finished");
  }
}
