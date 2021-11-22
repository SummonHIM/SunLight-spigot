package su.nexmedia.sunlight.modules.kits.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.kits.KitManager;
import su.nexmedia.sunlight.modules.kits.KitPerms;

public class KitEditorCommand extends SunModuleCommand<KitManager> {

    public static final String NAME = "kiteditor";

    public KitEditorCommand(@NotNull KitManager module) {
        super(module, CommandConfig.getAliases(NAME), KitPerms.KITS_CMD_KIT_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_KitEditor_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
