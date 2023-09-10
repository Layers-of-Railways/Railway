package com.railwayteam.railways.forge.events;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.incompatible_mods.IncompatibleModsCheck;
import com.railwayteam.railways.compat.incompatible_mods.optifine.OptifineWarningScreen;
import com.railwayteam.railways.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventsForge {
	@SubscribeEvent
	public static void onClientStart(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof TitleScreen) {
			ClientEvents.onClientStarted(Minecraft.getInstance());
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.START)
			ClientEvents.onClientTickStart(Minecraft.getInstance());
		else if (event.phase == Phase.END)
			ClientEvents.onClientTickEnd(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load event) {
		ClientEvents.onClientWorldLoad((Level) event.getLevel());
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.Key event) {
		int key = event.getKey();
		boolean pressed = event.getAction() != 0;
		ClientEvents.onKeyInput(key, pressed);
	}
}
