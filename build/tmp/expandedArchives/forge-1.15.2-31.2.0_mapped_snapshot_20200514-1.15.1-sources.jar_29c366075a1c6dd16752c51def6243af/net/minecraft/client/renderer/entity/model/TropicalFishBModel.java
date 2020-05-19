package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishBModel<T extends Entity> extends AbstractTropicalFishModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer tail;
   private final ModelRenderer finRight;
   private final ModelRenderer finLeft;
   private final ModelRenderer finTop;
   private final ModelRenderer finBottom;

   public TropicalFishBModel(float p_i48891_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 19;
      this.body = new ModelRenderer(this, 0, 20);
      this.body.addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, p_i48891_1_);
      this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
      this.tail = new ModelRenderer(this, 21, 16);
      this.tail.addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, p_i48891_1_);
      this.tail.setRotationPoint(0.0F, 19.0F, 3.0F);
      this.finRight = new ModelRenderer(this, 2, 16);
      this.finRight.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48891_1_);
      this.finRight.setRotationPoint(-1.0F, 20.0F, 0.0F);
      this.finRight.rotateAngleY = ((float)Math.PI / 4F);
      this.finLeft = new ModelRenderer(this, 2, 12);
      this.finLeft.addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48891_1_);
      this.finLeft.setRotationPoint(1.0F, 20.0F, 0.0F);
      this.finLeft.rotateAngleY = (-(float)Math.PI / 4F);
      this.finTop = new ModelRenderer(this, 20, 11);
      this.finTop.addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_i48891_1_);
      this.finTop.setRotationPoint(0.0F, 16.0F, -3.0F);
      this.finBottom = new ModelRenderer(this, 20, 21);
      this.finBottom.addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_i48891_1_);
      this.finBottom.setRotationPoint(0.0F, 22.0F, -3.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.tail, this.finRight, this.finLeft, this.finTop, this.finBottom);
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