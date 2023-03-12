package com.railwayteam.railways.events;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.TrainMarkerDataUpdatePacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.Train;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        Level level = event.world;
        if (level.isClientSide) return;
        RedstoneLinkInstruction.tick(level);
        long ticks = level.getGameTime();
        for (Train train : Create.RAILWAYS.trains.values()) {
            long offsetTicks = ticks + train.id.hashCode();
            if (offsetTicks % Config.FAR_TRAIN_SYNC_TICKS.get() == 0) {
                CRPackets.channel.send(PacketDistributor.ALL.noArg(), new TrainMarkerDataUpdatePacket(train));
            }
            if (offsetTicks % Config.NEAR_TRAIN_SYNC_TICKS.get() == 0) {
                Entity trainEntity = train.carriages.get(0).anyAvailableEntity();
                if (trainEntity != null)
                    CRPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> trainEntity), new TrainMarkerDataUpdatePacket(train));
            }
        }
    }
}
