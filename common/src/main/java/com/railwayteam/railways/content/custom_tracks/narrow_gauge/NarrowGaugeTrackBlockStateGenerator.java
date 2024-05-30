/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.custom_tracks.narrow_gauge;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class NarrowGaugeTrackBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @ExpectPlatform
    public static NarrowGaugeTrackBlockStateGenerator create() {
        throw new AssertionError();
    }
}


/*
model list

done: x_ortho
done: z_ortho
done: tie
done: segment left
done: segment right
done: teleport
done: diag
done: diag2
done: ascending
done: cross_ortho
done: cross_diag
done: cross_d1_xo
done: cross_d1_zo
done: cross_d2_xo
done: cross_d2_zo

 */