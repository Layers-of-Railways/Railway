/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
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
import org.joml.Vector3f;

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
	private static final Map<Pair<String, Boolean>, ConductorCapModel<?>> customModels = new HashMap<>();

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
				customModels.put(Pair.of(name, false), model); // normal caps should apply whether they are on a conductor
				customModels.put(Pair.of(name, true), model);
			});
			CRBlockPartials.CUSTOM_CONDUCTOR_ONLY_CAPS.forEach((name, partial) -> {
				ConductorCapModel<?> model = new ConductorCapModel<>(root, partial, CRBlockPartials.shouldPreventTiltingCap(name));
				Pair<String, Boolean> key = Pair.of(name, true); // for conductors, override conductor model
				customModels.put(key, model);
			});
			defaultModel = new ConductorCapModel<>(root, null, false);
		}
		String name = stack.getHoverName().getString();
		if (name.startsWith("[sus]"))
			name = name.substring(5);
		ConductorCapModel<?> model = customModels.getOrDefault(Pair.of(name, entity instanceof ConductorEntity), defaultModel);
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
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
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
		if (base instanceof PlayerModel<?> && override == null) {
			float s = 1 / 16f;
			this.cap.offsetScale(new Vector3f(s, s, s));
			this.cap.offsetPos(new Vector3f(0, 0, -10 / 16f));
		}
	}
}