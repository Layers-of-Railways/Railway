package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class MixinAbstractMinecart {
	@Inject(
			method = "createMinecart",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void railways$createCustomMinecarts(Level level, double x, double y, double z, Type type, CallbackInfoReturnable<AbstractMinecart> cir) {
		if (type == MinecartJukebox.TYPE)
			cir.setReturnValue(MinecartJukebox.create(level, x, y, z));
		if (type == MinecartWorkbench.TYPE)
			cir.setReturnValue(MinecartWorkbench.create(level, x, y, z));
	}
}
