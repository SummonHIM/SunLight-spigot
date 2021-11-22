package su.nexmedia.sunlight.modules;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.module.AbstractModule;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public abstract class SunModule extends AbstractModule<SunLight> implements ConfigHolder {

    protected JYML                cfg;
    protected SunModuleCommand<?> moduleCommand;

    public SunModule(@NotNull SunLight plugin) {
        super(plugin);
        this.cfg = JYML.loadOrExtract(plugin, this.getPath() + "settings.yml");
    }

    @Override
    @NotNull
    public JYML getConfig() {
        return this.cfg;
    }

    @Override
    public void onSave() {

    }

	/*private void registerCommands() {
		String alias = cfg.getString("Command_Aliases");
		if (alias == null) return;

		String[] aliases = alias.split(",");
		if (aliases.length == 0 || aliases[0].isEmpty()) return;

		this.moduleCommand = new SunModuleCommand(this, aliases);
		this.moduleCommand.addDefaultCommand(new HelpSubCommand<>(this.plugin));
		this.plugin.getNewCommandManager().registerCommand(this.moduleCommand);
	}

	private void unregisterCommands() {
		if (this.moduleCommand == null) return;

		this.plugin.getNewCommandManager().unregisterCommand(this.moduleCommand);
		this.moduleCommand = null;
	}*/
}
