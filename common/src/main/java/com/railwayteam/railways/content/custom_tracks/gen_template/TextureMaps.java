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

import com.simibubi.create.content.trains.track.TrackShape;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum TextureMaps {
    STANDARD((shape, textureMap) -> {
        switch (shape) {
            case TE, TN, TS, TW -> {
                //portal 1, 2, 3 portal, portal_mip, standard
                textureMap.put("1", TextureKey.PORTAL_TRACK);
                textureMap.put("2", TextureKey.PORTAL_TRACK_MIP);
                textureMap.put("3", TextureKey.STANDARD_TRACK);
            }
            case AE, AW, AN, AS -> {
                //ascending 0, 1 standard, mip
                textureMap.put("0", TextureKey.STANDARD_TRACK);
                textureMap.put("1", TextureKey.STANDARD_TRACK_MIP);
            }
            case CR_O, XO, ZO -> {
                //cross ortho 1, 2, 3, standard, mip, crossing
                //normal (x/z)_ortho 1, 2, standard mip
                textureMap.put("1", TextureKey.STANDARD_TRACK);
                textureMap.put("2", TextureKey.STANDARD_TRACK_MIP);
                textureMap.put("3", TextureKey.STANDARD_TRACK_CROSSING);
            }
            default -> {
                //obj_track, 0, 1, 2, standard, mip, crossing
                textureMap.put("0", TextureKey.STANDARD_TRACK);
                textureMap.put("1", TextureKey.STANDARD_TRACK_MIP);
                textureMap.put("2", TextureKey.STANDARD_TRACK_CROSSING);
            }
        }
    }),
    NARROW((shape, textureMap) -> {
        switch (shape) {
            case TE, TN, TS, TW -> {
                //portal 1, 2, 3 portal, portal_mip, standard
                textureMap.put("1", TextureKey.PORTAL_TRACK);
                textureMap.put("2", TextureKey.PORTAL_TRACK_MIP);
                textureMap.put("3", TextureKey.STANDARD_TRACK);
            }
            case AE, AW, AN, AS -> {
                //ascending 0, 1 standard, mip
                textureMap.put("0", TextureKey.STANDARD_TRACK);
                textureMap.put("1", TextureKey.STANDARD_TRACK_MIP);
            }
            case CR_O, XO, ZO, ND, PD, CR_D, CR_NDX, CR_NDZ, CR_PDX, CR_PDZ -> { // switched a lot of models to json
                //cross ortho 1, 2, 3, standard, mip, crossing
                //normal (x/z)_ortho 1, 2, standard mip
                textureMap.put("1", TextureKey.STANDARD_TRACK);
                textureMap.put("2", TextureKey.STANDARD_TRACK_MIP);
                textureMap.put("3", TextureKey.STANDARD_TRACK_CROSSING);
            }
            default -> {
                //obj_track, 0, 1, 2, standard, mip, crossing
                textureMap.put("0", TextureKey.STANDARD_TRACK);
                textureMap.put("1", TextureKey.STANDARD_TRACK_MIP);
                textureMap.put("2", TextureKey.STANDARD_TRACK_CROSSING);
            }
        }
    }),
    WIDE(NARROW);

    public final @Unmodifiable Map<TrackShape, @Unmodifiable Map<String, TextureKey>> map;

    TextureMaps(TextureMaps other) {
        this.map = other.map;
    }

    TextureMaps(BiConsumer<TrackShape, Map<String, TextureKey>> populator) {
        Map<TrackShape, Map<String, TextureKey>> map = new HashMap<>();
        for (TrackShape shape : TrackShape.values()) {
            Map<String, TextureKey> textureMap = new HashMap<>();
            populator.accept(shape, textureMap);
            map.put(shape, Map.copyOf(textureMap));
        }
        this.map = Map.copyOf(map);
    }
}
