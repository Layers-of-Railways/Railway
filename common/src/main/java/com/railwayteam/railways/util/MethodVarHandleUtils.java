/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unchecked")
public class MethodVarHandleUtils {
    private static final Cache<VarHandleInfo, VarHandle> varHandleCache = CacheBuilder.newBuilder().build();

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        T value = null;
        
        try {
            value = (T) varHandleCache.get(
                    new VarHandleInfo(clazz, fieldName, type),
                    () -> lookup.findStaticVarHandle(clazz, fieldName, type)
            ).get();
        } catch (ExecutionException ignored) {}
        
        return value;
    }

    public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> type, T defaultValue) {
        T returnValue = defaultValue;

        try {
            returnValue = getStaticField(clazz, fieldName, type);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        return returnValue;
    }

    public static <T, U> T getPrivateField(U instance, Class<U> clazz, String fieldName, Class<T> type, T defaultValue) {
        T returnValue = defaultValue;

        VarHandle handle = findPrivateFieldVarHandle(new VarHandleInfo(clazz, fieldName, type));
        if (handle != null) {
            returnValue = (T) handle.get(instance);
        }

        return returnValue;
    }

    @Nullable
    public static VarHandle findPrivateFieldVarHandle(VarHandleInfo info) {
        try {
            return varHandleCache.get(info, () -> {
                MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(info.clazz(), lookup);
                return privateLookup.findVarHandle(info.clazz(), info.fieldName(), info.type());
            });
        } catch (ExecutionException ignored) {
            return null;
        }
    }

    public record VarHandleInfo(Class<?> clazz, String fieldName, Class<?> type) {
    }
}