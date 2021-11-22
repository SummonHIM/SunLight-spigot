package su.nexmedia.sunlight.command;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.utils.FileUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.command.list.*;
import su.nexmedia.sunlight.config.CommandConfig;

import java.io.File;

public class CommandRegulator extends AbstractManager<SunLight> {

    public CommandRegulator(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        CommandConfig.load(this.plugin);

        this.register(new AnvilCommand(plugin));
        this.register(new AirCommand(plugin));
        this.register(new ArmorCommand(plugin));
        this.register(new BackCommand(plugin));
        this.register(new BroadcastCommand(plugin));
        this.register(new BurnCommand(plugin));
        this.register(new ClearchatCommand(plugin));
        this.register(new ClearInventoryCommand(plugin));
        this.register(new CondenseCommand(plugin));
        this.register(new DeathBackCommand(plugin));
        this.register(new DisposalCommand(plugin));
        this.register(new EnchantCommand(plugin));
        this.register(new EnchantTableCommand(plugin));
        this.register(new EnderchestCommand(plugin));
        this.register(new ExpCommand(plugin));
        this.register(new ExtinguishCommand(plugin));
        this.register(new FeedCommand(plugin));
        this.register(new FlyCommand(plugin));
        this.register(new GamemodeCommand(plugin));
        this.register(new GiveCommand(plugin));
        this.register(new GodCommand(plugin));
        this.register(new HatCommand(plugin));
        this.register(new HealCommand(plugin));
        this.register(new IgnoreCommand(plugin));
        this.register(new InventoryCommand(plugin));
        this.register(new ItemCommand(plugin));
        this.register(new ItemNameCommand(plugin));
        this.register(new ItemLoreCommand(plugin));
        this.register(new ListCommand(plugin));
        this.register(new MeCommand(plugin));
        this.register(new MobkillCommand(plugin));
        this.register(new MoreCommand(plugin));
        this.register(new NearCommand(plugin));
        this.register(new NickCommand(plugin));
        this.register(new NoPhantomCommand(plugin));
        this.register(new PlayerInfoCommand(plugin));
        this.register(new PotionCommand(plugin));
        this.register(new RepairCommand(plugin));
        this.register(new ReplyCommand(plugin));
        this.register(new SkullCommand(plugin));
        this.register(new TellCommand(plugin));
        this.register(new SocialSpyCommand(plugin));
        this.register(new SpawnerCommand(plugin));
        this.register(new SpawnmobCommand(plugin));
        this.register(new SpeedCommand(plugin));
        this.register(new SudoCommand(plugin));
        this.register(new SuicideCommand(plugin));
        this.register(new SummonCommand(plugin));
        this.register(new SystemCommand(plugin));
        this.register(new TimeShortCommand(plugin));
        this.register(new ThunderCommand(plugin));
        this.register(new TopCommand(plugin));
        this.register(new TpacceptCommand(plugin));
        this.register(new TpaCommand(plugin));
        this.register(new TpahereCommand(this.plugin));
        this.register(new TeleportCommand(plugin));
        this.register(new TpdenyCommand(plugin));
        this.register(new TpposCommand(plugin));
        this.register(new TptoggleCommand(plugin));
        this.register(new UnignoreCommand(plugin));
        this.register(new VanishCommand(plugin));
        this.register(new WeatherCommand(plugin));
        this.register(new WorkbenchCommand(plugin));

        for (File file : FileUT.getFiles(plugin.getDataFolder() + "/custom_text/", true)) {
            CustomTextCommand cmdText = new CustomTextCommand(plugin, file);
            this.plugin.getNewCommandManager().registerCommand(cmdText);
        }
    }

    @Override
    public void onShutdown() {
        this.plugin.getNewCommandManager().getCommands().forEach(cmd -> {
            if (cmd instanceof ICleanable cleanable) {
                cleanable.clear();
            }
            CommandRegister.unregister(cmd.getAliases()[0]);
        });
    }

    private boolean isDisabled(@NotNull GeneralCommand<SunLight> cmd) {
        return CommandConfig.DISABLED.stream().anyMatch(off -> ArrayUtils.contains(cmd.getAliases(), off));
    }

    public void register(@NotNull GeneralCommand<SunLight> cmd) {
        if (this.isDisabled(cmd)) {
            if (cmd instanceof ICleanable cleanable) {
                cleanable.clear();
            }
            return;
        }

        if (CommandConfig.UNREGISTER_CONFLICTS) {
            CommandRegister.unregister(cmd.getAliases()[0]);
        }
        plugin.getNewCommandManager().registerCommand(cmd);
    }
}
