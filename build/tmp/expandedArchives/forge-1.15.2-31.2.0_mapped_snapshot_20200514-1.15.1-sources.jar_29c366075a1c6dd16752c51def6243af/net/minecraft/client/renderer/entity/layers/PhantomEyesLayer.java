package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomEyesLayer<T extends Entity> extends AbstractEyesLayer<T, PhantomModel<T>> {
   private static final RenderType field_229138_a_ = RenderType.getEyes(new ResourceLocation("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(IEntityRenderer<T, PhantomModel<T>> p_i50928_1_) {
      super(p_i50928_1_);
   }

   public RenderType getRenderType() {
      return field_229138_a_;
   }
}