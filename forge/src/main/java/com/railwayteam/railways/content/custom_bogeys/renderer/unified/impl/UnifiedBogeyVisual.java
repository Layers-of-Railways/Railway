/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.custom_bogeys.renderer.unified.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplayHolder;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisualizer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public class UnifiedBogeyVisual implements BogeyVisual, BogeyDisplayHolder {
    private final BogeyDisplay display;
    private final List<TransformedInstance> instances = new ArrayList<>();
    private final @Nullable BogeyVisual customVisual;

    public static BogeyVisualizer create(BogeyDisplay.Factory factory) {
        return (ctx, partialTick, inContraption) -> new UnifiedBogeyVisual(ctx, partialTick, inContraption, factory);
    }

    private UnifiedBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption, BogeyDisplay.Factory displayFactory) {
        var prov = new VisualElementProvider(ctx, instances);
        display = displayFactory.create(prov, inContraption);
        prov.freeze();

        var customVisualizer = displayFactory.createCustomVisualizer();
        if (customVisualizer != null) {
            customVisual = customVisualizer.createVisual(ctx, partialTick, inContraption);
        } else {
            customVisual = null;
        }
    }

    @Override
    public void runWithDisplay(Consumer<BogeyDisplay> consumer) {
        consumer.accept(display);

        if (customVisual instanceof BogeyDisplayHolder customDisplayHolder) {
            customDisplayHolder.runWithDisplay(consumer);
        }
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
        for (TransformedInstance i : instances) {
            i.setTransform(poseStack);
        }

        display.update(bogeyData, wheelAngle);

        for (TransformedInstance i : instances) {
            i.setChanged();
        }

        if (customVisual != null) {
            customVisual.update(bogeyData, wheelAngle, poseStack);
        }
    }

    @Override
    public void hide() {
        for (TransformedInstance i : instances) {
            i.setZeroTransform().setChanged();
        }

        if (customVisual != null) {
            customVisual.hide();
        }
    }

    @Override
    public void updateLight(int packedLight) {
        for (TransformedInstance i : instances) {
            i.light(packedLight).setChanged();
        }

        if (customVisual != null) {
            customVisual.updateLight(packedLight);
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        for (TransformedInstance i : instances) {
            consumer.accept(i);
        }

        if (customVisual != null) {
            customVisual.collectCrumblingInstances(consumer);
        }
    }

    @Override
    public void delete() {
        for (TransformedInstance i : instances) {
            i.delete();
        }
        instances.clear();

        if (customVisual != null) {
            customVisual.delete();
        }
    }
}
