package su.nexmedia.sunlight.modules.enhancements.module.chairs.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.enhancements.EnhancementPerms;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.ChairsManager;

public class ChairsCommand extends SunModuleCommand<EnhancementManager> {

    public static final String NAME = "chairs";

    public ChairsCommand(@NotNull EnhancementManager enhancementManager) {
        super(enhancementManager, CommandConfig.getAliases(NAME), EnhancementPerms.CHAIRS_CMD_CHAIRS);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Chairs_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        user.setSettingBoolean(ChairsManager.USER_SETTING_CHAIRS, !user.getSettingBoolean(ChairsManager.USER_SETTING_CHAIRS));

        this.module.getLang().Command_Chairs_Toggle
            .replace("%state%", plugin.lang().getOnOff(user.getSettingBoolean(ChairsManager.USER_SETTING_CHAIRS)))
            .send(player);
    }
}
