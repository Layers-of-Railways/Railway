package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicTicker {
   private final Random random = new Random();
   private final Minecraft client;
   private ISound currentMusic;
   private int timeUntilNextMusic = 100;

   public MusicTicker(Minecraft client) {
      this.client = client;
   }

   public void tick() {
      MusicTicker.MusicType musicticker$musictype = this.client.getAmbientMusicType();
      if (this.currentMusic != null) {
         if (!musicticker$musictype.getSound().getName().equals(this.currentMusic.getSoundLocation())) {
            this.client.getSoundHandler().stop(this.currentMusic);
            this.timeUntilNextMusic = MathHelper.nextInt(this.random, 0, musicticker$musictype.getMinDelay() / 2);
         }

         if (!this.client.getSoundHandler().isPlaying(this.currentMusic)) {
            this.currentMusic = null;
            this.timeUntilNextMusic = Math.min(MathHelper.nextInt(this.random, musicticker$musictype.getMinDelay(), musicticker$musictype.getMaxDelay()), this.timeUntilNextMusic);
         }
      }

      this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, musicticker$musictype.getMaxDelay());
      if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
         this.play(musicticker$musictype);
      }

   }

   /**
    * Plays a music track for the maximum allowable period of time
    */
   public void play(MusicTicker.MusicType type) {
      this.currentMusic = SimpleSound.music(type.getSound());
      this.client.getSoundHandler().play(this.currentMusic);
      this.timeUntilNextMusic = Integer.MAX_VALUE;
   }

   public void stop() {
      if (this.currentMusic != null) {
         this.client.getSoundHandler().stop(this.currentMusic);
         this.currentMusic = null;
         this.timeUntilNextMusic = 0;
      }

   }

   public boolean isPlaying(MusicTicker.MusicType type) {
      return this.currentMusic == null ? false : type.getSound().getName().equals(this.currentMusic.getSoundLocation());
   }

   @OnlyIn(Dist.CLIENT)
   public static enum MusicType {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent sound;
      private final int minDelay;
      private final int maxDelay;

      private MusicType(SoundEvent sound, int minDelayIn, int maxDelayIn) {
         this.sound = sound;
         this.minDelay = minDelayIn;
         this.maxDelay = maxDelayIn;
      }

      /**
       * Gets the {@link SoundEvent} containing the current music track's location
       */
      public SoundEvent getSound() {
         return this.sound;
      }

      /**
       * Returns the minimum delay between playing music of this type.
       */
      public int getMinDelay() {
         return this.minDelay;
      }

      /**
       * Returns the maximum delay between playing music of this type.
       */
      public int getMaxDelay() {
         return this.maxDelay;
      }
   }
}