package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.multiloader.forge.PacketSetImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(
			method = "handleCustomPayload",
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V",
					remap = false
			),
			cancellable = true
	)
	private void railway$handleS2C(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
		ResourceLocation id = packet.getIdentifier();
		PacketSet handler = PacketSetImpl.HANDLERS.get(id);
		if (handler != null) {
			handler.handleS2CPacket(minecraft, packet.getData());
			ci.cancel();
		}
	}
}
