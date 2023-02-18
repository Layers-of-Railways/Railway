package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.Consumer;

@Mixin(value = TrackBlockOutline.class, remap = false)
public abstract class MixinTrackBlockOutline {
    @Shadow
    private static void walkShapes(TrackShape shape, TransformStack msr, Consumer<VoxelShape> renderer) {
        throw new AssertionError();
    }

    private static boolean railway$renderingMonorail;

    @ModifyVariable(
            method = "drawCustomBlockSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/track/TrackShape;isJunction()Z",
                    remap = true
            )
    )
    private static BlockState replaceShapes(BlockState state) {
        railway$renderingMonorail = state.getBlock() instanceof MonorailTrackBlock;
        return state;
    }

    @Redirect(
            method = "drawCustomBlockSelection",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/track/TrackBlockOutline;walkShapes(Lcom/simibubi/create/content/logistics/trains/track/TrackShape;Lcom/jozufozu/flywheel/util/transform/TransformStack;Ljava/util/function/Consumer;)V"
            )
    )
    private static void railway$walkMonorailInstead(TrackShape d, TransformStack shape, Consumer<VoxelShape> msr) {
        if (railway$renderingMonorail) {
            monorailWalkShapes(d, shape, msr);
        }
        walkShapes(d, shape, msr);
    }

    private static boolean tmpCurveIsMonorail = false;
    private static boolean persistentCurveIsMonorail = false;

    @ModifyVariable(
            method = "pickCurves",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;getStepLUT()[F"
            )
    )
    private static BezierConnection railway$storeMonorail(BezierConnection bc) {
        if (((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL)
            tmpCurveIsMonorail = true;
        return bc;
    }

    @Redirect(
            method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/utility/VoxelShaper;get(Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
            )
    )
    private static VoxelShape replaceSelectionAABB(VoxelShaper instance, Direction direction) {
        VoxelShape shape = tmpCurveIsMonorail ? CRShapes.MONORAIL_TRACK_ORTHO.get(direction).move(0, 8/16f, 0) : instance.get(direction);
        tmpCurveIsMonorail = false;
        return shape;
    }

    @ModifyVariable(
            method = "pickCurves",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/track/BezierTrackPointLocation;<init>(Lnet/minecraft/core/BlockPos;I)V"
            )
    )
    private static BezierConnection railway$storePersistentMonorail(BezierConnection bc) {
        persistentCurveIsMonorail = ((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL;
        return bc;
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



