/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.mixin.AccessorTrain;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Train;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AddTrainEndPacket implements S2CPacket {
    final UUID trainId;
    final UUID backTrainId;
    final int middleSpacing;
    final boolean doubleEnded;

    public AddTrainEndPacket(Train train, Train backTrain, int middleSpacing, boolean doubleEnded) {
        trainId = train.id;
        this.backTrainId = backTrain.id;
        this.middleSpacing = middleSpacing;
        this.doubleEnded = doubleEnded;
    }

    public AddTrainEndPacket(FriendlyByteBuf buf) {
        trainId = buf.readUUID();
        backTrainId = buf.readUUID();
        middleSpacing = buf.readInt();
        doubleEnded = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.trainId);
        buffer.writeUUID(this.backTrainId);
        buffer.writeInt(this.middleSpacing);
        buffer.writeBoolean(this.doubleEnded);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(Minecraft mc) {
        Level level = mc.level;
        if (level != null) {
            Train train = CreateClient.RAILWAYS.trains.get(trainId);
            Train backTrain = CreateClient.RAILWAYS.trains.get(backTrainId);
            if (train != null && backTrain != null) {
                train.carriages.addAll(backTrain.carriages);
                backTrain.carriages.clear();

                train.carriageSpacing.add(middleSpacing);
                train.carriageSpacing.addAll(backTrain.carriageSpacing);
                backTrain.carriageSpacing.clear();

                double[] newStress = new double[((AccessorTrain) train).railways$getStress().length + ((AccessorTrain) backTrain).railways$getStress().length + 1];
                System.arraycopy(((AccessorTrain) train).railways$getStress(), 0, newStress, 0, ((AccessorTrain) train).railways$getStress().length);
                newStress[((AccessorTrain) train).railways$getStress().length] = 0;
                System.arraycopy(((AccessorTrain) backTrain).railways$getStress(), 0, newStress, ((AccessorTrain) train).railways$getStress().length + 1, ((AccessorTrain) backTrain).railways$getStress().length);
                ((AccessorTrain) train).railways$setStress(newStress);
                train.doubleEnded = doubleEnded;

                train.carriages.forEach(c -> c.setTrain(train));

                CreateClient.RAILWAYS.trains.remove(backTrainId);
            }
        }
    }
}
