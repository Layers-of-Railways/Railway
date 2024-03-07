package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient {
    @Inject(method = "handleSpecialInteractions", at = @At(value = "HEAD"))
    private static void handCoupler(AbstractContraptionEntity contraptionEntity, Player player, BlockPos localPos, Direction side, InteractionHand interactionHand, CallbackInfoReturnable<Boolean> cir){
        if(CRItems.HAND_COUPLER.isIn(player.getItemInHand(interactionHand)) && contraptionEntity instanceof CarriageContraptionEntity car){
            if (player.level.isClientSide)
                return;

            if(player.getItemInHand(interactionHand).hasTag()){
                UUID firstTrainId = player.getItemInHand(interactionHand).getTag().getUUID("TrainId");
                int firstCarriageIndex = player.getItemInHand(interactionHand).getTag().getInt("CarriageIndex");
                Train firstTrain = Create.RAILWAYS.trains.get(firstTrainId);
                if(firstTrain == null) return;

                if(firstTrainId.equals(car.trainId)){
                        int noe = firstTrain.carriages.size()- firstCarriageIndex -1;
                        Railways.LOGGER.info(String.valueOf(noe));
                        TrainUtils.splitTrain(firstTrain, noe);

                }else{
                    int distance = (int) Math.round(Objects.requireNonNull(firstTrain.carriages.get(firstTrain.carriages.size() - 1).trailingBogey().getAnchorPosition())
                            .distanceTo(car.getCarriage().leadingBogey().getAnchorPosition()));

                    TrainUtils.combineTrains(firstTrain, car.getCarriage().train, player.position(), player.level, distance);
                }
                player.getItemInHand(interactionHand).removeTagKey("TrainId");
                player.getItemInHand(interactionHand).removeTagKey("CarriageIndex");
                return;

            }
            CompoundTag tag = new CompoundTag();
            tag.putUUID("TrainId", car.trainId);
            tag.putInt("CarriageIndex", car.carriageIndex);
            player.getItemInHand(interactionHand).setTag(tag);
        }
    }
}
