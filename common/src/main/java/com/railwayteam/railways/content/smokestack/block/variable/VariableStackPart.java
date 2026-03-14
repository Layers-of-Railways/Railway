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

package com.railwayteam.railways.content.smokestack.block.variable;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum VariableStackPart implements StringRepresentable {
    SINGLE("single", true, false),
    DOUBLE("double", true, true),
    SEGMENT("segment", false, true);

    private final String name;
    private final boolean top;
    private final boolean fullHeight;

    VariableStackPart(String name, boolean top, boolean fullHeight) {
        this.name = name;
        this.top = top;
        this.fullHeight = fullHeight;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isSegment() {
        return this == SEGMENT;
    }

    public boolean isFullHeight() {
        return fullHeight;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    public String generatedModelName() {
        return switch (this) {
            case SINGLE -> "";
            case DOUBLE -> "_double";
            case SEGMENT -> "_segment";
        };
    }

    public enum Type {
        STANDARD(VariableSmokeStackBlock.PART, VariableStackPart.SINGLE, ""),
        NO_HALF(VariableSmokeStackBlock.PART_NO_HALF, VariableStackPart.DOUBLE, "_double")
        ;

        public final EnumProperty<VariableStackPart> property;
        public final VariableStackPart defaultPart;
        public final String modelSuffix;

        Type(EnumProperty<VariableStackPart> property, VariableStackPart defaultPart, String modelSuffix) {
            this.property = property;
            this.defaultPart = defaultPart;
            this.modelSuffix = modelSuffix;
        }
    }
}
