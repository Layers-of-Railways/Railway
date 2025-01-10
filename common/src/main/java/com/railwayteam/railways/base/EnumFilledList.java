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

import java.util.Iterator;
import java.util.function.Function;

public class EnumFilledList<E extends Enum<E>, T> implements Iterable<T> {
    private final Class<E> clazz;
    private final T[] values;

    @SuppressWarnings("unchecked")
    public EnumFilledList(Class<E> clazz, Function<E, T> filler) {
        this.clazz = clazz;
        E[] enumConstants = clazz.getEnumConstants();
        values = (T[]) new Object[enumConstants.length];
        for (int i = 0; i < enumConstants.length; i++) {
            values[i] = filler.apply(enumConstants[i]);
        }
    }

    public T get(E e) {
        return values[e.ordinal()];
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new EnumFilledListIterator();
    }

    private class EnumFilledListIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < clazz.getEnumConstants().length;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new IndexOutOfBoundsException();
            return values[index++];
        }
    }
}
