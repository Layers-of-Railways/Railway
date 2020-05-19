package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.TypeReferences;

public class LWJGL3KeyOptions extends DataFix {
   private static final Int2ObjectMap<String> field_199186_a = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_206279_0_) -> {
      p_206279_0_.put(0, "key.unknown");
      p_206279_0_.put(11, "key.0");
      p_206279_0_.put(2, "key.1");
      p_206279_0_.put(3, "key.2");
      p_206279_0_.put(4, "key.3");
      p_206279_0_.put(5, "key.4");
      p_206279_0_.put(6, "key.5");
      p_206279_0_.put(7, "key.6");
      p_206279_0_.put(8, "key.7");
      p_206279_0_.put(9, "key.8");
      p_206279_0_.put(10, "key.9");
      p_206279_0_.put(30, "key.a");
      p_206279_0_.put(40, "key.apostrophe");
      p_206279_0_.put(48, "key.b");
      p_206279_0_.put(43, "key.backslash");
      p_206279_0_.put(14, "key.backspace");
      p_206279_0_.put(46, "key.c");
      p_206279_0_.put(58, "key.caps.lock");
      p_206279_0_.put(51, "key.comma");
      p_206279_0_.put(32, "key.d");
      p_206279_0_.put(211, "key.delete");
      p_206279_0_.put(208, "key.down");
      p_206279_0_.put(18, "key.e");
      p_206279_0_.put(207, "key.end");
      p_206279_0_.put(28, "key.enter");
      p_206279_0_.put(13, "key.equal");
      p_206279_0_.put(1, "key.escape");
      p_206279_0_.put(33, "key.f");
      p_206279_0_.put(59, "key.f1");
      p_206279_0_.put(68, "key.f10");
      p_206279_0_.put(87, "key.f11");
      p_206279_0_.put(88, "key.f12");
      p_206279_0_.put(100, "key.f13");
      p_206279_0_.put(101, "key.f14");
      p_206279_0_.put(102, "key.f15");
      p_206279_0_.put(103, "key.f16");
      p_206279_0_.put(104, "key.f17");
      p_206279_0_.put(105, "key.f18");
      p_206279_0_.put(113, "key.f19");
      p_206279_0_.put(60, "key.f2");
      p_206279_0_.put(61, "key.f3");
      p_206279_0_.put(62, "key.f4");
      p_206279_0_.put(63, "key.f5");
      p_206279_0_.put(64, "key.f6");
      p_206279_0_.put(65, "key.f7");
      p_206279_0_.put(66, "key.f8");
      p_206279_0_.put(67, "key.f9");
      p_206279_0_.put(34, "key.g");
      p_206279_0_.put(41, "key.grave.accent");
      p_206279_0_.put(35, "key.h");
      p_206279_0_.put(199, "key.home");
      p_206279_0_.put(23, "key.i");
      p_206279_0_.put(210, "key.insert");
      p_206279_0_.put(36, "key.j");
      p_206279_0_.put(37, "key.k");
      p_206279_0_.put(82, "key.keypad.0");
      p_206279_0_.put(79, "key.keypad.1");
      p_206279_0_.put(80, "key.keypad.2");
      p_206279_0_.put(81, "key.keypad.3");
      p_206279_0_.put(75, "key.keypad.4");
      p_206279_0_.put(76, "key.keypad.5");
      p_206279_0_.put(77, "key.keypad.6");
      p_206279_0_.put(71, "key.keypad.7");
      p_206279_0_.put(72, "key.keypad.8");
      p_206279_0_.put(73, "key.keypad.9");
      p_206279_0_.put(78, "key.keypad.add");
      p_206279_0_.put(83, "key.keypad.decimal");
      p_206279_0_.put(181, "key.keypad.divide");
      p_206279_0_.put(156, "key.keypad.enter");
      p_206279_0_.put(141, "key.keypad.equal");
      p_206279_0_.put(55, "key.keypad.multiply");
      p_206279_0_.put(74, "key.keypad.subtract");
      p_206279_0_.put(38, "key.l");
      p_206279_0_.put(203, "key.left");
      p_206279_0_.put(56, "key.left.alt");
      p_206279_0_.put(26, "key.left.bracket");
      p_206279_0_.put(29, "key.left.control");
      p_206279_0_.put(42, "key.left.shift");
      p_206279_0_.put(219, "key.left.win");
      p_206279_0_.put(50, "key.m");
      p_206279_0_.put(12, "key.minus");
      p_206279_0_.put(49, "key.n");
      p_206279_0_.put(69, "key.num.lock");
      p_206279_0_.put(24, "key.o");
      p_206279_0_.put(25, "key.p");
      p_206279_0_.put(209, "key.page.down");
      p_206279_0_.put(201, "key.page.up");
      p_206279_0_.put(197, "key.pause");
      p_206279_0_.put(52, "key.period");
      p_206279_0_.put(183, "key.print.screen");
      p_206279_0_.put(16, "key.q");
      p_206279_0_.put(19, "key.r");
      p_206279_0_.put(205, "key.right");
      p_206279_0_.put(184, "key.right.alt");
      p_206279_0_.put(27, "key.right.bracket");
      p_206279_0_.put(157, "key.right.control");
      p_206279_0_.put(54, "key.right.shift");
      p_206279_0_.put(220, "key.right.win");
      p_206279_0_.put(31, "key.s");
      p_206279_0_.put(70, "key.scroll.lock");
      p_206279_0_.put(39, "key.semicolon");
      p_206279_0_.put(53, "key.slash");
      p_206279_0_.put(57, "key.space");
      p_206279_0_.put(20, "key.t");
      p_206279_0_.put(15, "key.tab");
      p_206279_0_.put(22, "key.u");
      p_206279_0_.put(200, "key.up");
      p_206279_0_.put(47, "key.v");
      p_206279_0_.put(17, "key.w");
      p_206279_0_.put(45, "key.x");
      p_206279_0_.put(21, "key.y");
      p_206279_0_.put(44, "key.z");
   });

   public LWJGL3KeyOptions(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyLwjgl3Fix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_207423_0_) -> {
         return p_207423_0_.update(DSL.remainderFinder(), (p_207424_0_) -> {
            return p_207424_0_.getMapValues().map((p_209663_1_) -> {
               return p_207424_0_.createMap(p_209663_1_.entrySet().stream().map((p_209661_0_) -> {
                  if (p_209661_0_.getKey().asString("").startsWith("key_")) {
                     int i = Integer.parseInt(p_209661_0_.getValue().asString(""));
                     if (i < 0) {
                        int j = i + 100;
                        String s1;
                        if (j == 0) {
                           s1 = "key.mouse.left";
                        } else if (j == 1) {
                           s1 = "key.mouse.right";
                        } else if (j == 2) {
                           s1 = "key.mouse.middle";
                        } else {
                           s1 = "key.mouse." + (j + 1);
                        }

                        return Pair.of(p_209661_0_.getKey(), p_209661_0_.getValue().createString(s1));
                     } else {
                        String s = field_199186_a.getOrDefault(i, "key.unknown");
                        return Pair.of(p_209661_0_.getKey(), p_209661_0_.getValue().createString(s));
                     }
                  } else {
                     return Pair.of(p_209661_0_.getKey(), p_209661_0_.getValue());
                  }
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).orElse((com.mojang.datafixers.Dynamic)p_207424_0_);
         });
      });
   }
}