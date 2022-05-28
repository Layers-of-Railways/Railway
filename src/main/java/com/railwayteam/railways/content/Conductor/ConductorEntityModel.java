package com.railwayteam.railways.content.Conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.Railways;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class ConductorEntityModel<T extends Entity> extends EntityModel<T> implements ArmedModel, HeadedModel {
  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Railways.MODID, "conductor"), "main");

  private final ModelPart Head;
  private final ModelPart Body;
  private final ModelPart RightArm;
  private final ModelPart LeftArm;
  private final ModelPart RightLeg;
  private final ModelPart LeftLeg;
//  private final ModelPart Hat;

  public ConductorEntityModel (ModelPart root) {
    this.Head = root.getChild("Head");
    this.Body = root.getChild("Body");
    this.RightArm = root.getChild("RightArm");
    this.LeftArm = root.getChild("LeftArm");
    this.RightLeg = root.getChild("RightLeg");
    this.LeftLeg = root.getChild("LeftLeg");
    //this.Hat = root.getChild("Hat");
  }

  public static LayerDefinition createBodyLayer () {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 10.0F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
      .texOffs(0, 29).addBox(-3.0F, 15.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 8.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
    PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, 9.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 0.0F));
    PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(50, 15).addBox(-1.1F, 7.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
    PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(34, 15).addBox(-1.9F, 7.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
  //  PartDefinition Hat = partdefinition.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -14.5F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 16.0F, 8.0F));
  //  PartDefinition Hat_r1 = Hat.addOrReplaceChild("Hat_r1", CubeListBuilder.create().texOffs(6, 12).addBox(-4.5F, -0.1681F, -4.6157F, 9.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.963F, -2.9429F, 0.3927F, 0.0F, 0.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim (@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

  }

  @Override
  public void translateToHand (@NotNull HumanoidArm arm, @NotNull PoseStack stack) {
    (arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm).translateAndRotate(stack);
  }

  @Override
  public @NotNull ModelPart getHead () {
    return this.Head;
  }

  @Override
  public void renderToBuffer (@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  //  Hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}