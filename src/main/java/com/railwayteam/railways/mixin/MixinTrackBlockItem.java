package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.track.TrackBlockItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.railwayteam.railways.mixin_interfaces.ITrackCheck.check;

@Mixin(TrackBlockItem.class)
public class MixinTrackBlockItem {
  @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", remap = false)) //TODO track api
  private boolean customCheck(BlockEntry<?> instance, ItemStack itemStack) {
    return check(instance, itemStack);
  }
}
