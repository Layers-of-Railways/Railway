package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.ConductorCapItem;
import com.railwayteam.railways.registry.CRBlockPartials;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemModelShaper.class, remap = false)
public abstract class MixinItemModelShaper {
    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true, remap = true)
    private void addCustomConductorCapModels(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof ConductorCapItem) {
            String name = stack.getHoverName().getString();
            if (name.equals("IThundxr")) {
                cir.setReturnValue(CRBlockPartials.CUSTOM_CONDUCTOR_CAPS.get("Crown").get());
                return;
            }
            if (CRBlockPartials.CUSTOM_CONDUCTOR_CAPS.containsKey(name)) {
                cir.setReturnValue(CRBlockPartials.CUSTOM_CONDUCTOR_CAPS.get(name).get());
            }
        }
    }
}
