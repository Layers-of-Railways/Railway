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

package com.railwayteam.railways.util;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class MinRespectingScrollValueBehaviour extends ScrollValueBehaviour {
    private int min;

    public MinRespectingScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Override
    public ScrollValueBehaviour between(int min, int max) {
        this.min = min;
        return super.between(min, max);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(this.label, this.max - this.min, 1,
            ImmutableList.of(Components.literal("Value")),
            new ValueSettingsFormatter(vs -> new ValueSettings(vs.row(), vs.value() + this.min).format()));
    }

    @Override
    public void setValueSettings(Player player, ValueSettings vs, boolean ctrlDown) {
        super.setValueSettings(player, new ValueSettings(vs.row(), vs.value() + this.min), ctrlDown);
    }

    @Override
    public ValueSettings getValueSettings() {
        ValueSettings vs = super.getValueSettings();
        return new ValueSettings(vs.row(), vs.value() - min);
    }
}
