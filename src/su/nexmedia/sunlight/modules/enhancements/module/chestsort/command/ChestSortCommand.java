package su.nexmedia.sunlight.modules.enhancements.module.chestsort.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.enhancements.EnhancementPerms;
import su.nexmedia.sunlight.modules.enhancements.module.chestsort.ChestSortManager;

public class ChestSortCommand extends SunModuleCommand<EnhancementManager> {

    public static final String NAME = "chestsort";

    public ChestSortCommand(@NotNull EnhancementManager enhancementManager) {
        super(enhancementManager, CommandConfig.getAliases(NAME), EnhancementPerms.CHESTSORT_CMD_CHESTSORT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_ChestSort_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        user.setSettingBoolean(ChestSortManager.USER_SETTING_CHEST_SORT, !user.getSettingBoolean(ChestSortManager.USER_SETTING_CHEST_SORT));

        this.module.getLang().Command_ChestSort_Toggle
            .replace("%state%", plugin.lang().getOnOff(user.getSettingBoolean(ChestSortManager.USER_SETTING_CHEST_SORT)))
            .send(sender);

    }
}
