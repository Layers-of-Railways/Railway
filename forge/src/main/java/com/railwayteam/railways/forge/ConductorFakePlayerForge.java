package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.foundation.utility.Components;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

// NOTICE: changes must be replicated in ConductorFakePlayerFabric.
// annoying that we can't merge them any further.
public class ConductorFakePlayerForge extends FakePlayer {
	private static final Connection NETWORK_MANAGER = new Connection(PacketFlow.CLIENTBOUND);

	public ConductorFakePlayerForge(ServerLevel level) {
		super(level, ConductorEntity.FAKE_PLAYER_PROFILE);
		connection = new ConductorNetHandler(level.getServer(), this);
	}

	@Override
	@NotNull
	public OptionalInt openMenu(MenuProvider container) {
		return OptionalInt.empty();
	}

	@Override
	@NotNull
	public Component getDisplayName() {
		return Components.translatable(Railways.MODID + "." + "conductor_name");
	}

	@Override
	public float getEyeHeight(@NotNull Pose pose) {
		return 0;
	}

	@Override
	public Vec3 position() {
		return new Vec3(getX(), getY(), getZ());
	}

	@Override
	public float getCurrentItemAttackStrengthDelay() {
		return 1 / 64f;
	}

	@Override
	public boolean canEat(boolean ignoreHunger) {
		return false;
	}

	@Override
	@NotNull
	public ItemStack eat(@NotNull Level world, ItemStack stack) {
		stack.shrink(1);
		return stack;
	}

	private static class ConductorNetHandler extends ServerGamePacketListenerImpl {
		public ConductorNetHandler(MinecraftServer server, ServerPlayer player) {
			super(server, NETWORK_MANAGER, player);
		}

		@Override
		public void send(@NotNull Packet<?> packet) {
		}

		@Override
		public void send(Packet<?> packet, @Nullable PacketSendListener listener) {
		}
	}
}
