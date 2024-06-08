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

package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes sure the server does not move the player viewing a camera to the camera's position
 *
 * Confirmed compatible with SecurityCraft
 */
@Mixin(value = ServerPlayer.class, priority = 1200)
public class ServerPlayerMixin {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
	private void railways$securitycraft$tick(ServerPlayer player, double x, double y, double z, float yaw, float pitch) { // done add some quiet compat with SC to also cancel if player is on a camera
		if (player.getCamera().getClass().getName().equals("net.geforcemods.securitycraft.entity.camera.SecurityCamera")) return;
		if (!ConductorPossessionController.isPossessingConductor(player))
			player.absMoveTo(x, y, z, yaw, pitch);
	}

	@Inject(method = "setCamera", at = @At("HEAD"), cancellable = true)
	private void railways$railways$setCamera(Entity entityToSpectate, CallbackInfo ci) {
		if (entityToSpectate instanceof ConductorEntity) ci.cancel();
	}
}
