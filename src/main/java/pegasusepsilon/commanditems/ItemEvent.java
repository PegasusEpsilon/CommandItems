package pegasusepsilon.commanditems;

import pegasusepsilon.commanditems.Core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.client.event.MouseEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber
public class ItemEvent {
	@SubscribeEvent
	public static void mouseEvent (MouseEvent event) {
		// make sure the event is use of a hand
		int button = event.getButton();
		boolean state = event.isButtonstate();
		if (0 > button || !state || 1 < button) return;

		// make sure there is an item with NBT data
		// with a set commandItem flag
		// in the appropriate hand
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		NBTTagCompound tag = player.getHeldItem(Core.hands[button]).getTagCompound();
		if (null == tag || !tag.getBoolean("commandItem")) return;

		// tell the server to use the item
		player.sendChatMessage("/"+Core.CMDNAME+" "+button);
	}
}
