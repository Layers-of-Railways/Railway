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

package com.railwayteam.railways.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier.MonorailAngles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackVisual;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_BOTTOM;
import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_MIDDLE;
import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_TOP;

@OnlyIn(Dist.CLIENT)
@Mixin(targets = "com.simibubi.create.content.trains.track.TrackVisual$BezierTrackVisual", remap = false)
public abstract class MixinTrackVisual$BezierTrackVisual {
    @Mutable
    @Shadow(remap = false)
    @Final
    private TransformedInstance[] ties;

    @Shadow(remap = false)
    @Final
    @Mutable
    private TransformedInstance[] right;

    @Shadow(remap = false)
    @Final
    @Mutable
    private TransformedInstance[] left;

    @Shadow(remap = false)
    abstract void updateLight();

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/BezierConnection;getSegmentCount()I"))
    private int railways$messWithCtor(BezierConnection instance, Operation<Integer> original) {
        return instance.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL ? 0 : original.call(instance);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/BezierConnection;getBakedSegments()[Lcom/simibubi/create/content/trains/track/BezierConnection$SegmentAngles;"))
    private BezierConnection.SegmentAngles[] railways$messWithCtor2(BezierConnection instance, Operation<BezierConnection.SegmentAngles[]> original) {
        return instance.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL ? new BezierConnection.SegmentAngles[0] : original.call(instance);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void addActualMonorail(TrackVisual trackInstance, BezierConnection bc, CallbackInfo ci) {
        //Use right for top section
        //Use ties for center section
        //use left for bottom section
        if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            PoseStack pose = new PoseStack();
            TransformStack.of(pose)
                .translate(trackInstance.getVisualPosition())
                .nudge((int) bc.bePositions.getFirst()
                    .asLong());

            MonorailAngles[] monorails = ((IMonorailBezier) bc).getBakedMonorails();

            right = new TransformedInstance[monorails.length - 1];
            ties = new TransformedInstance[monorails.length - 1];
            left = new TransformedInstance[monorails.length - 1];

            TransformedInstance[] top = right;
            TransformedInstance[] middle = ties;
            TransformedInstance[] bottom = left;

            InstancerProvider provider = ((AccessorAbstractVisual) trackInstance).railways$getInstancerProvider();

            provider.instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(MONORAIL_SEGMENT_TOP)).createInstances(top);
            provider.instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(MONORAIL_SEGMENT_MIDDLE)).createInstances(middle);
            provider.instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatChunk(MONORAIL_SEGMENT_BOTTOM)).createInstances(bottom);

            for (int i = 1; i < monorails.length; i++) {
                MonorailAngles segment = monorails[i];
                int modelIndex = i - 1;

                PoseStack.Pose beamTransform = segment.beam;

                middle[modelIndex].setTransform(pose)
                    .mul(beamTransform)
                    .setChanged();

                for (boolean isTop : Iterate.trueAndFalse) {
                    PoseStack.Pose beamCapTransform = segment.beamCaps.get(isTop);
                    (isTop ? top : bottom)[modelIndex].setTransform(pose)
                        .mul(beamCapTransform)
                        .setChanged();
                }
            }
        }
    }
}
