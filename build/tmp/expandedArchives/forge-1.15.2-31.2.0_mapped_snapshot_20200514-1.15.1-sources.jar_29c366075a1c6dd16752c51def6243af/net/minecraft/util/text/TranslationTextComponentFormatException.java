package net.minecraft.util.text;

public class TranslationTextComponentFormatException extends IllegalArgumentException {
   public TranslationTextComponentFormatException(TranslationTextComponent component, String message) {
      super(String.format("Error parsing: %s: %s", component, message));
   }

   public TranslationTextComponentFormatException(TranslationTextComponent component, int index) {
      super(String.format("Invalid index %d requested for %s", index, component));
   }

   public TranslationTextComponentFormatException(TranslationTextComponent component, Throwable cause) {
      super(String.format("Error while parsing: %s", component), cause);
   }
}