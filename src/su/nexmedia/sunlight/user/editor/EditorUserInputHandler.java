package su.nexmedia.sunlight.user.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;

public class EditorUserInputHandler extends SunEditorInputHandler<SunUser> {

    public static JYML IGNORED_LIST;
    public static JYML IGNORED_SETTINGS;

    public EditorUserInputHandler(@NotNull SunLight plugin) {
        super(plugin);

        IGNORED_LIST = JYML.loadOrExtract(this.plugin, "/editor/user/ignoredUsers_list.yml");
        IGNORED_SETTINGS = JYML.loadOrExtract(this.plugin, "/editor/user/ignoredUsers_settings.yml");
    }

    @Override
    public boolean onType(
        @NotNull Player player, @Nullable SunUser user,
        @NotNull SunEditorType type, @NotNull String msg) {
        // TODO Auto-generated method stub

        return true;
    }
}
