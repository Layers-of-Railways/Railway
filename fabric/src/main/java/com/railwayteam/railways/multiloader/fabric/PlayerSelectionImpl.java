package com.railwayteam.railways.multiloader.fabric;

import com.railwayteam.railways.multiloader.PlayerSelection;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PlayerSelectionImpl extends PlayerSelection {

	final Collection<ServerPlayer> players;

	private PlayerSelectionImpl(Collection<ServerPlayer> players) {
		this.players = players;
	}

	@Override
	public void accept(ResourceLocation id, FriendlyByteBuf buffer) {
		Packet<?> packet = ServerPlayNetworking.createS2CPacket(id, buffer);
		for (ServerPlayer player : players) {
			ServerPlayNetworking.getSender(player).sendPacket(packet);
		}
	}

	public static PlayerSelection all() {
		return new PlayerSelectionImpl(PlayerLookup.all(ServerLifecycleHooks.getCurrentServer()));
	}

	public static PlayerSelection of(ServerPlayer player) {
		return new PlayerSelectionImpl(Collections.singleton(player));
	}

	public static PlayerSelection tracking(Entity entity) {
		return new PlayerSelectionImpl(PlayerLookup.tracking(entity));
	}

	public static PlayerSelection tracking(BlockEntity be) {
		return new PlayerSelectionImpl(PlayerLookup.tracking(be));
	}

	public static PlayerSelection tracking(ServerLevel level, BlockPos pos) {
		return new PlayerSelectionImpl(PlayerLookup.tracking(level, pos));
	}

	public static PlayerSelection trackingAndSelf(ServerPlayer player) {
		ArrayList<ServerPlayer> players = new ArrayList<>(PlayerLookup.tracking(player));
		players.add(player);
		return new PlayerSelectionImpl(players);
	}
}
