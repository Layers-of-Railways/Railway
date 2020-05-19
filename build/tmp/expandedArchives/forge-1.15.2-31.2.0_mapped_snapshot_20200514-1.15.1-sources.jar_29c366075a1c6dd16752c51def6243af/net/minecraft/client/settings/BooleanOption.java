package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BooleanOption extends AbstractOption {
   private final Predicate<GameSettings> getter;
   private final BiConsumer<GameSettings, Boolean> setter;

   public BooleanOption(String translationKeyIn, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
      super(translationKeyIn);
      this.getter = getter;
      this.setter = setter;
   }

   public void set(GameSettings options, String valueIn) {
      this.set(options, "true".equals(valueIn));
   }

   public void nextValue(GameSettings options) {
      this.set(options, !this.get(options));
      options.saveOptions();
   }

   private void set(GameSettings options, boolean valueIn) {
      this.setter.accept(options, valueIn);
   }

   public boolean get(GameSettings options) {
      return this.getter.test(options);
   }

   public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn) {
      return new OptionButton(xIn, yIn, widthIn, 20, this, this.getText(options), (p_216745_2_) -> {
         this.nextValue(options);
         p_216745_2_.setMessage(this.getText(options));
      });
   }

   public String getText(GameSettings options) {
      return this.getDisplayString() + I18n.format(this.get(options) ? "options.on" : "options.off");
   }
}