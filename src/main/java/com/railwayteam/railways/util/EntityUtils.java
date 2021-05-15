package com.railwayteam.railways.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

public abstract class EntityUtils {
    public static Method Entity_canBeRidden_Method = ObfuscationReflectionHelper.findMethod(Entity.class, "func_184228_n", Entity.class);
    public static Method Entity_canFitPassenger_Method = ObfuscationReflectionHelper.findMethod(Entity.class, "func_184219_q", Entity.class);

    public static <T extends Entity> T getClosestEntity(Entity closestTo, List<T> entities, Predicate<T> check) {
        T closest = null;

        for (T entity : entities) {
            if (!check.test(entity)) continue;
            if (closest == null) {
                closest = entity;
                continue;
            }
            if (closest.getDistance(closestTo) < closest.getDistance(closestTo)) closest = entity;
        }

        return closest;
    }

    public static <T extends Entity> T getClosestEntity(Entity closestTo, List<T> entities) {
        return getClosestEntity(closestTo, entities, (e) -> true);
    }

    public static boolean canEntitySitInMinecart(Entity entity, MinecartEntity minecart) {
        try {
            return (boolean) Entity_canBeRidden_Method.invoke(minecart, entity) && (boolean) Entity_canFitPassenger_Method.invoke(minecart, minecart);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // this should be impossible
        }
        return false;
    }
}
