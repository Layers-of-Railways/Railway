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


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphVisualizer;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackGraphVisualizer.class, remap = false)
public class MixinTrackGraphVisualizer {
    @Unique private static boolean railways$isEnabled = false;

    // If the track edge is a monorail track, then change the y offset and make it higher so that
    // the signal line is visible to the player
    @ModifyExpressionValue(method = "visualiseSignalEdgeGroups", at = @At(value = "CONSTANT", args = "floatValue=64f"))
    private static float fixYOffsetForMonorailTracks(float original, @Local TrackEdge edge) {
        return edge.getTrackMaterial() == CRTrackMaterials.MONORAIL ? 5.1f : original;
    }

    @Inject(method = "debugViewGraph",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 1, remap = true))
    private static void saveEdge(TrackGraph graph, boolean extended, CallbackInfo ci, @Local TrackEdge edge) {
        MixinTrackGraphVisualizer.railways$isEnabled = ((ISwitchDisabledEdge) edge.getEdgeData()).isEnabled();
    }

    @Redirect(method = "debugViewGraph", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;color:Lcom/simibubi/create/foundation/utility/Color;"))
    private static Color railways$replaceColor(TrackGraph instance) {
        if (!MixinTrackGraphVisualizer.railways$isEnabled) {
            return Color.RED;
        }
        return instance.color;
    }

    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showLine(Ljava/lang/Object;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams railways$offsetLineVisualization(Outliner instance, Object slot, Vec3 start, Vec3 end, Operation<Outline.OutlineParams> original) {
        double offset = CRConfigs.client().trackOverlayOffset.get();
        return original.call(instance, slot, start.add(0, offset, 0), end.add(0, offset, 0));
    }

    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showAABB(Ljava/lang/Object;Lnet/minecraft/world/phys/AABB;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams railways$offsetAABBVisualization(Outliner instance, Object slot, AABB aabb, Operation<Outline.OutlineParams> original) {
        double offset = CRConfigs.client().trackOverlayOffset.get();
        return original.call(instance, slot, aabb.move(0, offset, 0));
    }

    @WrapOperation(method = {
        "visualiseSignalEdgeGroups",
        "debugViewGraph"
    }, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/outliner/Outliner;showItem(Ljava/lang/Object;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/foundation/outliner/Outline$OutlineParams;", remap = true), require = 0, remap = true)
    private static Outline.OutlineParams railways$offsetAABBVisualization(Outliner instance, Object slot, Vec3 pos, ItemStack itemStack, Operation<Outline.OutlineParams> original) {
        double offset = CRConfigs.client().trackOverlayOffset.get();
        return original.call(instance, slot, pos.add(0, offset, 0), itemStack);
    }
}
