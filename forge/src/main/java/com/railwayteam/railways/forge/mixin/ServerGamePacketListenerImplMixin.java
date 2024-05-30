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

package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.multiloader.forge.PacketSetImpl;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(
			method = "handleCustomPayload",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/network/NetworkHooks;onCustomPayload(Lnet/minecraftforge/network/ICustomPacket;Lnet/minecraft/network/Connection;)Z",
					remap = false // forge method + names are moj
			),
			cancellable = true
	)
	private void railways$handleC2S(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
		ResourceLocation id = packet.getIdentifier();
		PacketSet handler = PacketSetImpl.HANDLERS.get(id);
		if (handler != null) {
			handler.handleC2SPacket(player, packet.getData());
			ci.cancel();
		}
	}
}
