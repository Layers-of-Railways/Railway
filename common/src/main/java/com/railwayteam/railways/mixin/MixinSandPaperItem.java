/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.RepaintingTarget;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SandPaperItem.class)
public class MixinSandPaperItem {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void stripLocometal(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        RepaintingTarget target = RepaintingTarget.get(level, pos, state);
        if (target == null) return;
        if (target.getColor().isNetherite()) return;

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        target.repaint(PalettesColor.NETHERITE);
        AllSoundEvents.SANDING_LONG.play(level, player, pos, 1, 1 + (level.random.nextFloat() * 0.5f - 1f) / 5f);
        level.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, pos, 0); // Spawn particles

        if (player != null)
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));

        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
