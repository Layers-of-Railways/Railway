package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.simibubi.create.content.logistics.trains.entity.CarriageBogey;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CarriageContraptionEntityRenderer.class, remap = false)
public class MixinCarriageContraptionEntityRenderer {

    /**
     * @author Steam 'n Rails (Railways)
     * @reason Support hanging bogeys etc
     */
    @Overwrite
    public static void translateBogey(PoseStack ms, CarriageBogey bogey, int bogeySpacing, float viewYRot,
                                      float viewXRot, float partialTicks) {
        MonoBogeyBlock.translateBogey(ms, bogey, bogeySpacing, viewYRot, viewXRot, partialTicks);
    }
}
