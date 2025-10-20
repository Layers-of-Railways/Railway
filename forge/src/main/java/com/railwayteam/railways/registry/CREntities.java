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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorRenderer;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.railwayteam.railways.multiloader.EntityTypeConfigurator;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Consumer;

public class CREntities {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final EntityEntry<MinecartWorkbench> CART_BLOCK = REGISTRATE.<MinecartWorkbench>entity("benchcart", MinecartWorkbench::new, MobCategory.MISC)
            .renderer(() -> CREntities::cartRenderer)
            .properties(configure(c -> c.size(0.98F, 0.7F)))
            .lang("Minecart with Workbench")
            .register();

    public static final EntityEntry<MinecartJukebox> CART_JUKEBOX = REGISTRATE.<MinecartJukebox>entity("jukeboxcart", MinecartJukebox::new, MobCategory.MISC)
            .renderer(() -> CREntities::cartRenderer)
            .properties(configure(c -> c.size(0.98F, 0.7F)))
            .lang("Minecart with Jukebox")
            .register();

    public static final EntityEntry<ConductorEntity> CONDUCTOR = REGISTRATE.entity("conductor", ConductorEntity::new, MobCategory.CREATURE)
            .renderer(() -> ConductorRenderer::new)
            .lang("Conductor")
            .properties(configure(c -> c.size(0.6f, 1.5f).fireImmune()))
            .loot((table, type) -> table.add(type, new LootTable.Builder().withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0f))
                            .add(LootItem.lootTableItem(AllItems.ANDESITE_ALLOY.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 1.0f)))
                            )
            )))
            .attributes(ConductorEntity::createAttributes)
            .register();

    private static <T> NonNullConsumer<T> configure(Consumer<EntityTypeConfigurator> consumer) {
        return builder -> consumer.accept(EntityTypeConfigurator.of(builder));
    }

    @OnlyIn(Dist.CLIENT)
    private static EntityRenderer<AbstractMinecart> cartRenderer(Context ctx) {
        return new MinecartRenderer<>(ctx, ModelLayers.MINECART);
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() {
    }
}
