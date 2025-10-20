/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.switches.TrackSwitchDebugVisualizer;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackTargetingClient.class, remap = false)
public abstract class MixinTrackTargetingClient {
    @Shadow
    static EdgePointType<?> lastType;

    @Shadow
    static BlockPos lastHovered;

    @Shadow
    static boolean lastDirection;

    @Shadow
    static BezierTrackPointLocation lastHoveredBezierSegment;

    @Inject(method = "render", at = @At(
        value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/content/trains/track/TrackTargetingClient;lastType:Lcom/simibubi/create/content/trains/graph/EdgePointType;",
        ordinal = 0
    ), cancellable = true)
    private static void renderCustom(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, CallbackInfo ci) {
        if (CustomTrackOverlayRendering.CUSTOM_OVERLAYS.containsKey(lastType)) {
            Minecraft mc = Minecraft.getInstance();
            BlockPos pos = lastHovered;
            int light = LevelRenderer.getLightColor(mc.level, pos);
            Direction.AxisDirection direction = lastDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

            ms.pushPose();
            TransformStack.of(ms)
                .translate(Vec3.atLowerCornerOf(pos)
                    .subtract(camera));
            CustomTrackOverlayRendering.renderOverlay(mc.level, pos, direction, lastHoveredBezierSegment, ms, buffer, light,
                OverlayTexture.NO_OVERLAY, lastType, 1 + 1 / 16f);
            ms.popPose();
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private static void renderSwitchHints(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, CallbackInfo ci) {
        TrackSwitchDebugVisualizer.visualizePotentialLocations();
    }
}
