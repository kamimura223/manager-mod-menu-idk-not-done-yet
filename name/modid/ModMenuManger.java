package name.modid;

import net.fabricmc.api.ModInitializer;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModMenuManger implements ModInitializer {
   public static final String MOD_ID = "mod-menu-manger";
   public static final Logger LOGGER = LoggerFactory.getLogger("mod-menu-manger");

   public void onInitialize() {
      LOGGER.info("Hello Fabric world!");
   }

   public static class_2960 id(String path) {
      return class_2960.method_60655("mod-menu-manger", path);
   }
}
