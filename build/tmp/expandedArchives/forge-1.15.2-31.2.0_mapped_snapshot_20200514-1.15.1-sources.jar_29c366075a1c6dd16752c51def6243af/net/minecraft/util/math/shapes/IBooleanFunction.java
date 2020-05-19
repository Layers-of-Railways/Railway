package net.minecraft.util.math.shapes;

public interface IBooleanFunction {
   IBooleanFunction FALSE = (p_223272_0_, p_223272_1_) -> {
      return false;
   };
   IBooleanFunction NOT_OR = (p_223271_0_, p_223271_1_) -> {
      return !p_223271_0_ && !p_223271_1_;
   };
   IBooleanFunction ONLY_SECOND = (p_223270_0_, p_223270_1_) -> {
      return p_223270_1_ && !p_223270_0_;
   };
   IBooleanFunction NOT_FIRST = (p_223269_0_, p_223269_1_) -> {
      return !p_223269_0_;
   };
   IBooleanFunction ONLY_FIRST = (p_223268_0_, p_223268_1_) -> {
      return p_223268_0_ && !p_223268_1_;
   };
   IBooleanFunction NOT_SECOND = (p_223267_0_, p_223267_1_) -> {
      return !p_223267_1_;
   };
   IBooleanFunction NOT_SAME = (p_223266_0_, p_223266_1_) -> {
      return p_223266_0_ != p_223266_1_;
   };
   IBooleanFunction NOT_AND = (p_223265_0_, p_223265_1_) -> {
      return !p_223265_0_ || !p_223265_1_;
   };
   IBooleanFunction AND = (p_223264_0_, p_223264_1_) -> {
      return p_223264_0_ && p_223264_1_;
   };
   IBooleanFunction SAME = (p_223263_0_, p_223263_1_) -> {
      return p_223263_0_ == p_223263_1_;
   };
   IBooleanFunction SECOND = (p_223262_0_, p_223262_1_) -> {
      return p_223262_1_;
   };
   IBooleanFunction CAUSES = (p_223261_0_, p_223261_1_) -> {
      return !p_223261_0_ || p_223261_1_;
   };
   IBooleanFunction FIRST = (p_223260_0_, p_223260_1_) -> {
      return p_223260_0_;
   };
   IBooleanFunction CAUSED_BY = (p_223259_0_, p_223259_1_) -> {
      return p_223259_0_ || !p_223259_1_;
   };
   IBooleanFunction OR = (p_223258_0_, p_223258_1_) -> {
      return p_223258_0_ || p_223258_1_;
   };
   IBooleanFunction TRUE = (p_223257_0_, p_223257_1_) -> {
      return true;
   };

   boolean apply(boolean p_apply_1_, boolean p_apply_2_);
}