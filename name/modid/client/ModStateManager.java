package name.modid.client;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

@Environment(EnvType.CLIENT)
public class ModStateManager {
   private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir().toAbsolutePath().normalize();
   private static final Path MODS_DIR;
   private static final Path DISABLED_DIR;
   private static final Path HIDDEN_DIR;

   public static ModState getState(String modId) {
      if (findJarInDir(DISABLED_DIR, modId).isPresent()) {
         return ModStateManager.ModState.DISABLED;
      } else {
         return findJarInDir(HIDDEN_DIR, modId).isPresent() ? ModStateManager.ModState.HIDDEN : ModStateManager.ModState.ACTIVE;
      }
   }

   public static boolean canManage(String modId) {
      return resolveJarPath(modId).isPresent() || findJarInDir(DISABLED_DIR, modId).isPresent() || findJarInDir(HIDDEN_DIR, modId).isPresent();
   }

   public static boolean hide(String modId) {
      Optional<Path> jar = resolveJarPath(modId);
      return !jar.isEmpty() && ((Path)jar.get()).startsWith(MODS_DIR) ? moveFile((Path)jar.get(), HIDDEN_DIR) : false;
   }

   public static boolean disable(String modId) {
      Optional<Path> jar = resolveJarPath(modId);
      return !jar.isEmpty() && ((Path)jar.get()).startsWith(MODS_DIR) ? moveFile((Path)jar.get(), DISABLED_DIR) : false;
   }

   public static boolean reenable(String modId) {
      Optional<Path> jar = findJarInDir(DISABLED_DIR, modId);
      if (jar.isPresent()) {
         return moveFile((Path)jar.get(), MODS_DIR);
      } else {
         jar = findJarInDir(HIDDEN_DIR, modId);
         return jar.isPresent() ? moveFile((Path)jar.get(), MODS_DIR) : false;
      }
   }

   public static boolean delete(String modId) {
      Optional<Path> jar = resolveJarPath(modId);
      if (jar.isEmpty()) {
         jar = findJarInDir(DISABLED_DIR, modId);
         if (jar.isEmpty()) {
            jar = findJarInDir(HIDDEN_DIR, modId);
         }
      }

      if (jar.isPresent()) {
         try {
            Files.delete((Path)jar.get());
            return true;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return false;
   }

   private static Optional<Path> resolveJarPath(String modId) {
      Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
      if (container.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            Path root = (Path)((ModContainer)container.get()).getRootPaths().get(0);
            URI uri = root.toUri();
            String uriStr = uri.toString();
            Path jarFile;
            if (uriStr.startsWith("jar:")) {
               String filePart = uriStr.substring(4);
               int bang = filePart.indexOf(33);
               if (bang >= 0) {
                  filePart = filePart.substring(0, bang);
               }

               jarFile = Paths.get(new URI(filePart)).toAbsolutePath().normalize();
            } else {
               if (!uriStr.startsWith("file:")) {
                  return Optional.empty();
               }

               jarFile = Paths.get(uri).toAbsolutePath().normalize();
            }

            if (Files.exists(jarFile, new LinkOption[0]) && jarFile.toString().endsWith(".jar")) {
               return Optional.of(jarFile);
            }
         } catch (Exception var8) {
         }

         return findJarInDir(MODS_DIR, modId);
      }
   }

   private static Optional<Path> findJarInDir(Path dir, String modId) {
      if (!Files.exists(dir, new LinkOption[0])) {
         return Optional.empty();
      } else {
         String search = modId.toLowerCase().replace("_", "-");

         try {
            return Files.list(dir).filter((p) -> {
               String name = p.getFileName().toString().toLowerCase();
               return name.endsWith(".jar") && name.contains(search);
            }).findFirst();
         } catch (IOException var4) {
            return Optional.empty();
         }
      }
   }

   private static boolean moveFile(Path source, Path targetDir) {
      try {
         ensureDir(targetDir);
         Path target = targetDir.resolve(source.getFileName());
         if (Files.exists(target, new LinkOption[0])) {
            String name = source.getFileName().toString();
            String base = name.endsWith(".jar") ? name.substring(0, name.length() - 4) : name;
            target = targetDir.resolve(base + "_moved.jar");
         }

         Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
         return true;
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
   }

   private static void ensureDir(Path dir) throws IOException {
      if (!Files.exists(dir, new LinkOption[0])) {
         Files.createDirectories(dir);
      }

   }

   static {
      MODS_DIR = GAME_DIR.resolve("mods");
      DISABLED_DIR = GAME_DIR.resolve("disabled-mods");
      HIDDEN_DIR = GAME_DIR.resolve("hidden-mods");
   }

   @Environment(EnvType.CLIENT)
   public static enum ModState {
      ACTIVE,
      DISABLED,
      HIDDEN;

      // $FF: synthetic method
      private static ModState[] $values() {
         return new ModState[]{ACTIVE, DISABLED, HIDDEN};
      }
   }
}
