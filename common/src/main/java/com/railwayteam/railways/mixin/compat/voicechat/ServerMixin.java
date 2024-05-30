/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.compat.voicechat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.annotation.mixin.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import de.maxhenkel.voicechat.voice.server.Server;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.UUID;

@ConditionalMixin(mods = Mods.VOICECHAT)
@Mixin(Server.class)
public class ServerMixin {
    @WrapOperation(method = "processProximityPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;position()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 useConductorSpyPosition(ServerPlayer instance, Operation<Vec3> original) {
        if (ConductorPossessionController.isPossessingConductor(instance)) {
            return instance.getCamera().position();
        } else {
            return original.call(instance);
        }
    }

    @WrapOperation(method = "processProximityPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getUUID()Ljava/util/UUID;"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCrouching()Z")
        ))
    private UUID useConductorSpyUUID(ServerPlayer instance, Operation<UUID> original) {
        if (ConductorPossessionController.isPossessingConductor(instance)) {
            return instance.getCamera().getUUID();
        } else {
            return original.call(instance);
        }
    }
}
