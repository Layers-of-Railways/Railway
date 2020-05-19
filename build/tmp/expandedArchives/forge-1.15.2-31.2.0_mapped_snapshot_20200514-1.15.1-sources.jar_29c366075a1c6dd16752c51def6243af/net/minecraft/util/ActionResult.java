package net.minecraft.util;

public class ActionResult<T> {
   private final ActionResultType type;
   private final T result;

   public ActionResult(ActionResultType typeIn, T resultIn) {
      this.type = typeIn;
      this.result = resultIn;
   }

   public ActionResultType getType() {
      return this.type;
   }

   public T getResult() {
      return this.result;
   }

   public static <T> ActionResult<T> resultSuccess(T p_226248_0_) {
      return new ActionResult<>(ActionResultType.SUCCESS, p_226248_0_);
   }

   public static <T> ActionResult<T> resultConsume(T p_226249_0_) {
      return new ActionResult<>(ActionResultType.CONSUME, p_226249_0_);
   }

   public static <T> ActionResult<T> resultPass(T p_226250_0_) {
      return new ActionResult<>(ActionResultType.PASS, p_226250_0_);
   }

   public static <T> ActionResult<T> resultFail(T p_226251_0_) {
      return new ActionResult<>(ActionResultType.FAIL, p_226251_0_);
   }
}