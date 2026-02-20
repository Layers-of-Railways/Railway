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

package com.railwayteam.railways.base.registration;

import com.railwayteam.railways.Railways;
import com.tterrag.registrate.AbstractRegistrate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MultiRegistryCallback<A, B> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Railways.ID_NAME+"/MultiRegistryCallback");

    private static int TODO_COUNT = 0;
    private static boolean MAY_RUN_FINALIZERS = false;
    private static List<Runnable> FINALIZERS = new ArrayList<>();

    private final AbstractRegistrate<?> registrateA;
    private final AbstractRegistrate<?> registrateB;

    private final ResourceKey<? extends Registry<A>> typeA;
    private final ResourceKey<? extends Registry<B>> typeB;

    private final ResourceLocation idA;
    private final ResourceLocation idB;

    private @Nullable BiConsumer<A, B> callback;

    private @Nullable A valueA;
    private @Nullable B valueB;

    private MultiRegistryCallback(
        AbstractRegistrate<?> registrateA, ResourceKey<? extends Registry<A>> typeA, ResourceLocation idA,
        AbstractRegistrate<?> registrateB, ResourceKey<? extends Registry<B>> typeB, ResourceLocation idB,
        BiConsumer<A, B> callback
    ) {
        this.registrateA = registrateA;
        this.registrateB = registrateB;
        this.typeA = typeA;
        this.typeB = typeB;
        this.idA = idA;
        this.idB = idB;
        this.callback = callback;

        assert(registrateA.getModid().equals(idA.getNamespace())) : "Must pass correct registrate for idA";
        assert(registrateB.getModid().equals(idB.getNamespace())) : "Must pass correct registrate for idB";
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> @Nullable T getEntry(ResourceKey<? extends Registry<T>> registry, ResourceLocation id) {
        Registry<T> $registry = (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey) registry);
        if ($registry == null) return null;
        if (!$registry.containsKey(id)) return null; // don't just return the default value for defaulted registries
        return $registry.get(id);
    }

    private void setupCallbacks() {
        TODO_COUNT++;

        A a = getEntry(typeA, idA);
        B b = getEntry(typeB, idB);
        if (a != null) {
            LOGGER.info("Found existing entry for {} in {}", idA, typeA.location());
            storeA(a);
        } else {
            LOGGER.info("Registering callback for {} in {}", idA, typeA.location());
            registrateA.addRegisterCallback(idA.getPath(), typeA, this::storeA);
        }
        if (b != null) {
            LOGGER.info("Found existing entry for {} in {}", idB, typeB.location());
            storeB(b);
        } else {
            LOGGER.info("Registering callback for {} in {}", idB, typeB.location());
            registrateB.addRegisterCallback(idB.getPath(), typeB, this::storeB);
        }
    }

    private void storeA(A value) {
        this.valueA = value;
        checkAndRun();
    }

    private void storeB(B value) {
        this.valueB = value;
        checkAndRun();
    }

    /**
     * Check if the registry entries are present and run the callback if they are.
     */
    private void checkAndRun() {
        if (callback == null) {
            valueA = null;
            valueB = null;
        } else if (valueA != null && valueB != null) {
            LOGGER.info("Running callback for {} and {}", idA, idB);

            Throwable thrown = null;
            try {
                callback.accept(valueA, valueB);
            } catch (Exception e) {
                thrown = e.fillInStackTrace();
            }
            valueA = null;
            valueB = null;
            callback = null;

            if (--TODO_COUNT == 0) {
                runFinalizers();
            }

            if (thrown != null) {
                throw new RuntimeException("Error running MultiRegistryCallback", thrown);
            }
        }
    }

    private static void runFinalizers() {
        if (!MAY_RUN_FINALIZERS) return;
        LOGGER.info("Running finalizers");
        for (Runnable finalizer : FINALIZERS) {
            finalizer.run();
        }
        FINALIZERS = null;
    }

    public static <A, B> void create(
        AbstractRegistrate<?> registrateA, ResourceKey<? extends Registry<A>> typeA, ResourceLocation idA,
        AbstractRegistrate<?> registrateB, ResourceKey<? extends Registry<B>> typeB, ResourceLocation idB,
        BiConsumer<A, B> callback
    ) {
        if (FINALIZERS == null) {
            throw new RuntimeException("Cannot create MultiRegistryCallback after finalizers have run");
        }
        new MultiRegistryCallback<>(registrateA, typeA, idA, registrateB, typeB, idB, callback).setupCallbacks();
    }

    /**
     * Add a finalizer to run after all MultiRegistryCallbacks are created.
     * @param finalizer callback to run
     */
    public static void addFinalizer(Runnable finalizer) {
        if (FINALIZERS == null) {
            throw new RuntimeException("Finalizer added after finalizers have run");
        }
        FINALIZERS.add(finalizer);
    }

    /**
     * Enable finalizers to run if they are not already running.
     * Should be called after all MultiRegistryCallbacks are created.
     */
    public static void enableFinalizers() {
        MAY_RUN_FINALIZERS = true;
        if (TODO_COUNT == 0) {
            runFinalizers();
        }
    }
}
