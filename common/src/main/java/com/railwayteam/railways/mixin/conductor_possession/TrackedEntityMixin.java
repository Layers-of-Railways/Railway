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
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Lets entities get sent to the client even though they're not in range of the player
 *
 * Confirmed working with Security Craft
 */
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1200)
public abstract class TrackedEntityMixin {
	@Shadow
	@Final
	Entity entity;
	@Unique
	private boolean shouldBeSent = false;

	/**
	 * Checks if this entity is in range of a camera that is currently being viewed, and stores the result in the field
	 * shouldBeSent
	 */
	@Inject(method = "updatePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;x:D", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void railways$securitycraft$onUpdatePlayer(ServerPlayer player, CallbackInfo callback, Vec3 unused, double viewDistance) {
		if (ConductorPossessionController.isPossessingConductor(player)) {
			Vec3 relativePosToCamera = player.getCamera().position().subtract(entity.position());

			if (relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance)
				shouldBeSent = true;
		}
	}

	/**
	 * Enables entities that should be sent as well as security camera entities to be sent to the client
	 */
	@SuppressWarnings("InvalidInjectorMethodSignature")
	// variable name: flag or bl
	@ModifyVariable(method = "updatePlayer", ordinal = 0, at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 1))
	public boolean railways$securitycraft$modifyFlag(boolean original) {
		boolean shouldBeSent = this.shouldBeSent;

		this.shouldBeSent = false;
		return entity instanceof ConductorEntity || original || shouldBeSent;
	}
}
