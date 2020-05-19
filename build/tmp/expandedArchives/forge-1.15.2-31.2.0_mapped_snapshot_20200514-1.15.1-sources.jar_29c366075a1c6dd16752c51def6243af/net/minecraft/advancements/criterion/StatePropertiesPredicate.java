package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;

public class StatePropertiesPredicate {
   public static final StatePropertiesPredicate EMPTY = new StatePropertiesPredicate(ImmutableList.of());
   private final List<StatePropertiesPredicate.Matcher> matchers;

   private static StatePropertiesPredicate.Matcher deserializeProperty(String p_227188_0_, JsonElement p_227188_1_) {
      if (p_227188_1_.isJsonPrimitive()) {
         String s2 = p_227188_1_.getAsString();
         return new StatePropertiesPredicate.ExactMatcher(p_227188_0_, s2);
      } else {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_227188_1_, "value");
         String s = jsonobject.has("min") ? getNullableString(jsonobject.get("min")) : null;
         String s1 = jsonobject.has("max") ? getNullableString(jsonobject.get("max")) : null;
         return (StatePropertiesPredicate.Matcher)(s != null && s.equals(s1) ? new StatePropertiesPredicate.ExactMatcher(p_227188_0_, s) : new StatePropertiesPredicate.RangedMacher(p_227188_0_, s, s1));
      }
   }

   @Nullable
   private static String getNullableString(JsonElement p_227189_0_) {
      return p_227189_0_.isJsonNull() ? null : p_227189_0_.getAsString();
   }

   private StatePropertiesPredicate(List<StatePropertiesPredicate.Matcher> p_i225790_1_) {
      this.matchers = ImmutableList.copyOf(p_i225790_1_);
   }

   public <S extends IStateHolder<S>> boolean matchesAll(StateContainer<?, S> p_227182_1_, S p_227182_2_) {
      for(StatePropertiesPredicate.Matcher statepropertiespredicate$matcher : this.matchers) {
         if (!statepropertiespredicate$matcher.test(p_227182_1_, p_227182_2_)) {
            return false;
         }
      }

      return true;
   }

   public boolean matches(BlockState p_227181_1_) {
      return this.matchesAll(p_227181_1_.getBlock().getStateContainer(), p_227181_1_);
   }

   public boolean matches(IFluidState p_227185_1_) {
      return this.matchesAll(p_227185_1_.getFluid().getStateContainer(), p_227185_1_);
   }

   public void forEachNotPresent(StateContainer<?, ?> p_227183_1_, Consumer<String> p_227183_2_) {
      this.matchers.forEach((p_227184_2_) -> {
         p_227184_2_.runIfNotPresent(p_227183_1_, p_227183_2_);
      });
   }

   public static StatePropertiesPredicate deserializeProperties(@Nullable JsonElement p_227186_0_) {
      if (p_227186_0_ != null && !p_227186_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_227186_0_, "properties");
         List<StatePropertiesPredicate.Matcher> list = Lists.newArrayList();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            list.add(deserializeProperty(entry.getKey(), entry.getValue()));
         }

         return new StatePropertiesPredicate(list);
      } else {
         return EMPTY;
      }
   }

   public JsonElement toJsonElement() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.matchers.isEmpty()) {
            this.matchers.forEach((p_227187_1_) -> {
               jsonobject.add(p_227187_1_.getPropertyName(), p_227187_1_.toJsonElement());
            });
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private final List<StatePropertiesPredicate.Matcher> matchers = Lists.newArrayList();

      private Builder() {
      }

      public static StatePropertiesPredicate.Builder newBuilder() {
         return new StatePropertiesPredicate.Builder();
      }

      public StatePropertiesPredicate.Builder withStringProp(IProperty<?> p_227194_1_, String p_227194_2_) {
         this.matchers.add(new StatePropertiesPredicate.ExactMatcher(p_227194_1_.getName(), p_227194_2_));
         return this;
      }

      public StatePropertiesPredicate.Builder withIntProp(IProperty<Integer> p_227192_1_, int p_227192_2_) {
         return this.withStringProp(p_227192_1_, Integer.toString(p_227192_2_));
      }

      public StatePropertiesPredicate.Builder withBoolProp(IProperty<Boolean> p_227195_1_, boolean p_227195_2_) {
         return this.withStringProp(p_227195_1_, Boolean.toString(p_227195_2_));
      }

      public <T extends Comparable<T> & IStringSerializable> StatePropertiesPredicate.Builder withProp(IProperty<T> p_227193_1_, T p_227193_2_) {
         return this.withStringProp(p_227193_1_, ((IStringSerializable)p_227193_2_).getName());
      }

      public StatePropertiesPredicate build() {
         return new StatePropertiesPredicate(this.matchers);
      }
   }

   static class ExactMatcher extends StatePropertiesPredicate.Matcher {
      private final String valueToMatch;

      public ExactMatcher(String p_i225792_1_, String p_i225792_2_) {
         super(p_i225792_1_);
         this.valueToMatch = p_i225792_2_;
      }

      protected <T extends Comparable<T>> boolean func_225554_a_(IStateHolder<?> p_225554_1_, IProperty<T> p_225554_2_) {
         T t = p_225554_1_.get(p_225554_2_);
         Optional<T> optional = p_225554_2_.parseValue(this.valueToMatch);
         return optional.isPresent() && t.compareTo(optional.get()) == 0;
      }

      public JsonElement toJsonElement() {
         return new JsonPrimitive(this.valueToMatch);
      }
   }

   abstract static class Matcher {
      private final String propertyName;

      public Matcher(String p_i225793_1_) {
         this.propertyName = p_i225793_1_;
      }

      public <S extends IStateHolder<S>> boolean test(StateContainer<?, S> p_227199_1_, S p_227199_2_) {
         IProperty<?> iproperty = p_227199_1_.getProperty(this.propertyName);
         return iproperty == null ? false : this.func_225554_a_(p_227199_2_, iproperty);
      }

      protected abstract <T extends Comparable<T>> boolean func_225554_a_(IStateHolder<?> p_225554_1_, IProperty<T> p_225554_2_);

      public abstract JsonElement toJsonElement();

      public String getPropertyName() {
         return this.propertyName;
      }

      public void runIfNotPresent(StateContainer<?, ?> p_227200_1_, Consumer<String> p_227200_2_) {
         IProperty<?> iproperty = p_227200_1_.getProperty(this.propertyName);
         if (iproperty == null) {
            p_227200_2_.accept(this.propertyName);
         }

      }
   }

   static class RangedMacher extends StatePropertiesPredicate.Matcher {
      @Nullable
      private final String minimum;
      @Nullable
      private final String field_227203_b_;

      public RangedMacher(String p_i225794_1_, @Nullable String p_i225794_2_, @Nullable String p_i225794_3_) {
         super(p_i225794_1_);
         this.minimum = p_i225794_2_;
         this.field_227203_b_ = p_i225794_3_;
      }

      protected <T extends Comparable<T>> boolean func_225554_a_(IStateHolder<?> p_225554_1_, IProperty<T> p_225554_2_) {
         T t = p_225554_1_.get(p_225554_2_);
         if (this.minimum != null) {
            Optional<T> optional = p_225554_2_.parseValue(this.minimum);
            if (!optional.isPresent() || t.compareTo(optional.get()) < 0) {
               return false;
            }
         }

         if (this.field_227203_b_ != null) {
            Optional<T> optional1 = p_225554_2_.parseValue(this.field_227203_b_);
            if (!optional1.isPresent() || t.compareTo(optional1.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      public JsonElement toJsonElement() {
         JsonObject jsonobject = new JsonObject();
         if (this.minimum != null) {
            jsonobject.addProperty("min", this.minimum);
         }

         if (this.field_227203_b_ != null) {
            jsonobject.addProperty("max", this.field_227203_b_);
         }

         return jsonobject;
      }
   }
}