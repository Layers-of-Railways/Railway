package com.railwayteam.railways.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.util.IHaveCustomGoggleIcon;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayRendererMixin {
    @WrapOperation(
            method = "renderOverlay",
            at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/ItemEntry;asStack()Lnet/minecraft/world/item/ItemStack;")
    )
    private static ItemStack changeDisplayItem(ItemEntry<GogglesItem> instance, Operation<ItemStack> original,
                                               @Local BlockEntity be, @Local boolean wearingGoggles) {
        if (be instanceof IHaveCustomGoggleIcon gte && wearingGoggles) {
            return gte.railways$setGoggleIcon(Minecraft.getInstance().player.isShiftKeyDown());
        }

        return original.call(instance);
    }
}
