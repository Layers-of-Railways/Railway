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

package com.railwayteam.railways.content.custom_tracks.gen_template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TextureKey {
    STANDARD_TRACK("standard_track"),
    STANDARD_TRACK_MIP("standard_track_mip"),
    STANDARD_TRACK_CROSSING("standard_track_crossing"),
    PORTAL_TRACK("portal_track"),
    PORTAL_TRACK_MIP("portal_track_mip"),
    PARTICLE(null);

    private final @Nullable String path;

    TextureKey(@Nullable String path) {
        this.path = path;
    }

    public @NotNull String getPrefix() {
        if (this.path == null) {
            throw new IllegalStateException(name() + " texture key does not have a prefix");
        }

        return path + "_";
    }

    public @NotNull String getPath() {
        if (this.path == null) {
            throw new IllegalStateException(name() + " texture key does not have a path");
        }

        return path;
    }
}
