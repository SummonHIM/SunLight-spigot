package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

public class DisposalCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "disposal";

    public DisposalCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_DISPOSAL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Disposal_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        DisposalMenu menu = new DisposalMenu(plugin, CommandConfig.getConfig(), "Settings." + NAME + ".Menu.");
        menu.open(player, 1);
    }

    static class DisposalMenu extends AbstractMenu<SunLight> {

        public DisposalMenu(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String path) {
            super(plugin, cfg, path);
        }

        @Override
        public boolean cancelClick(@NotNull SlotType slotType, int slot) {
            return false;
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }
    }
}
