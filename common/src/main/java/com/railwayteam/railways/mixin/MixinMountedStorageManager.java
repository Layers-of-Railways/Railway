package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedStorageManager.class)
public class MixinMountedStorageManager {
    @Inject(method = "handlePlayerStorageInteraction", at = @At("HEAD"), cancellable = true)
    private void handlePlayerStorageInteraction(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir) {
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);

        if (player.level.isClientSide()) {
            if (info.state.getBlock() instanceof CraftingTableBlock)
                cir.setReturnValue(true);
        }

        Component name = info != null ? info.state.getBlock().getName() : Component.translatable("container.crafting");
        player.openMenu(new SimpleMenuProvider((syncId, inventory, player1) -> new CraftingMenu(syncId, inventory), name));

        Vec3 soundPos = contraption.entity.toGlobalVector(Vec3.atCenterOf(localPos), 0);
        player.level.playSound(null, BlockPos.containing(soundPos), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.75f, 1f);
        cir.setReturnValue(true);
    }
}
