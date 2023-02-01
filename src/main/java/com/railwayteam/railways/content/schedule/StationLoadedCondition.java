package com.railwayteam.railways.content.schedule;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
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
        ResourceKey<Level> stationDim = currentStation.getTileDimension();
        MinecraftServer server = level.getServer();
        if (server == null)
            return false;
        ServerLevel stationLevel = server.getLevel(stationDim);
        if (stationLevel == null) {
            return false;
        }
        return stationLevel.isPositionEntityTicking(currentStation.getTilePos());
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
