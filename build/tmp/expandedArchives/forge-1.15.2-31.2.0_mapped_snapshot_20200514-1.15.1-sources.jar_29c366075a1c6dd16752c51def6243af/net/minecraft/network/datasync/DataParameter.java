package net.minecraft.network.datasync;

public class DataParameter<T> {
   private final int id;
   private final IDataSerializer<T> serializer;

   public DataParameter(int idIn, IDataSerializer<T> serializerIn) {
      this.id = idIn;
      this.serializer = serializerIn;
   }

   public int getId() {
      return this.id;
   }

   public IDataSerializer<T> getSerializer() {
      return this.serializer;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         DataParameter<?> dataparameter = (DataParameter)p_equals_1_;
         return this.id == dataparameter.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public String toString() {
      return "<entity data: " + this.id + ">";
   }
}