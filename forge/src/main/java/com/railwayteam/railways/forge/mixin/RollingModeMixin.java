package com.railwayteam.railways.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;

// empty mixin to pass class through the mixin plugin (can't just implement enum adding in mixin because we can't access the RollingMode class)
@Mixin(targets = "com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode", remap = false)
public class RollingModeMixin { }
