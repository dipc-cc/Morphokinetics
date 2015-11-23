/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Néstor
 */
public class Wait {

  /**
   * Waits one second
   */
  public static void oneSec() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Waits given seconds
   * @param s seconds
   */
  public static void manySec(long s) {
    try {
      Thread.sleep(s * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Waits given milliseconds 
   * @param ms milliseconds 
   */
  public static void manyMilliSec(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
