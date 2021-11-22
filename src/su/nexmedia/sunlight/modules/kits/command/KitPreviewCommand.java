package su.nexmedia.sunlight.modules.kits.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;
import su.nexmedia.sunlight.modules.kits.KitPerms;

import java.util.List;

public class KitPreviewCommand extends SunModuleCommand<KitManager> {

    public static final String NAME = "kitpreview";

    public KitPreviewCommand(KitManager module) {
        super(module, CommandConfig.getAliases(NAME), KitPerms.KITS_CMD_KIT_PREVIEW);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_KitPreview_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_KitPreview_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.module.getKitIds();
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        String kitId = args[0];
        Kit kit = this.module.getKitById(kitId);
        if (kit == null) {
            this.module.getLang().Kit_Error_InvalidKit.replace(Kit.PLACEHOLDER_ID, kitId).send(sender);
            return;
        }

        Player player = (Player) sender;
        kit.getPreview().open(player, 1);
    }

}
