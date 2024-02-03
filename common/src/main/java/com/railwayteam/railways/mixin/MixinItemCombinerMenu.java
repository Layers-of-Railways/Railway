package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.moving_bes.GuiBlockUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCombinerMenu.class)
public class MixinItemCombinerMenu {
    @Shadow @Final protected ContainerLevelAccess access;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void railways$addGuiBlocksToStillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        GuiBlockUtils.checkAccess(access, player, cir);
    }
}
