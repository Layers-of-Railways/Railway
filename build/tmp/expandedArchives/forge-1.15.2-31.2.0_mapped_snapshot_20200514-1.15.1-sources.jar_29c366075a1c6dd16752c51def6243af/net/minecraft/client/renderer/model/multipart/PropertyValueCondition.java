package net.minecraft.client.renderer.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PropertyValueCondition implements ICondition {
   private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();
   private final String key;
   private final String value;

   public PropertyValueCondition(String keyIn, String valueIn) {
      this.key = keyIn;
      this.value = valueIn;
   }

   public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_) {
      IProperty<?> iproperty = p_getPredicate_1_.getProperty(this.key);
      if (iproperty == null) {
         throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, p_getPredicate_1_.getOwner().toString()));
      } else {
         String s = this.value;
         boolean flag = !s.isEmpty() && s.charAt(0) == '!';
         if (flag) {
            s = s.substring(1);
         }

         List<String> list = SPLITTER.splitToList(s);
         if (list.isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.value, this.key, p_getPredicate_1_.getOwner().toString()));
         } else {
            Predicate<BlockState> predicate;
            if (list.size() == 1) {
               predicate = this.func_212485_a(p_getPredicate_1_, iproperty, s);
            } else {
               List<Predicate<BlockState>> list1 = list.stream().map((p_212482_3_) -> {
                  return this.func_212485_a(p_getPredicate_1_, iproperty, p_212482_3_);
               }).collect(Collectors.toList());
               predicate = (p_200687_1_) -> {
                  return list1.stream().anyMatch((p_200685_1_) -> {
                     return p_200685_1_.test(p_200687_1_);
                  });
               };
            }

            return flag ? predicate.negate() : predicate;
         }
      }
   }

   private Predicate<BlockState> func_212485_a(StateContainer<Block, BlockState> p_212485_1_, IProperty<?> p_212485_2_, String p_212485_3_) {
      Optional<?> optional = p_212485_2_.parseValue(p_212485_3_);
      if (!optional.isPresent()) {
         throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", p_212485_3_, this.key, p_212485_1_.getOwner().toString(), this.value));
      } else {
         return (p_212483_2_) -> {
            return p_212483_2_.get(p_212485_2_).equals(optional.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
   }
}