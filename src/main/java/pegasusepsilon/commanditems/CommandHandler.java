package pegasusepsilon.commanditems;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraft.world.World;

import net.minecraft.server.MinecraftServer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

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

public class CommandHandler extends CommandBase {
	@Override public String getName () { return Core.CMDNAME; }

	@Override public String getUsage (ICommandSender sender) {
		return "Activates useCommand or digCommand bound to item in MAIN_HAND or OFF_HAND";
	}

	@Override public boolean checkPermission (MinecraftServer x, ICommandSender y) { return true; }

	@Override public void execute (
		MinecraftServer server, ICommandSender player, String[] args
	) throws CommandException {
		// args[0] = MAIN_HAND/OFF_HAND
		// args[1] = digCommands/useCommands
		// args[2] = MISS/BLOCK/ENTITY
		if (
			args.length != 3 ||
			!Core.hands.containsKey(args[0]) ||
			!Arrays.asList(Core.methods).contains(args[1]) ||
			!Core.hitTypes.containsKey(args[2])
		) throw new WrongUsageException("commands." + Core.CMDNAME + ".usage", new Object[0]);

		Core.debug("Command Item triggered: {}", (Object[])args);

		if (!(player instanceof EntityPlayer)) return;

		NBTTagList commands;
		try {
			NBTTagCompound tag;
			Core.debug("hand: {}", args[0]);
			tag = ((EntityPlayer) player).getHeldItem(Core.hands.get(args[0])).getTagCompound();
			Core.debug("tag: {}", tag.toString());
			Core.debug("method: {}", args[1]);
			tag = tag.getCompoundTag("commandItem").getCompoundTag(args[1]);
			Core.debug("tag: {}", tag.toString());
			Core.debug("target: {}", args[2]);
			commands = tag.getTagList(args[2], Constants.NBT.TAG_STRING);
			Core.debug("commands: {}", commands);
		} catch (NullPointerException e) {
			Core.debug("Invalid commandItem");
			return;
		}

		// create an admin sockpuppet for the player
		ICommandSender admin = new ICommandSender() {
			public String getName () { return player.getName(); }
			public ITextComponent getDisplayName () { return player.getDisplayName(); }
			public void sendMessage (ITextComponent x) { player.sendMessage(x); }
			public boolean canUseCommand (int x, String y) {
				Core.debug("Called with permission level", x, "and player name", y);
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

		for (NBTBase tagBase : commands) {
			String command;
			try {
				command = ((NBTTagString) tagBase).getString();
			} catch (Exception e) {
				Core.debug("Exception caught while converting NBTBase to String");
				return;
			}
			Core.debug("Running command {}", command);
			if (0 == commandManager.executeCommand(admin, command)) {
				Core.debug("Command failed!");
				return;
			}
		}

		Core.debug("Command succeeded!");
	}
}
