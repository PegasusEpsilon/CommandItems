package pegasusepsilon.commanditems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Mod.EventBusSubscriber(modid = Core.MODID)
public class PlayerInteractEventHandler {
	// this is dumb, there should be a way to construct an interface that covers both PlayerEvent and AttackEntityEvent
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
		/*
		if (player.world.isRemote) return;
		Core.debug("AttackEntityEvent");
		EntityPlayer player = event.getEntityPlayer();
		CommandHandler.activateItem(player, EnumHand.MAIN_HAND, "digCommands", "ENTITY", event.getTarget().getUniqueID().toString());
		*/
	}

	// BOTH SIDES fires on all entity use/place
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (EntityInteract event) {
		serverTriggerEntity(event, event.getHand(), "useCommands", event.getTarget().getUniqueID().toString());
		/*
		if (player.world.isRemote) return;
		Core.debug("PlayerInteractEvent.EntityInteract");
		EntityPlayer player = event.getEntityPlayer();
		CommandHandler.activateItem(player, event.getHand(), "useCommands", "ENTITY", event.getTarget().getUniqueID().toString());
		*/
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
		/*
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		Core.debug("PlayerInteractEvent.RightClickBlock");
		BlockPos t = event.getPos();
		String target = t.getX() + " " + t.getY() + " " + t.getZ();
		Core.debug("target: {}", target);
		CommandHandler.activateItem(player, event.getHand(), "useCommands", "BLOCK", target);
		*/
		serverTriggerBlock((PlayerInteractEvent)event, "useCommands");
	}

	// CLIENT SIDE -- ACTIVATES COMMAND THROUGH CHAT
	//private static void clientTriggerMethod (EntityPlayer player, EnumHand hand, String method, String target) {
	private static void clientTriggerMethod (PlayerInteractEvent event, String method, String target) {
		EntityPlayer player = event.getEntityPlayer();
		if (!(player instanceof EntityPlayerSP)) {
			Core.debug("invalid entity for sending commands...");
			return;
		}
		String hand = event.getHand().toString();
		Core.debug("CommandItem Client passing event ({} {} {}) to server...", hand, method, target);
		((EntityPlayerSP)player).sendChatMessage(
			"/" + Core.CMDNAME + " " + hand + " " + method + " " + target
		);
	}

	// CLIENTSIDE fires on all attack/destroy on empty space, main hand only
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (LeftClickEmpty event) {
		Core.debug("PlayerInteractEvent.LeftClickEmpty");
		//clientTriggerMethod(event.getEntityPlayer(), event.getHand(), "digCommands", "MISS");
		clientTriggerMethod((PlayerInteractEvent)event, "digCommands", "MISS");
	}

	// CLIENTSIDE fires on all use/place on empty space, for both hands
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (RightClickEmpty event) {
		Core.debug("PlayerInteractEvent.RightClickEmpty {}");
		//clientTriggerMethod(event.getEntityPlayer(), event.getHand(), "useCommands", "MISS");
		clientTriggerMethod((PlayerInteractEvent)event, "useCommands", "MISS");
	}
}
