package com.railwayteam.railways.content.smokestack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DieselSmokeStackRenderer extends SmartBlockEntityRenderer<DieselSmokeStackBlockEntity> {
    public DieselSmokeStackRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(DieselSmokeStackBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
        CachedBufferer.partial(CRBlockPartials.DIESEL_STACK_FAN, te.getBlockState())
            .translate(0.5, 0.5, 0.5)
            .rotateY(te.getFanRotation(te.getRpm(partialTicks)))
            .translate(-0.5, -0.5, -0.5)
            .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
