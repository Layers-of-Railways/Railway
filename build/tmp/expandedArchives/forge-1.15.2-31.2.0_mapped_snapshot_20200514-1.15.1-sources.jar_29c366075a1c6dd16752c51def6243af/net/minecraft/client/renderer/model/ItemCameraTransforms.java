package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @deprecated use {@link net.minecraft.client.renderer.model.IBakedModel#handlePerspective(TransformType)} instead
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public class ItemCameraTransforms {
   public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
   public final ItemTransformVec3f thirdperson_left;
   public final ItemTransformVec3f thirdperson_right;
   public final ItemTransformVec3f firstperson_left;
   public final ItemTransformVec3f firstperson_right;
   public final ItemTransformVec3f head;
   public final ItemTransformVec3f gui;
   public final ItemTransformVec3f ground;
   public final ItemTransformVec3f fixed;

   private ItemCameraTransforms() {
      this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
   }

   @Deprecated
   public ItemCameraTransforms(ItemCameraTransforms transforms) {
      this.thirdperson_left = transforms.thirdperson_left;
      this.thirdperson_right = transforms.thirdperson_right;
      this.firstperson_left = transforms.firstperson_left;
      this.firstperson_right = transforms.firstperson_right;
      this.head = transforms.head;
      this.gui = transforms.gui;
      this.ground = transforms.ground;
      this.fixed = transforms.fixed;
   }

   @Deprecated
   public ItemCameraTransforms(ItemTransformVec3f thirdperson_leftIn, ItemTransformVec3f thirdperson_rightIn, ItemTransformVec3f firstperson_leftIn, ItemTransformVec3f firstperson_rightIn, ItemTransformVec3f headIn, ItemTransformVec3f guiIn, ItemTransformVec3f groundIn, ItemTransformVec3f fixedIn) {
      this.thirdperson_left = thirdperson_leftIn;
      this.thirdperson_right = thirdperson_rightIn;
      this.firstperson_left = firstperson_leftIn;
      this.firstperson_right = firstperson_rightIn;
      this.head = headIn;
      this.gui = guiIn;
      this.ground = groundIn;
      this.fixed = fixedIn;
   }

   @Deprecated
   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      switch(type) {
      case THIRD_PERSON_LEFT_HAND:
         return this.thirdperson_left;
      case THIRD_PERSON_RIGHT_HAND:
         return this.thirdperson_right;
      case FIRST_PERSON_LEFT_HAND:
         return this.firstperson_left;
      case FIRST_PERSON_RIGHT_HAND:
         return this.firstperson_right;
      case HEAD:
         return this.head;
      case GUI:
         return this.gui;
      case GROUND:
         return this.ground;
      case FIXED:
         return this.fixed;
      default:
         return ItemTransformVec3f.DEFAULT;
      }
   }

   public boolean hasCustomTransform(ItemCameraTransforms.TransformType type) {
      return this.getTransform(type) != ItemTransformVec3f.DEFAULT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         ItemTransformVec3f itemtransformvec3f = this.getTransform(p_deserialize_3_, jsonobject, "thirdperson_righthand");
         ItemTransformVec3f itemtransformvec3f1 = this.getTransform(p_deserialize_3_, jsonobject, "thirdperson_lefthand");
         if (itemtransformvec3f1 == ItemTransformVec3f.DEFAULT) {
            itemtransformvec3f1 = itemtransformvec3f;
         }

         ItemTransformVec3f itemtransformvec3f2 = this.getTransform(p_deserialize_3_, jsonobject, "firstperson_righthand");
         ItemTransformVec3f itemtransformvec3f3 = this.getTransform(p_deserialize_3_, jsonobject, "firstperson_lefthand");
         if (itemtransformvec3f3 == ItemTransformVec3f.DEFAULT) {
            itemtransformvec3f3 = itemtransformvec3f2;
         }

         ItemTransformVec3f itemtransformvec3f4 = this.getTransform(p_deserialize_3_, jsonobject, "head");
         ItemTransformVec3f itemtransformvec3f5 = this.getTransform(p_deserialize_3_, jsonobject, "gui");
         ItemTransformVec3f itemtransformvec3f6 = this.getTransform(p_deserialize_3_, jsonobject, "ground");
         ItemTransformVec3f itemtransformvec3f7 = this.getTransform(p_deserialize_3_, jsonobject, "fixed");
         return new ItemCameraTransforms(itemtransformvec3f1, itemtransformvec3f, itemtransformvec3f3, itemtransformvec3f2, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
      }

      private ItemTransformVec3f getTransform(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_) {
         return p_181683_2_.has(p_181683_3_) ? p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TransformType {
      NONE,
      THIRD_PERSON_LEFT_HAND,
      THIRD_PERSON_RIGHT_HAND,
      FIRST_PERSON_LEFT_HAND,
      FIRST_PERSON_RIGHT_HAND,
      HEAD,
      GUI,
      GROUND,
      FIXED;
   }
}