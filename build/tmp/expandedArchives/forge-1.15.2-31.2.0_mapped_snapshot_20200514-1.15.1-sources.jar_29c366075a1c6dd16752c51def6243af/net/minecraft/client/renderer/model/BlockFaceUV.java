package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(@Nullable float[] uvsIn, int rotationIn) {
      this.uvs = uvsIn;
      this.rotation = rotationIn;
   }

   public float getVertexU(int indexIn) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getVertexRotated(indexIn);
         return this.uvs[i != 0 && i != 1 ? 2 : 0];
      }
   }

   public float getVertexV(int indexIn) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getVertexRotated(indexIn);
         return this.uvs[i != 0 && i != 3 ? 3 : 1];
      }
   }

   private int getVertexRotated(int indexIn) {
      return (indexIn + this.rotation / 90) % 4;
   }

   public int getVertexRotatedRev(int indexIn) {
      return (indexIn + 4 - this.rotation / 90) % 4;
   }

   public void setUvs(float[] uvsIn) {
      if (this.uvs == null) {
         this.uvs = uvsIn;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      public BlockFaceUV deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         float[] afloat = this.parseUV(jsonobject);
         int i = this.parseRotation(jsonobject);
         return new BlockFaceUV(afloat, i);
      }

      protected int parseRotation(JsonObject object) {
         int i = JSONUtils.getInt(object, "rotation", 0);
         if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
            return i;
         } else {
            throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
         }
      }

      @Nullable
      private float[] parseUV(JsonObject object) {
         if (!object.has("uv")) {
            return null;
         } else {
            JsonArray jsonarray = JSONUtils.getJsonArray(object, "uv");
            if (jsonarray.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[4];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JSONUtils.getFloat(jsonarray.get(i), "uv[" + i + "]");
               }

               return afloat;
            }
         }
      }
   }
}