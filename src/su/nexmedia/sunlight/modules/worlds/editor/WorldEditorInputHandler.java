package su.nexmedia.sunlight.modules.worlds.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;
import su.nexmedia.sunlight.modules.worlds.WorldManager;

public class WorldEditorInputHandler extends SunEditorInputHandler<SunWorld> {

    //private final WorldManager worldManager;

    public static JYML YML_LIST;
    public static JYML YML_WORLD;

    public WorldEditorInputHandler(@NotNull WorldManager worldManager) {
        super(worldManager.plugin());
        //this.worldManager = worldManager;

        YML_LIST = JYML.loadOrExtract(plugin, worldManager.getPath() + "editor/list.yml");
        YML_WORLD = JYML.loadOrExtract(plugin, worldManager.getPath() + "editor/world.yml");
    }

    @Override
    public boolean onType(
        @NotNull Player player, @NotNull SunWorld world,
        @NotNull SunEditorType type, @NotNull String msg) {

        switch (type) {
            case WORLD_CHANGE_SEED -> {
                try {
                    world.setSeed(Long.parseLong(msg));
                } catch (NumberFormatException e) {
                    EditorUtils.errorCustom(player, "Invalid seed!");
                    return false;
                }
            }
            case WORLD_CHANGE_GENERATOR -> world.setGenerator(msg);
        }

        world.save();
        return true;
    }
}
