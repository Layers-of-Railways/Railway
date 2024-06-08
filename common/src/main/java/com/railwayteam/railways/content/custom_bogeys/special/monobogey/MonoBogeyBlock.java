/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.custom_bogeys.special.monobogey;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MonoBogeyBlock extends AbstractMonoBogeyBlock<MonoBogeyBlockEntity> {
    public MonoBogeyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.MONOBOGEY;
    }

    @Override
    public Class<MonoBogeyBlockEntity> getBlockEntityClass() {
        return MonoBogeyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MonoBogeyBlockEntity> getBlockEntityType() {
        return CRBlockEntities.MONO_BOGEY.get();
    }
}
