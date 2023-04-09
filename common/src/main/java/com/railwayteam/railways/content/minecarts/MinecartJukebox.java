package com.railwayteam.railways.content.minecarts;

import com.mojang.math.Vector3d;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.util.packet.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import org.jetbrains.annotations.NotNull;

public class MinecartJukebox extends MinecartBlock {
  public static final Type TYPE = Type.valueOf("RAILWAY_JUKEBOX");

  private static final int COOLDOWN = 100; // ticks
  private int cooldownCount = 0;

  private ItemStack disc = ItemStack.EMPTY;
  @Environment(EnvType.CLIENT)
  private JukeboxCartSoundInstance sound;

  public MinecartJukebox(EntityType<?> type, Level level) {
    super(type, level, Blocks.JUKEBOX);
  }

  protected MinecartJukebox(Level level, double x, double y, double z) {
    super(CREntities.CART_JUKEBOX.get(), level, x, y, z);
  }

  // need to detour through this or generics explode somehow
  public static MinecartJukebox create(Level level, double x, double y, double z) {
    return new MinecartJukebox(level, x, y, z);
  }

  @Override
  public Type getMinecartType() {
    return TYPE;
  }

  @Override
  protected Item getDropItem() {
    return CRItems.ITEM_JUKEBOXCART.get();
  }

  @Override
  public void tick () {
    super.tick();
    if (cooldownCount > 0) cooldownCount--;
  }

  @Override
  public void activateMinecart(int x, int y, int z, boolean active) {
    if (!level.isClientSide) {
      if (cooldownCount <= 0) {
        cooldownCount = COOLDOWN;
        PacketSender.updateJukeboxClientside(this, this.disc);
      }
    }
  }

  @Override
  public ItemStack getPickResult() {
    return CRItems.ITEM_JUKEBOXCART.asStack();
  }

  @NotNull
  @Override
  public InteractionResult interact (@NotNull Player player, @NotNull InteractionHand hand) {
    InteractionResult ret = super.interact(player, hand);
    if (ret.consumesAction()) return ret;

    if (!level.isClientSide) {
      if (disc.isEmpty()) { // no disc inserted
        // get the disc from the player, if they have one
        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.getItem() instanceof RecordItem) {
          __insertRecord(handStack);
          if (!player.isCreative()) player.setItemInHand(hand, ItemStack.EMPTY);
          player.awardStat(Stats.PLAY_RECORD);
        }
        else return InteractionResult.PASS;
      }
      else {
        __ejectRecord();
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  // clientside
  public void insertRecord (ItemStack record) {
    __insertRecord(record);
    if (level.isClientSide) {
      if (!this.disc.isEmpty()) {
        if (sound == null || sound.isStopped()) {
          startPlaying();
        } else sound.requestStop();
      } else if (sound != null) sound.requestStop();
    }
  }

  // serverside. Checks for side due to public method above being used clientside
  private void __insertRecord (ItemStack record) {
    this.disc = record.copy();
    this.content = content.setValue(JukeboxBlock.HAS_RECORD, !disc.isEmpty());
    if (!level.isClientSide) PacketSender.updateJukeboxClientside(this, this.disc);
  }

  // serverside
  private void __ejectRecord () {
    if (level.isClientSide) return;

    Vector3d pos = new Vector3d(
      this.position().x + 0.5d,
      this.position().y + 1d,
      this.position().z + 0.5d
    );
    ItemEntity out = new ItemEntity(level, pos.x, pos.y, pos.z, this.disc);
    out.setDefaultPickUpDelay();
    level.addFreshEntity(out);
    __insertRecord(ItemStack.EMPTY);
  }

  @Environment(EnvType.CLIENT)
  // clientside
  private void startPlaying () {
    if (!this.disc.isEmpty()) {
      sound = new JukeboxCartSoundInstance(((RecordItem)this.disc.getItem()).getSound());
      Minecraft.getInstance().getSoundManager().play(sound);
    }
  }

  @Environment(EnvType.CLIENT)
  public class JukeboxCartSoundInstance extends AbstractTickableSoundInstance {
    public JukeboxCartSoundInstance (SoundEvent event) {
      super(event, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
    }

    @Override
    public void tick () {
      if (isRemoved()) requestStop();

      this.x = blockPosition().getX();
      this.y = blockPosition().getY();
      this.z = blockPosition().getZ();
    }

    public void requestStop () {
      stop();
    }
  }
}
