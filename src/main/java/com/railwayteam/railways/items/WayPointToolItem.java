package com.railwayteam.railways.items;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.blocks.WayPointBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.Event;

import static net.minecraftforge.eventbus.api.Event.Result.DEFAULT;
import static net.minecraftforge.eventbus.api.Event.Result.DENY;

public class WayPointToolItem extends Item{
	public static final String name      = "waypoint_manager";
	public static final String selectTag = "FirstPoint";

	private static String MSG_RESET   = "Selection reset.";
	private static String MSG_VALID   = "Valid connection with slope: "; // Y-delta is appended to this
	private static String MSG_INVALID = "Invalid connection!";
	private static String MSG_TOOLONG = "Track segment too long for one run!";
	private static int MAX_TRACK_SEG_LEN = 64;

	public WayPointToolItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player != null) {
			if (player.isSneaking()) {
				context.getItem().setTag(null);
				player.sendMessage(new StringTextComponent(MSG_RESET));
				return ActionResultType.SUCCESS;
			}
			World world   = context.getWorld();
			BlockPos pos  = context.getPos();
			boolean valid = (world.getBlockState(pos).getBlock() instanceof WayPointBlock);
			if (world.isRemote) {
				return valid ? ActionResultType.SUCCESS : ActionResultType.FAIL;
			}
			if (valid) {
				CompoundNBT tag = context.getItem().getOrCreateTag();
				BlockPos first = null;
				if (tag.contains(selectTag)) {
					first = NBTUtil.readBlockPos(tag.getCompound(selectTag));
					tag.remove(selectTag);
				}
				else {
					tag.put(selectTag, NBTUtil.writeBlockPos(pos));
				}
				context.getItem().setTag(tag);

				if (first != null && !first.equals(pos)) {
					// do connection check
					Vec3i diff = pos.subtract(first);
					player.sendMessage(new StringTextComponent(diff.toString()));
					// straight line or 45* diagonal
					if (diff.getX()==0 || diff.getZ()==0 || Math.abs(diff.getX())==Math.abs(diff.getZ())) {
						float slope = diff.getY()/(float)Math.abs(diff.getX()==0?diff.getZ():diff.getX());
						player.sendMessage(new StringTextComponent(MSG_VALID + String.format("%.2f.",slope)));
						context.getItem().setTag(null);
						// this is where we'd fire the event to build a track connection...
						// TODO: fire track path connection event stuff
						tryToPlaceTracks(player, first, pos);
						return ActionResultType.SUCCESS;
					}
					else {
						player.sendMessage(new StringTextComponent(MSG_INVALID));
						return ActionResultType.SUCCESS;
					}
				}
			}
		}
		// else player is null
		return super.onItemUse(context);
	}

	private boolean tryToPlaceTracks (PlayerEntity player, BlockPos start, BlockPos end) {
		if (player.world.isRemote || start.equals(end)) return false;

		// let's figure out the direction to iterate
		Vec3i delta = end.subtract(start);

		if (Math.abs(delta.getX()) + Math.abs(delta.getZ()) > MAX_TRACK_SEG_LEN) {
			player.sendMessage(new StringTextComponent(MSG_TOOLONG));
			return false;
		}
		int stepX = delta.getX()==0 ? 0 : Math.abs(delta.getX())/delta.getX();
		int stepZ = delta.getZ()==0 ? 0 : Math.abs(delta.getZ())/delta.getZ();
		int slot  = 0;
		ItemStack stack = null;
		boolean zig = (stepZ==0); // zig = stepX, zag = stepZ
		BlockPos step = new BlockPos(start.getX(), start.getY(), start.getZ());

		// now it's time to iterate
		while (!step.equals(end)) {
			// make sure we have tracks available
			if ((stack == null || stack.isEmpty()) && !player.isCreative()) {
				while (slot < player.inventory.getSizeInventory()) {
					ItemStack check = player.inventory.getStackInSlot(slot);
					if (ItemTags.getCollection().getOrCreate(ItemTags.RAILS.getId()).contains(check.getItem())) {
						stack = check;
						break;
					}
					slot++;
				}
			}

			// check if we're making a valid path
			// logic:
			// if next block is air:
			//   start checking down. if next down block is air:
			//     if next down-2 block is NOT air:
			//       valid.
			// else check up:
			//   if next up block is air:
			//     valid
			boolean valid = false;

			if (player.world.getBlockState(step).getBlock() instanceof WayPointBlock) {
				valid = true;
				if (!player.isCreative()) {
					player.world.addEntity(
					  new ItemEntity(player.world, step.getX(), step.getY(), step.getZ(),
					    new ItemStack(ModSetup.R_BLOCK_WAYPOINT.get(), 1))
					);
				}
			}
			else if (player.world.isAirBlock(step)) {
				if (player.world.isAirBlock(step.add(0,-1,0))) {
					if (!player.world.isAirBlock(step.add(0,-2,0))) {
						valid = true;
						step = step.add(0,-1,0);
					}
				}
				else valid = true;
			}
			else if (player.world.isAirBlock(step.add(0,1,0))) {
				valid = true;
				step = step.add(0,1,0);
			}
			// validation done, get a rail from the player's inventory
			if (valid) {
				// we got a rail, let's place it
				if (player.isCreative() || stack.getCount() > 0) {
					player.world.setBlockState(step, Blocks.RAIL.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
					if (!player.isCreative()) stack.setCount(stack.getCount() - 1);
				}
			}
			// escape if we can't get any more
			if (!valid || (slot == player.inventory.getSizeInventory() && stack.isEmpty())) break;

			// otherwise step to the next iteration
			step = step.add(zig ? stepX : 0, 0, zig ? 0 : stepZ);
			if (Math.abs(stepX) == Math.abs(stepZ)) zig = !zig;
		}
		return step.equals(end); // false if we failed early for whatever reason
	}
}
