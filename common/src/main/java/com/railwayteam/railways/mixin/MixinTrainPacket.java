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

import com.railwayteam.railways.mixin_interfaces.IHandcarTrain;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainPacket;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainPacket.class, priority = 523) // random priority to ensure consistent order if another mod changes packets as well
public class MixinTrainPacket {
    @Shadow(remap = false) Train train;

    @Inject(method = "write", at = @At(value = "RETURN", ordinal = 1))
    private void writeHandcarStatus(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(((IHandcarTrain) train).railways$isHandcar());
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "RETURN", ordinal = 1))
    private void readHandcarStatus(FriendlyByteBuf buffer, CallbackInfo ci) {
        ((IHandcarTrain) train).railways$setHandcar(buffer.readBoolean());
    }
}
