package com.railwayteam.railways.util;

import org.joml.Matrix4f;

public class MathUtils {
    public static Matrix4f copy(Matrix4f matrix) {
        try {
            return (Matrix4f) matrix.clone();
        } catch (CloneNotSupportedException e) {
            return new Matrix4f(matrix);
        }
    }
}
