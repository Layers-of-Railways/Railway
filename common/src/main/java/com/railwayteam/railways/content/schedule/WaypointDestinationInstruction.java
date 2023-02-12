package com.railwayteam.railways.content.schedule;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.DestinationInstruction;
import net.minecraft.resources.ResourceLocation;

public class WaypointDestinationInstruction extends DestinationInstruction {

	@Override
	public boolean supportsConditions() {
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return Railways.asResource("waypoint_destination");
	}
}