package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetaParticle extends Particle {
   protected MetaParticle(World p_i51020_1_, double p_i51020_2_, double p_i51020_4_, double p_i51020_6_) {
      super(p_i51020_1_, p_i51020_2_, p_i51020_4_, p_i51020_6_);
   }

   protected MetaParticle(World p_i51021_1_, double p_i51021_2_, double p_i51021_4_, double p_i51021_6_, double p_i51021_8_, double p_i51021_10_, double p_i51021_12_) {
      super(p_i51021_1_, p_i51021_2_, p_i51021_4_, p_i51021_6_, p_i51021_8_, p_i51021_10_, p_i51021_12_);
   }

   public final void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.NO_RENDER;
   }
}