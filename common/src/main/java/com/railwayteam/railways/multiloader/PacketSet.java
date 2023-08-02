package com.railwayteam.railways.multiloader;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.Components;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Manages sending and receiving registered packets.
 * <pre>
 *     C2S -> Client-to-Server
 *     S2C -> Server-to-Client
 * </pre>
 */
public abstract class PacketSet {
	public final String id;
	public final int version;

	public final ResourceLocation c2sPacket;
	public final ResourceLocation s2cPacket;

	private final List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets;
	private final Object2IntMap<Class<? extends S2CPacket>> s2cTypes;
	private final List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets;
	private final Object2IntMap<Class<? extends C2SPacket>> c2sTypes;

	protected PacketSet(String id, int version,
						List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
						Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
						List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
						Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
		this.id = id;
		this.version = version;

		this.s2cPackets = s2cPackets;
		this.s2cTypes = s2cTypes;

		this.c2sPackets = c2sPackets;
		this.c2sTypes = c2sTypes;

		c2sPacket = new ResourceLocation(id, "c2s");
		s2cPacket = new ResourceLocation(id, "s2c");
	}

	/**
	 * Send the given C2S packet to the server.
	 */
	@Environment(EnvType.CLIENT)
	public void send(C2SPacket packet) {
		int i = idOfC2S(packet);
		if (i != -1) {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeVarInt(i);
			packet.write(buf);
			doSendC2S(buf);
		} else {
			throw new IllegalArgumentException("Cannot send unregistered C2SPacket: " + packet);
		}
	}

	/**
	 * Send one of Create's packets to the server.
	 */
	@Environment(EnvType.CLIENT)
	public abstract void send(SimplePacketBase packet);

	/**
	 * Send the given S2C packet to the given player.
	 */
	public void sendTo(ServerPlayer player, S2CPacket packet) {
		sendTo(PlayerSelection.of(player), packet);
	}

	/**
	 * Send the given Create packet to the given player.
	 */
	public abstract void sendTo(ServerPlayer player, SimplePacketBase packet);

	/**
	 * Send the given S2C packet to the given players.
	 */
	public void sendTo(PlayerSelection selection, S2CPacket packet) {
		int i = idOfS2C(packet);
		if (i != -1) {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeVarInt(i);
			packet.write(buf);
			selection.accept(s2cPacket, buf);
		} else {
			throw new IllegalArgumentException("Cannot send unregistered S2CPacket: " + packet);
		}
	}

	/**
	 * Send the given Create packet to the given players.
	 */
	public abstract void sendTo(PlayerSelection selection, SimplePacketBase packet);

	@Environment(EnvType.CLIENT)
	public abstract void registerS2CListener();
	public abstract void registerC2SListener();

	@Environment(EnvType.CLIENT)
	protected abstract void doSendC2S(FriendlyByteBuf buf);

	protected int idOfC2S(C2SPacket packet) {
		return c2sTypes.getOrDefault(packet.getClass(), -1);
	}

	protected int idOfS2C(S2CPacket packet) {
		return s2cTypes.getOrDefault(packet.getClass(), -1);
	}

	@Environment(EnvType.CLIENT)
	@Internal
	public void handleS2CPacket(Minecraft mc, FriendlyByteBuf buf) {
		int i = buf.readVarInt();
		if (i < 0 || i >= s2cPackets.size()) {
			Railways.LOGGER.error("Invalid S2C Packet {}, ignoring", i);
			return;
		}
		Function<FriendlyByteBuf, S2CPacket> factory = s2cPackets.get(i);
		S2CPacket packet = factory.apply(buf);
		mc.execute(() -> packet.handle(mc));
	}

	@Internal
	public void handleC2SPacket(ServerPlayer sender, FriendlyByteBuf buf) {
		int i = buf.readVarInt();
		if (i < 0 || i >= c2sPackets.size()) {
			Railways.LOGGER.error("Invalid C2S Packet {}, ignoring", i);
			return;
		}
		Function<FriendlyByteBuf, C2SPacket> factory = c2sPackets.get(i);
		C2SPacket packet = factory.apply(buf);
		sender.server.execute(() -> packet.handle(sender));
	}

	@ExpectPlatform
	@Internal
	public static PacketSet create(String id, int version,
								   List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
								   Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
								   List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
								   Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
		throw new AssertionError();
	}

	/**
	 * Start building a new PacketSet. Starts with a version sync packet.
	 */
	public static Builder builder(String id, int version) {
		return new Builder(id, version).s2c(CheckVersionPacket.class, CheckVersionPacket::new);
	}

	/**
	 * Send the player a packet with the current network version. If they do not match, the player will disconnect.
	 */
	public void onPlayerJoin(ServerPlayer player) {
		sendTo(player, new CheckVersionPacket(version));
	}

	public record CheckVersionPacket(int serverVersion) implements S2CPacket {
		public CheckVersionPacket(FriendlyByteBuf buf) {
			this(buf.readVarInt());
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
			buffer.writeVarInt(serverVersion);
		}

		@Override
		public void handle(Minecraft mc) {
			if (CRPackets.PACKETS.version == serverVersion)
				return;
			Component error = Components.literal("Steam n' Rails on the client uses a different network format than the server.")
					.append(" You should use the same version of the mod on both sides.");
			mc.getConnection().onDisconnect(error);
		}
	}

	public static class Builder {
		public final String id;
		public final int version;

		private final List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets = new ArrayList<>();
		private final Object2IntMap<Class<? extends S2CPacket>> s2cTypes = new Object2IntOpenHashMap<>();
		private final List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets = new ArrayList<>();
		private final Object2IntMap<Class<? extends C2SPacket>> c2sTypes = new Object2IntOpenHashMap<>();

		protected Builder(String id, int version) {
			this.id = id;
			this.version = version;
		}

		public Builder s2c(Class<? extends S2CPacket> clazz, Function<FriendlyByteBuf, S2CPacket> factory) {
			s2cPackets.add(factory);
			s2cTypes.put(clazz, s2cPackets.indexOf(factory));
			return this;
		}

		public Builder c2s(Class<? extends C2SPacket> clazz, Function<FriendlyByteBuf, C2SPacket> factory) {
			c2sPackets.add(factory);
			c2sTypes.put(clazz, c2sPackets.indexOf(factory));
			return this;
		}

		public PacketSet build() {
			return create(id, version, s2cPackets, s2cTypes, c2sPackets, c2sTypes);
		}
	}
}
