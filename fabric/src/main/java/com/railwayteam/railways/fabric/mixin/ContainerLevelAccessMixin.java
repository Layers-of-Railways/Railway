package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.moving_bes.GuiBlockContraptionWorld;
import com.railwayteam.railways.content.moving_bes.GuiBlockLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Empty mixin to get this to pass through the mixin config plugin, so we can do some asm on it :3
// Priority of 2000 to be applied after any other @Overwrite's
@Mixin(value = ContainerLevelAccess.class, priority = 2000)
public interface ContainerLevelAccessMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void create(Level level, BlockPos pos, CallbackInfoReturnable<ContainerLevelAccess> cir) {
        if (level instanceof GuiBlockContraptionWorld guiBlockContraptionWorld) {
            cir.setReturnValue(new GuiBlockLevelAccess(
                    guiBlockContraptionWorld.getLevel(),
                    guiBlockContraptionWorld.contraption.entity,
                    guiBlockContraptionWorld.blockPos
            ));
        }
    }
}
