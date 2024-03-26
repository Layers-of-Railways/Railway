package com.railwayteam.railways.content.coupling.hand_coupler;

import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class HandCouplerCarriageSelectionPacket implements C2SPacket {
    private final UUID trainUUID;
    private final int carriageIndex;

    public HandCouplerCarriageSelectionPacket(UUID trainUUID, int carriageIndex){
        this.trainUUID = trainUUID;
        this.carriageIndex = carriageIndex;
    }

    public HandCouplerCarriageSelectionPacket(FriendlyByteBuf buffer){
        trainUUID = buffer.readUUID();
        carriageIndex = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(trainUUID);
        buffer.writeInt(carriageIndex);
    }

    @Override
    public void handle(ServerPlayer sender) {
        ItemStack stack = sender.getItemInHand(sender.getUsedItemHand());
        if (!stack.is(CRItems.HAND_COUPLER.get())) return;

        Train train = Create.RAILWAYS.trains.get(trainUUID);
        Carriage car = train.carriages.get(carriageIndex);

        if (stack.hasTag() && stack.getTag() != null) {
            UUID firstTrainId = stack.getTag().getUUID("TrainId");
            int firstCarriageIndex = stack.getTag().getInt("CarriageIndex");
            Train firstTrain = Create.RAILWAYS.trains.get(firstTrainId);

            if (firstTrain == null){
                endWithReason("railways.hand_coupler.no_train", false, sender, stack);
                return;
            }

            if(firstTrain.graph.id != train.graph.id){
                endWithReason("railways.hand_coupler.different_graph", false, sender, stack);
                return;
            }

            if (firstTrainId.equals(trainUUID)){
                if((carriageIndex-firstCarriageIndex)*(carriageIndex-firstCarriageIndex) != 1){
                    endWithReason("railways.hand_coupler.not_adjacent", false, sender, stack);
                    return;
                }
                int noe = firstTrain.carriages.size()- Math.min(carriageIndex, firstCarriageIndex) -1;
                TrainUtils.splitTrain(firstTrain, noe);
                endWithReason("railways.hand_coupler.uncoupled", true, sender, stack);
                return;
            } else {
                Vec3 leadingPosition = train.carriages.get(0).leadingBogey().leading().getPosition(train.graph);
                Vec3 trailingPosition = train.carriages.get(train.carriages.size()-1).trailingBogey().trailing().getPosition(train.graph);


                Vec3 firstLeadingPosition = firstTrain.carriages.get(0).leadingBogey().leading().getPosition(firstTrain.graph);
                Vec3 firstTrailingPosition = firstTrain.carriages.get(firstTrain.carriages.size() - 1).trailingBogey().trailing().getPosition(firstTrain.graph);

                double leadingToTrailingDistanceSqr = leadingPosition.distanceToSqr(firstTrailingPosition);
                double trailingToLeadingDistanceSqr = trailingPosition.distanceToSqr(firstLeadingPosition);

                double closestDistance = Math.min(leadingToTrailingDistanceSqr, trailingToLeadingDistanceSqr);
                if(leadingPosition.distanceToSqr(firstLeadingPosition) < closestDistance
                || trailingPosition.distanceToSqr(firstTrailingPosition) < closestDistance){
                    endWithReason("railways.hand_coupler.wrong_direction", false, sender, stack);
                    return;
                }

                if (leadingToTrailingDistanceSqr <= trailingToLeadingDistanceSqr){
                    int distance = (int) Math.round(train.carriages.get(0).leadingBogey().getAnchorPosition()
                            .distanceTo(firstTrain.carriages.get(firstTrain.carriages.size()-1).trailingBogey().getAnchorPosition())
                    );
                    if(distance > 10) return;
                    TrainUtils.combineTrains(firstTrain, train, sender.position(), sender.level, distance);
                }
                else{
                    int distance = (int) Math.round(train.carriages.get(train.carriages.size()-1).trailingBogey().getAnchorPosition()
                            .distanceTo(firstTrain.carriages.get(0).leadingBogey().getAnchorPosition())
                    );
                    if(distance > 10) return;
                    TrainUtils.combineTrains(train, firstTrain, sender.position(), sender.level, distance);
                }
            }
            endWithReason("railways.hand_coupler.coupled", true, sender, stack);
            return;
        }
        setHandCouplerTags(stack, trainUUID, carriageIndex);
    }

    void endWithReason(String key, boolean success, Player player, ItemStack stack){
        (success ? AllSoundEvents.CONTRAPTION_DISASSEMBLE : AllSoundEvents.DENY).playFrom(player);
        endWithReason(Components.translatable(key).withStyle(success ? ChatFormatting.GREEN : ChatFormatting.RED),
                player, stack);
    }
    void endWithReason(Component reason, Player player, ItemStack stack){
        player.displayClientMessage(reason, true);
        clearHandCouplerTags(stack);
    }

    void setHandCouplerTags(ItemStack stack, UUID trainUUID, int carriageIndex){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("TrainId", trainUUID);
        tag.putInt("CarriageIndex", carriageIndex);
        stack.setTag(tag);
    }
    void clearHandCouplerTags(ItemStack stack){
        stack.removeTagKey("TrainId");
        stack.removeTagKey("CarriageIndex");
    }
}
