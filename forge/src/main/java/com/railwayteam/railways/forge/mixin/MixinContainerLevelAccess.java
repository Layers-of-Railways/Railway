package com.railwayteam.railways.forge.mixin;

import net.minecraft.world.inventory.ContainerLevelAccess;
import org.spongepowered.asm.mixin.Mixin;

// Empty mixin to get this to pass through the mixin config plugin, so we can do some asm on it :3
@Mixin(ContainerLevelAccess.class)
public interface MixinContainerLevelAccess { }
