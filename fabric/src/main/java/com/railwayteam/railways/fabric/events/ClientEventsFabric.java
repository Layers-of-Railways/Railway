package com.railwayteam.railways.fabric.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.events.ClientEvents;
import com.railwayteam.railways.registry.CRParticleTypes;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class ClientEventsFabric {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
		ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onClientTickEnd);
		KeyInputCallback.EVENT.register((key, scancode, action, mods) -> {
			ClientEvents.onKeyInput(key, action != 0);
		});
		ClientWorldEvents.LOAD.register((mc, level) -> ClientEvents.onClientWorldLoad(level));
		ParticleManagerRegistrationCallback.EVENT.register(CRParticleTypes::registerFactories);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(ClientEventsFabric::onRenderWorld);
	}

	private static void onRenderWorld(WorldRenderContext event) {
		PoseStack ms = event.matrixStack();
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
