package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.server.ServerWorld;

public class TestRegistry {
   private static final Collection<TestFunctionInfo> field_229526_a_ = Lists.newArrayList();
   private static final Set<String> field_229527_b_ = Sets.newHashSet();
   private static final Map<String, Consumer<ServerWorld>> field_229528_c_ = Maps.newHashMap();

   public static Collection<TestFunctionInfo> func_229530_a_(String p_229530_0_) {
      return field_229526_a_.stream().filter((p_229535_1_) -> {
         return func_229532_a_(p_229535_1_, p_229530_0_);
      }).collect(Collectors.toList());
   }

   public static Collection<TestFunctionInfo> func_229529_a_() {
      return field_229526_a_;
   }

   public static Collection<String> func_229533_b_() {
      return field_229527_b_;
   }

   public static boolean func_229534_b_(String p_229534_0_) {
      return field_229527_b_.contains(p_229534_0_);
   }

   @Nullable
   public static Consumer<ServerWorld> func_229536_c_(String p_229536_0_) {
      return field_229528_c_.get(p_229536_0_);
   }

   public static Optional<TestFunctionInfo> func_229537_d_(String p_229537_0_) {
      return func_229529_a_().stream().filter((p_229531_1_) -> {
         return p_229531_1_.func_229657_a_().equalsIgnoreCase(p_229537_0_);
      }).findFirst();
   }

   public static TestFunctionInfo func_229538_e_(String p_229538_0_) {
      Optional<TestFunctionInfo> optional = func_229537_d_(p_229538_0_);
      if (!optional.isPresent()) {
         throw new IllegalArgumentException("Can't find the test function for " + p_229538_0_);
      } else {
         return optional.get();
      }
   }

   private static boolean func_229532_a_(TestFunctionInfo p_229532_0_, String p_229532_1_) {
      return p_229532_0_.func_229657_a_().toLowerCase().startsWith(p_229532_1_.toLowerCase() + ".");
   }
}