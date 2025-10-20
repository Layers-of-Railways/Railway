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

public class ChopTrainEndPacket implements S2CPacket {
    final UUID trainId;
    final int numberOfCarriages;
    final boolean doubleEnded;

    public ChopTrainEndPacket(Train train, int numberOfCarriages, boolean doubleEnded) {
        trainId = train.id;
        this.numberOfCarriages = numberOfCarriages;
        this.doubleEnded = doubleEnded;
    }

    public ChopTrainEndPacket(FriendlyByteBuf buf) {
        trainId = buf.readUUID();
        numberOfCarriages = buf.readInt();
        doubleEnded = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.trainId);
        buffer.writeInt(this.numberOfCarriages);
        buffer.writeBoolean(this.doubleEnded);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(Minecraft mc) {
        Level level = mc.level;
        if (level != null) {
            Train train = CreateClient.RAILWAYS.trains.get(trainId);
            if (train != null) {
                for (int i = 0; i < numberOfCarriages; i++) {
                    train.carriages.remove(train.carriages.size() - 1);
                    train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
                }
                double[] originalStress = ((AccessorTrain) train).railways$getStress();
                double[] newStress = new double[originalStress.length - numberOfCarriages];
                System.arraycopy(originalStress, 0, newStress, 0, newStress.length);
                ((AccessorTrain) train).railways$setStress(newStress);
                train.doubleEnded = doubleEnded;
            }
        }
    }
}
