package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackVoxelShapes;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.*;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderHighlightEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = TrackBlockOutline.class, remap = false)
public abstract class MixinTrackBlockOutline {
    @Shadow
    private static void renderShape(VoxelShape s, PoseStack ms, VertexConsumer vb, Boolean valid) {}

    @Inject(method = "drawCustomBlockSelection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void replaceShapes(RenderHighlightEvent.Block event, CallbackInfo ci, Minecraft mc, BlockHitResult target, BlockPos pos,
                                      BlockState blockstate, VertexConsumer vb, Vec3 camPos, PoseStack ms, boolean holdingTrack) {
        if (blockstate.getBlock() instanceof MonorailTrackBlock) {
            TrackShape shape = blockstate.getValue(TrackBlock.SHAPE);
            boolean isJunction = shape.isJunction();
            monorailWalkShapes(shape, TransformStack.cast(ms), s -> {
                renderShape(s, ms, vb, holdingTrack ? !isJunction : null);
                event.setCanceled(true);
            });

            ms.popPose();
            ci.cancel();
        }
    }

    /*
    TODO:
        drawCurveSelection
     */

    private static boolean tmpCurveIsMonorail = false;
    private static boolean persistentCurveIsMonorail = false;

    @Inject(method = "pickCurves", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;getStepLUT()[F"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void storeIsMonorail(CallbackInfo ci, Minecraft mc, LocalPlayer player, Vec3 origin, double maxRange, AttributeInstance range,
                                        Vec3 target, Map turns, Iterator var8, TrackTileEntity te, Iterator var10, BezierConnection bc, AABB bounds) {
        if (((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL)
            tmpCurveIsMonorail = true;
    }

    @Redirect(method = "pickCurves", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VoxelShaper;get(Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private static VoxelShape replaceSelectionAABB(VoxelShaper instance, Direction direction) {
        VoxelShape shape = tmpCurveIsMonorail ? CRShapes.MONORAIL_TRACK_ORTHO.get(direction).move(0, 8/16f, 0) : instance.get(direction);
        tmpCurveIsMonorail = false;
        return shape;
    }

    @Inject(method = "pickCurves", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/trains/track/TrackBlockOutline;result:Lcom/simibubi/create/content/logistics/trains/track/TrackBlockOutline$BezierPointSelection;", opcode = Opcodes.PUTSTATIC, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void storeIsMonorailPersistent(CallbackInfo ci, Minecraft mc, LocalPlayer player, Vec3 origin, double maxRange, AttributeInstance range, Vec3 target, Map turns, Iterator var8, TrackTileEntity te, Iterator var10, BezierConnection bc) {
        persistentCurveIsMonorail = ((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL;
    }

    @Redirect(method = "drawCurveSelection", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VoxelShaper;get(Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private static VoxelShape replaceCurveSelectionAABB(VoxelShaper instance, Direction direction) {
        return persistentCurveIsMonorail ? CRShapes.MONORAIL_TRACK_ORTHO.get(direction) : instance.get(direction);
    }

    private static final VoxelShape MONORAIL_LONG_CROSS = Shapes.or(MonorailTrackVoxelShapes.longOrthogonalZ(), MonorailTrackVoxelShapes.longOrthogonalX());
    private static final VoxelShape MONORAIL_LONG_ORTHO = MonorailTrackVoxelShapes.longOrthogonalZ();
    private static final VoxelShape MONORAIL_LONG_ORTHO_OFFSET = MonorailTrackVoxelShapes.longOrthogonalZOffset();

    private static void monorailWalkShapes(TrackShape shape, TransformStack msr, Consumer<VoxelShape> renderer) {
        float angle45 = Mth.PI / 4;

        if (shape == TrackShape.XO || shape == TrackShape.CR_NDX || shape == TrackShape.CR_PDX)
            renderer.accept(CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.EAST));
        else if (shape == TrackShape.ZO || shape == TrackShape.CR_NDZ || shape == TrackShape.CR_PDZ)
            renderer.accept(CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.SOUTH));

        if (shape.isPortal()) {
            for (Direction d : Iterate.horizontalDirections) {
                if (TrackShape.asPortal(d) != shape)
                    continue;
                msr.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(d)));
                renderer.accept(MONORAIL_LONG_ORTHO_OFFSET);
                return;
            }
        }

        if (shape == TrackShape.PD || shape == TrackShape.CR_PDX || shape == TrackShape.CR_PDZ) {
            msr.rotateCentered(Direction.UP, angle45);
            renderer.accept(MONORAIL_LONG_ORTHO);
        } else if (shape == TrackShape.ND || shape == TrackShape.CR_NDX || shape == TrackShape.CR_NDZ) {
            msr.rotateCentered(Direction.UP, -Mth.PI / 4);
            renderer.accept(MONORAIL_LONG_ORTHO);
        }

        if (shape == TrackShape.CR_O)
            renderer.accept(CRShapes.MONORAIL_TRACK_CROSS);
        else if (shape == TrackShape.CR_D) {
            msr.rotateCentered(Direction.UP, angle45);
            renderer.accept(MONORAIL_LONG_CROSS);
        }

        if (!(shape == TrackShape.AE || shape == TrackShape.AN || shape == TrackShape.AW || shape == TrackShape.AS))
            return;

        msr.translate(0, 1, 0);
        msr.rotateCentered(Direction.UP, Mth.PI - AngleHelper.rad(shape.getModelRotation()));
        msr.rotateXRadians(angle45);
        msr.translate(0, -3 / 16f, 1 / 16f);
        renderer.accept(MONORAIL_LONG_ORTHO);
    }
}



