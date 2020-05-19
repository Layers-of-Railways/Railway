package net.minecraft.inventory.container;

import net.minecraft.util.text.ITextComponent;

public interface INamedContainerProvider extends IContainerProvider {
   ITextComponent getDisplayName();
}