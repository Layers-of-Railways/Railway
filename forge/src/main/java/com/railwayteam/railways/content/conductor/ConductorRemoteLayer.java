/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ConductorRemoteLayer<T extends ConductorEntity, M extends ConductorEntityModel<T>> extends RenderLayer<T, M> {

	public ConductorRemoteLayer(RenderLayerParent<T, M> pRenderer) {
		super(pRenderer);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
					   @NotNull T conductorEntity, float limbSwing, float limbSwingAmount, float partialTick,
					   float ageInTicks, float netHeadYaw, float headPitch) {
		if (conductorEntity.getJob() == ConductorEntity.Job.REMOTE_CONTROL) {

			poseStack.pushPose();

			getParentModel().getHead().translateAndRotate(poseStack);

			//poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
			//poseStack.translate(-0.5d, -1.2d, -0.94d);


			CachedBuffers.partial(CRBlockPartials.CONDUCTOR_ANTENNA, Blocks.AIR.defaultBlockState())
					/*.rotateY(netHeadYaw)
					.rotateX(headPitch)*/

					.rotateXDegrees(180)
					.translate(3 / 16.0, 3.5 / 16.0, 0 / 16.0)
					.rotateZDegrees(-30)
					.light(packedLight)
					.renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

			poseStack.popPose();
		} else if (conductorEntity.getJob() == ConductorEntity.Job.SPY) {

			poseStack.pushPose();

			getParentModel().getHead().translateAndRotate(poseStack);

			//poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
			//poseStack.translate(-0.5d, -1.2d, -0.94d);


			CachedBuffers.partial(AllPartialModels.BLAZE_GOGGLES, Blocks.AIR.defaultBlockState())
					/*.rotateY(netHeadYaw)
					.rotateX(headPitch)*/

					.rotateZDegrees(180)
					.translate(-8 / 16.0, 2 / 16.0, -8 / 16.0)
					.light(packedLight)
					.renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

			poseStack.popPose();
		}
	}
}
