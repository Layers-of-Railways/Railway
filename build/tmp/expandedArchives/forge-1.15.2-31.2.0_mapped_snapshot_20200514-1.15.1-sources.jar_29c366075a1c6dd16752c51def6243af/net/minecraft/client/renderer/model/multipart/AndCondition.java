package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AndCondition implements ICondition {
   private final Iterable<? extends ICondition> conditions;

   public AndCondition(Iterable<? extends ICondition> conditionsIn) {
      this.conditions = conditionsIn;
   }

   public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_) {
      List<Predicate<BlockState>> list = Streams.stream(this.conditions).map((p_200683_1_) -> {
         return p_200683_1_.getPredicate(p_getPredicate_1_);
      }).collect(Collectors.toList());
      return (p_212481_1_) -> {
         return list.stream().allMatch((p_212480_1_) -> {
            return p_212480_1_.test(p_212481_1_);
         });
      };
   }
}