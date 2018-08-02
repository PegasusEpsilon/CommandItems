package pegasusepsilon.commanditems;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.List;

public class CommandHandler extends CommandBase {
	@Override public String getName () { return Core.CMDNAME; }

	@Override public String getUsage (ICommandSender sender) {
		return "/" + Core.CMDNAME + "<MAIN_HAND/OFF_HAND> <hitCommands/useCommands> <ENTITY/BLOCK/MISS> Activates useCommand or digCommand bound to held CommandItem";
	}

	@Override public boolean checkPermission (MinecraftServer x, ICommandSender y) { return true; }

	static NBTTagList getCommandList (EntityPlayer player, EnumHand hand, String method, String type) {
		NBTTagList commands;
		try {
			return ((EntityPlayer)player).getHeldItem(hand).getTagCompound()
				.getCompoundTag("commandItem").getCompoundTag(method)
				.getTagList(type, Constants.NBT.TAG_STRING);
		} catch (NullPointerException e) {
			return null;
		}
	}

	static void activateItem (EntityPlayer player, EnumHand hand, int method, RayTraceResult.Type type, String target) {
		try {
			activateItem(player, NBTHelper.getCommandList(player, hand, method, type), target);
		} catch (NullPointerException e) {
			// d'oh
		}
	}

	private static void activateItem (EntityPlayer player, NBTTagList commands, String target) {
		if (null == commands) return;
		World world = player.getEntityWorld();
		MinecraftServer server = world.getMinecraftServer();

		// create an admin sockpuppet for the player
		ICommandSender admin = new ICommandSender() {
			public String getName () { return player.getName(); }
			public ITextComponent getDisplayName () { return player.getDisplayName(); }
			public void sendMessage (ITextComponent x) { player.sendMessage(x); }
			public boolean canUseCommand (int x, String y) {
				//Core.debug("Called with permission level {} and command name {}", x, y);
				return true;
			}
			public BlockPos getPosition () { return player.getPosition(); }
			public Vec3d getPositionVector () { return player.getPositionVector(); }
			public World getEntityWorld () { return player.getEntityWorld(); }
			public Entity getCommandSenderEntity () { return player.getCommandSenderEntity(); }
			public boolean sendCommandFeedback () { return player.sendCommandFeedback(); }
			public void setCommandStat (CommandResultStats.Type x, int y) { player.setCommandStat(x, y); }
			public MinecraftServer getServer () { return player.getServer(); }
		};

		ICommandManager commandManager = server.getCommandManager();

		for (NBTBase c : commands) {
			String command;
			try {
				command = ((NBTTagString)c).getString();
				if (null != target) command = command.replace("$TARGET$", target);
				if (0 == commandManager.executeCommand(admin, command)) {
					//Core.debug("Command failed!");
					return;
				}
			} catch (Exception e) { }
		}
	}

	@Override public void execute (
		MinecraftServer server, ICommandSender player, String[] args
	) throws CommandException {
		List methods = Arrays.asList(Core.methods);
		if (
			args.length != 3 ||
			!Core.hands.containsKey(args[0]) ||
			!methods.contains(args[1]) ||
			!Core.hitTypes.containsKey(args[2])
		) throw new WrongUsageException("commands." + Core.CMDNAME + ".usage", new Object[0]);

		if (!(player instanceof EntityPlayer)) return;

		BlockPos block = RayTraceHelper.rayTraceBlock((EntityPlayer)player);
		String target
			= args[2].equals("ENTITY") ? RayTraceHelper.rayTraceEntity((EntityPlayer)player).getUniqueID().toString()
			: args[2].equals("BLOCK") ? block.getX() + " " + block.getY() + " " + block.getZ()
			: null;

		activateItem((EntityPlayer)player, Core.hands.get(args[0]), methods.indexOf(args[1]), Core.hitTypes.get(args[2]), target);
	}
}
