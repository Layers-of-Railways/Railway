package com.railwayteam.railways.content.coupling.hand_coupler;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class HandCouplerCarriageSelectionPacket implements C2SPacket {
    private UUID trainUUID;
    private int carriageIndex;

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
        if(!stack.is(CRItems.HAND_COUPLER.get())) return;

        Train train = Create.RAILWAYS.trains.get(trainUUID);
        Carriage car = train.carriages.get(carriageIndex);

        if(stack.hasTag()){
            UUID firstTrainId = stack.getTag().getUUID("TrainId");
            int firstCarriageIndex = stack.getTag().getInt("CarriageIndex");
            Train firstTrain = Create.RAILWAYS.trains.get(firstTrainId);

            if(firstTrain == null) return;

            if(firstTrainId.equals(trainUUID)){
                int noe = firstTrain.carriages.size()- Math.min(carriageIndex, firstCarriageIndex) -1;
                TrainUtils.splitTrain(firstTrain, noe);

            }else{
                int distance1 = (int) Math.round(car.leadingBogey().getAnchorPosition()
                        .distanceTo(firstTrain.carriages.get(firstTrain.carriages.size() - 1).trailingBogey().getAnchorPosition()));
                int distance2 = (int) Math.round(car.trailingBogey().getAnchorPosition()
                        .distanceTo(firstTrain.carriages.get(firstTrain.carriages.size() - 1).leadingBogey().getAnchorPosition()));

                if(distance1 <= distance2)
                    TrainUtils.combineTrains(firstTrain, train, sender.position(), sender.level, distance1);
                else
                    TrainUtils.combineTrains(train, firstTrain, sender.position(), sender.level, distance2);

            }
            stack.removeTagKey("TrainId");
            stack.removeTagKey("CarriageIndex");
            return;

        }
        CompoundTag tag = new CompoundTag();
        tag.putUUID("TrainId", trainUUID);
        tag.putInt("CarriageIndex", carriageIndex);
        stack.setTag(tag);
    }
}
