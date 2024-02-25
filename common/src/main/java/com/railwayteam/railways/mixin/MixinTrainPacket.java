package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IHandcarTrain;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainPacket;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainPacket.class, priority = 523) // random priority to ensure consistent order if another mod changes packets as well
public class MixinTrainPacket {
    @Shadow
    Train train;

    @Inject(method = "write", at = @At(value = "RETURN", ordinal = 1))
    private void writeHandcarStatus(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(((IHandcarTrain) train).railways$isHandcar());
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "RETURN", ordinal = 1))
    private void readHandcarStatus(FriendlyByteBuf buffer, CallbackInfo ci) {
        ((IHandcarTrain) train).railways$setHandcar(buffer.readBoolean());
    }
}
