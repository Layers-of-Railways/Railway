package net.minecraft.util.registry;

import net.minecraft.util.ResourceLocation;

public abstract class MutableRegistry<T> extends Registry<T> {
   public abstract <V extends T> V register(int id, ResourceLocation name, V instance);

   public abstract <V extends T> V register(ResourceLocation name, V instance);

   public abstract boolean isEmpty();
}