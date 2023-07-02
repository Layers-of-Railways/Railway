package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import org.jetbrains.annotations.NotNull;

public class ConductorElytraLayer<T extends ConductorEntity, M extends ConductorEntityModel<T>> extends ElytraLayer<T, M> {
    public ConductorElytraLayer(RenderLayerParent<T, M> renderer, EntityModelSet loader) {
        super(renderer, loader);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, int packedLight,
                       @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();
        matrixStack.scale(0.7f, 0.7f, 0.7f);
        matrixStack.translate(0, 16 / 16.0, 0);
        super.render(matrixStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        matrixStack.popPose();
    }
}
