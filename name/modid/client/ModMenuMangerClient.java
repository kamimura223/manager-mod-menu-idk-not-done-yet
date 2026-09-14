package name.modid.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuMangerClient implements ClientModInitializer, ModMenuApi {
   public void onInitializeClient() {
   }

   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return ModManagerScreen::new;
   }
}
