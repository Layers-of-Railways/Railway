package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IStateHolder<C> {
   Logger field_215672_b = LogManager.getLogger();

   /**
    * Get the value of the given Property for this BlockState
    */
   <T extends Comparable<T>> T get(IProperty<T> property);

   <T extends Comparable<T>, V extends T> C with(IProperty<T> property, V value);

   ImmutableMap<IProperty<?>, Comparable<?>> getValues();

   static <T extends Comparable<T>> String getName(IProperty<T> propertyIn, Comparable<?> value) {
      return propertyIn.getName((T)value);
   }

   static <S extends IStateHolder<S>, T extends Comparable<T>> S withString(S state, IProperty<T> propertyIn, String propertyNameIn, String inputIn, String valueIn) {
      Optional<T> optional = propertyIn.parseValue(valueIn);
      if (optional.isPresent()) {
         return (S)(state.with(propertyIn, (T)(optional.get())));
      } else {
         field_215672_b.warn("Unable to read property: {} with value: {} for input: {}", propertyNameIn, valueIn, inputIn);
         return state;
      }
   }
}