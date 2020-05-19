package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
   public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator::new);
   public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeaveVineTreeDecorator::new);
   public static final TreeDecoratorType<CocoaTreeDecorator> COCOA = register("cocoa", CocoaTreeDecorator::new);
   public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator::new);
   public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator::new);
   private final Function<Dynamic<?>, P> field_227430_f_;

   private static <P extends TreeDecorator> TreeDecoratorType<P> register(String p_227432_0_, Function<Dynamic<?>, P> p_227432_1_) {
      return Registry.register(Registry.TREE_DECORATOR_TYPE, p_227432_0_, new TreeDecoratorType<>(p_227432_1_));
   }

   private TreeDecoratorType(Function<Dynamic<?>, P> p_i225872_1_) {
      this.field_227430_f_ = p_i225872_1_;
   }

   public P func_227431_a_(Dynamic<?> p_227431_1_) {
      return (P)(this.field_227430_f_.apply(p_227431_1_));
   }
}