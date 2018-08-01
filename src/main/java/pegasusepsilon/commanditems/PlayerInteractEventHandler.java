package pegasusepsilon.commanditems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.player.PlayerEvent; // does NOT provide .getHand()
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent; // provides .getHand()
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
// this one works as documented - only item-in-hand clicks trigger
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
// this one actually doesn't -- ALL clicks trigger.
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
/*
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
*/
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Mod.EventBusSubscriber(modid = Core.MODID)
public class PlayerInteractEventHandler {
	// SERVER SIDE - ACTIVATES COMMAND DIRECTLY
	private static void serverTriggerEntity (PlayerEvent event, EnumHand hand, String method, String target) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		Core.debug("EntityInteractEvent");
		CommandHandler.activateItem(player, hand, method, "ENTITY", target);
	}

	// BOTH SIDES fires on all entity attack/destroy
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (AttackEntityEvent event) {
		serverTriggerEntity(event, EnumHand.MAIN_HAND, "digCommands", event.getTarget().getUniqueID().toString());
	}

	// BOTH SIDES fires on all entity use/place
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (EntityInteract event) {
		serverTriggerEntity(event, event.getHand(), "useCommands", event.getTarget().getUniqueID().toString());
	}

	// SERVER SIDE - ACTIVATES COMMAND DIRECTLY
	private static void serverTriggerBlock (PlayerInteractEvent event, String method) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		BlockPos t = event.getPos();
		String target = t.getX() + " " + t.getY() + " " + t.getZ();
		Core.debug("PlayerInteractEvent: {} {} {}", player.getDisplayNameString(), method, target);
		Core.commandHandler.activateItem(player, event.getHand(), method, "BLOCK", target);
	}

	// BOTH SIDES fires on all block attack/destroy
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (LeftClickBlock event) {
		serverTriggerBlock((PlayerInteractEvent)event, "digCommands");
	}

	// BOTH SIDES fires on all block use/place
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (RightClickBlock event) {
		serverTriggerBlock((PlayerInteractEvent)event, "useCommands");
	}

	// CLIENT SIDE -- ACTIVATES COMMAND THROUGH CHAT
	private static void clientTriggerMiss (PlayerInteractEvent event, String method) {
		EntityPlayer player = event.getEntityPlayer();
		if (!(player instanceof EntityPlayerSP)) {
			Core.debug("invalid entity for sending commands...");
			return;
		}
		String hand = event.getHand().toString();
		Core.debug("CommandItem Client passing event ({} {} {}) to server...", hand, method, "MISS");
		((EntityPlayerSP)player).sendChatMessage(
			"/" + Core.CMDNAME + " " + hand + " " + method + " MISS"
		);
	}

	// CLIENTSIDE fires on all attack/destroy on empty space, with or without a held item, main hand only
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (LeftClickEmpty event) {
		Core.debug("PlayerInteractEvent.LeftClickEmpty");
		clientTriggerMiss(event, "digCommands");
	}

	// CLIENTSIDE fires on all use/place on empty space, with a held item, for both hands
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (RightClickItem event) {
		Core.debug("PlayerInteractEvent.RightClickItem");
		clientTriggerMiss(event, "useCommands");
	}
}
