package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.simibubi.create.content.logistics.trains.entity.CarriageBogey;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CarriageContraptionEntityRenderer.class, remap = false) //TODO bogey api
public class MixinCarriageContraptionEntityRenderer {

    /**
     * @author Steam 'n Rails (Railways)
     * @reason Support hanging bogeys etc
     */
    @Overwrite
    public static void translateBogey(PoseStack ms, CarriageBogey bogey, int bogeySpacing, float viewYRot,
                                      float viewXRot, float partialTicks) {
        boolean selfUpsideDown = IPotentiallyUpsideDownBogeyBlock.isUpsideDown(bogey);
        boolean leadingUpsideDown = IPotentiallyUpsideDownBogeyBlock.isUpsideDown(bogey.carriage.leadingBogey());
        TransformStack.cast(ms)
            .rotateY(viewYRot + 90)
            .rotateX(-viewXRot)
            .rotateY(180)
            .translate(0, 0, ((AccessorCarriageBogey)bogey).isLeading() ? 0 : -bogeySpacing)
            .rotateY(-180)
            .rotateX(viewXRot)
            .rotateY(-viewYRot - 90)
            .rotateY(((AccessorCarriageBogey) bogey).getYaw().getValue(partialTicks))
            .rotateX(((AccessorCarriageBogey) bogey).getPitch().getValue(partialTicks))
            .translate(0, .5f, 0) //END ORIGINAL
            .rotateZ(selfUpsideDown ? 180 : 0)
            .translateY(selfUpsideDown != leadingUpsideDown ? 2 : 0);
    }
}
