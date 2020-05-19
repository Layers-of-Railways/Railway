package net.minecraft.client.settings;

import java.util.function.BooleanSupplier;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleableKeyBinding extends KeyBinding {
   private final BooleanSupplier getterToggle;

   public ToggleableKeyBinding(String descriptionIn, int codeIn, String categoryIn, BooleanSupplier getterIn) {
      super(descriptionIn, InputMappings.Type.KEYSYM, codeIn, categoryIn);
      this.getterToggle = getterIn;
   }

   public void setPressed(boolean valueIn) {
      if (this.getterToggle.getAsBoolean()) {
         if (valueIn) {
            super.setPressed(!this.isKeyDown());
         }
      } else {
         super.setPressed(valueIn);
      }

   }
}