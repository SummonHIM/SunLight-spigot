package su.nexmedia.sunlight.modules.homes.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;

public class EditorHandlerHome extends SunEditorInputHandler<Home> {

    //private HomeManager homeManager;

    public EditorHandlerHome(@NotNull HomeManager homeManager) {
        super(homeManager.plugin());
        //this.homeManager = homeManager;
    }

    @Override
    public boolean onType(
        @NotNull Player player, @NotNull Home home,
        @NotNull SunEditorType type, @NotNull String msg) {

        switch (type) {
            case HOME_CHANGE_INVITED_PLAYERS -> home.addInvitedPlayer(msg);
            case HOME_CHANGE_NAME -> home.setName(msg);
            default -> {}
        }

        home.getEditor().open(player, 1);
        return true;
    }
}
