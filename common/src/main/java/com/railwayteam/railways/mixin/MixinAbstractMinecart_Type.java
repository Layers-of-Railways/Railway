package com.railwayteam.railways.mixin;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = AbstractMinecart.Type.class, priority = 1674) // random priority to hopefully apply in a consistent order, extra safety
public class MixinAbstractMinecart_Type {
	@Final
	@Mutable
	@Shadow(aliases = { "field_7673", "f_vsosozul" }) // yarn, qm
	private static Type[] $VALUES; // moj

	@Invoker("<init>")
	private static Type railways$createType(String name, int ordinal) {
		throw new AssertionError();
	}

	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void railways$addTypes(CallbackInfo ci) {
		ArrayList<Type> types = new ArrayList<>(List.of($VALUES));
		types.add(railways$createType("RAILWAY_JUKEBOX", $VALUES.length));
		types.add(railways$createType("RAILWAY_WORKBENCH", $VALUES.length + 1));
		$VALUES = types.toArray(Type[]::new);
	}
}
