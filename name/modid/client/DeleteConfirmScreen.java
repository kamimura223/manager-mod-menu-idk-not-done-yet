package name.modid.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;

@Environment(EnvType.CLIENT)
public class DeleteConfirmScreen extends class_437 {
   private final ModManagerScreen parent;
   private final String modId;
   private final String modDisplayName;

   public DeleteConfirmScreen(ModManagerScreen parent, String modId, String modDisplayName) {
      super(class_2561.method_43470("Confirm Delete"));
      this.parent = parent;
      this.modId = modId;
      this.modDisplayName = modDisplayName;
   }

   protected void method_25426() {
      int cx = this.field_22789 / 2;
      int cy = this.field_22790 / 2;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Yes, Delete"), (btn) -> {
         this.parent.onDeleteConfirmed(this.modId);
         class_310.method_1551().method_1507(this.parent);
      }).method_46434(cx - 110, cy + 20, 100, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Cancel"), (btn) -> class_310.method_1551().method_1507(this.parent)).method_46434(cx + 10, cy + 20, 100, 20).method_46431());
   }

   public void method_25394(class_332 graphics, int mouseX, int mouseY, float delta) {
      graphics.method_25294(0, 0, this.field_22789, this.field_22790, -1072689136);
      int cx = this.field_22789 / 2;
      int cy = this.field_22790 / 2;
      graphics.method_25294(cx - 125, cy - 55, cx + 125, cy + 50, -872415232);
      graphics.method_73198(cx - 125, cy - 55, 250, 105, -5592406);
      graphics.method_27534(this.field_22793, class_2561.method_43470("Delete Mod?"), cx, cy - 45, 16733525);
      graphics.method_27534(this.field_22793, class_2561.method_43470(this.modDisplayName), cx, cy - 28, 16777215);
      graphics.method_27534(this.field_22793, class_2561.method_43470("This will permanently delete the .jar file."), cx, cy - 10, 16755200);
      graphics.method_27534(this.field_22793, class_2561.method_43470("This cannot be undone!"), cx, cy + 4, 16733525);
      super.method_25394(graphics, mouseX, mouseY, delta);
   }

   public boolean method_25421() {
      return false;
   }

   public boolean method_25404(class_11908 event) {
      if (event.comp_4795() == 256) {
         class_310.method_1551().method_1507(this.parent);
         return true;
      } else {
         return super.method_25404(event);
      }
   }
}
