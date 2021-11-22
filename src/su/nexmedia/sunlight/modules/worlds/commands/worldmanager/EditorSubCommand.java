package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

public class EditorSubCommand extends AbstractWorldSubCommand {

    public static final String NAME = "editor";

    public EditorSubCommand(@NotNull WorldManager worldManager) {
        super(worldManager, new String[]{NAME}, WorldPerms.CMD_WORLDMANAGER_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.worldManager.getLang().Command_WorldManager_Editor_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.worldManager.getEditor().open(player, 1);
    }
}
