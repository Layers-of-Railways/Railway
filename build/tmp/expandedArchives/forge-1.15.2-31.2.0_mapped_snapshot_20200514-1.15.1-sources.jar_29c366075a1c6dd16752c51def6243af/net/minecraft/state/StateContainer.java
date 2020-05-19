package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.MapPopulator;

public class StateContainer<O, S extends IStateHolder<S>> {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final O owner;
   private final ImmutableSortedMap<String, IProperty<?>> properties;
   private final ImmutableList<S> validStates;

   protected <A extends StateHolder<O, S>> StateContainer(O object, StateContainer.IFactory<O, S, A> factory, Map<String, IProperty<?>> propertiesIn) {
      this.owner = object;
      this.properties = ImmutableSortedMap.copyOf(propertiesIn);
      Map<Map<IProperty<?>, Comparable<?>>, A> map = Maps.newLinkedHashMap();
      List<A> list = Lists.newArrayList();
      Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());

      for(IProperty<?> iproperty : this.properties.values()) {
         stream = stream.flatMap((p_200999_1_) -> {
            return iproperty.getAllowedValues().stream().map((p_200998_1_) -> {
               List<Comparable<?>> list1 = Lists.newArrayList(p_200999_1_);
               list1.add(p_200998_1_);
               return list1;
            });
         });
      }

      stream.forEach((p_201000_5_) -> {
         Map<IProperty<?>, Comparable<?>> map1 = MapPopulator.createMap(this.properties.values(), p_201000_5_);
         A a1 = factory.create(object, ImmutableMap.copyOf(map1));
         map.put(map1, a1);
         list.add(a1);
      });

      for(A a : list) {
         a.buildPropertyValueTable((Map<Map<IProperty<?>, Comparable<?>>, S>) map);
      }

      this.validStates = (ImmutableList<S>) ImmutableList.copyOf(list);
   }

   public ImmutableList<S> getValidStates() {
      return this.validStates;
   }

   public S getBaseState() {
      return (S)(this.validStates.get(0));
   }

   public O getOwner() {
      return this.owner;
   }

   public Collection<IProperty<?>> getProperties() {
      return this.properties.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(IProperty::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public IProperty<?> getProperty(String propertyName) {
      return this.properties.get(propertyName);
   }

   public static class Builder<O, S extends IStateHolder<S>> {
      private final O owner;
      private final Map<String, IProperty<?>> properties = Maps.newHashMap();

      public Builder(O object) {
         this.owner = object;
      }

      public StateContainer.Builder<O, S> add(IProperty<?>... propertiesIn) {
         for(IProperty<?> iproperty : propertiesIn) {
            this.validateProperty(iproperty);
            this.properties.put(iproperty.getName(), iproperty);
         }

         return this;
      }

      private <T extends Comparable<T>> void validateProperty(IProperty<T> property) {
         String s = property.getName();
         if (!StateContainer.NAME_PATTERN.matcher(s).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + s);
         } else {
            Collection<T> collection = property.getAllowedValues();
            if (collection.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + s + " with <= 1 possible values");
            } else {
               for(T t : collection) {
                  String s1 = property.getName(t);
                  if (!StateContainer.NAME_PATTERN.matcher(s1).matches()) {
                     throw new IllegalArgumentException(this.owner + " has property: " + s + " with invalidly named value: " + s1);
                  }
               }

               if (this.properties.containsKey(s)) {
                  throw new IllegalArgumentException(this.owner + " has duplicate property: " + s);
               }
            }
         }
      }

      public <A extends StateHolder<O, S>> StateContainer<O, S> create(StateContainer.IFactory<O, S, A> factory) {
         return new StateContainer<>(this.owner, factory, this.properties);
      }
   }

   public interface IFactory<O, S extends IStateHolder<S>, A extends StateHolder<O, S>> {
      A create(O p_create_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_create_2_);
   }
}