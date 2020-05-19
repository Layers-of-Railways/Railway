package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSectionSerializer implements IMetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSection deserialize(JsonObject json) {
      List<AnimationFrame> list = Lists.newArrayList();
      int i = JSONUtils.getInt(json, "frametime", 1);
      if (i != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
      }

      if (json.has("frames")) {
         try {
            JsonArray jsonarray = JSONUtils.getJsonArray(json, "frames");

            for(int j = 0; j < jsonarray.size(); ++j) {
               JsonElement jsonelement = jsonarray.get(j);
               AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement);
               if (animationframe != null) {
                  list.add(animationframe);
               }
            }
         } catch (ClassCastException classcastexception) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + json.get("frames"), classcastexception);
         }
      }

      int k = JSONUtils.getInt(json, "width", -1);
      int l = JSONUtils.getInt(json, "height", -1);
      if (k != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
      }

      if (l != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)l, "Invalid height");
      }

      boolean flag = JSONUtils.getBoolean(json, "interpolate", false);
      return new AnimationMetadataSection(list, k, l, i, flag);
   }

   private AnimationFrame parseAnimationFrame(int frame, JsonElement element) {
      if (element.isJsonPrimitive()) {
         return new AnimationFrame(JSONUtils.getInt(element, "frames[" + frame + "]"));
      } else if (element.isJsonObject()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(element, "frames[" + frame + "]");
         int i = JSONUtils.getInt(jsonobject, "time", -1);
         if (jsonobject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid frame time");
         }

         int j = JSONUtils.getInt(jsonobject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)j, "Invalid frame index");
         return new AnimationFrame(j, i);
      } else {
         return null;
      }
   }

   /**
    * The name of this section type as it appears in JSON.
    */
   public String getSectionName() {
      return "animation";
   }
}