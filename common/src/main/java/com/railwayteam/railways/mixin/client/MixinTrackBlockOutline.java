package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.content.custom_tracks.monorail.CustomTrackBlockOutline;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TrackBlockOutline.class, remap = false)
public abstract class MixinTrackBlockOutline {
    @Redirect(method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                target = "Lcom/simibubi/create/foundation/utility/VoxelShaper;get(Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
                remap = true
            )
    )
    private static VoxelShape pickWithCorrectShape(VoxelShaper instance, Direction direction, @Local BezierConnection bc) {
        return CustomTrackBlockOutline.convert(instance.get(direction), bc.getMaterial());
    }

    @Unique
    private static TrackMaterial railway$resultMaterial;

    @ModifyVariable(
            method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/BezierTrackPointLocation;<init>(Lnet/minecraft/core/BlockPos;I)V",
                    remap = true
            )
    )
    private static BezierConnection railway$grabResultMonorailState(BezierConnection bc) {
        railway$resultMaterial = bc.getMaterial();
        return bc;
    }

    @ModifyArg(
            method = "drawCurveSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/TrackBlockOutline;renderShape(Lnet/minecraft/world/phys/shapes/VoxelShape;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/lang/Boolean;)V",
                    remap = true
            )
    )
    private static VoxelShape railway$renderCurvedMonorailShape(VoxelShape shape) {
        return CustomTrackBlockOutline.convert(shape, railway$resultMaterial);
    }

    private static TrackMaterial railway$walkingMaterial;

    @ModifyVariable(
            method = "drawCustomBlockSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    remap = true
            )
    )
    private static BlockState railway$grabMonorailState(BlockState state) {
        railway$walkingMaterial = state.getBlock() instanceof TrackBlock trackBlock ? trackBlock.getMaterial() : TrackMaterial.ANDESITE;
        return state;
    }

    @ModifyArg(
            method = "walkShapes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
            )
    )
    private static Object railway$renderMonorailBlockShape(Object o) {
        return CustomTrackBlockOutline.convert(o, railway$walkingMaterial);
    }
}



