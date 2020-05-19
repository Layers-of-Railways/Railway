package net.minecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class NetherBedDamageSource extends DamageSource {
   protected NetherBedDamageSource() {
      super("netherBed");
      this.setDifficultyScaled();
      this.setExplosion();
   }

   /**
    * Gets the death message that is displayed when the player dies
    */
   public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("death.attack.netherBed.link")).applyTextStyle((p_211694_0_) -> {
         p_211694_0_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")));
      });
      return new TranslationTextComponent("death.attack.netherBed.message", entityLivingBaseIn.getDisplayName(), itextcomponent);
   }
}