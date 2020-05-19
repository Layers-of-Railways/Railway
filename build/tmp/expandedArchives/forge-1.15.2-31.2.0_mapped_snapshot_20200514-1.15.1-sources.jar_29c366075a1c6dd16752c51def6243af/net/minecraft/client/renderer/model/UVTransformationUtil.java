package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UVTransformationUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EnumMap<Direction, TransformationMatrix> TRANSFORM_LOCAL_TO_GLOBAL = Util.make(Maps.newEnumMap(Direction.class), (p_229382_0_) -> {
      p_229382_0_.put(Direction.SOUTH, TransformationMatrix.identity());
      p_229382_0_.put(Direction.EAST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.WEST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.NORTH, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.UP, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.DOWN, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
   });
   public static final EnumMap<Direction, TransformationMatrix> TRANSFORM_GLOBAL_TO_LOCAL = Util.make(Maps.newEnumMap(Direction.class), (p_229381_0_) -> {
      for(Direction direction : Direction.values()) {
         p_229381_0_.put(direction, TRANSFORM_LOCAL_TO_GLOBAL.get(direction).inverseVanilla());
      }

   });

   public static TransformationMatrix blockCenterToCorner(TransformationMatrix matrixIn) {
      Matrix4f matrix4f = Matrix4f.makeTranslate(0.5F, 0.5F, 0.5F);
      matrix4f.mul(matrixIn.getMatrix());
      matrix4f.mul(Matrix4f.makeTranslate(-0.5F, -0.5F, -0.5F));
      return new TransformationMatrix(matrix4f);
   }

   public static TransformationMatrix getUVLockTransform(TransformationMatrix matrixIn, Direction directionIn, Supplier<String> warningIn) {
      Direction direction = Direction.rotateFace(matrixIn.getMatrix(), directionIn);
      TransformationMatrix transformationmatrix = matrixIn.inverseVanilla();
      if (transformationmatrix == null) {
         LOGGER.warn(warningIn.get());
         return new TransformationMatrix((Vector3f)null, (Quaternion)null, new Vector3f(0.0F, 0.0F, 0.0F), (Quaternion)null);
      } else {
         TransformationMatrix transformationmatrix1 = TRANSFORM_GLOBAL_TO_LOCAL.get(directionIn).composeVanilla(transformationmatrix).composeVanilla(TRANSFORM_LOCAL_TO_GLOBAL.get(direction));
         return blockCenterToCorner(transformationmatrix1);
      }
   }
}