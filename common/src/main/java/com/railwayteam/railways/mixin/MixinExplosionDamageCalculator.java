/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.util.MixinVariables;
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
        if (explosion.getDirectSourceEntity() instanceof Creeper || MixinVariables.largeGhastFireballExplosion) {
            if (AllTags.AllBlockTags.TRACKS.matches(state) && !CRConfigs.server().explosiveTrackDamage.get()) {
                cir.setReturnValue(false);
            }
        }
    }
}
