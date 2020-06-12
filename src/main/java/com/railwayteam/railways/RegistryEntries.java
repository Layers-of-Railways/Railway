package com.railwayteam.railways;

import com.railwayteam.railways.blocks.WayPointBlock;
import com.railwayteam.railways.items.WayPointToolItem;

import net.minecraftforge.registries.ObjectHolder;

public class RegistryEntries {
	@ObjectHolder(Railways.MODID + ":" + WayPointBlock.name)
    public static WayPointBlock WAY_POINT_BLOCK;
	
	@ObjectHolder(Railways.MODID + ":" + WayPointToolItem.name)
    public static WayPointToolItem WAY_POINT_TOOL;
	

	/*
    @ObjectHolder("createintegration:dynamo")
    public static TileEntityType<DynamoTile> DYNAMO_TILE;

    @ObjectHolder("createintegration:ender_crate")
    public static ContainerType<EnderContainer> ENDER_CONTAINER;*/	// EXAMPLE
}

