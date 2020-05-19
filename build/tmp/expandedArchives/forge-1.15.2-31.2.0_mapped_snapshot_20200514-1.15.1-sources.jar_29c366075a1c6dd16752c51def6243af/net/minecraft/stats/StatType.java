package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatType<T> extends net.minecraftforge.registries.ForgeRegistryEntry<StatType<?>> implements Iterable<Stat<T>> {
   private final Registry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap<>();

   public StatType(Registry<T> p_i49818_1_) {
      this.registry = p_i49818_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean contains(T p_199079_1_) {
      return this.map.containsKey(p_199079_1_);
   }

   public Stat<T> get(T p_199077_1_, IStatFormatter formatter) {
      return this.map.computeIfAbsent(p_199077_1_, (p_199075_2_) -> {
         return new Stat<>(this, p_199075_2_, formatter);
      });
   }

   public Registry<T> getRegistry() {
      return this.registry;
   }

   public Iterator<Stat<T>> iterator() {
      return this.map.values().iterator();
   }

   public Stat<T> get(T stat) {
      return this.get(stat, IStatFormatter.DEFAULT);
   }

   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "stat_type." + Registry.STATS.getKey(this).toString().replace(':', '.');
   }
}