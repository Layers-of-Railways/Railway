package com.railwayteam.railways.mixin;


import com.simibubi.create.content.trains.entity.Carriage;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Carriage.class, remap = false)
public interface AccessorCarriage {
	@Accessor
	Map<Integer, CompoundTag> getSerialisedPassengers();
}
