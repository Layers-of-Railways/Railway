package com.railwayteam.railways.content.smokestack.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.smokestack.block.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.be.DieselSmokeStackBlockEntity;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class DieselSmokeStackRenderer extends SmartBlockEntityRenderer<DieselSmokeStackBlockEntity> {
    public DieselSmokeStackRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(DieselSmokeStackBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
        Direction dir = te.getBlockState().getValue(DieselSmokeStackBlock.FACING);
        CachedBufferer.partial(CRBlockPartials.DIESEL_STACK_FAN, te.getBlockState())
            .translate(0.5, 0.5, 0.5)
            .rotateX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
            .rotateZ(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot()) % 360)
            .rotateY(te.getFanRotation(te.getRpm(partialTicks)))
            .translate(-0.5, -0.5, -0.5)
            .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
