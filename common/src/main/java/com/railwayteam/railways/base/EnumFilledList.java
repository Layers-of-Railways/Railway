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

package com.railwayteam.railways.base;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

public class EnumFilledList<E extends Enum<E>, T> implements Iterable<T> {
    protected final Class<E> clazz;
    protected final T[] values;
    protected final int[] ordinal_to_idx;

    @SuppressWarnings("unchecked")
    public EnumFilledList(Class<E> clazz, Function<@NotNull E, T> filler) {
        this.clazz = clazz;
        E[] enumVals = clazz.getEnumConstants();

        ordinal_to_idx = new int[enumVals.length];
        int idx = 0;
        for (E value : enumVals) {
            if (filter(value)) {
                ordinal_to_idx[value.ordinal()] = idx++;
            } else {
                ordinal_to_idx[value.ordinal()] = -1;
            }
        }
        values = (T[]) new Object[idx];

        for (E value : enumVals) {
            int i = ordinal_to_idx[value.ordinal()];
            if (i != -1) {
                values[i] = filler.apply(value);
            }
        }
    }

    protected boolean filter(E value) {
        return true;
    }

    public T get(E e) {
        int idx = ordinal_to_idx[e.ordinal()];
        if (idx == -1) {
            throw new NoSuchElementException();
        }
        return values[idx];
    }

    public T[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    public T[] toArray(IntFunction<T[]> newType) {
        var array = newType.apply(values.length);
        System.arraycopy(values, 0, array, 0, values.length);
        return array;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new EnumFilledListIterator();
    }

    private class EnumFilledListIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index + 1 < clazz.getEnumConstants().length;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new IndexOutOfBoundsException();
            return values[index++];
        }
    }
}
