package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @deprecated use {@link net.minecraft.client.renderer.TransformationMatrix} through {@link net.minecraftforge.client.extensions.IForgeBakedModel#handlePerspective}
 */
@OnlyIn(Dist.CLIENT)
@Deprecated
public class ItemTransformVec3f {
   public static final ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;

   public ItemTransformVec3f(Vector3f rotationIn, Vector3f translationIn, Vector3f scaleIn) {
      this.rotation = rotationIn.copy();
      this.translation = translationIn.copy();
      this.scale = scaleIn.copy();
   }

   public void apply(boolean leftHand, MatrixStack matrixStackIn) {
      if (this != DEFAULT) {
         float f = this.rotation.getX();
         float f1 = this.rotation.getY();
         float f2 = this.rotation.getZ();
         if (leftHand) {
            f1 = -f1;
            f2 = -f2;
         }

         int i = leftHand ? -1 : 1;
         matrixStackIn.translate((double)((float)i * this.translation.getX()), (double)this.translation.getY(), (double)this.translation.getZ());
         matrixStackIn.rotate(new Quaternion(f, f1, f2, true));
         matrixStackIn.scale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (this.getClass() != p_equals_1_.getClass()) {
         return false;
      } else {
         ItemTransformVec3f itemtransformvec3f = (ItemTransformVec3f)p_equals_1_;
         return this.rotation.equals(itemtransformvec3f.rotation) && this.scale.equals(itemtransformvec3f.scale) && this.translation.equals(itemtransformvec3f.translation);
      }
   }

   public int hashCode() {
      int i = this.rotation.hashCode();
      i = 31 * i + this.translation.hashCode();
      i = 31 * i + this.scale.hashCode();
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      public static final Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);

      public ItemTransformVec3f deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.parseVector(jsonobject, "rotation", ROTATION_DEFAULT);
         Vector3f vector3f1 = this.parseVector(jsonobject, "translation", TRANSLATION_DEFAULT);
         vector3f1.mul(0.0625F);
         vector3f1.clamp(-5.0F, 5.0F);
         Vector3f vector3f2 = this.parseVector(jsonobject, "scale", SCALE_DEFAULT);
         vector3f2.clamp(-4.0F, 4.0F);
         return new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
      }

      private Vector3f parseVector(JsonObject json, String key, Vector3f fallback) {
         if (!json.has(key)) {
            return fallback;
         } else {
            JsonArray jsonarray = JSONUtils.getJsonArray(json, key);
            if (jsonarray.size() != 3) {
               throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[3];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JSONUtils.getFloat(jsonarray.get(i), key + "[" + i + "]");
               }

               return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
         }
      }
   }
}