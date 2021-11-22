package su.nexmedia.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.utils.SunUtils;

import java.util.ArrayList;
import java.util.List;

public class MobkillCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "mobkill";

    public MobkillCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_MOBKILL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Mobkill_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Mobkill_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            List<String> list = new ArrayList<>(SunUtils.ENTITY_TYPES);
            list.add("ALL");
            return list;
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        String type = args[0].toUpperCase();
        EntityType entityType = null;
        ILangMsg msg = plugin.lang().Command_Mobkill_Done_All;

        if (!type.equalsIgnoreCase("ALL")) {
            entityType = CollectionsUT.getEnum(type, EntityType.class);
            if (entityType == null) {
                this.errorType(sender, EntityType.class);
                return;
            }
            msg = plugin.lang().Command_Mobkill_Done_Type.replace("%type%", plugin.lang().getEnum(entityType));
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        int amount = 0;

        for (Entity entity : world.getEntities()) {
            if (!entity.isValid()) continue;
            if (entityType != null) {
                if (entity.getType() != entityType) continue;
            }
            if (entity instanceof Tameable tameable) {
                if (tameable.getOwner() != null) continue;
            }
            if (entity instanceof Player) continue;
            if (entity instanceof LivingEntity living) {
                if (living.isInvulnerable()) continue;
            }

            entity.remove();
            amount++;
        }

        msg.replace("%amount%", String.valueOf(amount)).send(sender);
    }
}
