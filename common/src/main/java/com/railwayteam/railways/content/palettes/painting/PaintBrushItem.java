/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.palettes.painting;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PaintBrushItem extends Item implements Vanishable {
    public PaintBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (context.getHand() != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player == null) return InteractionResult.PASS;

        ItemStack offhandStack = player.getOffhandItem();
        if (!(offhandStack.getItem() instanceof PaintPitcherItem paintPitcher)) return InteractionResult.PASS;

        BlockState clickedState = level.getBlockState(clickedPos);
        if (!CRTags.AllBlockTags.LOCOMETAL.matches(clickedState.getBlock())) return InteractionResult.PASS;

        PalettesColor pitcherColor = paintPitcher.getColor();
        Pair<Styles, PalettesColor> info = CRPalettes.getStyleForBlock(clickedState.getBlock());
        if (info == null) return InteractionResult.PASS;
        if (info.getSecond() == pitcherColor) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (!PaintPitcherItem.repaint(level, clickedPos, clickedState, pitcherColor)) return InteractionResult.FAIL;

        PaintPitcherItem.usePaint(player, InteractionHand.OFF_HAND);
        player.getMainHandItem().hurtAndBreak(1, player,
            (playerEntity) -> playerEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        return InteractionResult.SUCCESS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }
}
