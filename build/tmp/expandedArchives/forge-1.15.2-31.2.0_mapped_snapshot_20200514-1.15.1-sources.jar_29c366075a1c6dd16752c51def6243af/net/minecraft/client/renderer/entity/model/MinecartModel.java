package net.minecraft.client.renderer.entity.model;

import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] sideModels = new ModelRenderer[6];

   public MinecartModel() {
      this.sideModels[0] = new ModelRenderer(this, 0, 10);
      this.sideModels[1] = new ModelRenderer(this, 0, 0);
      this.sideModels[2] = new ModelRenderer(this, 0, 0);
      this.sideModels[3] = new ModelRenderer(this, 0, 0);
      this.sideModels[4] = new ModelRenderer(this, 0, 0);
      this.sideModels[5] = new ModelRenderer(this, 44, 10);
      int i = 20;
      int j = 8;
      int k = 16;
      int l = 4;
      this.sideModels[0].addBox(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F, 0.0F);
      this.sideModels[0].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.sideModels[5].addBox(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F, 0.0F);
      this.sideModels[5].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.sideModels[1].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.sideModels[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
      this.sideModels[2].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.sideModels[2].setRotationPoint(9.0F, 4.0F, 0.0F);
      this.sideModels[3].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.sideModels[3].setRotationPoint(0.0F, 4.0F, -7.0F);
      this.sideModels[4].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.sideModels[4].setRotationPoint(0.0F, 4.0F, 7.0F);
      this.sideModels[0].rotateAngleX = ((float)Math.PI / 2F);
      this.sideModels[1].rotateAngleY = ((float)Math.PI * 1.5F);
      this.sideModels[2].rotateAngleY = ((float)Math.PI / 2F);
      this.sideModels[3].rotateAngleY = (float)Math.PI;
      this.sideModels[5].rotateAngleX = (-(float)Math.PI / 2F);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.sideModels[5].rotationPointY = 4.0F - ageInTicks;
   }

   public Iterable<ModelRenderer> getParts() {
      return Arrays.asList(this.sideModels);
   }
}