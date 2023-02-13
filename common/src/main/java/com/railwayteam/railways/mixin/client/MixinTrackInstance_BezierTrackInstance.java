package com.railwayteam.railways.mixin.client;


import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IGetBezierConnection;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier.MonorailAngles;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackInstance;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.simibubi.create.AllBlockPartials.TRACK_TIE;
import static com.simibubi.create.AllBlockPartials.TRACK_SEGMENT_LEFT;
import static com.simibubi.create.AllBlockPartials.TRACK_SEGMENT_RIGHT;

@Environment(EnvType.CLIENT)
@Mixin(targets = "com.simibubi.create.content.logistics.trains.track.TrackInstance$BezierTrackInstance", remap = false)
public abstract class MixinTrackInstance_BezierTrackInstance {

    @Final
    @Shadow(aliases = {"this$0"})
    TrackInstance myOuter;

    @Mutable
    @Shadow @Final private ModelData[] ties;

    @Mutable
    @Shadow @Final private ModelData[] right;

    @Mutable
    @Shadow @Final private ModelData[] left;

    @Shadow @Final private TrackInstance this$0;

    @Mutable
    @Shadow @Final private BlockPos[] tiesLightPos;

    @Mutable
    @Shadow @Final private BlockPos[] leftLightPos;

    @Mutable
    @Shadow @Final private BlockPos[] rightLightPos;

    @Shadow abstract void updateLight();

    @Redirect(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_TIE:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
    private PartialModel replaceTie() {
        BezierConnection bc = ((IGetBezierConnection) myOuter).getBezierConnection();
        if (bc != null) {
            TrackMaterial material = ((IHasTrackMaterial) bc).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).tie;
            }
        }
        return TRACK_TIE;
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_LEFT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
    private PartialModel replaceSegLeft() {
        BezierConnection bc = ((IGetBezierConnection) myOuter).getBezierConnection();
        if (bc != null) {
            TrackMaterial material = ((IHasTrackMaterial) bc).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).segment_left;
            }
        }
        return TRACK_SEGMENT_LEFT;
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_RIGHT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
    private PartialModel replaceSegRight() {
        BezierConnection bc = ((IGetBezierConnection) myOuter).getBezierConnection();
        if (bc != null) {
            TrackMaterial material = ((IHasTrackMaterial) bc).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).segment_right;
            }
        }
        return TRACK_SEGMENT_RIGHT;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;getSegmentCount()I", remap = false))
    private int messWithCtor(BezierConnection instance) {
        return ((IHasTrackMaterial) instance).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL ? 0 : instance.getSegmentCount();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;getBakedSegments()[Lcom/simibubi/create/content/logistics/trains/BezierConnection$SegmentAngles;", remap = false))
    private BezierConnection.SegmentAngles[] messWithCtor2(BezierConnection instance) {
        return ((IHasTrackMaterial) instance).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL ? new BezierConnection.SegmentAngles[0] : instance.getBakedSegments();
    }

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void addActualMonorail(TrackInstance trackInstance, BezierConnection bc, CallbackInfo ci) {
        //Use right for top section
        //Use ties for center section
        //use left for bottom section
        if (((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL) {
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

            right = new ModelData[monorails.length-1];
            ties = new ModelData[monorails.length-1];
            left = new ModelData[monorails.length-1];
            tiesLightPos = new BlockPos[monorails.length-1];
            leftLightPos = new BlockPos[monorails.length-1];
            rightLightPos = new BlockPos[monorails.length-1];

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
