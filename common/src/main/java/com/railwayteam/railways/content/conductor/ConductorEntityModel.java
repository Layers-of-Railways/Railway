/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.Railways;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class ConductorEntityModel<T extends ConductorEntity> extends HumanoidModel<T> implements ArmedModel, HeadedModel {
  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Railways.MODID, "conductor"), "main");

  public ConductorEntityModel (ModelPart root) {
    super(root);
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

    PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

    PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 12.0F, 0.0F));

    PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(34, 0).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 12.0F, 0.0F));

    PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -9.0F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(0, 29).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

    PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(34, 15).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 19.0F, 0.0F));

    PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 15).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 19.0F, 0.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  private float quadraticArmUpdate(float limbSwing) {
    return limbSwing;
//    return -65.0f * limbSwing + limbSwing * limbSwing;
  }

  @Override
  public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // mostly based on HumanoidModel::setupAnim
    // TODO can't call super directly due to rotation anchor offsets, find a way to fix them?
    boolean fallFlying = entity.visualBaseEntity != null && entity.visualBaseEntity.getFallFlyingTicks() > 4;
    boolean visuallySwimming = entity.visualBaseEntity != null && entity.visualBaseEntity.isVisuallySwimming();
    float swimAmount = entity.visualBaseModel != null ? entity.visualBaseModel.swimAmount : this.swimAmount;
    this.head.xRot = fallFlying ? -0.7853982f : (swimAmount > 0.0f ? (visuallySwimming ? this.rotlerpRad(swimAmount, this.head.xRot, -0.7853982f) : this.rotlerpRad(swimAmount, this.head.xRot, headPitch * ((float)Math.PI / 180))) : headPitch * ((float)Math.PI / 180));
    this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

    this.hat.xRot = (float) (this.head.xRot + (-10 * (Math.PI / 180)));
    this.hat.yRot = this.head.yRot;
    float amt = -0.1f*16;
    this.hat.x = (float) (Math.cos(this.head.xRot) * amt * Math.sin(this.head.yRot));
    this.hat.z = (float) (Math.cos(this.head.xRot) * amt * Math.cos(this.head.yRot));
    this.hat.y = ((entity.visualBaseModel != null && entity.visualBaseModel.crouching) ? 14.2f : 10.0f) - (float) (Math.sin(this.head.xRot) * amt);

    this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F * 2 + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
    this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F * 2) * 2.0F * limbSwingAmount * 0.5F;
    this.rightArm.zRot = 0.0f;
    this.leftArm.zRot = 0.0f;

    this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F * 3) * 1.4F * limbSwingAmount;
    this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F * 3 + (float)Math.PI) * 1.4F * limbSwingAmount;
    this.rightLeg.yRot = 0.0F;
    this.leftLeg.yRot = 0.0F;
    this.rightLeg.zRot = 0.0F;
    this.leftLeg.zRot = 0.0F;
    if (this.riding) {
      this.rightArm.xRot += (-(float)Math.PI / 2f);
      this.leftArm.xRot += (-(float)Math.PI / 2f);
      this.rightLeg.xRot = -1.4137167F;
      this.rightLeg.yRot = (-(float)Math.PI / 20f);
      this.rightLeg.zRot = 0.07853982F;
      this.leftLeg.xRot = -1.4137167F;
      this.leftLeg.yRot = ((float)Math.PI / 20f);
      this.leftLeg.zRot = -0.07853982F;
    }
    this.rightArm.yRot = 0.0f;
    this.leftArm.yRot = 0.0f;

    if (entity.visualBaseModel != null && entity.visualBaseModel.crouching) {
      this.body.xRot = 0.5f;
      this.rightArm.xRot += 0.4f;
      this.leftArm.xRot += 0.4f;

      this.rightLeg.z = 3.9f; //
      this.leftLeg.z = 3.9f; //
      this.rightLeg.y = 19.2f; //
      this.leftLeg.y = 19.2f; //
      this.head.y = 14.2f; //
      this.body.z = 4.0f; //
      this.body.y = 20.2f; //
      this.leftArm.y = 15.2f; //
      this.rightArm.y = 15.2f; //
    } else {
      this.body.xRot = 0.0f;

      this.rightLeg.z = 0.0f; //
      this.leftLeg.z = 0.0f; //
      this.rightLeg.y = 19.0f; //
      this.leftLeg.y = 19.0f; //
      this.head.y = 10.0f; //
      this.body.z = 0.0f; //
      this.body.y = 19.0f; //
      this.leftArm.y = 12.0f; //
      this.rightArm.y = 12.0f; //
    }

    if (swimAmount > 0.0f) {
      float j;
      float g = limbSwing % 26.0f;
      float h = swimAmount;
      float i = swimAmount;
      if (g < 14.0f) {
        this.leftArm.xRot = this.rotlerpRad(i, this.leftArm.xRot, 0.0f);
        this.rightArm.xRot = Mth.lerp(h, this.rightArm.xRot, 0.0f);
        this.leftArm.yRot = this.rotlerpRad(i, this.leftArm.yRot, (float) Math.PI);
        this.rightArm.yRot = Mth.lerp(h, this.rightArm.yRot, (float) Math.PI);
        this.leftArm.zRot = this.rotlerpRad(i, this.leftArm.zRot, (float) Math.PI + 1.8707964f * this.quadraticArmUpdate(g) / this.quadraticArmUpdate(14.0f));
        this.rightArm.zRot = Mth.lerp(h, this.rightArm.zRot, (float) Math.PI - 1.8707964f * this.quadraticArmUpdate(g) / this.quadraticArmUpdate(14.0f));
      } else if (g >= 14.0f && g < 22.0f) {
        j = (g - 14.0f) / 8.0f;
        this.leftArm.xRot = this.rotlerpRad(i, this.leftArm.xRot, 1.5707964f * j);
        this.rightArm.xRot = Mth.lerp(h, this.rightArm.xRot, 1.5707964f * j);
        this.leftArm.yRot = this.rotlerpRad(i, this.leftArm.yRot, (float) Math.PI);
        this.rightArm.yRot = Mth.lerp(h, this.rightArm.yRot, (float) Math.PI);
        this.leftArm.zRot = this.rotlerpRad(i, this.leftArm.zRot, 5.012389f - 1.8707964f * j);
        this.rightArm.zRot = Mth.lerp(h, this.rightArm.zRot, 1.2707963f + 1.8707964f * j);
      } else if (g >= 22.0f && g < 26.0f) {
        j = (g - 22.0f) / 4.0f;
        this.leftArm.xRot = this.rotlerpRad(i, this.leftArm.xRot, 1.5707964f - 1.5707964f * j);
        this.rightArm.xRot = Mth.lerp(h, this.rightArm.xRot, 1.5707964f - 1.5707964f * j);
        this.leftArm.yRot = this.rotlerpRad(i, this.leftArm.yRot, (float) Math.PI);
        this.rightArm.yRot = Mth.lerp(h, this.rightArm.yRot, (float) Math.PI);
        this.leftArm.zRot = this.rotlerpRad(i, this.leftArm.zRot, (float) Math.PI);
        this.rightArm.zRot = Mth.lerp(h, this.rightArm.zRot, (float) Math.PI);
      }
      this.leftLeg.xRot = Mth.lerp(swimAmount, this.leftLeg.xRot, 0.3f * Mth.cos(limbSwing * 0.33333334f + (float)Math.PI));
      this.rightLeg.xRot = Mth.lerp(swimAmount, this.rightLeg.xRot, 0.3f * Mth.cos(limbSwing * 0.33333334f));
    }
  }

  @Override
  public void translateToHand(@NotNull HumanoidArm arm, @NotNull PoseStack stack) {
    (arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm).translateAndRotate(stack);
  }

  @Override
  public @NotNull ModelPart getHead() {
    return this.head;
  }

  @Override
  public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    //hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}