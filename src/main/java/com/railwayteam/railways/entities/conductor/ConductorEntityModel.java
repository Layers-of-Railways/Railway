package com.railwayteam.railways.entities.conductor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.content.contraptions.components.actors.SeatEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//public class ConductorEntityModel extends AnimatedGeoModel<ConductorEntity>
//{
//	public ConductorEntityModel() {
//
//	}
//
//	@Override
//	public ResourceLocation getModelLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "geo/conductor.geo.json");
//	}
//
//	@Override
//	public ResourceLocation getTextureLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "textures/entity/conductor.png");
//	}
//
//	@Override
//	public ResourceLocation getAnimationFileLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "animations/conductor.animation.json");
//	}
//
//	@Override
//	public void setLivingAnimations(ConductorEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
//		super.setLivingAnimations(entity, uniqueID, customPredicate);
//
//		IBone head = this.getAnimationProcessor().getBone("Head");
//
//		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
//		head.setRotationX(extraData.headPitch * ((float) Math.PI / 360F));
//		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 340F));
//	}
//}


public class ConductorEntityModel extends EntityModel<ConductorEntity> implements IHasArm, IHasHead {

	public final ModelRenderer Head;
	public final ModelRenderer Body;
	public final ModelRenderer RightArm;
	public final ModelRenderer LeftArm;
	public final ModelRenderer RightLeg;
	public final ModelRenderer LeftLeg;
//	private final ModelRenderer hat;
//	private final ModelRenderer hatBrim;

	public ConductorEntityModel() {
		super();

		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 6.5F, 0.0F);
		Head.setTextureOffset(0, 1).addCuboid(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 8.0F, 0.0F, true);

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 17.0F, 0.0F);
		Body.setTextureOffset(2, 16).addCuboid(-4.0F, -7.0F, -3.0F, 8.0F, 5.0F, 6.0F, 0.0F, false);
		Body.setTextureOffset(5, 27).addCuboid(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, 0.0F, false);

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-4.0F, 11.0F, 0.0F);
		RightArm.setTextureOffset(50, 0).addCuboid(-3.0F, -1.0F, -2.0F, 3.0F, 9.0F, 4.0F, 0.0F, false);

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(4.0F, 11.0F, 0.0F);
		LeftArm.setTextureOffset(36, 0).addCuboid(0.0F, -1.0F, -2.0F, 3.0F, 9.0F, 4.0F, 0.0F, false);

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-1.9F, 20.0F, 0.0F);
		RightLeg.setTextureOffset(50, 13).addCuboid(-1.1F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(1.9F, 20.0F, 0.0F);
		LeftLeg.setTextureOffset(36, 13).addCuboid(-1.9F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

//		body.addChild(legLeft);
//		body.addChild(legRight);
//		body.addChild(armLeft);
//		body.addChild(armRight);
//		body.addChild(head);
//		body.setRotationPoint(8.0F, 0.0F, -8.0F);
	}

	@Override
	public void setAngles(ConductorEntity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
		Head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
		Head.rotateAngleY = headYaw * ((float)Math.PI / 180F);

		RightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		LeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

		RightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		LeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;

		// default angles
		RightArm.rotateAngleZ = 0.0F;
		LeftArm.rotateAngleZ = 0.0F;
		RightArm.rotateAngleX = 0;
		LeftArm.rotateAngleX = 0;
		RightArm.rotateAngleY = 0;
		LeftArm.rotateAngleY = 0;
		RightLeg.rotateAngleY = 0.0F;
		LeftLeg.rotateAngleY = 0.0F;
		RightLeg.rotateAngleZ = 0.0F;
		LeftLeg.rotateAngleZ = 0.0F;
		Body.rotateAngleX = 0;

		// default rotation points
		RightArm.rotationPointY = 11;
		LeftArm.rotationPointY = 11;
		Body.rotationPointY = 17;
		Head.rotationPointY = 6.5F;
		Head.rotationPointZ = 0;
		RightLeg.rotationPointY = 20;
		LeftLeg.rotationPointY = 20;
		RightLeg.rotationPointZ = 0;
		LeftLeg.rotationPointZ = 0;

		// sitting poses
		if (this.isSitting) {
			Entity riding = entity.getRidingEntity();
			if(riding != null) {
				if(riding instanceof MinecartEntity) {
					setMinecartPose();
				} else if(riding instanceof SeatEntity || riding instanceof AbstractContraptionEntity) {
					setSeatPose();
				} else {
					setSittingPose();
				}
			} else {
				setSittingPose();
			}
		}
	}

	public void setMinecartPose() {
		LeftArm.rotateAngleX = -45.5F;
		LeftArm.rotateAngleY -= 0.2;
		RightArm.rotateAngleX = -45.5F;
		RightArm.rotateAngleY += 0.2;
		// leg angles copied from biped model
		RightLeg.rotateAngleX = -1.4137167F;
		RightLeg.rotateAngleY = ((float)Math.PI / 10F);
		RightLeg.rotateAngleZ = 0.07853982F;
		LeftLeg.rotateAngleX = -1.4137167F;
		LeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
		LeftLeg.rotateAngleZ = -0.07853982F;
	}

	public void setSeatPose() {
		RightArm.rotationPointY += 2;
		RightArm.rotateAngleX += 0.5F;

		LeftArm.rotationPointY += 2;
		LeftArm.rotateAngleX += 0.5F;

		RightLeg.rotateAngleX = 80;
		RightLeg.rotateAngleY += 0.5F;
		RightLeg.rotationPointY -= 1;
		RightLeg.rotationPointZ -= 3;

		LeftLeg.rotateAngleX = 80;
		LeftLeg.rotateAngleY -= 0.5F;
		LeftLeg.rotationPointY -= 1;
		LeftLeg.rotationPointZ -= 3;

		Body.rotationPointY += 1; // WHY DOES ADDING TO THE ROTATION POINT Y LOWER IT THIS DOESNT MAKE ANY SENSE
		Body.rotateAngleX -= 0.2;

		Head.rotationPointY += 1;
		Head.rotationPointZ += 1;
	}

	public void setSittingPose() {
		// copied from biped model
		RightArm.rotateAngleX += (-(float)Math.PI / 5F);
		LeftArm.rotateAngleX += (-(float)Math.PI / 5F);
		RightLeg.rotateAngleX = -1.4137167F;
		RightLeg.rotateAngleY = ((float)Math.PI / 10F);
		RightLeg.rotateAngleZ = 0.07853982F;
		LeftLeg.rotateAngleX = -1.4137167F;
		LeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
		LeftLeg.rotateAngleZ = -0.07853982F;
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Arrays.asList(Head, Body, RightArm, LeftArm, RightLeg, LeftLeg)
				.forEach(part -> part.render(matrixStack, buffer, packedLight, packedOverlay));
	}

	@Override
	public void setArmAngle(HandSide hand, MatrixStack stack) {
		(hand == HandSide.LEFT ? LeftArm : RightArm).rotate(stack);
	}

	@Override
	public ModelRenderer func_205072_a() { // getModelHead
		return Head;
	}
}