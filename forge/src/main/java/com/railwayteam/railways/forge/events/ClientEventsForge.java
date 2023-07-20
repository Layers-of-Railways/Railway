package com.railwayteam.railways.forge.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.events.ClientEvents;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventsForge {
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

	@SubscribeEvent
	public static void onRenderWorld(RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_PARTICLES)
			return;
		PoseStack ms = event.getPoseStack();
		ms.pushPose();
		SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
		float partialTicks = AnimationTickHolder.getPartialTicks();
		Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
			.getPosition();

		ClientEvents.renderWorldLast(ms, buffer, camera);

		buffer.draw();
		RenderSystem.enableCull();
		ms.popPose();
	}
}
