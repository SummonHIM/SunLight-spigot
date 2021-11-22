package su.nexmedia.sunlight.modules.spawn.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;

public class EditorHandlerSpawn extends SunEditorInputHandler<Spawn> {

    //private final SpawnManager spawnManager;

    public EditorHandlerSpawn(@NotNull SpawnManager spawnManager) {
        super(spawnManager.plugin());
        //this.spawnManager = spawnManager;
    }

    @Override
    public boolean onType(
        @NotNull Player player, @NotNull Spawn spawn,
        @NotNull SunEditorType type, @NotNull String msg) {

        switch (type) {
            case SPAWN_CHANGE_NAME -> spawn.setName(msg);
            case SPAWN_CHANGE_PRIORITY -> {
                int input = StringUT.getInteger(msg, -999, true);
                if (input == -999) {
                    EditorUtils.errorNumber(player, false);
                    return false;
                }
                spawn.setPriority(input);
            }
            case SPAWN_ADD_LOGIN_GROUP -> spawn.getTeleportOnLoginGroups().add(msg);
            case SPAWN_ADD_DEATH_GROUP -> spawn.getTeleportOnDeathGroups().add(msg);
            default -> { }
        }

        spawn.save();
        return true;
    }
}
