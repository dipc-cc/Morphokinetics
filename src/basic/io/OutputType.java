/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class OutputType {

  public enum formatFlag {
    TXT(1 << 0), MKO(1 << 1), PNG(1 << 2), EXTRA(1 << 3), AE(1 << 4), XYZ(1 << 5), EXTRA2(1 << 6), CAT(1 << 7);

    public static final EnumSet<formatFlag> ALL_OPTS = EnumSet.allOf(formatFlag.class);
    private final long statusFlagValue;

    formatFlag(long statusFlagValue) {
      this.statusFlagValue = statusFlagValue;
    }

    public long getStatusFlagValue() {
      return statusFlagValue;
    }
  }

  /**
   * Translates a numeric status code into a Set of StatusFlag enums
   *
   * @param statusValue statusValue
   * @return EnumSet representing a documents status
   */
  public EnumSet<formatFlag> getStatusFlags(long statusValue) {
    EnumSet statusFlags = EnumSet.noneOf(formatFlag.class);

    // Iterate all possible flag values and add them if the power of two is equal
    for (formatFlag myFlag : formatFlag.values()) {
      if ((myFlag.getStatusFlagValue() & statusValue) == myFlag.getStatusFlagValue()) {
        statusFlags.add(myFlag);
      }
    }

    return statusFlags;
  }

  /**
   * Translates a set of StatusFlag enums into a numeric status code
   *
   * @param flags if statusFlags
   * @return numeric representation of the document status
   */
  public long getStatusValue(Set<formatFlag> flags) {
    long value = 0;
    for (formatFlag myFlag : flags) {
      value |= myFlag.getStatusFlagValue();
    }
    return value;
  }
}
