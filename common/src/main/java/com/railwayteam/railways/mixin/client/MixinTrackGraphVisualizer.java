package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
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
}
