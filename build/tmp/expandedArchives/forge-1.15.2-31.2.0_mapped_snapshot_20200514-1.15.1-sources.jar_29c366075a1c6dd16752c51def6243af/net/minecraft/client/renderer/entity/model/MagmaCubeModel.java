package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagmaCubeModel<T extends SlimeEntity> extends SegmentedModel<T> {
   private final ModelRenderer[] segments = new ModelRenderer[8];
   private final ModelRenderer core;
   private final ImmutableList<ModelRenderer> field_228271_f_;

   public MagmaCubeModel() {
      for(int i = 0; i < this.segments.length; ++i) {
         int j = 0;
         int k = i;
         if (i == 2) {
            j = 24;
            k = 10;
         } else if (i == 3) {
            j = 24;
            k = 19;
         }

         this.segments[i] = new ModelRenderer(this, j, k);
         this.segments[i].addBox(-4.0F, (float)(16 + i), -4.0F, 8.0F, 1.0F, 8.0F);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F);
      Builder<ModelRenderer> builder = ImmutableList.builder();
      builder.add(this.core);
      builder.addAll(Arrays.asList(this.segments));
      this.field_228271_f_ = builder.build();
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      float f = MathHelper.lerp(partialTick, entityIn.prevSquishFactor, entityIn.squishFactor);
      if (f < 0.0F) {
         f = 0.0F;
      }

      for(int i = 0; i < this.segments.length; ++i) {
         this.segments[i].rotationPointY = (float)(-(4 - i)) * f * 1.7F;
      }

   }

   public ImmutableList<ModelRenderer> getParts() {
      return this.field_228271_f_;
   }
}