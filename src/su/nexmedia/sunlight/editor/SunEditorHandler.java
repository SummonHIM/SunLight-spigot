package su.nexmedia.sunlight.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.AbstractEditorHandler;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.user.editor.EditorUserInputHandler;
import su.nexmedia.sunlight.modules.kits.KitManager;

public class SunEditorHandler extends AbstractEditorHandler<SunLight, SunEditorType> {

    public SunEditorHandler(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.addInputHandler(SunUser.class, new EditorUserInputHandler(this.plugin()));
    }

    @Override
    protected boolean onType(
        @NotNull Player player, @NotNull Object object,
        @NotNull SunEditorType type, @NotNull String input) {

        if (object instanceof KitManager kitManager) {
            if (type == SunEditorType.KIT_CREATE) {
                return kitManager.create(player, input);
            }
        }

        return super.onType(player, object, type, input);
    }
}
