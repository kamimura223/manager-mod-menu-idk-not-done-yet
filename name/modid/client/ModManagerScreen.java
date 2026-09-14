package name.modid.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ModManagerScreen extends class_437 {
   private static final Logger LOGGER = LoggerFactory.getLogger("mod-manager-screen");
   private static final int ROW_H = 24;
   private static final int BTN_W = 60;
   private static final int BTN_H = 14;
   private static final int BTN_GAP = 3;
   private static final int LIST_TOP = 30;
   private static final int LEFT = 6;
   private static final int RIGHT_PAD = 6;
   private final class_437 parent;
   private List<ModEntry> allMods = new ArrayList();
   private List<ModEntry> visibleMods = new ArrayList();
   private class_342 searchField;
   private int scrollOffset = 0;

   public ModManagerScreen(class_437 parent) {
      super(class_2561.method_43470("Mod Menu Manager"));
      this.parent = parent;
   }

   private int listBottom() {
      return this.field_22790 - 26;
   }

   private int maxRows() {
      return Math.max(1, (this.listBottom() - 30) / 24);
   }

   protected void method_25426() {
      this.allMods = (List)FabricLoader.getInstance().getAllMods().stream().map((m) -> new ModEntry(m.getMetadata().getId(), m.getMetadata().getName(), m)).sorted(Comparator.comparing((e) -> e.displayName.toLowerCase())).collect(Collectors.toList());
      this.visibleMods = new ArrayList(this.allMods);
      this.scrollOffset = 0;
      LOGGER.info("[ModManager] init: {} mods, screen {}x{}", new Object[]{this.allMods.size(), this.field_22789, this.field_22790});
      this.searchField = new class_342(this.field_22793, 6, 7, this.field_22789 - 6 - 6, 16, class_2561.method_43470("Search mods..."));
      this.searchField.method_47404(class_2561.method_43470("Search mods..."));
      this.searchField.method_1863(this::onSearch);
      this.method_37063(this.searchField);
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Done"), (btn) -> class_310.method_1551().method_1507(this.parent)).method_46434(this.field_22789 / 2 - 50, this.field_22790 - 23, 100, 20).method_46431());
      this.buildRowWidgets();
   }

   private void onSearch(String q) {
      String lower = q.toLowerCase().trim();
      this.visibleMods = (List)this.allMods.stream().filter((e) -> lower.isEmpty() || e.displayName.toLowerCase().contains(lower) || e.modId.toLowerCase().contains(lower)).collect(Collectors.toList());
      this.scrollOffset = 0;
      this.buildRowWidgets();
   }

   private void buildRowWidgets() {
      this.method_37067();
      this.method_37063(this.searchField);
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Done"), (btn) -> class_310.method_1551().method_1507(this.parent)).method_46434(this.field_22789 / 2 - 50, this.field_22790 - 23, 100, 20).method_46431());
      int rows = this.maxRows();
      int start = this.scrollOffset;
      int end = Math.min(start + rows, this.visibleMods.size());
      int right = this.field_22789 - 6;

      for(int i = start; i < end; ++i) {
         ModEntry entry = (ModEntry)this.visibleMods.get(i);
         int rowY = 30 + (i - start) * 24;
         int btnY = rowY + 5;
         if (ModStateManager.canManage(entry.modId)) {
            ModStateManager.ModState state = ModStateManager.getState(entry.modId);
            String modId = entry.modId;
            if (state == ModStateManager.ModState.ACTIVE) {
               int xDel = right - 60;
               int xDis = xDel - 60 - 3;
               int xHide = xDis - 60 - 3;
               this.method_37063(class_4185.method_46430(class_2561.method_43470("Delete"), (btn) -> this.onDelete(modId)).method_46434(xDel, btnY, 60, 14).method_46431());
               this.method_37063(class_4185.method_46430(class_2561.method_43470("Disable"), (btn) -> this.onDisable(modId)).method_46434(xDis, btnY, 60, 14).method_46431());
               this.method_37063(class_4185.method_46430(class_2561.method_43470("Hide"), (btn) -> this.onHide(modId)).method_46434(xHide, btnY, 60, 14).method_46431());
            } else {
               int xRe = right - 60 - 14;
               this.method_37063(class_4185.method_46430(class_2561.method_43470("Re-enable"), (btn) -> this.onReenable(modId)).method_46434(xRe, btnY, 74, 14).method_46431());
            }
         }
      }

      if (this.scrollOffset > 0) {
         this.method_37063(class_4185.method_46430(class_2561.method_43470("^"), (btn) -> {
            --this.scrollOffset;
            this.buildRowWidgets();
         }).method_46434(this.field_22789 - 16, 30, 12, 12).method_46431());
      }

      if (end < this.visibleMods.size()) {
         this.method_37063(class_4185.method_46430(class_2561.method_43470("v"), (btn) -> {
            ++this.scrollOffset;
            this.buildRowWidgets();
         }).method_46434(this.field_22789 - 16, this.listBottom() - 14, 12, 12).method_46431());
      }

   }

   private void onHide(String modId) {
      boolean ok = ModStateManager.hide(modId);
      LOGGER.info("[ModManager] hide {} -> {}", modId, ok);
      this.buildRowWidgets();
   }

   private void onDisable(String modId) {
      boolean ok = ModStateManager.disable(modId);
      LOGGER.info("[ModManager] disable {} -> {}", modId, ok);
      this.buildRowWidgets();
   }

   private void onDelete(String modId) {
      class_310.method_1551().method_1507(new DeleteConfirmScreen(this, modId, this.getDisplayName(modId)));
   }

   public void onDeleteConfirmed(String modId) {
      ModStateManager.delete(modId);
      this.allMods.removeIf((e) -> e.modId.equals(modId));
      this.visibleMods.removeIf((e) -> e.modId.equals(modId));
      this.buildRowWidgets();
   }

   private void onReenable(String modId) {
      boolean ok = ModStateManager.reenable(modId);
      LOGGER.info("[ModManager] reenable {} -> {}", modId, ok);
      this.buildRowWidgets();
   }

   private String getDisplayName(String modId) {
      return (String)this.allMods.stream().filter((e) -> e.modId.equals(modId)).map((e) -> e.displayName).findFirst().orElse(modId);
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -15658735);
      g.method_27534(this.field_22793, class_2561.method_43470("Mod Menu Manager"), this.field_22789 / 2, 2, 16777215);
      g.method_27535(this.field_22793, class_2561.method_43470(this.visibleMods.size() + " mods"), 6, 20, 8947848);
      int rows = this.maxRows();
      int start = this.scrollOffset;
      int end = Math.min(start + rows, this.visibleMods.size());

      for(int i = start; i < end; ++i) {
         ModEntry entry = (ModEntry)this.visibleMods.get(i);
         int rowY = 30 + (i - start) * 24;
         g.method_25294(6, rowY, this.field_22789 - 6, rowY + 24 - 1, i % 2 == 0 ? 1157627903 : 587202559);
         boolean canManage = ModStateManager.canManage(entry.modId);
         ModStateManager.ModState state = ModStateManager.getState(entry.modId);
         int color = canManage ? 16777215 : 8947848;
         String tag = !canManage ? " [built-in]" : (state == ModStateManager.ModState.DISABLED ? " [disabled]" : (state == ModStateManager.ModState.HIDDEN ? " [hidden]" : ""));
         g.method_27535(this.field_22793, class_2561.method_43470(entry.displayName + tag), 9, rowY + 8, color);
      }

      super.method_25394(g, mx, my, delta);
   }

   public boolean method_25401(double mx, double my, double sx, double sy) {
      int rows = this.maxRows();
      if (sy < (double)0.0F) {
         this.scrollOffset = Math.min(this.scrollOffset + 1, Math.max(0, this.visibleMods.size() - rows));
      } else {
         this.scrollOffset = Math.max(0, this.scrollOffset - 1);
      }

      this.buildRowWidgets();
      return true;
   }

   public boolean method_25421() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   private static class ModEntry {
      final String modId;
      final String displayName;
      final ModContainer container;

      ModEntry(String id, String name, ModContainer c) {
         this.modId = id;
         this.displayName = name;
         this.container = c;
      }
   }
}
