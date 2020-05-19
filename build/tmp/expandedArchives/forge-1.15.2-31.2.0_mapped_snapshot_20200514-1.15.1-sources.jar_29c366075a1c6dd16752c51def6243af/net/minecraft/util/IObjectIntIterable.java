package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T> {
   @Nullable
   T getByValue(int value);
}