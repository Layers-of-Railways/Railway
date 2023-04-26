package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlockOutline;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.track_api.TrackMaterial;
import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = TrackBlockOutline.class, remap = false)
public abstract class MixinTrackBlockOutline {
    @ModifyArg(
            method = {
                    "drawCurveSelection",
                    "drawCustomBlockSelection"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z",
                    remap = true
            )
    )
    private static ItemStack railway$allowCustomTracks(ItemStack held) {
        return CustomTrackChecks.check(held);
    }

    @Unique
    private static boolean railway$resultIsMonorail;

    @ModifyVariable(
            method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/track/BezierTrackPointLocation;<init>(Lnet/minecraft/core/BlockPos;I)V",
                    remap = true
            )
    )
    private static BezierConnection railway$grabResultMonorailState(BezierConnection bc) {
        railway$resultIsMonorail = ((IHasTrackMaterial) bc).getMaterial() == CRTrackMaterials.MONORAIL;
        return bc;
    }

    @ModifyArg(
            method = "drawCurveSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/trains/track/TrackBlockOutline;renderShape(Lnet/minecraft/world/phys/shapes/VoxelShape;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/lang/Boolean;)V",
                    remap = true
            )
    )
    private static VoxelShape railway$renderCurvedMonorailShape(VoxelShape shape) {
        return MonorailTrackBlockOutline.convert(shape, railway$resultIsMonorail);
    }

    private static boolean railway$walkingMonorail;

    @ModifyVariable(
            method = "drawCustomBlockSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    remap = true
            )
    )
    private static BlockState railway$grabMonorailState(BlockState state) {
        railway$walkingMonorail = state.getBlock() instanceof MonorailTrackBlock;
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
        return MonorailTrackBlockOutline.convert(o, railway$walkingMonorail);
    }
}



