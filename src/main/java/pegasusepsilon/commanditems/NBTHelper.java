package pegasusepsilon.commanditems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.util.Constants;

class NBTHelper {
	static NBTTagCompound getTypesForMethod (ItemStack item, int method) {
		NBTTagCompound tag;
		try { tag = item.getTagCompound().getCompoundTag("commandItem"); }
		catch (NullPointerException e) { return null; }
		if (!tag.hasKey(Core.methods[method])) return null;
		return tag.getCompoundTag(Core.methods[method]);
	}

	static boolean hasActivator (ItemStack item, int method, RayTraceResult.Type type) {
		try { return getTypesForMethod(item, method).hasKey(type.toString()); }
		catch (NullPointerException e) { return false; }
	}

	static NBTTagList getCommandList (NBTTagCompound tag, RayTraceResult.Type type) {
		try { return tag.getTagList(type.toString(), Constants.NBT.TAG_STRING); }
		catch (NullPointerException e) { return null; }
	}

	static NBTTagList getCommandList (ItemStack item, int method, RayTraceResult.Type type) {
		return getCommandList(getTypesForMethod(item, method), type);
	}

	static NBTTagList getCommandList (EntityPlayer player, EnumHand hand, int method, RayTraceResult.Type type) {
		try { return getCommandList(player.getHeldItem(hand), method, type); }
		catch (NullPointerException e) { return null; }
	}

	static boolean isCommandItem (ItemStack item) {
		try { NBTTagCompound tag = item.getTagCompound().getCompoundTag("commandItem"); }
		catch (NullPointerException e) { return false; }
		return true;
	}

	static boolean isCommandItem (EntityPlayer player, EnumHand hand) {
		return isCommandItem(player.getHeldItem(hand));
	}
}
