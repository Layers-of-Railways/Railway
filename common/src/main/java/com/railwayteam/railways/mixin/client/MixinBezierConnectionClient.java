package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BezierConnection.class, remap = false)
public abstract class MixinBezierConnectionClient implements IMonorailBezier {
    @Shadow public abstract int getSegmentCount();

    private MonorailAngles[] bakedMonorails;

    @Override
    @OnlyIn(Dist.CLIENT)
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

            angles.lightPosition = new BlockPos(mainGirder);

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
                    .rotateZ(top ? 0 : 0)
                    .scale(1, 1, (float) diff.length() * scale);
                angles.beamCaps.set(top, poseStack.last());
            }

            previousOffsets = offsets;

        }

        return bakedMonorails;
    }
}
