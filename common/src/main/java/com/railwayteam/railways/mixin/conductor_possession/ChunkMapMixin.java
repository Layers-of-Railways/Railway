package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 *
 * Confirmed compatible with SecurityCraft
 */
@Mixin(value = ChunkMap.class, priority = 1200)
public abstract class ChunkMapMixin {
	@Shadow
	int viewDistance;

	@Shadow
	protected abstract void updateChunkTracking(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking methods
	 */
	@Redirect(method = {
			"getPlayers",
			"lambda$setViewDistance$0", "m_ntjylyau", "method_17219" // these 3 all refer to the same thing with different mappings
	}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"))
	private SectionPos securitycraft$getCameraSectionPos(ServerPlayer player) {
		if (ConductorPossessionController.isPossessingConductor(player) || player.getCamera().getClass().getName().equals("net.geforcemods.securitycraft.entity.camera.SecurityCamera"))
			return SectionPos.of(player.getCamera());

		return player.getLastSectionPos();
	}

	/**
	 * Tracks chunks loaded by cameras to send them to the client, and tracks chunks around the player to properly update them
	 * when they stop viewing a camera
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
		if (player.getCamera() instanceof ConductorEntity camera) {
			if (!camera.hasSentChunks()) {
				SectionPos oldPos = camera.oldSectionPos;
				SectionPos pos = SectionPos.of(camera);
				camera.oldSectionPos = pos;

				for (int i = pos.x() - viewDistance - 1; i <= pos.x() + viewDistance + 1; ++i) {
					for (int j = pos.z() - viewDistance - 1; j <= pos.z() + viewDistance + 1; ++j) {
						if (oldPos != null) { // if we are updating from a previous position, only load / unload relevant chunks
							updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(),
									ChunkMap.isChunkInRange(i, j, oldPos.x(), oldPos.z(), viewDistance), // was loaded
									ChunkMap.isChunkInRange(i, j, pos.x(), pos.z(), viewDistance)        // is  loaded
							);
						} else if (ChunkMap.isChunkInRange(i, j, pos.x(), pos.z(), viewDistance))
							updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
					}
				}

				camera.setHasSentChunks(true);
			}
		}
		else if (ConductorEntity.hasRecentlyDismounted(player)) {
			SectionPos pos = player.getLastSectionPos();

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
				}
			}
		}
	}
}
