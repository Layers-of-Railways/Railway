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

package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.forge.mixin.ChunkMapAccessor;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PlayerSelectionImpl extends PlayerSelection {

	private static Consumer<Packet<?>> playerListAllWith(final PacketDistributor<Predicate<ServerPlayer>> distributor,
														 final Supplier<Predicate<ServerPlayer>> predicateSupplier) {
		return p -> {
			Predicate<ServerPlayer> predicate = predicateSupplier.get();
			for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
				if (predicate.test(player)) {
					player.connection.send(p);
				}
			}
		};
	}

	private static Consumer<Packet<?>> trackingEntityWith(final PacketDistributor<Pair<Entity, Predicate<ServerPlayer>>> distributor,
														 final Supplier<Pair<Entity, Predicate<ServerPlayer>>> pairSupplier) {
		return p -> {
			Pair<Entity, Predicate<ServerPlayer>> pair = pairSupplier.get();
			Entity entity = pair.getFirst();
			Predicate<ServerPlayer> predicate = pair.getSecond();

			ServerChunkCache manager = (ServerChunkCache) entity.getLevel().getChunkSource();
			ChunkMap storage = manager.chunkMap;
			ChunkMap.TrackedEntity trackedEntity = ((ChunkMapAccessor)storage).getEntityMap().get(entity.getId());

			if (trackedEntity == null)
				return;

			for (ServerPlayerConnection connection : ((ChunkMapAccessor.TrackedEntityAccessor) trackedEntity).getSeenBy()) {
				if (predicate.test(connection.getPlayer())) {
					connection.send(p);
				}
			}
		};
	}

	private static final PacketDistributor<Predicate<ServerPlayer>> ALL_WITH =
		new PacketDistributor<>(PlayerSelectionImpl::playerListAllWith, NetworkDirection.PLAY_TO_CLIENT);

	private static final PacketDistributor<Pair<Entity, Predicate<ServerPlayer>>> TRACKING_ENTITY_WITH =
		new PacketDistributor<>(PlayerSelectionImpl::trackingEntityWith, NetworkDirection.PLAY_TO_CLIENT);

	final PacketTarget target;

	private PlayerSelectionImpl(PacketTarget target) {
		this.target = target;
	}

	@Override
	public void accept(ResourceLocation id, FriendlyByteBuf buffer) {
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(id, buffer);
		target.send(packet);
	}

	public static PlayerSelection all() {
		return new PlayerSelectionImpl(PacketDistributor.ALL.noArg());
	}

	public static PlayerSelection allWith(Predicate<ServerPlayer> condition) {
		return new PlayerSelectionImpl(ALL_WITH.with(() -> condition));
	}

	public static PlayerSelection of(ServerPlayer player) {
		return new PlayerSelectionImpl(PacketDistributor.PLAYER.with(() -> player));
	}

	public static PlayerSelection tracking(Entity entity) {
		return new PlayerSelectionImpl(PacketDistributor.TRACKING_ENTITY.with(() -> entity));
	}

	public static PlayerSelection trackingWith(Entity entity, Predicate<ServerPlayer> condition) {
		return new PlayerSelectionImpl(TRACKING_ENTITY_WITH.with(() -> Pair.of(entity, condition)));
	}

	public static PlayerSelection tracking(BlockEntity be) {
		LevelChunk chunk = be.getLevel().getChunkAt(be.getBlockPos());
		return new PlayerSelectionImpl(PacketDistributor.TRACKING_CHUNK.with(() -> chunk));
	}

	public static PlayerSelection tracking(ServerLevel level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		return new PlayerSelectionImpl(PacketDistributor.TRACKING_CHUNK.with(() -> chunk));
	}

	public static PlayerSelection trackingAndSelf(ServerPlayer player) {
		return new PlayerSelectionImpl(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
	}
}
