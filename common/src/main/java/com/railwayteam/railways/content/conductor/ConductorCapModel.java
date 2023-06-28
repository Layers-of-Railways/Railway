package com.railwayteam.railways.content.conductor;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRDevCaps;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConductorCapModel<T extends LivingEntity> extends Model implements HeadedModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Railways.MODID, "conductor_cap"), "main");

	private final ModelPart cap;
	@Nullable
	private final PartialModel override;
	private final boolean doNotTilt;

	public ConductorCapModel(ModelPart root, @Nullable PartialModel override, boolean doNotTilt) {
		super(override == null ? RenderType::armorCutoutNoCull : rl -> RenderType.cutout());
		this.cap = root.getChild("cap");
		this.override = override;
		this.doNotTilt = doNotTilt;
	}

	private static ConductorCapModel<?> defaultModel = null;
	private static final Map<String, ConductorCapModel<?>> customModels = new HashMap<>();

	public static void clearModelCache() {
		defaultModel = null;
		customModels.clear();
	}

	public static ConductorCapModel<?> of(ItemStack stack, HumanoidModel<?> base, LivingEntity entity) {
		if (defaultModel == null) {
			EntityModelSet set = Minecraft.getInstance().getEntityModels();
			ModelPart root = set.bakeLayer(ConductorCapModel.LAYER_LOCATION);
			CRBlockPartials.CUSTOM_CONDUCTOR_CAPS.forEach((name, partial) -> {
				ConductorCapModel<?> model = new ConductorCapModel<>(root, partial, CRBlockPartials.shouldPreventTiltingCap(name));
				customModels.put(name, model);
			});
			defaultModel = new ConductorCapModel<>(root, null, false);
		}
		String name = stack.getHoverName().getString();
		if (name.equals("IThundxr") && entity instanceof ConductorEntity) {
			EntityModelSet set = Minecraft.getInstance().getEntityModels();
			ModelPart root = set.bakeLayer(ConductorCapModel.LAYER_LOCATION);
			ConductorCapModel<?> model = new ConductorCapModel<>(root, CRDevCaps.ITHUNDXR_MODEL, true);
			model.setProperties(base);
			return model;
		}
		ConductorCapModel<?> model = customModels.getOrDefault(name, defaultModel);
		model.setProperties(base);
		return model;
	}

	public static LayerDefinition createBodyLayer () {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hat = partdefinition.addOrReplaceChild("cap",
			CubeListBuilder.create().texOffs(0, 0)
				.addBox(-4.5F, -9.0F, -4.0F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition brim = hat.addOrReplaceChild("brim",
			CubeListBuilder.create().texOffs(6, 12)
				.addBox(-4.5F, 3.8F, -3.0F, 9.0F, 0.02F, 3.0F, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(0.0F, -10.0F, -4.0F, 0.2618F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (override != null) {
			poseStack.pushPose();
			cap.translateAndRotate(poseStack);
//			poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 0, 180)));
			poseStack.translate(0, -0.25d, 0);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			poseStack.scale(0.625F, -0.625F, -0.625F);
			// DONE: this should be an easy replacement but it requires tweaking and testing - simple replacement
//			ForgeHooksClient.handleCameraTransforms(poseStack, override.get(), ItemTransforms.TransformType.HEAD, false);
//			override.get().applyTransform(ItemTransforms.TransformType.HEAD, poseStack, false);
			override.get().getTransforms().head.apply(false, poseStack);
			poseStack.translate(-0.5, -0.5, -0.5);
			CachedBufferer.partial(override, Blocks.AIR.defaultBlockState())
				.light(packedLight)
				.overlay(packedOverlay)
				.renderInto(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.translucent()));
			poseStack.popPose();
		} else {
			cap.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	@Override
	public ModelPart getHead() {
		return this.cap;
	}

	public void setProperties (HumanoidModel<?> base) {
		this.cap.copyFrom(doNotTilt ? base.head : base.hat);
	}
}