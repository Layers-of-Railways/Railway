/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Predicate;

@SuppressWarnings("SameParameterValue")
public class CRPotatoProjectileTypes {

    @SuppressWarnings("unused")
    public static final PotatoCannonProjectileType PAINT_PITCHER = create("paint_pitcher")
        .damage(3)
        .reloadTicks(15)
        .velocity(1.25f)
        .knockback(1.5f)
        .renderTumbling()
        .onEntityHit(potion(MobEffects.POISON, 1, 100, false))
        .onBlockHit(($, $$) -> true) // actual hit logic handled by mixin
        .registerAndAssign(CRItems.FILLED_PITCHERS.toArray(ItemEntry[]::new));

    private static PotatoCannonProjectileType.Builder create(String name) {
        return new PotatoCannonProjectileType.Builder(Railways.asResource(name));
    }

    private static Predicate<EntityHitResult> potion(MobEffect effect, int level, int ticks, boolean recoverable) {
        return ray -> {
            Entity entity = ray.getEntity();
            if (entity.level.isClientSide)
                return true;
            if (entity instanceof LivingEntity)
                applyEffect((LivingEntity) entity, new MobEffectInstance(effect, ticks, level - 1));
            return !recoverable;
        };
    }

    private static void applyEffect(LivingEntity entity, MobEffectInstance effect) {
        if (effect.getEffect()
            .isInstantenous())
            effect.getEffect()
                .applyInstantenousEffect(null, null, entity, effect.getDuration(), 1.0);
        else
            entity.addEffect(effect);
    }

    public static void register() {}
}
