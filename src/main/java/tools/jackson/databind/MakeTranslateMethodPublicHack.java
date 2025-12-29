package tools.jackson.databind;

public class MakeTranslateMethodPublicHack {

  // hack around translate method having protected access
  public static String translate(
    PropertyNamingStrategies.NamingBase namingBase,
    String input
  ) {
    return namingBase.translate(input);
  }
}
