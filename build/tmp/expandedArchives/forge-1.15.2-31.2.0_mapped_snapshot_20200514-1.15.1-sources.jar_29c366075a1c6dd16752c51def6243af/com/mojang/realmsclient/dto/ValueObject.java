package com.mojang.realmsclient.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ValueObject {
   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");

      for(Field field : this.getClass().getFields()) {
         if (!isStatic(field)) {
            try {
               stringbuilder.append(field.getName()).append("=").append(field.get(this)).append(" ");
            } catch (IllegalAccessException var7) {
               ;
            }
         }
      }

      stringbuilder.deleteCharAt(stringbuilder.length() - 1);
      stringbuilder.append('}');
      return stringbuilder.toString();
   }

   private static boolean isStatic(Field p_isStatic_0_) {
      return Modifier.isStatic(p_isStatic_0_.getModifiers());
   }
}