package pegasusepsilon.commanditems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;

import net.minecraftforge.fml.common.ModMetadata;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import org.apache.logging.log4j.Logger;

@Mod(modid = Core.MODID, name = Core.NAME, version = Core.VERSION)
public class Core {
	public static final String MODID = "commanditems";
	public static final String NAME = "Command Items";
	public static final String VERSION = "1.12.2.0";

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
}
