package com.railwayteam.railways.items;

import com.railwayteam.railways.Railways;

import net.minecraft.item.Item;

public class WayPointToolItem extends Item{
	public static final String name = "waypoint_manager";
	
	public WayPointToolItem(Properties properties) {
		super(properties);
		setRegistryName(Railways.createResourceLocation(name));
	}
}
