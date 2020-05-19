package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishAModel<T extends Entity> extends AbstractTropicalFishModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer tail;
   private final ModelRenderer finRight;
   private final ModelRenderer finLeft;
   private final ModelRenderer finTop;

   public TropicalFishAModel(float p_i48892_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, p_i48892_1_);
      this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.tail = new ModelRenderer(this, 22, -6);
      this.tail.addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, p_i48892_1_);
      this.tail.setRotationPoint(0.0F, 22.0F, 3.0F);
      this.finRight = new ModelRenderer(this, 2, 16);
      this.finRight.addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48892_1_);
      this.finRight.setRotationPoint(-1.0F, 22.5F, 0.0F);
      this.finRight.rotateAngleY = ((float)Math.PI / 4F);
      this.finLeft = new ModelRenderer(this, 2, 12);
      this.finLeft.addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48892_1_);
      this.finLeft.setRotationPoint(1.0F, 22.5F, 0.0F);
      this.finLeft.rotateAngleY = (-(float)Math.PI / 4F);
      this.finTop = new ModelRenderer(this, 10, -5);
      this.finTop.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, p_i48892_1_);
      this.finTop.setRotationPoint(0.0F, 20.5F, -3.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.tail, this.finRight, this.finLeft, this.finTop);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.5F;
      }

      this.tail.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * ageInTicks);
   }
}