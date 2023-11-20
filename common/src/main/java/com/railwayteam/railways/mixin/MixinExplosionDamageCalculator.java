package com.railwayteam.railways.mixin;

import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {
    ExplosionDamageCalculator.class,
    EntityBasedExplosionDamageCalculator.class
})
public class MixinExplosionDamageCalculator {
    @Inject(method = "shouldBlockExplode", at = @At("HEAD"), cancellable = true)
    private void creepersDontBreakTracks(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> cir) {
        if (explosion.getSourceMob() instanceof Creeper && AllTags.AllBlockTags.TRACKS.matches(state) && !CRConfigs.server().creeperTrackDamage.get()) {
            cir.setReturnValue(false);
        }
    }
}
