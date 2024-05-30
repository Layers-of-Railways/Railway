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

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PacketSetImpl extends PacketSet {
	public static final Map<ResourceLocation, PacketSet> HANDLERS = new HashMap<>();

	protected PacketSetImpl(String id, int version,
							List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
							Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
							List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
							Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
		super(id, version, s2cPackets, s2cTypes, c2sPackets, c2sTypes);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerS2CListener() {
		HANDLERS.put(s2cPacket, this);
	}

	@Override
	public void registerC2SListener() {
		HANDLERS.put(c2sPacket, this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void send(SimplePacketBase packet) {
		AllPackets.getChannel().sendToServer(packet);
	}

	@Override
	public void sendTo(ServerPlayer player, SimplePacketBase packet) {
		AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	@Override
	public void sendTo(PlayerSelection selection, SimplePacketBase packet) {
		AllPackets.getChannel().send(((PlayerSelectionImpl) selection).target, packet);
	}

	@Override
	protected void doSendC2S(FriendlyByteBuf buf) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection != null) {
			connection.send(new ServerboundCustomPayloadPacket(c2sPacket, buf));
		} else {
			Railways.LOGGER.error("Cannot send a C2S packet before the client connection exists, skipping!");
		}
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
