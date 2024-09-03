/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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


import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier.MonorailAngles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackInstance;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

@Environment(EnvType.CLIENT)
@Mixin(targets = "com.simibubi.create.content.trains.track.TrackInstance$BezierTrackInstance", remap = false)
public abstract class MixinTrackInstance_BezierTrackInstance {

    @Final
    @Shadow(remap = false)
    TrackInstance this$0;

    @Mutable
    @Shadow(remap = false)
    @Final
    private ModelData[] ties;

    @Shadow(remap = false)
    @Final
    @Mutable
    private ModelData[] right;

    @Shadow(remap = false)
    @Final
    @Mutable
    private ModelData[] left;

    @Shadow(remap = false)
    @Final
    @Mutable
    private BlockPos[] tiesLightPos;

    @Shadow(remap = false)
    @Final
    @Mutable
    private BlockPos[] leftLightPos;

    @Shadow(remap = false)
    @Final
    @Mutable
    private BlockPos[] rightLightPos;

    @Shadow(remap = false)
    abstract void updateLight();


    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/BezierConnection;getSegmentCount()I"))
    private int messWithCtor(BezierConnection instance) {
        return instance.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL ? 0 : instance.getSegmentCount();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/BezierConnection;getBakedSegments()[Lcom/simibubi/create/content/trains/track/BezierConnection$SegmentAngles;"))
    private BezierConnection.SegmentAngles[] messWithCtor2(BezierConnection instance) {
        return instance.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL ? new BezierConnection.SegmentAngles[0] : instance.getBakedSegments();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addActualMonorail(TrackInstance trackInstance, BezierConnection bc, CallbackInfo ci) {
        //Use right for top section
        //Use ties for center section
        //use left for bottom section
        if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            BlockPos tePosition = bc.tePositions.getFirst();
            PoseStack pose = new PoseStack();
            TransformStack.cast(pose)
                    .translate(this$0.getInstancePosition())
                    .nudge((int) bc.tePositions.getFirst()
                            .asLong());

            BlockState air = Blocks.AIR.defaultBlockState();
            MonorailAngles[] monorails = ((IMonorailBezier) bc).getBakedMonorails();
            var mat = ((AccessorInstance) this$0).getMaterialManager().cutout(RenderType.cutoutMipped())
                    .material(Materials.TRANSFORMED);

            right = new ModelData[monorails.length - 1];
            ties = new ModelData[monorails.length - 1];
            left = new ModelData[monorails.length - 1];
            tiesLightPos = new BlockPos[monorails.length - 1];
            leftLightPos = new BlockPos[monorails.length - 1];
            rightLightPos = new BlockPos[monorails.length - 1];

            ModelData[] top = right;
            ModelData[] middle = ties;
            ModelData[] bottom = left;
            BlockPos[] topLight = rightLightPos;
            BlockPos[] middleLight = tiesLightPos;
            BlockPos[] bottomLight = leftLightPos;

            mat.getModel(MONORAIL_SEGMENT_TOP).createInstances(top);
            mat.getModel(MONORAIL_SEGMENT_MIDDLE).createInstances(middle);
            mat.getModel(MONORAIL_SEGMENT_BOTTOM).createInstances(bottom);

            for (int i = 1; i < monorails.length; i++) {
                MonorailAngles segment = monorails[i];
                int modelIndex = i - 1;

                PoseStack.Pose beamTransform = segment.beam;

                middle[modelIndex].setTransform(pose)
                        .mulPose(beamTransform.pose())
                        .mulNormal(beamTransform.normal());
                middleLight[modelIndex] = segment.lightPosition.offset(tePosition);

                for (boolean isTop : Iterate.trueAndFalse) {
                    PoseStack.Pose beamCapTransform = segment.beamCaps.get(isTop);
                    (isTop ? top : bottom)[modelIndex].setTransform(pose)
                            .mulPose(beamCapTransform.pose())
                            .mulNormal(beamCapTransform.normal());
                    (isTop ? topLight : bottomLight)[modelIndex] = segment.lightPosition.offset(tePosition);
                }
            }

            updateLight();
        }
    }
}
