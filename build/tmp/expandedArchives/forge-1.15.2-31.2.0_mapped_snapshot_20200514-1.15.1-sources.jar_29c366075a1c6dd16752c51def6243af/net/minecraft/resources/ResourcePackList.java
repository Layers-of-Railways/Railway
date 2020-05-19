package net.minecraft.resources;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class ResourcePackList<T extends ResourcePackInfo> implements AutoCloseable {
   private final Set<IPackFinder> packFinders = Sets.newHashSet();
   private final Map<String, T> packNameToInfo = Maps.newLinkedHashMap();
   private final List<T> enabled = Lists.newLinkedList();
   private final ResourcePackInfo.IFactory<T> packInfoFactory;

   public ResourcePackList(ResourcePackInfo.IFactory<T> packInfoFactoryIn) {
      this.packInfoFactory = packInfoFactoryIn;
   }

   public void reloadPacksFromFinders() {
      this.close();
      Set<String> set = this.enabled.stream().map(ResourcePackInfo::getName).collect(Collectors.toCollection(LinkedHashSet::new));
      this.packNameToInfo.clear();
      this.enabled.clear();

      for(IPackFinder ipackfinder : this.packFinders) {
         ipackfinder.addPackInfosToMap(this.packNameToInfo, this.packInfoFactory);
      }

      this.sortPackNameToInfo();
      this.enabled.addAll(set.stream().map(this.packNameToInfo::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

      for(T t : this.packNameToInfo.values()) {
         if (t.isAlwaysEnabled() && !this.enabled.contains(t)) {
            t.getPriority().insert(this.enabled, t, Functions.identity(), false);
         }
      }

   }

   private void sortPackNameToInfo() {
      List<Entry<String, T>> list = Lists.newArrayList(this.packNameToInfo.entrySet());
      this.packNameToInfo.clear();
      list.stream().sorted(net.minecraftforge.fml.packs.ResourcePackLoader.getSorter()).forEachOrdered((p_198984_1_) -> {
         ResourcePackInfo resourcepackinfo = (ResourcePackInfo)this.packNameToInfo.put(p_198984_1_.getKey(), p_198984_1_.getValue());
      });
   }

   public void setEnabledPacks(Collection<T> p_198985_1_) {
      this.enabled.clear();
      this.enabled.addAll(p_198985_1_);

      for(T t : this.packNameToInfo.values()) {
         if (t.isAlwaysEnabled() && !this.enabled.contains(t)) {
            t.getPriority().insert(this.enabled, t, Functions.identity(), false);
         }
      }

   }

   /**
    * Gets all known packs, including those that are not enabled.
    */
   public Collection<T> getAllPacks() {
      return this.packNameToInfo.values();
   }

   /**
    * Gets all packs that are known but not enabled.
    */
   public Collection<T> getAvailablePacks() {
      Collection<T> collection = Lists.newArrayList(this.packNameToInfo.values());
      collection.removeAll(this.enabled);
      return collection;
   }

   /**
    * Gets all packs that have been enabled.
    */
   public Collection<T> getEnabledPacks() {
      return this.enabled;
   }

   @Nullable
   public T getPackInfo(String name) {
      return (T)(this.packNameToInfo.get(name));
   }

   public void addPackFinder(IPackFinder packFinder) {
      this.packFinders.add(packFinder);
   }

   public void close() {
      this.packNameToInfo.values().forEach(ResourcePackInfo::close);
   }
}