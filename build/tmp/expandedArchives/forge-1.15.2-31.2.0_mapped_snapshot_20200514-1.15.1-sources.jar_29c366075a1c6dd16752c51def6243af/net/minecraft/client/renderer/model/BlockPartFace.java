package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPartFace {
   public final Direction cullFace;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV blockFaceUV;

   public BlockPartFace(@Nullable Direction cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn) {
      this.cullFace = cullFaceIn;
      this.tintIndex = tintIndexIn;
      this.texture = textureIn;
      this.blockFaceUV = blockFaceUVIn;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockPartFace> {
      public BlockPartFace deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Direction direction = this.parseCullFace(jsonobject);
         int i = this.parseTintIndex(jsonobject);
         String s = this.parseTexture(jsonobject);
         BlockFaceUV blockfaceuv = p_deserialize_3_.deserialize(jsonobject, BlockFaceUV.class);
         return new BlockPartFace(direction, i, s, blockfaceuv);
      }

      protected int parseTintIndex(JsonObject object) {
         return JSONUtils.getInt(object, "tintindex", -1);
      }

      private String parseTexture(JsonObject object) {
         return JSONUtils.getString(object, "texture");
      }

      @Nullable
      private Direction parseCullFace(JsonObject object) {
         String s = JSONUtils.getString(object, "cullface", "");
         return Direction.byName(s);
      }
   }
}