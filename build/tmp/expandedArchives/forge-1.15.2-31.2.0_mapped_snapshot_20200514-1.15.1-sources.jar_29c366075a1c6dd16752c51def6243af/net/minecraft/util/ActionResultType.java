package net.minecraft.util;

public enum ActionResultType {
   SUCCESS,
   CONSUME,
   PASS,
   FAIL;

   public boolean isSuccessOrConsume() {
      return this == SUCCESS || this == CONSUME;
   }

   public boolean isSuccess() {
      return this == SUCCESS;
   }
}