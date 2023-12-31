package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeFireball.class)
public abstract class MixinLargeFireball extends Fireball {
    private MixinLargeFireball(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private void markExplode(HitResult result, CallbackInfo ci) {
        if (getOwner() instanceof Ghast) {
            Railways.largeGhastFireballExplosion = true;
        }
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LargeFireball;discard()V"))
    private void markExplodeEnd(HitResult result, CallbackInfo ci) {
        Railways.largeGhastFireballExplosion = false;
    }
}
