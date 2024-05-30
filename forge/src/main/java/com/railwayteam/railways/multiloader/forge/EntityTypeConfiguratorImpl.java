/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.multiloader.EntityTypeConfigurator;
import net.minecraft.world.entity.EntityType;

public class EntityTypeConfiguratorImpl extends EntityTypeConfigurator {
	private final EntityType.Builder<?> builder;

	protected EntityTypeConfiguratorImpl(EntityType.Builder<?> builder) {
		this.builder = builder;
	}

	public static EntityTypeConfigurator of(Object builder) {
		if (builder instanceof EntityType.Builder<?> fabricBuilder)
			return new EntityTypeConfiguratorImpl(fabricBuilder);
		throw new IllegalArgumentException("builder must be an EntityType.Builder");
	}

	@Override
	public EntityTypeConfigurator size(float width, float height) {
		builder.sized(width, height);
		return this;
	}

	@Override
	public EntityTypeConfigurator fireImmune() {
		builder.fireImmune();
		return this;
	}
}
