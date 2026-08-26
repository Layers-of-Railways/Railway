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

package com.railwayteam.railways.fabric;

import com.simibubi.create.content.decoration.copycat.FilteredBlockAndTintGetter;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public class FilteredRenderAttachedBlockView extends FilteredBlockAndTintGetter implements RenderAttachedBlockView {

    private final RenderAttachedBlockView wrapped;

    public FilteredRenderAttachedBlockView(RenderAttachedBlockView wrapped, Predicate<BlockPos> filter) {
        super(wrapped, filter);
        this.wrapped = wrapped;
    }

    @Override
    public @Nullable Object getBlockEntityRenderAttachment(BlockPos pos) {
        return wrapped.getBlockEntityRenderAttachment(pos);
    }
}
