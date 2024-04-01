package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Train.class)
public class TrainMixin {
    @ModifyArg(method = "burnFuel", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getBurnTime(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/RecipeType;)I"), remap = false)
    private ItemStack railways$disableFuelConsumptionBasedOnTag(ItemStack stack) {
        if (stack.is(CRTags.AllItemTags.NOT_TRAIN_FUEL.tag)) {
            return Items.AIR.getDefaultInstance();
        }
        return stack;
    }
}

