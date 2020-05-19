package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class JigsawPatternRegistry {
   private final Map<ResourceLocation, JigsawPattern> registry = Maps.newHashMap();

   public JigsawPatternRegistry() {
      this.register(JigsawPattern.EMPTY);
   }

   public void register(JigsawPattern pattern) {
      this.registry.put(pattern.getName(), pattern);
   }

   public JigsawPattern get(ResourceLocation name) {
      JigsawPattern jigsawpattern = this.registry.get(name);
      return jigsawpattern != null ? jigsawpattern : JigsawPattern.INVALID;
   }
}