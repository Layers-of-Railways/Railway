package net.minecraft.client.renderer;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionRenderCacheBuilder {
   private final Map<RenderType, BufferBuilder> builders = RenderType.getBlockRenderTypes().stream().collect(Collectors.toMap((p_228369_0_) -> {
      return p_228369_0_;
   }, (p_228368_0_) -> {
      return new BufferBuilder(p_228368_0_.getBufferSize());
   }));

   public BufferBuilder getBuilder(RenderType renderTypeIn) {
      return this.builders.get(renderTypeIn);
   }

   public void resetBuilders() {
      this.builders.values().forEach(BufferBuilder::reset);
   }

   public void discardBuilders() {
      this.builders.values().forEach(BufferBuilder::discard);
   }
}