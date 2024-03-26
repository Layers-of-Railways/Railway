package com.railwayteam.railways.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LevelRenderer.class, priority = 1050)
public class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 3), require = 0)
    private Entity overrideEntity(Camera instance, Operation<Entity> original) {
        return instance.getEntity() instanceof ConductorEntity ? minecraft.player : original.call(instance);
    }
}
