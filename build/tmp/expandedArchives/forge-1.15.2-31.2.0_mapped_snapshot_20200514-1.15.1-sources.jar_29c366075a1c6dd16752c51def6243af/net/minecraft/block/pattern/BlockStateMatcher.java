package net.minecraft.block.pattern;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

public class BlockStateMatcher implements Predicate<BlockState> {
   public static final Predicate<BlockState> ANY = (p_201026_0_) -> {
      return true;
   };
   private final StateContainer<Block, BlockState> blockstate;
   private final Map<IProperty<?>, Predicate<Object>> propertyPredicates = Maps.newHashMap();

   private BlockStateMatcher(StateContainer<Block, BlockState> blockStateIn) {
      this.blockstate = blockStateIn;
   }

   public static BlockStateMatcher forBlock(Block blockIn) {
      return new BlockStateMatcher(blockIn.getStateContainer());
   }

   public boolean test(@Nullable BlockState p_test_1_) {
      if (p_test_1_ != null && p_test_1_.getBlock().equals(this.blockstate.getOwner())) {
         if (this.propertyPredicates.isEmpty()) {
            return true;
         } else {
            for(Entry<IProperty<?>, Predicate<Object>> entry : this.propertyPredicates.entrySet()) {
               if (!this.matches(p_test_1_, entry.getKey(), entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean matches(BlockState blockState, IProperty<T> property, Predicate<Object> predicate) {
      T t = blockState.get(property);
      return predicate.test(t);
   }

   public <V extends Comparable<V>> BlockStateMatcher where(IProperty<V> property, Predicate<Object> is) {
      if (!this.blockstate.getProperties().contains(property)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
      } else {
         this.propertyPredicates.put(property, is);
         return this;
      }
   }
}