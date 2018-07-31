package pegasusepsilon.commanditems;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.minecraft.util.text.ITextComponent;

import net.minecraft.util.EnumHand;
import net.minecraft.util.EntitySelectors;

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
		return "/" + Core.CMDNAME + "<digCommands/useCommands> <ENTITY/BLOCK/MISS> Activates useCommand or digCommand bound to held CommandItem";
	}

	@Override public boolean checkPermission (MinecraftServer x, ICommandSender y) { return true; }

	// server-side entity raytracing.
	private static Entity findTargetedEntity (EntityPlayer player) {
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();

		Vec3d start = player.getPositionEyes(1.0F);
		Vec3d look = player.getLook(1.0F);
		Vec3d end = start.addVector(look.x * reach, look.y * reach, look.z * reach);

		Entity target = null;
		List<Entity> entities = player.world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(start, end), EntitySelectors.NOT_SPECTATING);

		double d0 = 0.0D;

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			Core.debug("Entity: {}", entity);

			double magic = 0.30000001192092896D; // wtf magic number
			AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(magic, magic, magic);
			RayTraceResult result = aabb.calculateIntercept(start, end);

			if (result != null) {
				double d1 = start.squareDistanceTo(result.hitVec);
				if (d1 < d0 || d0 == 0.0D) {
					target = entity;
					d0 = d1;
				}
			}
		}
		return target;
	}

	static NBTTagList getCommandList (EntityPlayer player, EnumHand hand, String method, String type) {
		NBTTagList commands;
		try {
			NBTTagCompound tag;
			Core.debug("hand: {}", hand);
			tag = ((EntityPlayer) player).getHeldItem(hand).getTagCompound();
			Core.debug("tag: {}", tag.toString());
			Core.debug("method: {}", method);
			tag = tag.getCompoundTag("commandItem").getCompoundTag(method);
			Core.debug("tag: {}", tag.toString());
			Core.debug("type: {}", type);
			commands = tag.getTagList(type, Constants.NBT.TAG_STRING);
			Core.debug("commands: {}", commands);
			return commands;
		} catch (NullPointerException e) {
			Core.debug("Invalid commandItem");
			return null;
		}
	}

	static void activateItem (EntityPlayer player, EnumHand hand, String method, String type, String target) {
		NBTTagList commands = getCommandList(player, hand, method, type);
		if (null != commands) try {
			activateItem(player, commands, target);
		} catch (NullPointerException e) {
			Core.debug("Everything's fucked: {}", e);
		}
	}

	private static void activateItem (EntityPlayer player, NBTTagList commands, String target) {
		if (null == commands) return;
		Core.debug("ACTIVATING ITEM");
		Core.debug("Player: {}", player.getDisplayNameString());
		Core.debug("Commands: {}", commands);
		Core.debug("Target: {}", target);
		World world = player.getEntityWorld();
		Core.debug("World: {}", world);
		MinecraftServer server = world.getMinecraftServer();
		Core.debug("Server: {}", server);

		// create an admin sockpuppet for the player
		ICommandSender admin = new ICommandSender() {
			public String getName () { return player.getName(); }
			public ITextComponent getDisplayName () { return player.getDisplayName(); }
			public void sendMessage (ITextComponent x) { player.sendMessage(x); }
			public boolean canUseCommand (int x, String y) {
				Core.debug("Called with permission level {} and command name {}", x, y);
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
			Core.debug("Parsing command {}", command);
			if (null != target) command = command.replace("$TARGET$", target);
			Core.debug("Running command {}", command);
			if (0 == commandManager.executeCommand(admin, command)) {
				Core.debug("Command failed!");
				return;
			}
		}
	}

	@Override public void execute (
		MinecraftServer server, ICommandSender player, String[] args
	) throws CommandException {
		if (
			args.length != 3 ||
			!Core.hands.containsKey(args[0]) ||
			!Arrays.asList(Core.methods).contains(args[1]) ||
			!Core.hitTypes.containsKey(args[2])
		) throw new WrongUsageException("commands." + Core.CMDNAME + ".usage", new Object[0]);

		Core.debug("Command Item triggered: {} {}", ((EntityPlayer)player).getDisplayNameString(), args);

		if (!(player instanceof EntityPlayer)) return;

		String target
			= args[2].equals("ENTITY") ? findTargetedEntity((EntityPlayer)player).getUniqueID().toString()
			: args[2].equals("BLOCK") ? null // TODO: block targeting
			: null;
		Core.debug("target: {}", target);

		//NBTTagList commands = getCommandList((EntityPlayer)player, Core.hands.get(args[0]), args[1], args[2]);
		activateItem((EntityPlayer)player, Core.hands.get(args[0]), args[1], args[2], target);

		Core.debug("Command succeeded!");
	}
}
