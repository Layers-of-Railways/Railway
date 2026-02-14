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

package com.railwayteam.railways.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IterableUtils {
    public static <T> Iterable<@Nullable T> nullAnd(Iterable<@NotNull T> iterable) {
        return new NullAndIterable<>(iterable);
    }

    public static <T> Iterable<@Nullable T> nullAnd(T[] array) {
        return nullAnd(new ArrayIterable<>(array));
    }

    private record ArrayIterable<T>(T[] array) implements Iterable<T> {
        @Override
        public @NotNull Iterator<T> iterator() {
            return Arrays.stream(array).iterator();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            for (T t : array) {
                action.accept(t);
            }
        }

        @Override
        public Spliterator<T> spliterator() {
            return Arrays.stream(array).spliterator();
        }
    }

    private record NullAndIterable<T>(Iterable<T> wrapped) implements Iterable<@Nullable T> {
        @Override
        public @NotNull Iterator<@Nullable T> iterator() {
            return new NullAndIterator<>(wrapped.iterator());
        }

        @Override
        public void forEach(Consumer<? super @Nullable T> action) {
            action.accept(null);
            wrapped.forEach(action);
        }

        @Override
        public Spliterator<@Nullable T> spliterator() {
            return new NullAndSpliterator<>(wrapped.spliterator());
        }
    }

    private static class NullAndIterator<T> implements Iterator<@Nullable T> {
        private final Iterator<T> wrapped;
        private boolean nullConsumed = false;

        private NullAndIterator(Iterator<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean hasNext() {
            return !nullConsumed || wrapped.hasNext();
        }

        @Override
        public @Nullable T next() {
            if (!nullConsumed) {
                nullConsumed = true;
                return null;
            }
            return wrapped.next();
        }
    }

    private static class NullAndSpliterator<T> implements Spliterator<@Nullable T> {
        private final Spliterator<T> wrapped;
        private boolean nullConsumed = false;

        private NullAndSpliterator(Spliterator<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean tryAdvance(Consumer<? super @Nullable T> consumer) {
            if (!nullConsumed) {
                consumer.accept(null);
                nullConsumed = true;
                return true;
            }
            return wrapped.tryAdvance(consumer);
        }

        @Override
        public Spliterator<@Nullable T> trySplit() {
            return wrapped.trySplit();
        }

        @Override
        public long estimateSize() {
            return wrapped.estimateSize() + 1;
        }

        @Override
        public int characteristics() {
            return wrapped.characteristics() & ~Spliterator.NONNULL;
        }
    }
}
