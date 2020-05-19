package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerModel<T extends Entity> extends SegmentedModel<T> implements IHasHead, IHeadToggle {
   protected ModelRenderer villagerHead;
   protected ModelRenderer field_217151_b;
   protected final ModelRenderer field_217152_f;
   protected final ModelRenderer villagerBody;
   protected final ModelRenderer field_217153_h;
   protected final ModelRenderer villagerArms;
   protected final ModelRenderer rightVillagerLeg;
   protected final ModelRenderer leftVillagerLeg;
   protected final ModelRenderer villagerNose;

   public VillagerModel(float scale) {
      this(scale, 64, 64);
   }

   public VillagerModel(float p_i51059_1_, int p_i51059_2_, int p_i51059_3_) {
      float f = 0.5F;
      this.villagerHead = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerHead.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51059_1_);
      this.field_217151_b = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217151_b.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217151_b.setTextureOffset(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51059_1_ + 0.5F);
      this.villagerHead.addChild(this.field_217151_b);
      this.field_217152_f = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217152_f.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217152_f.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, p_i51059_1_);
      this.field_217152_f.rotateAngleX = (-(float)Math.PI / 2F);
      this.field_217151_b.addChild(this.field_217152_f);
      this.villagerNose = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerNose.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, p_i51059_1_);
      this.villagerHead.addChild(this.villagerNose);
      this.villagerBody = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerBody.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, p_i51059_1_);
      this.field_217153_h = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217153_h.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217153_h.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, p_i51059_1_ + 0.5F);
      this.villagerBody.addChild(this.field_217153_h);
      this.villagerArms = (new ModelRenderer(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerArms.setRotationPoint(0.0F, 2.0F, 0.0F);
      this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, p_i51059_1_);
      this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, p_i51059_1_, true);
      this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, p_i51059_1_);
      this.rightVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
      this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51059_1_);
      this.leftVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.leftVillagerLeg.mirror = true;
      this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
      this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51059_1_);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.villagerHead, this.villagerBody, this.rightVillagerLeg, this.leftVillagerLeg, this.villagerArms);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      boolean flag = false;
      if (entityIn instanceof AbstractVillagerEntity) {
         flag = ((AbstractVillagerEntity)entityIn).getShakeHeadTicks() > 0;
      }

      this.villagerHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.villagerHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      if (flag) {
         this.villagerHead.rotateAngleZ = 0.3F * MathHelper.sin(0.45F * ageInTicks);
         this.villagerHead.rotateAngleX = 0.4F;
      } else {
         this.villagerHead.rotateAngleZ = 0.0F;
      }

      this.villagerArms.rotationPointY = 3.0F;
      this.villagerArms.rotationPointZ = -1.0F;
      this.villagerArms.rotateAngleX = -0.75F;
      this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
      this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
      this.rightVillagerLeg.rotateAngleY = 0.0F;
      this.leftVillagerLeg.rotateAngleY = 0.0F;
   }

   public ModelRenderer getModelHead() {
      return this.villagerHead;
   }

   public void func_217146_a(boolean p_217146_1_) {
      this.villagerHead.showModel = p_217146_1_;
      this.field_217151_b.showModel = p_217146_1_;
      this.field_217152_f.showModel = p_217146_1_;
   }
}