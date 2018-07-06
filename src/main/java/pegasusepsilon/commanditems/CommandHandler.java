package pegasusepsilon.commanditems;

import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import net.minecraft.nbt.NBTTagCompound;

import pegasusepsilon.commanditems.Core;

public class CommandHandler extends CommandBase {
	@Override public String getName () { return Core.CMDNAME; }

	@Override public String getUsage (ICommandSender sender) {
		return "Uses held command item in main hand (0) or off-hand (1)";
	}

	@Override public int getRequiredPermissionLevel () {
		return 0;
	}

	@Override public void execute (
		MinecraftServer server, ICommandSender player, String[] args
	) throws CommandException {
		if (!(player instanceof EntityPlayer)) return;
		NBTTagCompound tag = ((EntityPlayer) player).getHeldItem(Core.hands[Integer.parseInt(args[0])]).getTagCompound();
		if (!tag.getBoolean(Core.CMDFLAG)) return;
		server.getCommandManager().executeCommand(server, tag.getString(Core.CMDFIELD));
		Core.logger.info("Command authorized!");
	}
}
