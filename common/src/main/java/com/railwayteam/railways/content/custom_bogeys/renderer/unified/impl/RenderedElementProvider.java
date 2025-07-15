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

package com.railwayteam.railways.content.custom_bogeys.renderer.unified.impl;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class RenderedElementProvider implements ElementProvider<RenderedElement> {
    private final List<RenderedElement.Single> singleElements;
    private final List<RenderedElement.Multiple> multipleElements;
    private boolean frozen = false;

    RenderedElementProvider(List<RenderedElement.Single> singleElements, List<RenderedElement.Multiple> multipleElements) {
        this.singleElements = singleElements;
        this.multipleElements = multipleElements;
    }

    @Override
    public @NotNull Affine<RenderedElement> create(@NotNull PartialModel model) {
        if (frozen) {
            throw new IllegalStateException("Cannot create elements after build");
        }

        RenderedElement element = new RenderedElement();
        singleElements.add(new RenderedElement.Single(element, model));
        return element;
    }

    @Override
    public @NotNull Affine<RenderedElement> @NotNull [] create(@NotNull PartialModel model, int count) {
        if (frozen) {
            throw new IllegalStateException("Cannot create elements after build");
        }

        RenderedElement[] elements = new RenderedElement[count];
        for (int i = 0; i < count; i++) {
            elements[i] = new RenderedElement();
        }
        multipleElements.add(new RenderedElement.Multiple(elements, model));
        return elements;
    }

    @Override
    public void freeze() {
        frozen = true;
    }
}
