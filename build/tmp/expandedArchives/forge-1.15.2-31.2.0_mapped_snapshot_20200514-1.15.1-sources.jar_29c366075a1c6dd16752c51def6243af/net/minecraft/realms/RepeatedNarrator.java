package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RepeatedNarrator {
   final Duration repeatDelay;
   private final float permitsPerSecond;
   final AtomicReference<RepeatedNarrator.Parameter> params;

   public RepeatedNarrator(Duration p_i49961_1_) {
      this.repeatDelay = p_i49961_1_;
      this.params = new AtomicReference<>();
      float f = (float)p_i49961_1_.toMillis() / 1000.0F;
      this.permitsPerSecond = 1.0F / f;
   }

   public void narrate(String p_narrate_1_) {
      RepeatedNarrator.Parameter repeatednarrator$parameter = this.params.updateAndGet((p_229956_2_) -> {
         return p_229956_2_ != null && p_narrate_1_.equals(p_229956_2_.field_214462_a) ? p_229956_2_ : new RepeatedNarrator.Parameter(p_narrate_1_, RateLimiter.create((double)this.permitsPerSecond));
      });
      if (repeatednarrator$parameter.field_214463_b.tryAcquire(1)) {
         NarratorChatListener narratorchatlistener = NarratorChatListener.INSTANCE;
         narratorchatlistener.say(ChatType.SYSTEM, new StringTextComponent(p_narrate_1_));
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class Parameter {
      String field_214462_a;
      RateLimiter field_214463_b;

      Parameter(String p_i50913_1_, RateLimiter p_i50913_2_) {
         this.field_214462_a = p_i50913_1_;
         this.field_214463_b = p_i50913_2_;
      }
   }
}