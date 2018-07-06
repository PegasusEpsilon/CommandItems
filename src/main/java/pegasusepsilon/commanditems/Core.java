package pegasusepsilon.commanditems;

import net.minecraft.util.EnumHand;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import pegasusepsilon.commanditems.CommandHandler;

@Mod(modid = Core.MODID, name = Core.NAME, version = Core.VERSION)
public class Core {
	public static final String MODID = "commanditems";
	public static final String NAME = "Command Items";
	public static final String VERSION = "1.12.2.0";
	public static final String CMDNAME = "usecommanditem";
	public static final String CMDFLAG = "commandItem";
	public static final String CMDFIELD = "command";
	// You don't want to cast int to enum? Fine, we'll do it the hard way.
	public static final EnumHand[] hands = { EnumHand.MAIN_HAND, EnumHand.OFF_HAND };

	static Logger logger;

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
		event.registerServerCommand(new CommandHandler());
		logger.info("{} {} Command registration ({}) complete.", NAME, VERSION, CMDNAME);
	}
}
