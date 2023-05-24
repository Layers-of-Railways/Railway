package com.railwayteam.railways.content.coupling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.logistics.trains.IBogeyBlock;
import com.simibubi.create.content.logistics.trains.track.StandardBogeyTileEntity;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class VirtualCouplerRendering {
    private VirtualCouplerRendering() {}

    public static void renderCoupler(Direction direction, double couplingDistance, boolean front, float partialTicks,
                                     PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                     StandardBogeyTileEntity te) {
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState air = Blocks.AIR.defaultBlockState();

        if (te.getBlockState().getBlock() instanceof IBogeyBlock bogeyBlock) {
            Vec3 anchor = bogeyBlock.getConnectorAnchorOffset().multiply(front ? -1 : 1, 1, front ? -1 : 1);
//                .add(Vec3.atBottomCenterOf(te.getBlockPos()));
            Vec3 anchor2 = anchor.relative(direction, couplingDistance);

            double diffX = anchor2.x - anchor.x;
            double diffY = anchor2.y - anchor.y;
            double diffZ = anchor2.z - anchor.z;
            float yRot = AngleHelper.deg(Mth.atan2(diffZ, diffX)) + 90;
            float xRot = AngleHelper.deg(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)));

            ms.pushPose();
            ms.pushPose();
            ms.translate(anchor.x, anchor.y, anchor.z);
            CachedBufferer.partial(AllBlockPartials.TRAIN_COUPLING_HEAD, air)
                .rotateY(-yRot)
                .rotateX(xRot)
                .light(light)
                .renderInto(ms, vb);

            float margin = 3 / 16f;
            int couplingSegments = (int) Math.round(couplingDistance * 4);
            double stretch = 1.0;

            for (int j = 0; j < couplingSegments; j++) {
                CachedBufferer.partial(AllBlockPartials.TRAIN_COUPLING_CABLE, air)
                    .rotateY(-yRot + 180)
                    .rotateX(-xRot)
                    .translate(0, 0, margin + 2 / 16f)
                    .scale(1, 1, (float) stretch)
                    .translate(0, 0, j / 4f)
                    .light(light)
                    .renderInto(ms, vb);
            }

            ms.popPose();

            ms.pushPose();
            ms.translate(anchor2.x, anchor2.y, anchor2.z);
            CachedBufferer.partial(AllBlockPartials.TRAIN_COUPLING_HEAD, air)
                .rotateY(-yRot + 180)
                .rotateX(-xRot)
                .light(light)
                .renderInto(ms, vb);
            ms.popPose();
            ms.popPose();
        }
    }
}
