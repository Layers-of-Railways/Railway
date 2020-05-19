package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldLoadProgressScreen extends Screen {
   private final TrackingChunkStatusListener tracker;
   private long lastNarratorUpdateTime = -1L;
   private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), (p_213039_0_) -> {
      p_213039_0_.defaultReturnValue(0);
      p_213039_0_.put(ChunkStatus.EMPTY, 5526612);
      p_213039_0_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      p_213039_0_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      p_213039_0_.put(ChunkStatus.BIOMES, 8434258);
      p_213039_0_.put(ChunkStatus.NOISE, 13750737);
      p_213039_0_.put(ChunkStatus.SURFACE, 7497737);
      p_213039_0_.put(ChunkStatus.CARVERS, 7169628);
      p_213039_0_.put(ChunkStatus.LIQUID_CARVERS, 3159410);
      p_213039_0_.put(ChunkStatus.FEATURES, 2213376);
      p_213039_0_.put(ChunkStatus.LIGHT, 13421772);
      p_213039_0_.put(ChunkStatus.SPAWN, 15884384);
      p_213039_0_.put(ChunkStatus.HEIGHTMAPS, 15658734);
      p_213039_0_.put(ChunkStatus.FULL, 16777215);
   });

   public WorldLoadProgressScreen(TrackingChunkStatusListener p_i51113_1_) {
      super(NarratorChatListener.EMPTY);
      this.tracker = p_i51113_1_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      NarratorChatListener.INSTANCE.say(I18n.format("narrator.loading.done"));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      String s = MathHelper.clamp(this.tracker.getPercentDone(), 0, 100) + "%";
      long i = Util.milliTime();
      if (i - this.lastNarratorUpdateTime > 2000L) {
         this.lastNarratorUpdateTime = i;
         NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.loading", s)).getString());
      }

      int j = this.width / 2;
      int k = this.height / 2;
      int l = 30;
      drawProgress(this.tracker, j, k + 30, 2, 0);
      this.drawCenteredString(this.font, s, j, k - 9 / 2 - 30, 16777215);
   }

   public static void drawProgress(TrackingChunkStatusListener trackerParam, int p_213038_1_, int p_213038_2_, int p_213038_3_, int p_213038_4_) {
      int i = p_213038_3_ + p_213038_4_;
      int j = trackerParam.getDiameter();
      int k = j * i - p_213038_4_;
      int l = trackerParam.func_219523_d();
      int i1 = l * i - p_213038_4_;
      int j1 = p_213038_1_ - i1 / 2;
      int k1 = p_213038_2_ - i1 / 2;
      int l1 = k / 2 + 1;
      int i2 = -16772609;
      if (p_213038_4_ != 0) {
         fill(p_213038_1_ - l1, p_213038_2_ - l1, p_213038_1_ - l1 + 1, p_213038_2_ + l1, -16772609);
         fill(p_213038_1_ + l1 - 1, p_213038_2_ - l1, p_213038_1_ + l1, p_213038_2_ + l1, -16772609);
         fill(p_213038_1_ - l1, p_213038_2_ - l1, p_213038_1_ + l1, p_213038_2_ - l1 + 1, -16772609);
         fill(p_213038_1_ - l1, p_213038_2_ + l1 - 1, p_213038_1_ + l1, p_213038_2_ + l1, -16772609);
      }

      for(int j2 = 0; j2 < l; ++j2) {
         for(int k2 = 0; k2 < l; ++k2) {
            ChunkStatus chunkstatus = trackerParam.getStatus(j2, k2);
            int l2 = j1 + j2 * i;
            int i3 = k1 + k2 * i;
            fill(l2, i3, l2 + p_213038_3_, i3 + p_213038_3_, COLORS.getInt(chunkstatus) | -16777216);
         }
      }

   }
}