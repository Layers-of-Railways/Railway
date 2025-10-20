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

package com.railwayteam.railways.content.custom_bogeys.special.invisible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InvisibleBogeyVisual implements BogeyVisual {
    public InvisibleBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {}

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {}

    @Override
    public void hide() {}

    @Override
    public void updateLight(int packedLight) {}

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {}

    @Override
    public void delete() {}
}
