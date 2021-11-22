package su.nexmedia.sunlight.modules.enhancements.module.chairs.command;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.enhancements.EnhancementPerms;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.ChairsManager;

public class SitCommand extends SunModuleCommand<EnhancementManager> {

    public static final String NAME = "sit";
    private final ChairsManager chairsManager;

    public SitCommand(@NotNull EnhancementManager enhancementManager, @NotNull ChairsManager chairsManager) {
        super(enhancementManager, CommandConfig.getAliases(NAME), EnhancementPerms.CHAIRS_CMD_SIT);
        this.chairsManager = chairsManager;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Sit_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Block block = player.getLocation().add(0, -0.8, 0).getBlock();
        if (block.isEmpty() || !block.getType().isSolid()) return;

        this.chairsManager.sitPlayer(player, block);
    }
}
