package net.minecraft.state;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;

public class EnumProperty<T extends Enum<T> & IStringSerializable> extends Property<T> {
   private final ImmutableSet<T> allowedValues;
   /** Map of names to Enum values */
   private final Map<String, T> nameToValue = Maps.newHashMap();

   protected EnumProperty(String name, Class<T> valueClass, Collection<T> allowedValues) {
      super(name, valueClass);
      this.allowedValues = ImmutableSet.copyOf(allowedValues);

      for(T t : allowedValues) {
         String s = ((IStringSerializable)t).getName();
         if (this.nameToValue.containsKey(s)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
         }

         this.nameToValue.put(s, t);
      }

   }

   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public Optional<T> parseValue(String value) {
      return Optional.ofNullable(this.nameToValue.get(value));
   }

   /**
    * Get the name for the given value.
    */
   public String getName(T value) {
      return ((IStringSerializable)value).getName();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof EnumProperty && super.equals(p_equals_1_)) {
         EnumProperty<?> enumproperty = (EnumProperty)p_equals_1_;
         return this.allowedValues.equals(enumproperty.allowedValues) && this.nameToValue.equals(enumproperty.nameToValue);
      } else {
         return false;
      }
   }

   public int computeHashCode() {
      int i = super.computeHashCode();
      i = 31 * i + this.allowedValues.hashCode();
      i = 31 * i + this.nameToValue.hashCode();
      return i;
   }

   /**
    * Create a new PropertyEnum with all Enum constants of the given class.
    */
   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String name, Class<T> clazz) {
      return create(name, clazz, Predicates.alwaysTrue());
   }

   /**
    * Create a new PropertyEnum with all Enum constants of the given class that match the given Predicate.
    */
   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String name, Class<T> clazz, Predicate<T> filter) {
      return create(name, clazz, Arrays.<T>stream(clazz.getEnumConstants()).filter(filter).collect(Collectors.toList()));
   }

   /**
    * Create a new PropertyEnum with the specified values
    */
   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String name, Class<T> clazz, T... values) {
      return create(name, clazz, Lists.newArrayList(values));
   }

   /**
    * Create a new PropertyEnum with the specified values
    */
   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String name, Class<T> clazz, Collection<T> values) {
      return new EnumProperty<>(name, clazz, values);
   }
}