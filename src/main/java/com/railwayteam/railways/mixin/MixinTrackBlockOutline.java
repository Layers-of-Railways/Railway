package com.railwayteam.railways.mixin;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackVoxelShapes;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.trains.track.*;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = TrackBlockOutline.class, remap = false)
public class MixinTrackBlockOutline {

    @Inject(method = "drawCustomBlockSelection", at = @At("HEAD"))
    private static void drawCustomBlockSelection(RenderHighlightEvent.Block event, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        BlockState blockstate = mc.level.getBlockState(pos);

        if (!(blockstate.getBlock() instanceof TrackBlock))
            return;
        if (!mc.level.getWorldBorder()
                .isWithinBounds(pos))
            return;

        VertexConsumer vb = event.getMultiBufferSource()
                .getBuffer(RenderType.lines());
        Vec3 camPos = event.getCamera()
                .getPosition();

        PoseStack ms = event.getPoseStack();

        ms.pushPose();
        ms.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);

        boolean holdingTrack = CRTags.AllBlockTags.TRACKS.matches(Minecraft.getInstance().player.getMainHandItem());
        TrackShape shape = blockstate.getValue(TrackBlock.SHAPE);
        boolean isJunction = shape.isJunction();
        if(!(blockstate.getBlock() instanceof MonorailTrackBlock)) {
            walkShapes(shape, TransformStack.cast(ms), s -> {
                renderShape(s, ms, vb, holdingTrack ? !isJunction : null);
                event.setCanceled(true);
            });
            ms.popPose();
        }

        if(blockstate.getBlock() instanceof MonorailTrackBlock) {
            monorailWalkShapes(shape, TransformStack.cast(ms), s -> {
                renderShape(s, ms, vb, holdingTrack ? !isJunction : null);
                event.setCanceled(true);
            });
            ms.popPose();
        }
    }


    private static void renderShape(VoxelShape s, PoseStack ms, VertexConsumer vb, Boolean valid) {
        PoseStack.Pose transform = ms.last();
        s.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float xDiff = (float) (x2 - x1);
            float yDiff = (float) (y2 - y1);
            float zDiff = (float) (z2 - z1);
            float length = Mth.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

            xDiff /= length;
            yDiff /= length;
            zDiff /= length;

            float r = 0f;
            float g = 0f;
            float b = 0f;

            if (valid != null && valid) {
                g = 1f;
                b = 1f;
                r = 1f;
            }

            if (valid != null && !valid) {
                r = 1f;
                b = 0.125f;
                g = 0.25f;
            }

            vb.vertex(transform.pose(), (float) x1, (float) y1, (float) z1)
                    .color(r, g, b, .4f)
                    .normal(transform.normal(), xDiff, yDiff, zDiff)
                    .endVertex();
            vb.vertex(transform.pose(), (float) x2, (float) y2, (float) z2)
                    .color(r, g, b, .4f)
                    .normal(transform.normal(), xDiff, yDiff, zDiff)
                    .endVertex();

        });
    }

    private static final VoxelShape LONG_CROSS =
            Shapes.or(TrackVoxelShapes.longOrthogonalZ(), TrackVoxelShapes.longOrthogonalX());
    private static final VoxelShape LONG_ORTHO = TrackVoxelShapes.longOrthogonalZ();
    private static final VoxelShape LONG_ORTHO_OFFSET = TrackVoxelShapes.longOrthogonalZOffset();

    private static void walkShapes(TrackShape shape, TransformStack msr, Consumer<VoxelShape> renderer) {
        float angle45 = Mth.PI / 4;

        if (shape == TrackShape.XO || shape == TrackShape.CR_NDX || shape == TrackShape.CR_PDX)
            renderer.accept(AllShapes.TRACK_ORTHO.get(Direction.EAST));
        else if (shape == TrackShape.ZO || shape == TrackShape.CR_NDZ || shape == TrackShape.CR_PDZ)
            renderer.accept(AllShapes.TRACK_ORTHO.get(Direction.SOUTH));

        if (shape.isPortal()) {
            for (Direction d : Iterate.horizontalDirections) {
                if (TrackShape.asPortal(d) != shape)
                    continue;
                msr.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(d)));
                renderer.accept(LONG_ORTHO_OFFSET);
                return;
            }
        }

        if (shape == TrackShape.PD || shape == TrackShape.CR_PDX || shape == TrackShape.CR_PDZ) {
            msr.rotateCentered(Direction.UP, angle45);
            renderer.accept(LONG_ORTHO);
        } else if (shape == TrackShape.ND || shape == TrackShape.CR_NDX || shape == TrackShape.CR_NDZ) {
            msr.rotateCentered(Direction.UP, -Mth.PI / 4);
            renderer.accept(LONG_ORTHO);
        }

        if (shape == TrackShape.CR_O)
            renderer.accept(AllShapes.TRACK_CROSS);
        else if (shape == TrackShape.CR_D) {
            msr.rotateCentered(Direction.UP, angle45);
            renderer.accept(LONG_CROSS);
        }

        if (!(shape == TrackShape.AE || shape == TrackShape.AN || shape == TrackShape.AW || shape == TrackShape.AS))
            return;

        msr.translate(0, 1, 0);
        msr.rotateCentered(Direction.UP, Mth.PI - AngleHelper.rad(shape.getModelRotation()));
        msr.rotateXRadians(angle45);
        msr.translate(0, -3 / 16f, 1 / 16f);
        renderer.accept(LONG_ORTHO);
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



