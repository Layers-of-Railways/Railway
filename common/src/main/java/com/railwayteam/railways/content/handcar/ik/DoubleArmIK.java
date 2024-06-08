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

package com.railwayteam.railways.content.handcar.ik;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class DoubleArmIK {
    /**
     * Calculates coordinates of a double-armed joint
     * @param topPoint coordinates of the top fixed point
     * @param bottomPoint coordinates of the bottom fixed point
     * @param k radius of 'upper' circle
     * @param g radius of 'lower' circle
     * @return coordinates of the moving hinge
     */
    public static Vec2 calculateJointOffset(final Vec2 topPoint, final Vec2 bottomPoint, final double k, final double g) {
        final double d = distance(topPoint, bottomPoint);

        /*
        Y-axis of localIntersection is along the axis from bottomPoint->topPoint
        X-axis is perpendicular
         */
        Vec2 localIntersection = getIntersection(d, k, g);

        /*
        ----------- topPoint
            /|
           / |
          /  | dy
         /   |
        / dx | <-- angle alpha
        ----------- bottomPoint
        SOH CAH TOA
        tan(alpha) = dy/dx
         */
        final double dx = topPoint.x - bottomPoint.x;
        final double dy = topPoint.y - bottomPoint.y;
        final double alpha = Mth.atan2(dx, dy);

        /*
        globalIntersection:
        (convert to polar) calculate r & theta for localIntersection
        add alpha to theta
        convert back to rectangular
         */

        final double r = localIntersection.length();
        final float theta = (float) (Mth.atan2(localIntersection.y, -localIntersection.x) - alpha);

        final float globalX = (float) r * Mth.cos(theta);
        final float globalY = (float) r * Mth.sin(theta);
        return new Vec2(globalX, globalY);
    }

    private static double distance(Vec2 topPoint, Vec2 bottomPoint) {
        // step 1: transform so bottomPoint is the origin (so topPoint -= bottomPoint; bottomPoint = ZERO)
        topPoint = topPoint.add(bottomPoint.negated());

        // step 2: find distance between centers
        return topPoint.length();
    }

    /**
     * Calculates intersection point of two circles
     * @param d distance between centers of both circles
     * @param k radius of 'upper' circle
     * @param g radius of 'lower' circle
     * @return (x, y) intersection point between two circles
     */
    private static Vec2 getIntersection(final double d, final double k, final double g) {
        final double g2 = g*g;
        final double d2 = d*d;
        final double k2 = k*k;

        final double y = (g2 + d2 - k2) / (2*d);
        final double y2 = y*y;
        final double x = Math.sqrt(g2 - y2);

        return new Vec2((float) x, (float) y);
    }
}
