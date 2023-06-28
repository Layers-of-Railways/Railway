package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
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

			//poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
			//poseStack.translate(-0.5d, -1.2d, -0.94d);


			CachedBufferer.partial(CRBlockPartials.CONDUCTOR_ANTENNA, Blocks.AIR.defaultBlockState())
					/*.rotateY(netHeadYaw)
					.rotateX(headPitch)*/

					.rotateX(180)
					.translate(3 / 16.0, 3.5 / 16.0, 0 / 16.0)
					.rotateZ(-30)
					.light(packedLight)
					.renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

			poseStack.popPose();
		} else if (conductorEntity.getJob() == ConductorEntity.Job.SPY) {

			poseStack.pushPose();

			getParentModel().getHead().translateAndRotate(poseStack);

			//poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
			//poseStack.translate(-0.5d, -1.2d, -0.94d);


			CachedBufferer.partial(AllPartialModels.BLAZE_GOGGLES, Blocks.AIR.defaultBlockState())
					/*.rotateY(netHeadYaw)
					.rotateX(headPitch)*/

					.rotateZ(180)
					.translate(-8 / 16.0, 2 / 16.0, -8 / 16.0)
					.light(packedLight)
					.renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

			poseStack.popPose();
		}
	}
}
