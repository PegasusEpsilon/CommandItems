package pegasusepsilon.commanditems;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.client.event.MouseEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import net.minecraft.item.ItemStack;

import net.minecraft.nbt.NBTTagCompound;

//import net.minecraft.nbt.NBTTagString;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

@SideOnly(Side.CLIENT) @Mod.EventBusSubscriber public class MouseEventHandler {
	private static boolean triggerMethod (
		EntityPlayerSP player,
		EnumHand hand,
		int method,
		RayTraceResult.Type target
	) {
		player.sendChatMessage(
			"/" + Core.CMDNAME + " " + hand + " " +
			Core.methods[method] + " " + target.toString()
		);
		return true;
	}

	private static boolean clickActivate (
		EntityPlayerSP player, EnumHand hand, int method
	) {
		// {
		//   commandItem: {
		//     useCommands: {
		//       ENTITY: [ "...", "..." ],
		//       BLOCK: [ "...", "..." ],
		//       MISS: [ "...", "..." ]
		//     },
		//     digCommands: {
		//       ENTITY: [ "...", "..." ],
		//       BLOCK: [ "...", "..." ],
		//       MISS: [ "...", "..." ]
		//     }
		//   }
		// }
		Minecraft mc = Minecraft.getMinecraft();

		Core.debug("Hand: {}", hand);

		ItemStack item = player.getHeldItem(hand);
		NBTTagCompound tag = item.getTagCompound();

		if (null == tag) {
			Core.debug("{} item has no tag.", hand);
			return false;
		}

		Core.debug("tag: {}", tag);

		NBTTagCompound targets;
		try {
			targets = player.getHeldItem(hand)
			.getTagCompound().getCompoundTag("commandItem");
		} catch (NullPointerException e) {
			Core.debug("{} item is not a commandItem.", hand);
			return false;
		}

		Core.debug("all methods: {}", targets);
		Core.debug("method: {}", Core.methods[method]);

		if (!targets.hasKey(Core.methods[method])) {
			Core.debug("{} item has no {} targets.", hand, Core.methods[method]);
			return false;
		}

		targets = targets.getCompoundTag(Core.methods[method]);

		Core.debug("objectMouseOver: {}", mc.objectMouseOver.toString());
		Core.debug("typeOfHit: {}", mc.objectMouseOver.typeOfHit);

		if (player.isSneaking() && targets.hasKey("MISS"))
			return triggerMethod(player, hand, method, RayTraceResult.Type.MISS);

		if (targets.hasKey(mc.objectMouseOver.typeOfHit.toString()))
			return triggerMethod(player, hand, method, mc.objectMouseOver.typeOfHit);

		if (targets.hasKey("MISS"))
			return triggerMethod(player, hand, method, RayTraceResult.Type.MISS);

		return false;
	}

	@SubscribeEvent public static void mouseEventHandler (MouseEvent event) {
		// make sure the event is use of a hand
		int button = event.getButton();

		// from more likely to least likely
		// anything that isn't a mouse click event (button > 0)
		// anything that isn't a mouse button down event (MouseEvent#isButtonstate = true)
		// special mouse click events (middle click, button = 2, higher values for other buttons)
		if (0 > button || !event.isButtonstate() || 1 < button) return;

		// make sure there is an item, with appropriate commandItem NBT data,
		// in the appropriate hand, for the observed click,
		// using vanilla precedence:
		// * prefer main-hand use over off-hand use
		// * dig is only valid for main-hand
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		// successful main-hand use cancels off-hand use attempt
		if (clickActivate(player, EnumHand.MAIN_HAND, button)) return;
		// off-hand may not dig, only use.
		if (0 == button) return;
		clickActivate(player, EnumHand.OFF_HAND, button);

		// note that you can still activate the off-hand item's dig command
		// with /commanditem digCommand OFF_HAND, but it is not click-activate-able.
	}
}
