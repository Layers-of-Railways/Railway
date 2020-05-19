package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class DifficultyInstance {
   private final Difficulty worldDifficulty;
   private final float additionalDifficulty;

   public DifficultyInstance(Difficulty worldDifficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
      this.worldDifficulty = worldDifficulty;
      this.additionalDifficulty = this.calculateAdditionalDifficulty(worldDifficulty, worldTime, chunkInhabitedTime, moonPhaseFactor);
   }

   public Difficulty getDifficulty() {
      return this.worldDifficulty;
   }

   public float getAdditionalDifficulty() {
      return this.additionalDifficulty;
   }

   public boolean isHarderThan(float p_193845_1_) {
      return this.additionalDifficulty > p_193845_1_;
   }

   public float getClampedAdditionalDifficulty() {
      if (this.additionalDifficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.additionalDifficulty > 4.0F ? 1.0F : (this.additionalDifficulty - 2.0F) / 2.0F;
      }
   }

   private float calculateAdditionalDifficulty(Difficulty difficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
      if (difficulty == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean flag = difficulty == Difficulty.HARD;
         float f = 0.75F;
         float f1 = MathHelper.clamp(((float)worldTime + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         f = f + f1;
         float f2 = 0.0F;
         f2 = f2 + MathHelper.clamp((float)chunkInhabitedTime / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
         f2 = f2 + MathHelper.clamp(moonPhaseFactor * 0.25F, 0.0F, f1);
         if (difficulty == Difficulty.EASY) {
            f2 *= 0.5F;
         }

         f = f + f2;
         return (float)difficulty.getId() * f;
      }
   }
}