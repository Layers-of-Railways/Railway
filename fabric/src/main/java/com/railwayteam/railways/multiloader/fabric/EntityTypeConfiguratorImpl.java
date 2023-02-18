package com.railwayteam.railways.multiloader.fabric;

import com.railwayteam.railways.multiloader.EntityTypeConfigurator;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;

public class EntityTypeConfiguratorImpl extends EntityTypeConfigurator {
	private final FabricEntityTypeBuilder<?> builder;

	protected EntityTypeConfiguratorImpl(FabricEntityTypeBuilder<?> builder) {
		this.builder = builder;
	}

	public static EntityTypeConfigurator of(Object builder) {
		if (builder instanceof FabricEntityTypeBuilder<?> fabricBuilder)
			return new EntityTypeConfiguratorImpl(fabricBuilder);
		throw new IllegalArgumentException("builder must be a FabricEntityTypeBuilder");
	}

	@Override
	public EntityTypeConfigurator size(float width, float height) {
		builder.dimensions(EntityDimensions.scalable(width, height));
		return this;
	}

	@Override
	public EntityTypeConfigurator fireImmune() {
		builder.fireImmune();
		return this;
	}
}
