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

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainStatus;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(TrainStatus.class)
public class MixinTrainStatus {
    @Shadow(remap = false) Train train;

    @WrapOperation(method = "displayInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private boolean addPositionInfo(List<?> instance, Object e, Operation<Boolean> original) {
        if (e instanceof MutableComponent mutable) {
            CarriageBogey bogey = train.carriages.get(0).leadingBogey();
            Vec3 pos = bogey.getAnchorPosition();
            if (pos != null) {
                pos = pos.scale(10);
                pos = new Vec3(Math.round(pos.x()), Math.round(pos.y()), Math.round(pos.z())).scale(0.1);
            }
            ResourceKey<Level> dimension = bogey.getDimension();
            Style style = mutable.getStyle()
                .withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Components.literal((pos == null ? "???" : pos) + " [" + (dimension == null ? "???" : dimension.location()) + "]")
                ));
            mutable.setStyle(style);
        }
        return original.call(instance, e);
    }
}
