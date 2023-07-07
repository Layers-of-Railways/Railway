package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
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

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Lets entities get sent to the client even though they're not in range of the player
 */
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1200)
public abstract class TrackedEntityMixin {
	@Shadow
	@Final
	ServerEntity serverEntity;
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
	private void securitycraft$onUpdatePlayer(ServerPlayer player, CallbackInfo callback, Vec3 unused, double viewDistance) {
		if (ConductorPossessionController.isPossessingConductor(player)) {
			Vec3 relativePosToCamera = player.getCamera().position().subtract(serverEntity.sentPos());

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
	public boolean securitycraft$modifyFlag(boolean original) {
		boolean shouldBeSent = this.shouldBeSent;

		this.shouldBeSent = false;
		return entity instanceof ConductorEntity || original || shouldBeSent;
	}
}
