package com.railwayteam.railways.forge.events;

import com.railwayteam.railways.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventsForge {
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.START)
			ClientEvents.onClientTickStart(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		ClientEvents.onClientWorldLoad((Level) event.getWorld());
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		int key = event.getKey();
		boolean pressed = event.getAction() != 0;
		ClientEvents.onKeyInput(key, pressed);
	}
}
