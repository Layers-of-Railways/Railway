package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class StateHolder<O, S> implements IStateHolder<S> {
   private static final Function<Entry<IProperty<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<IProperty<?>, Comparable<?>> p_apply_1_) {
         if (p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty<?> iproperty = p_apply_1_.getKey();
            return iproperty.getName() + "=" + this.getPropertyName(iproperty, p_apply_1_.getValue());
         }
      }

      private <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> entry) {
         return property.getName((T)entry);
      }
   };
   protected final O object;
   private final ImmutableMap<IProperty<?>, Comparable<?>> properties;
   private Table<IProperty<?>, Comparable<?>, S> propertyToStateMap;

   protected StateHolder(O objectIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
      this.object = objectIn;
      this.properties = propertiesIn;
   }

   /**
    * Create a version of this BlockState with the given property cycled to the next value in order. If the property was
    * at the highest possible value, it is set to the lowest one instead.
    */
   public <T extends Comparable<T>> S cycle(IProperty<T> property) {
      return (S)this.with(property, (T)(cyclePropertyValue(property.getAllowedValues(), this.get(property))));
   }

   /**
    * Helper method for cycleProperty.
    */
   protected static <T> T cyclePropertyValue(Collection<T> values, T currentValue) {
      Iterator<T> iterator = values.iterator();

      while(iterator.hasNext()) {
         if (iterator.next().equals(currentValue)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return values.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(this.object);
      if (!this.getValues().isEmpty()) {
         stringbuilder.append('[');
         stringbuilder.append(this.getValues().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")));
         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   public Collection<IProperty<?>> getProperties() {
      return Collections.unmodifiableCollection(this.properties.keySet());
   }

   public <T extends Comparable<T>> boolean has(IProperty<T> property) {
      return this.properties.containsKey(property);
   }

   /**
    * Get the value of the given Property for this BlockState
    */
   public <T extends Comparable<T>> T get(IProperty<T> property) {
      Comparable<?> comparable = this.properties.get(property);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.object);
      } else {
         return (T)(property.getValueClass().cast(comparable));
      }
   }

   public <T extends Comparable<T>, V extends T> S with(IProperty<T> property, V value) {
      Comparable<?> comparable = this.properties.get(property);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.object);
      } else if (comparable == value) {
         return (S)this;
      } else {
         S s = this.propertyToStateMap.get(property, value);
         if (s == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.object + ", it is not an allowed value");
         } else {
            return s;
         }
      }
   }

   public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, S> map) {
      if (this.propertyToStateMap != null) {
         throw new IllegalStateException();
      } else {
         Table<IProperty<?>, Comparable<?>, S> table = HashBasedTable.create();

         for(Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            IProperty<?> iproperty = entry.getKey();

            for(Comparable<?> comparable : iproperty.getAllowedValues()) {
               if (comparable != entry.getValue()) {
                  table.put(iproperty, comparable, map.get(this.getPropertiesWithValue(iproperty, comparable)));
               }
            }
         }

         this.propertyToStateMap = (Table<IProperty<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
      }
   }

   private Map<IProperty<?>, Comparable<?>> getPropertiesWithValue(IProperty<?> property, Comparable<?> value) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap(this.properties);
      map.put(property, value);
      return map;
   }

   public ImmutableMap<IProperty<?>, Comparable<?>> getValues() {
      return this.properties;
   }
}