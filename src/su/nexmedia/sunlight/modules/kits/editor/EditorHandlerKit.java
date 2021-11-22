package su.nexmedia.sunlight.modules.kits.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;

public class EditorHandlerKit extends SunEditorInputHandler<Kit> {

    public static JYML YML_LIST;
    public static JYML YML_MAIN;

    //private final KitManager kitManager;

    public EditorHandlerKit(@NotNull KitManager kitManager) {
        super(kitManager.plugin());
        //this.kitManager = kitManager;

        YML_LIST = JYML.loadOrExtract(plugin, kitManager.getPath() + "editor/list.yml");
        YML_MAIN = JYML.loadOrExtract(plugin, kitManager.getPath() + "editor/main.yml");
    }

    @Override
    public boolean onType(
        @NotNull Player player, @NotNull Kit kit,
        @NotNull SunEditorType type, @NotNull String msg) {

        switch (type) {
            case KIT_CHANGE_COMMAND -> kit.getCommands().add(StringUT.colorRaw(msg));
            case KIT_CHANGE_COOLDOWN, KIT_CHANGE_PRIORITY, KIT_CHANGE_COST -> {
                double val = StringUT.getDouble(msg, -999);
                if (val == -999) {
                    EditorUtils.errorNumber(player, false);
                    return false;
                }
                if (type == SunEditorType.KIT_CHANGE_COST) {
                    kit.setCost(val);
                }
                else if (type == SunEditorType.KIT_CHANGE_PRIORITY) {
                    kit.setPriority((int) val);
                }
                else if (type == SunEditorType.KIT_CHANGE_COOLDOWN) {
                    kit.setCooldown((int) val);
                }
            }
            case KIT_CHANGE_NAME -> kit.setName(msg);
            case KIT_CHANGE_DESCRIPTION -> kit.getDescription().add(msg);
            default -> {}
        }

        kit.save();
        return true;
    }
}
