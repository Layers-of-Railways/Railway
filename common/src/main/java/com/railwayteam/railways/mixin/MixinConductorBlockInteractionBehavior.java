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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.simibubi.create.api.behaviour.interaction.ConductorBlockInteractionBehavior;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConductorBlockInteractionBehavior.class, remap = false)
public abstract class MixinConductorBlockInteractionBehavior {

    @Inject(method = "handlePlayerInteraction", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;setSchedule(Lcom/simibubi/create/content/trains/schedule/Schedule;Z)V"))
    private void storeIndex(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity, CallbackInfoReturnable<Boolean> cir) {
        if (contraptionEntity instanceof CarriageContraptionEntity cce) {
            Carriage carriage = cce.getCarriage();
            Train train = carriage.train;
            ((IIndexedSchedule) train).railways$setIndex(train.carriages.indexOf(carriage));
        }
    }
}
