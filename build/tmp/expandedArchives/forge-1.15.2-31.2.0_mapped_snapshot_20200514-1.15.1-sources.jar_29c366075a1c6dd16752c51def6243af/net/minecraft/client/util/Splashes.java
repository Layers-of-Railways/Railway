package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Splashes extends ReloadListener<List<String>> {
   private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
   private static final Random RANDOM = new Random();
   private final List<String> possibleSplashes = Lists.newArrayList();
   private final Session gameSession;

   public Splashes(Session gameSessionIn) {
      this.gameSession = gameSessionIn;
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected List<String> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
      try (
         IResource iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASHES_LOCATION);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
      ) {
         List list = bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> {
            return p_215277_0_.hashCode() != 125780783;
         }).collect(Collectors.toList());
         return list;
      } catch (IOException var36) {
         return Collections.emptyList();
      }
   }

   protected void apply(List<String> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      this.possibleSplashes.clear();
      this.possibleSplashes.addAll(objectIn);
   }

   @Nullable
   public String getSplashText() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         return "Merry X-mas!";
      } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         return "Happy new year!";
      } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         return "OOoooOOOoooo! Spooky!";
      } else if (this.possibleSplashes.isEmpty()) {
         return null;
      } else {
         return this.gameSession != null && RANDOM.nextInt(this.possibleSplashes.size()) == 42 ? this.gameSession.getUsername().toUpperCase(Locale.ROOT) + " IS YOU" : this.possibleSplashes.get(RANDOM.nextInt(this.possibleSplashes.size()));
      }
   }
}