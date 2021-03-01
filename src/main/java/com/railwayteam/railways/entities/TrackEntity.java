package com.railwayteam.railways.entities;

import com.railwayteam.railways.entities.model.TrackEntityModel;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.function.Predicate;

public class TrackEntity extends Entity {

  public static final String name   = "track";
  public static int SEARCH_RADIUS   = 10;
  public static int SEARCH_VERTICAL = 1;

  private static final Vector3f ZERO = new Vector3f(0,0,0);

  private TrackEntityModel<TrackEntity> model;
  private Vector3f[] paths = new Vector3f[8];

  public TrackEntity (EntityType typeIn, World worldIn) {
    super(typeIn, worldIn);
    model = new TrackEntityModel<TrackEntity>();
  }

  @Override
  public void onAddedToWorld() {
    super.onAddedToWorld();
    //System.out.println("coordinates are " + getPosition());
    BlockPos me = getPosition();
    List<TrackEntity> found = findTracks(getEntityWorld(), me);
    if (!found.isEmpty()) {
      int count = 0;
      for (TrackEntity track : found) {
        if (track.getPosition() != me) {
          BlockPos diff = me.subtract(track.getPosition());
          paths[count] = new Vector3f(
            diff.getX()==0 ? 0.5f : diff.getX(),
            diff.getY()==0 ? 0.5f : diff.getY(),
            diff.getZ()==0 ? 0.5f : diff.getZ()
          );
          //System.out.println("Scale factor: " + paths[count]);
        }
      }
    }
    System.out.println("scale: " + getScale());
    System.out.println("found track qty " + found.size());
  }

  @Override
  public void tick() {
    super.tick();

    List<Entity> found = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
    if (!found.isEmpty()) {
      Vec3d offset = new Vec3d(0d, Direction.UP.getYOffset()*getScale().getY(), 0d);
      for (Entity e : found) {
        if (!e.noClip && !(e instanceof TrackEntity)) e.move(MoverType.SHULKER_BOX, offset);
      }
    }
  }

  @Override
  protected void registerData() {
  }

  @Override
  protected void readAdditional(CompoundNBT compound) {
  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  protected TrackEntityModel<TrackEntity> getModel () {
    return model;
  }

  protected Vector3f getScale () {
    Vector3f ret;
    if (paths[1] == null || paths[1].equals(ZERO)) {
      ret = new Vector3f(1,1,1);
    }
    else ret = paths[1];

    return ret;
  }

  public List<TrackEntity> findTracks (World world, BlockPos pos) {
    List<TrackEntity> ret = world.getEntitiesWithinAABB(TrackEntity.class, getDetectionBox(pos), (Predicate<Entity>)null);
    ret.remove(this);
    return ret;
  }

  private AxisAlignedBB getDetectionBox (BlockPos pos) {
    return new AxisAlignedBB(
    (double)pos.getX()-SEARCH_RADIUS, pos.getY()-SEARCH_VERTICAL, (double)pos.getZ()-SEARCH_RADIUS,
    (double)pos.getX()+SEARCH_RADIUS, pos.getY()+SEARCH_VERTICAL, (double)pos.getZ()+SEARCH_RADIUS
    );
  }
}
