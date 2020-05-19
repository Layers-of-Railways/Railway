package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractHorseRenderer<T extends AbstractHorseEntity, M extends HorseModel<T>> extends MobRenderer<T, M> {
   private final float scale;

   public AbstractHorseRenderer(EntityRendererManager renderManagerIn, M p_i50975_2_, float scaleIn) {
      super(renderManagerIn, p_i50975_2_, 0.75F);
      this.scale = scaleIn;
   }

   protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(this.scale, this.scale, this.scale);
      super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
   }
}