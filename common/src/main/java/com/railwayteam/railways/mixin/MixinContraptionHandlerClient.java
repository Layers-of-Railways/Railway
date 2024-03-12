package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.hand_coupler.HandCouplerCarriageSelectionPacket;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient {
    @Inject(method = "handleSpecialInteractions", at = @At(value = "HEAD"))
    private static void handCoupler(AbstractContraptionEntity contraptionEntity, Player player, BlockPos localPos, Direction side, InteractionHand interactionHand, CallbackInfoReturnable<Boolean> cir){
        if(CRItems.HAND_COUPLER.isIn(player.getItemInHand(interactionHand)) && contraptionEntity instanceof CarriageContraptionEntity car){
            CRPackets.PACKETS.send(new HandCouplerCarriageSelectionPacket(car.trainId, car.carriageIndex));
        }
    }
}
