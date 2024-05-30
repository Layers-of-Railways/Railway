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

package com.railwayteam.railways.multiloader.fabric;

import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.List;
import java.util.function.Function;

public class PacketSetImpl extends PacketSet {
	protected PacketSetImpl(String id, int version,
							List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
							Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
							List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
							Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
		super(id, version, s2cPackets, s2cTypes, c2sPackets, c2sTypes);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void registerS2CListener() {
		ClientPlayNetworking.registerGlobalReceiver(s2cPacket, this::handleClientPacket);
	}

	@Environment(EnvType.CLIENT)
	private void handleClientPacket(Minecraft mc, ClientPacketListener listener,
									FriendlyByteBuf buf, PacketSender sender) {
		handleS2CPacket(mc, buf);
	}

	@Override
	public void registerC2SListener() {
		ServerPlayNetworking.registerGlobalReceiver(c2sPacket, this::handleServerPacket);
	}

	private void handleServerPacket(MinecraftServer server, ServerPlayer player,
									ServerGamePacketListenerImpl listener, FriendlyByteBuf buf,
									PacketSender sender) {
		handleC2SPacket(player, buf);
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void doSendC2S(FriendlyByteBuf buf) {
		ClientPlayNetworking.send(c2sPacket, buf);
	}

	@Override
	public void send(SimplePacketBase packet) {
		AllPackets.getChannel().sendToServer(packet);
	}

	@Override
	public void sendTo(ServerPlayer player, SimplePacketBase packet) {
		AllPackets.getChannel().sendToClient(packet, player);
	}

	@Override
	public void sendTo(PlayerSelection selection, SimplePacketBase packet) {
		AllPackets.getChannel().sendToClients(packet, ((PlayerSelectionImpl) selection).players);
	}

	@Internal
	public static PacketSet create(String id, int version,
								   List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
								   Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
								   List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
								   Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
		return new PacketSetImpl(id, version, s2cPackets, s2cTypes, c2sPackets, c2sTypes);
	}
}
