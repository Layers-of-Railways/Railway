package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayConfig implements IFeatureConfig {
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfig(Optional<BlockPos> exit, boolean exact) {
      this.exit = exit;
      this.exact = exact;
   }

   public static EndGatewayConfig func_214702_a(BlockPos p_214702_0_, boolean p_214702_1_) {
      return new EndGatewayConfig(Optional.of(p_214702_0_), p_214702_1_);
   }

   public static EndGatewayConfig func_214698_a() {
      return new EndGatewayConfig(Optional.empty(), false);
   }

   public Optional<BlockPos> func_214700_b() {
      return this.exit;
   }

   public boolean func_214701_c() {
      return this.exact;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, (T)this.exit.map((p_214703_2_) -> {
         return ops.createMap(ImmutableMap.of(ops.createString("exit_x"), ops.createInt(p_214703_2_.getX()), ops.createString("exit_y"), ops.createInt(p_214703_2_.getY()), ops.createString("exit_z"), ops.createInt(p_214703_2_.getZ()), ops.createString("exact"), ops.createBoolean(this.exact)));
      }).orElse(ops.emptyMap()));
   }

   public static <T> EndGatewayConfig deserialize(Dynamic<T> p_214697_0_) {
      Optional<BlockPos> optional = p_214697_0_.get("exit_x").asNumber().flatMap((p_214696_1_) -> {
         return p_214697_0_.get("exit_y").asNumber().flatMap((p_214695_2_) -> {
            return p_214697_0_.get("exit_z").asNumber().map((p_214699_2_) -> {
               return new BlockPos(p_214696_1_.intValue(), p_214695_2_.intValue(), p_214699_2_.intValue());
            });
         });
      });
      boolean flag = p_214697_0_.get("exact").asBoolean(false);
      return new EndGatewayConfig(optional, flag);
   }
}