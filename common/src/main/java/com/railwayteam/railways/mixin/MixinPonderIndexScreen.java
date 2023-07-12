package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.conductor.ConductorCapItem;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.foundation.ponder.ui.PonderIndexScreen;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PonderIndexScreen.class, remap = false)
public class MixinPonderIndexScreen {
    @Inject(method = "Lcom/simibubi/create/foundation/ponder/ui/PonderIndexScreen;exclusions(Lnet/minecraft/world/item/Item;)Z", at =@At("HEAD"), cancellable = true)
    private static void exclusions(Item item, CallbackInfoReturnable<Boolean> cir) {
        if (item instanceof ConductorCapItem && !CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.BLUE).is(item))
            cir.cancel();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if ((block instanceof TrackBlock && !AllBlocks.TRACK.is(item)))
                cir.cancel();
        }
    }
}
