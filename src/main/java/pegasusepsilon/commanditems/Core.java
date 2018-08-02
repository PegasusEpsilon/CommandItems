package pegasusepsilon.commanditems;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

@Mod(modid = Core.MODID, name = Core.NAME, version = Core.VERSION)
public class Core {
	public static final String MODID = "commanditems";
	public static final String NAME = "Command Items";
	public static final String VERSION = "1.12.2.a8";
	static final String CMDNAME = "commanditem";
	static final String CMDFLAG = "commandItem";
	static final String CMDFIELD = "command";
	static final CommandHandler commandHandler = new CommandHandler();
	static final String[] methods = { "hitCommands", "useCommands" };

	private static Map<String, EnumHand> initHands() {
		Map<String, EnumHand> hands = new HashMap<String, EnumHand>();
		hands.put("MAIN_HAND", EnumHand.MAIN_HAND);
		hands.put("OFF_HAND", EnumHand.OFF_HAND);
		return hands;
	};
	static final Map<String, EnumHand> hands = initHands();

	private static Map<String, RayTraceResult.Type> initHitTypes() {
		Map<String, RayTraceResult.Type> hitTypes = new HashMap<String, RayTraceResult.Type>();
		hitTypes.put("MISS", RayTraceResult.Type.MISS);
		hitTypes.put("BLOCK", RayTraceResult.Type.BLOCK);
		hitTypes.put("ENTITY", RayTraceResult.Type.ENTITY);
		return hitTypes;
	};
	static final Map<String, RayTraceResult.Type> hitTypes = initHitTypes();

	private static Logger logger;
	static void debug (String message, Object... params) {
		// DEBUG
		logger.info(message, params);
	}

	private void mcModInfo (FMLPreInitializationEvent event) {
		ModMetadata data = event.getModMetadata();
		data.modId = MODID;
		data.name = NAME;
		data.version = VERSION;
		data.description = "Items that run commands, from NBT, upon use";
		data.url = "http://pegasus.pimpninjas.org/minecraft/commanditems/";
		String author = "Pegasus Epsion <pegasus@pimpninjas.org>";
		data.authorList.add(author);
		data.credits = author;
		//data.logoFile = "";
		//data.screenshots[0] = "";
	}

	@EventHandler
	public void preInit (FMLPreInitializationEvent event) {
		mcModInfo(event);
		logger = event.getModLog();
		logger.info("{} {} preInit", NAME, VERSION);
	}

	@EventHandler
	public void init (FMLInitializationEvent event) {
		logger.info("{} {} init", NAME, VERSION);
	}

	@EventHandler
	public void postInit (FMLPostInitializationEvent event) {
		logger.info("{} {} postInit", NAME, VERSION);
	}

	@EventHandler
	public void serverStarting (FMLServerStartingEvent event) {
		logger.info("{} {} serverStarting", NAME, VERSION);
		event.registerServerCommand(commandHandler);
		logger.info("{} {} Command registration ({}) complete.", NAME, VERSION, CMDNAME);
	}
}
