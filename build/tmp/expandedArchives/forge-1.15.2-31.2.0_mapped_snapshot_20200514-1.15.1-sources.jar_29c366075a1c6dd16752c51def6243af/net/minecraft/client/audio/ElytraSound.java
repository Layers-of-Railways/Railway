package net.minecraft.client.audio;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraSound extends TickableSound {
   private final ClientPlayerEntity player;
   private int time;

   public ElytraSound(ClientPlayerEntity playerIn) {
      super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
      this.player = playerIn;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      if (!this.player.removed && (this.time <= 20 || this.player.isElytraFlying())) {
         this.x = (float)this.player.getPosX();
         this.y = (float)this.player.getPosY();
         this.z = (float)this.player.getPosZ();
         float f = (float)this.player.getMotion().lengthSquared();
         if ((double)f >= 1.0E-7D) {
            this.volume = MathHelper.clamp(f / 4.0F, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if (this.time < 20) {
            this.volume = 0.0F;
         } else if (this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float f1 = 0.8F;
         if (this.volume > 0.8F) {
            this.pitch = 1.0F + (this.volume - 0.8F);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}