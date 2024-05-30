/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BezierConnection.class, remap = false)
public abstract class MixinBezierConnectionClient implements IMonorailBezier {
    @Shadow public abstract int getSegmentCount();

    private MonorailAngles[] bakedMonorails;

    @Override
    @Environment(EnvType.CLIENT)
    public MonorailAngles[] getBakedMonorails() {
        if (bakedMonorails != null)
            return bakedMonorails;

        int segmentCount = getSegmentCount();
        bakedMonorails = new MonorailAngles[segmentCount + 1];
        Couple<Vec3> previousOffsets = null;

        BezierConnection this_ = (BezierConnection) (Object) this;

        for (BezierConnection.Segment segment : this_) {
            int i = segment.index;
            boolean end = i == 0 || i == segmentCount;
            MonorailAngles angles = bakedMonorails[i] = new MonorailAngles();

            Vec3 mainGirder = segment.position;//.add(segment.normal.scale(.965f));
//            Vec3 rightGirder = segment.position.subtract(segment.normal.scale(.965f));
            Vec3 upNormal = segment.derivative.normalize()
                .cross(segment.normal);
            Vec3 firstGirderOffset = upNormal.scale(8 / 16f);
            Vec3 secondGirderOffset = upNormal.scale(-10 / 16f);
            Vec3 mainTop = segment.position//.add(segment.normal.scale(1))
                .add(firstGirderOffset);
//            Vec3 rightTop = segment.position.subtract(segment.normal.scale(1))
//                .add(firstGirderOffset);
            Vec3 mainBottom = mainTop.add(secondGirderOffset);
//            Vec3 rightBottom = rightTop.add(secondGirderOffset);

            angles.lightPosition = BlockPos.containing(mainGirder);

            Couple<Vec3> offsets =
                Couple.create(mainTop, mainBottom);

            if (previousOffsets == null) {
                previousOffsets = offsets;
                continue;
            }

            angles.beam = null;
            angles.beamCaps = Couple.create(null, null);
            float scale = end ? 2.3f : 2.2f;

            // Middle
            Vec3 currentBeam = offsets.getFirst()
                .add(offsets.getSecond())
                .scale(.5);
            Vec3 previousBeam = previousOffsets.getFirst()
                .add(previousOffsets.getSecond())
                .scale(.5);
            Vec3 beamDiff = currentBeam.subtract(previousBeam);
            Vec3 beamAngles = TrackRenderer.getModelAngles(segment.normal, beamDiff);

            PoseStack poseStack = new PoseStack();
            TransformStack.cast(poseStack)
                .translate(previousBeam)
                .rotateYRadians(beamAngles.y)
                .rotateXRadians(beamAngles.x)
                .rotateZRadians(beamAngles.z)
                .translate(0, 2 / 16f + (segment.index % 2 == 0 ? 1 : -1) / 2048f - 1 / 1024f, -1 / 32f)
                .scale(1, 1, (float) beamDiff.length() * scale);
            angles.beam = poseStack.last();

            // Caps
            for (boolean top : Iterate.trueAndFalse) {
                Vec3 current = offsets.get(top);
                Vec3 previous = previousOffsets.get(top);
                Vec3 diff = current.subtract(previous);
                Vec3 capAngles = TrackRenderer.getModelAngles(segment.normal, diff);

                poseStack = new PoseStack();
                TransformStack.cast(poseStack)
                    .translate(previous)
                    .rotateYRadians(capAngles.y)
                    .rotateXRadians(capAngles.x)
                    .rotateZRadians(capAngles.z)
                    .translate(0, 2 / 16f + (segment.index % 2 == 0 ? 1 : -1) / 2048f - 1 / 1024f, -1 / 32f)
                    .rotateZ(0)
                    .scale(1, 1, (float) diff.length() * scale);
                angles.beamCaps.set(top, poseStack.last());
            }

            previousOffsets = offsets;

        }

        return bakedMonorails;
    }

    @ModifyExpressionValue(method="getBakedSegments", at = @At(value = "CONSTANT", args = "doubleValue=0.9649999737739563"))
    private double modifyRailWidth(double original) {
        BezierConnection this$ = (BezierConnection) (Object) this;
        if (this$.getMaterial().trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE) {
            return original + 0.5;
        } else if (this$.getMaterial().trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE) {
            return original - (7 / 16.);
        }
        return original;
    }
}
