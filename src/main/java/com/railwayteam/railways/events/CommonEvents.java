package com.railwayteam.railways.events;

import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        Level level = event.level;
        RedstoneLinkInstruction.tick(level);
    }
}
