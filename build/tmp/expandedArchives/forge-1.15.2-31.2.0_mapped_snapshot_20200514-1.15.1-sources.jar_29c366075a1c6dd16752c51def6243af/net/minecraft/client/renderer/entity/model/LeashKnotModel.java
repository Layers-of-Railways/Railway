package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeashKnotModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer knotRenderer;

   public LeashKnotModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.knotRenderer = new ModelRenderer(this, 0, 0);
      this.knotRenderer.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
      this.knotRenderer.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.knotRenderer);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.knotRenderer.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.knotRenderer.rotateAngleX = headPitch * ((float)Math.PI / 180F);
   }
}