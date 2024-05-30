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

package com.railwayteam.railways.content.schedule;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StationLoadedCondition extends ScheduleWaitCondition {
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(ItemStack.EMPTY, Components.translatable("railways.schedule.condition.loaded"));
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        GlobalStation currentStation = train.getCurrentStation();
        if (currentStation == null)
            return false;
        ResourceKey<Level> stationDim = currentStation.getBlockEntityDimension();
        MinecraftServer server = level.getServer();
        if (server == null)
            return false;
        ServerLevel stationLevel = server.getLevel(stationDim);
        if (stationLevel == null) {
            return false;
        }
        return stationLevel.isPositionEntityTicking(currentStation.getBlockEntityPos());
    }

    @Override
    protected void writeAdditional(CompoundTag tag) {}

    @Override
    protected void readAdditional(CompoundTag tag) {}

    @Override
    public ResourceLocation getId() {
        return Railways.asResource("loaded");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        return Components.translatable("railways.schedule.condition.unloaded.status");
    }
}
