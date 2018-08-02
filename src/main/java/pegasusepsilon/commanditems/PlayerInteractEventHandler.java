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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@Mod.EventBusSubscriber(modid = Core.MODID)
public class PlayerInteractEventHandler {
	// SERVER SIDE - ACTIVATE COMMAND DIRECTLY
	private static void serverTrigger (PlayerEvent event, EnumHand hand, int method, RayTraceResult.Type type, String target) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		ItemStack item = player.getHeldItem(hand);
		if ( // Don't fire the OFF_HAND activator if the MAIN_HAND has one.
			hand == EnumHand.OFF_HAND &&
			NBTHelper.hasActivator(player.getHeldItem(EnumHand.MAIN_HAND), method, type)
		) return;
		// Here be deep voodoo
		if (player.isSneaking() && NBTHelper.hasActivator(item, method, RayTraceResult.Type.MISS)) {
			if (method == 0) type = RayTraceResult.Type.MISS;
			else if (RayTraceResult.Type.MISS != type) return;
		} else {
			if (RayTraceResult.Type.MISS == type && (
				NBTHelper.hasActivator(item, method, RayTraceResult.Type.BLOCK) &&
				null != RayTraceHelper.rayTraceBlock(player) ||
				NBTHelper.hasActivator(item, method, RayTraceResult.Type.ENTITY) &&
				null != RayTraceHelper.rayTraceEntity(player)
			)) return;
		}
		Core.commandHandler.activateItem(player, hand, method, type, target);
	}

	// SERVER SIDE - ACTIVATES COMMAND DIRECTLY
	private static void serverTriggerEntity (EntityInteract event, int method) {
		serverTrigger(event, event.getHand(), method, RayTraceResult.Type.ENTITY, event.getTarget().getUniqueID().toString());
	}

	// BOTH SIDES fires on all entity attack/destroy
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (AttackEntityEvent event) {
		// EntityInteract has .getTarget() and .getHand()
		// AttackEntityEvent has .getTarget(), but no .getHand()
		// their shared ancestor, PlayerEvent, has neither.
		// best thing we can do is construct a new EntityInteract event here.
		serverTriggerEntity(new EntityInteract(
			event.getEntityPlayer(), EnumHand.MAIN_HAND, event.getTarget()
		), 0);
	}

	// BOTH SIDES fires on all entity use/place
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (EntityInteract event) {
		serverTriggerEntity(event, 1);
	}

	// SERVER SIDE - ACTIVATES COMMAND DIRECTLY
	private static void serverTriggerBlock (PlayerInteractEvent event, int method) {
		BlockPos t = event.getPos();
		serverTrigger((PlayerEvent)event, event.getHand(), method, RayTraceResult.Type.BLOCK, t.getX() + " " + t.getY() + " " + t.getZ());
	}

	// BOTH SIDES fires on all block attack/destroy
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (LeftClickBlock event) {
		serverTriggerBlock((PlayerInteractEvent)event, 0);
	}

	// BOTH SIDES fires on all block use/place
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (RightClickBlock event) {
		serverTriggerBlock((PlayerInteractEvent)event, 1);
	}

	// BOTH SIDES fires on all use/place on empty space, with a held item, for both hands
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (RightClickItem event) {
		serverTrigger(event, event.getHand(), 1, RayTraceResult.Type.MISS, null);
	}

	// CLIENTSIDE fires on all attack/destroy on empty space, with or without a held item, main hand only
	// ACTIVATES COMMAND THROUGH CHAT
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void playerInteractEventHandler (LeftClickEmpty event) {
		EntityPlayer player = event.getEntityPlayer();
		if (!(player instanceof EntityPlayerSP)) return;
		if (!NBTHelper.isCommandItem(player, EnumHand.MAIN_HAND)) return;
		((EntityPlayerSP)player).sendChatMessage(
			"/" + Core.CMDNAME + " MAIN_HAND hitCommands MISS"
		);
	}

}
