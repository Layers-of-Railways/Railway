package com.railwayteam.railways.content.moving_bes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GuiBlockUtils {
    public static void checkAccess(ContainerLevelAccess containerLevelAccess, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (containerLevelAccess instanceof GuiBlockLevelAccess) {
            cir.setReturnValue(containerLevelAccess.evaluate((level, pos) -> player.distanceToSqr(
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D) <= 64.0D,
                    true
            ));
        }
    }
}
