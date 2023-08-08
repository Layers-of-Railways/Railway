package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.Config;
import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(value = TrackGraphVisualizer.class, remap = false)
public class MixinTrackGraphVisualizer {
    private static boolean isEnabled = false;
    @Inject(method = "debugViewGraph",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 1, remap = true),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void saveEdge(TrackGraph graph, boolean extended, CallbackInfo ci, Minecraft mc, Entity cameraEntity,
                                 AABB box, Vec3 camera, Iterator<?> var6, Map.Entry<?, ?> nodeEntry,
                                 TrackNodeLocation nodeLocation, TrackNode node, Vec3 location, Vec3 yOffset, Vec3 v1,
                                 Vec3 v2, Map<?, ?> map, int hashCode, Iterator<?> var16, Map.Entry<?, ?> entry,
                                 TrackNode other, TrackEdge edge) {
        MixinTrackGraphVisualizer.isEnabled = ((ISwitchDisabledEdge) edge.getEdgeData()).isEnabled();
    }

    @Redirect(method = "debugViewGraph", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;color:Lcom/simibubi/create/foundation/utility/Color;"))
    private static Color snr$replaceColor(TrackGraph instance) {
        if (!MixinTrackGraphVisualizer.isEnabled) {
            return Color.RED;
        }
        return instance.color;
    }

    @SuppressWarnings("unused")
    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showLine(Ljava/lang/Object;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams snr$offsetLineVisualization(Outliner instance, Object slot, Vec3 start, Vec3 end, Operation<Outline.OutlineParams> original) {
        double offset = Config.TRACK_OVERLAY_OFFSET.get();
        return original.call(instance, slot, start.add(0, offset, 0), end.add(0, offset, 0));
    }

    @SuppressWarnings("unused")
    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showAABB(Ljava/lang/Object;Lnet/minecraft/world/phys/AABB;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams snr$offsetAABBVisualization(Outliner instance, Object slot, AABB aabb, Operation<Outline.OutlineParams> original) {
        double offset = Config.TRACK_OVERLAY_OFFSET.get();
        return original.call(instance, slot, aabb.move(0, offset, 0));
    }

    @SuppressWarnings("unused")
    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showItem(Ljava/lang/Object;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams snr$offsetAABBVisualization(Outliner instance, Object slot, Vec3 pos, ItemStack itemStack, Operation<Outline.OutlineParams> original) {
        double offset = Config.TRACK_OVERLAY_OFFSET.get();
        return original.call(instance, slot, pos.add(0, offset, 0), itemStack);
    }
}
