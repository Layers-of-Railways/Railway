package net.minecraft.client.tutorial;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompletedTutorialStep implements ITutorialStep {
   private final Tutorial tutorial;

   public CompletedTutorialStep(Tutorial tutorial) {
      this.tutorial = tutorial;
   }
}