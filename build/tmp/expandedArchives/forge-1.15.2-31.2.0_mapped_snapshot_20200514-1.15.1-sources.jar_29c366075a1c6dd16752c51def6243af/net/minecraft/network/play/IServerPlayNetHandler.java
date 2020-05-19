package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;

public interface IServerPlayNetHandler extends INetHandler {
   void handleAnimation(CAnimateHandPacket packetIn);

   /**
    * Process chat messages (broadcast back to clients) and commands (executes)
    */
   void processChatMessage(CChatMessagePacket packetIn);

   /**
    * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or acquiring
    * 'open inventory' achievement
    */
   void processClientStatus(CClientStatusPacket packetIn);

   /**
    * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
    * and whether to show the cape
    */
   void processClientSettings(CClientSettingsPacket packetIn);

   /**
    * Received in response to the server requesting to confirm that the client-side open container matches the servers'
    * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
    * contents
    */
   void processConfirmTransaction(CConfirmTransactionPacket packetIn);

   /**
    * Enchants the item identified by the packet given some convoluted conditions (matching window, which
    * should/shouldn't be in use?)
    */
   void processEnchantItem(CEnchantItemPacket packetIn);

   /**
    * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
    * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
    * the same open container/inventory
    */
   void processClickWindow(CClickWindowPacket packetIn);

   void processPlaceRecipe(CPlaceRecipePacket packetIn);

   /**
    * Processes the client closing windows (container)
    */
   void processCloseWindow(CCloseWindowPacket packetIn);

   /**
    * Synchronizes serverside and clientside book contents and signing
    */
   void processCustomPayload(CCustomPayloadPacket packetIn);

   /**
    * Processes left and right clicks on entities
    */
   void processUseEntity(CUseEntityPacket packetIn);

   /**
    * Updates a players' ping statistics
    */
   void processKeepAlive(CKeepAlivePacket packetIn);

   /**
    * Processes clients perspective on player positioning and/or orientation
    */
   void processPlayer(CPlayerPacket packetIn);

   /**
    * Processes a player starting/stopping flying
    */
   void processPlayerAbilities(CPlayerAbilitiesPacket packetIn);

   /**
    * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items
    */
   void processPlayerDigging(CPlayerDiggingPacket packetIn);

   /**
    * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
    * height of the horse the player is riding
    */
   void processEntityAction(CEntityActionPacket packetIn);

   /**
    * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
    * flying/sprinting
    */
   void processInput(CInputPacket packetIn);

   /**
    * Updates which quickbar slot is selected
    */
   void processHeldItemChange(CHeldItemChangePacket packetIn);

   /**
    * Update the server with an ItemStack in a slot.
    */
   void processCreativeInventoryAction(CCreativeInventoryActionPacket packetIn);

   void processUpdateSign(CUpdateSignPacket packetIn);

   void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket packetIn);

   /**
    * Called when a client is using an item while not pointing at a block, but simply using an item
    */
   void processTryUseItem(CPlayerTryUseItemPacket packetIn);

   void handleSpectate(CSpectatePacket packetIn);

   void handleResourcePackStatus(CResourcePackStatusPacket packetIn);

   void processSteerBoat(CSteerBoatPacket packetIn);

   void processVehicleMove(CMoveVehiclePacket packetIn);

   void processConfirmTeleport(CConfirmTeleportPacket packetIn);

   void handleRecipeBookUpdate(CRecipeInfoPacket packetIn);

   void handleSeenAdvancements(CSeenAdvancementsPacket packetIn);

   /**
    * This method is only called for manual tab-completion (the {@link
    * net.minecraft.command.arguments.SuggestionProviders#ASK_SERVER minecraft:ask_server} suggestion provider).
    */
   void processTabComplete(CTabCompletePacket packetIn);

   void processUpdateCommandBlock(CUpdateCommandBlockPacket packetIn);

   void processUpdateCommandMinecart(CUpdateMinecartCommandBlockPacket packetIn);

   void processPickItem(CPickItemPacket packetIn);

   void processRenameItem(CRenameItemPacket packetIn);

   void processUpdateBeacon(CUpdateBeaconPacket packetIn);

   void processUpdateStructureBlock(CUpdateStructureBlockPacket packetIn);

   void processSelectTrade(CSelectTradePacket packetIn);

   void processEditBook(CEditBookPacket packetIn);

   void processNBTQueryEntity(CQueryEntityNBTPacket packetIn);

   void processNBTQueryBlockEntity(CQueryTileEntityNBTPacket packetIn);

   void func_217262_a(CUpdateJigsawBlockPacket p_217262_1_);

   void func_217263_a(CSetDifficultyPacket p_217263_1_);

   void func_217261_a(CLockDifficultyPacket p_217261_1_);
}