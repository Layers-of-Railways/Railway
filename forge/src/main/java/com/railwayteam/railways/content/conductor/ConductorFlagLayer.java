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
import net.createmod.catnip.render.CachedBuffers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ConductorFlagLayer<T extends ConductorEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

	public ConductorFlagLayer(RenderLayerParent<T, M> pRenderer) {
		super(pRenderer);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T conductorEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (conductorEntity.isHoldingSchedulesClient()) {

			poseStack.pushPose();

			//poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
			//poseStack.translate(-0.5d, -1.2d, -0.94d);


			CachedBuffers.partial(CRBlockPartials.CONDUCTOR_WHISTLE_FLAGS.get(conductorEntity.getColor()), Blocks.AIR.defaultBlockState())
					.translate(-0.78125, 0.15, -0.688)
					.light(packedLight)
					.renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

			poseStack.popPose();
		}
	}
}
