package com.railwayteam.railways.content.Conductor.toolbox;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MountedToolboxSlot extends SlotItemHandler {

	private final MountedToolboxContainer toolboxMenu;

	public MountedToolboxSlot(MountedToolboxContainer container, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.toolboxMenu = container;
	}
	
	@Override
	public boolean isActive() {
		return !toolboxMenu.renderPass && super.isActive();
	}

}
