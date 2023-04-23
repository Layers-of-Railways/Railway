package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {
    @Shadow protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> cartType, Predicate<Entity> filter);

    @Inject(method = "getAnalogOutputSignal",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DetectorRailBlock;getInteractingMinecartOfType(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 1),
        cancellable = true)
    private void getOverrideAnalogOutputSignal(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        List<MinecartJukebox> jukeboxCarts = getInteractingMinecartOfType(level, pos, MinecartJukebox.class, cart -> true);
        if (!jukeboxCarts.isEmpty()) {
            cir.setReturnValue(jukeboxCarts.get(0).getComparatorOutput());
        }
    }
}
