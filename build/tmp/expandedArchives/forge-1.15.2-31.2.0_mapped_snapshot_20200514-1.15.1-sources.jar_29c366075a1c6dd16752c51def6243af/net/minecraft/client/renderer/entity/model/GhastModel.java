package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Random;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] tentacles = new ModelRenderer[9];
   private final ImmutableList<ModelRenderer> field_228260_b_;

   public GhastModel() {
      Builder<ModelRenderer> builder = ImmutableList.builder();
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 0);
      modelrenderer.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      modelrenderer.rotationPointY = 17.6F;
      builder.add(modelrenderer);
      Random random = new Random(1660L);

      for(int i = 0; i < this.tentacles.length; ++i) {
         this.tentacles[i] = new ModelRenderer(this, 0, 0);
         float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float f1 = ((float)(i / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int j = random.nextInt(7) + 8;
         this.tentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2.0F, (float)j, 2.0F);
         this.tentacles[i].rotationPointX = f;
         this.tentacles[i].rotationPointZ = f1;
         this.tentacles[i].rotationPointY = 24.6F;
         builder.add(this.tentacles[i]);
      }

      this.field_228260_b_ = builder.build();
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      for(int i = 0; i < this.tentacles.length; ++i) {
         this.tentacles[i].rotateAngleX = 0.2F * MathHelper.sin(ageInTicks * 0.3F + (float)i) + 0.4F;
      }

   }

   public Iterable<ModelRenderer> getParts() {
      return this.field_228260_b_;
   }
}