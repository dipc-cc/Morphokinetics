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
  public static void oneSec() {
     try {
       Thread.currentThread().sleep(1000);
       }
     catch (InterruptedException e) {
       e.printStackTrace();
       }
     }
  public static void manySec(long s) {
     try {
       Thread.currentThread().sleep(s * 1000);
       }
     catch (InterruptedException e) {
       e.printStackTrace();
       }
     }

    public static void manymsec(long s) {
     try {
       Thread.currentThread().sleep(s);
       }
     catch (InterruptedException e) {
       e.printStackTrace();
       }
     }

}
