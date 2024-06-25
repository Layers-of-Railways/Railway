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

package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.IConductorHoldingFakePlayer;
import com.simibubi.create.foundation.utility.Components;
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

import java.lang.ref.WeakReference;
import java.util.OptionalInt;

// NOTICE: changes must be replicated in ConductorFakePlayerFabric.
// annoying that we can't merge them any further.
public class ConductorFakePlayerForge extends FakePlayer implements IConductorHoldingFakePlayer {
	private static final Connection NETWORK_MANAGER = new Connection(PacketFlow.CLIENTBOUND);

	private final WeakReference<ConductorEntity> conductor;
	public ConductorFakePlayerForge(ServerLevel level, ConductorEntity conductor) {
		super(level, ConductorEntity.FAKE_PLAYER_PROFILE);
		connection = new ConductorNetHandler(level.getServer(), this);
		this.conductor = new WeakReference<>(conductor);
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

	@Override
	public @Nullable ConductorEntity getConductor() {
		return conductor.get();
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
