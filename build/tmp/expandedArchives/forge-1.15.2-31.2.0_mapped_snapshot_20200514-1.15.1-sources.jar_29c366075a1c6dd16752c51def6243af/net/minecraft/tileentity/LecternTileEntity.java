package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.LecternBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LecternTileEntity extends TileEntity implements IClearable, INamedContainerProvider {
   private final IInventory inventory = new IInventory() {
      /**
       * Returns the number of slots in the inventory.
       */
      public int getSizeInventory() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternTileEntity.this.book.isEmpty();
      }

      /**
       * Returns the stack in the given slot.
       */
      public ItemStack getStackInSlot(int index) {
         return index == 0 ? LecternTileEntity.this.book : ItemStack.EMPTY;
      }

      /**
       * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
       */
      public ItemStack decrStackSize(int index, int count) {
         if (index == 0) {
            ItemStack itemstack = LecternTileEntity.this.book.split(count);
            if (LecternTileEntity.this.book.isEmpty()) {
               LecternTileEntity.this.bookRemoved();
            }

            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      /**
       * Removes a stack from the given slot and returns it.
       */
      public ItemStack removeStackFromSlot(int index) {
         if (index == 0) {
            ItemStack itemstack = LecternTileEntity.this.book;
            LecternTileEntity.this.book = ItemStack.EMPTY;
            LecternTileEntity.this.bookRemoved();
            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      /**
       * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
       */
      public void setInventorySlotContents(int index, ItemStack stack) {
      }

      /**
       * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
       */
      public int getInventoryStackLimit() {
         return 1;
      }

      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         LecternTileEntity.this.markDirty();
      }

      /**
       * Don't rename this method to canInteractWith due to conflicts with Container
       */
      public boolean isUsableByPlayer(PlayerEntity player) {
         if (LecternTileEntity.this.world.getTileEntity(LecternTileEntity.this.pos) != LecternTileEntity.this) {
            return false;
         } else {
            return player.getDistanceSq((double)LecternTileEntity.this.pos.getX() + 0.5D, (double)LecternTileEntity.this.pos.getY() + 0.5D, (double)LecternTileEntity.this.pos.getZ() + 0.5D) > 64.0D ? false : LecternTileEntity.this.hasBook();
         }
      }

      /**
       * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
       * guis use Slot.isItemValid
       */
      public boolean isItemValidForSlot(int index, ItemStack stack) {
         return false;
      }

      public void clear() {
      }
   };
   private final IIntArray field_214049_b = new IIntArray() {
      public int get(int index) {
         return index == 0 ? LecternTileEntity.this.page : 0;
      }

      public void set(int index, int value) {
         if (index == 0) {
            LecternTileEntity.this.setPage(value);
         }

      }

      public int size() {
         return 1;
      }
   };
   private ItemStack book = ItemStack.EMPTY;
   private int page;
   private int pages;

   public LecternTileEntity() {
      super(TileEntityType.LECTERN);
   }

   public ItemStack getBook() {
      return this.book;
   }

   /**
    * True if the item in this lectern is a book or written book. False if it is another item (i.e. an empty stack)
    */
   public boolean hasBook() {
      Item item = this.book.getItem();
      return item == Items.WRITABLE_BOOK || item == Items.WRITTEN_BOOK;
   }

   /**
    * Sets the item stack in this lectern. Note that this does not update the block state; use {@link
    * LecternBlock#tryPlaceBook} for that.
    */
   public void setBook(ItemStack stack) {
      this.setBook(stack, (PlayerEntity)null);
   }

   private void bookRemoved() {
      this.page = 0;
      this.pages = 0;
      LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getBlockState(), false);
   }

   /**
    * Sets the item stack in this lectern. Note that this does not update the block state; use {@link
    * LecternBlock#tryPlaceBook} for that.
    */
   public void setBook(ItemStack stack, @Nullable PlayerEntity player) {
      this.book = this.ensureResolved(stack, player);
      this.page = 0;
      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.markDirty();
   }

   private void setPage(int pageIn) {
      int i = MathHelper.clamp(pageIn, 0, this.pages - 1);
      if (i != this.page) {
         this.page = i;
         this.markDirty();
         LecternBlock.pulse(this.getWorld(), this.getPos(), this.getBlockState());
      }

   }

   public int getPage() {
      return this.page;
   }

   public int getComparatorSignalLevel() {
      float f = this.pages > 1 ? (float)this.getPage() / ((float)this.pages - 1.0F) : 1.0F;
      return MathHelper.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
   }

   /**
    * Resolves the passed book, if it is a book and has not yet been resolved.
    */
   private ItemStack ensureResolved(ItemStack stack, @Nullable PlayerEntity player) {
      if (this.world instanceof ServerWorld && stack.getItem() == Items.WRITTEN_BOOK) {
         WrittenBookItem.resolveContents(stack, this.createCommandSource(player), player);
      }

      return stack;
   }

   /**
    * Creates a CommandSource for resolving the contents of a book. If the player is null, a fake commandsource for this
    * lectern is used.
    */
   private CommandSource createCommandSource(@Nullable PlayerEntity player) {
      String s;
      ITextComponent itextcomponent;
      if (player == null) {
         s = "Lectern";
         itextcomponent = new StringTextComponent("Lectern");
      } else {
         s = player.getName().getString();
         itextcomponent = player.getDisplayName();
      }

      Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
      return new CommandSource(ICommandSource.DUMMY, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, s, itextcomponent, this.world.getServer(), player);
   }

   /**
    * Checks if players can use this tile entity to access operator (permission level 2) commands either directly or
    * indirectly, such as give or setblock. A similar method exists for entities at {@link
    * net.minecraft.entity.Entity#ignoreItemEntityData()}.<p>For example, {@link
    * net.minecraft.tileentity.TileEntitySign#onlyOpsCanSetNbt() signs} (player right-clicking) and {@link
    * net.minecraft.tileentity.TileEntityCommandBlock#onlyOpsCanSetNbt() command blocks} are considered
    * accessible.</p>@return true if this block entity offers ways for unauthorized players to use restricted commands
    */
   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      if (compound.contains("Book", 10)) {
         this.book = this.ensureResolved(ItemStack.read(compound.getCompound("Book")), (PlayerEntity)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.page = MathHelper.clamp(compound.getInt("Page"), 0, this.pages - 1);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      if (!this.getBook().isEmpty()) {
         compound.put("Book", this.getBook().write(new CompoundNBT()));
         compound.putInt("Page", this.page);
      }

      return compound;
   }

   public void clear() {
      this.setBook(ItemStack.EMPTY);
   }

   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return new LecternContainer(p_createMenu_1_, this.inventory, this.field_214049_b);
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("container.lectern");
   }
}