package com.railwayteam.railways.items;

import com.railwayteam.railways.blocks.WayPointBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class WayPointToolItem extends Item{
	public static final String name      = "waypoint_manager";
	public static final String selectTag = "FirstPoint";

	private static String MSG_RESET   = "Selection reset.";
	private static String MSG_VALID   = "Valid connection with slope: "; // Y-delta is appended to this
	private static String MSG_INVALID = "Invalid connection.";
	
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
}
