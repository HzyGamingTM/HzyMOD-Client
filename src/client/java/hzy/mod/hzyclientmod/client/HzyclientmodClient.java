package hzy.mod.hzyclientmod.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HzyclientmodClient implements ClientModInitializer {
	public static Logger LOGGER;
	public static final String VERSION = "v1.0.0";

	@Override
	public void onInitializeClient() {
		LOGGER = LoggerFactory.getLogger("hzy_mod");
		LOGGER.info("Initialising HzyMOD {}", VERSION);

		LOGGER.info("Initialising Commands");
		CommandHandler.init();
	}
}
