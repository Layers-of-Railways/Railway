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
					target = "Lnet/minecraftforge/network/NetworkHooks;onCustomPayload(Lnet/minecraftforge/network/ICustomPacket;Lnet/minecraft/network/Connection;)Z"
			),
			cancellable = true
	)
	private void railway$handleC2S(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
		ResourceLocation id = packet.getIdentifier();
		PacketSet handler = PacketSetImpl.HANDLERS.get(id);
		if (handler != null) {
			handler.handleC2SPacket(player, packet.getData());
			ci.cancel();
		}
	}
}
