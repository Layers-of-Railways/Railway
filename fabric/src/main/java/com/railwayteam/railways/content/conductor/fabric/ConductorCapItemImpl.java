package com.railwayteam.railways.content.conductor.fabric;

import com.railwayteam.railways.content.conductor.ConductorCapItem;
import net.minecraft.world.item.DyeColor;

public class ConductorCapItemImpl extends ConductorCapItem {
	protected ConductorCapItemImpl(Properties props, DyeColor color) {
		super(props, color);
	}

	public static ConductorCapItem create(Properties props, DyeColor color) {
		return new ConductorCapItemImpl(props, color);
	}
}
