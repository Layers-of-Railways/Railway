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

package com.railwayteam.railways.mixin.compat.voicechat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.annotation.mixin.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import de.maxhenkel.voicechat.plugins.impl.audiochannel.EntityAudioChannelImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@ConditionalMixin(mods = Mods.VOICECHAT)
@Mixin(EntityAudioChannelImpl.class)
public class EntityAudioChannelImplMixin {
    @WrapOperation(method = "broadcast", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyePosition()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 useConductorSpyPosition(Entity instance, Operation<Vec3> original) {
        if (instance instanceof ServerPlayer serverPlayer && ConductorPossessionController.isPossessingConductor(instance)) {
            return serverPlayer.getCamera().getEyePosition();
        } else {
            return original.call(instance);
        }
    }

    @WrapOperation(method = {
            "send([B)V",
            "send(Lde/maxhenkel/voicechat/api/packets/MicrophonePacket;)V",
            "flush"
    }, at = @At(value = "INVOKE", target = "Lde/maxhenkel/voicechat/api/Entity;getUuid()Ljava/util/UUID;"), remap = false)
    private UUID useConductorSpyUUID(de.maxhenkel.voicechat.api.Entity instance, Operation<UUID> original) {
        if (instance.getEntity() instanceof ServerPlayer serverPlayer && ConductorPossessionController.isPossessingConductor(serverPlayer)) {
            return serverPlayer.getCamera().getUUID();
        } else {
            return original.call(instance);
        }
    }
}
