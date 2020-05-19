package net.minecraft.client.audio;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSounds {
   @OnlyIn(Dist.CLIENT)
   public static class SubSound extends TickableSound {
      private final ClientPlayerEntity player;

      protected SubSound(ClientPlayerEntity playerIn, SoundEvent soundIn) {
         super(soundIn, SoundCategory.AMBIENT);
         this.player = playerIn;
         this.repeat = false;
         this.repeatDelay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.global = true;
      }

      public void tick() {
         if (this.player.removed || !this.player.canSwim()) {
            this.donePlaying = true;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnderWaterSound extends TickableSound {
      private final ClientPlayerEntity player;
      private int ticksInWater;

      public UnderWaterSound(ClientPlayerEntity playerIn) {
         super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
         this.player = playerIn;
         this.repeat = true;
         this.repeatDelay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.global = true;
      }

      public void tick() {
         if (!this.player.removed && this.ticksInWater >= 0) {
            if (this.player.canSwim()) {
               ++this.ticksInWater;
            } else {
               this.ticksInWater -= 2;
            }

            this.ticksInWater = Math.min(this.ticksInWater, 40);
            this.volume = Math.max(0.0F, Math.min((float)this.ticksInWater / 40.0F, 1.0F));
         } else {
            this.donePlaying = true;
         }
      }
   }
}