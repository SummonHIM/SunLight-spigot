package su.nexmedia.sunlight;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.commands.api.IGeneralCommand;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.VaultHK;
import su.nexmedia.engine.utils.Reflex;
import su.nexmedia.sunlight.command.CommandRegulator;
import su.nexmedia.sunlight.config.Config;
import su.nexmedia.sunlight.config.Lang;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.data.SunUserData;
import su.nexmedia.sunlight.data.UserManager;
import su.nexmedia.sunlight.editor.SunEditorHandler;
import su.nexmedia.sunlight.hooks.HookId;
import su.nexmedia.sunlight.hooks.converter.EssentialsConverter;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.ModuleCache;
import su.nexmedia.sunlight.nms.SunNMS;
import su.nexmedia.sunlight.utils.actions.actions.AOpenGUI;
import su.nexmedia.sunlight.utils.actions.actions.AVaultAdd;

import java.sql.SQLException;

public class SunLight extends NexPlugin<SunLight> implements UserDataHolder<SunLight, SunUser> {
	
	/*
	    __  ______    ____  ______   _____   __   __  ______  ___    __ 
	   /  |/  /   |  / __ \/ ____/  /  _/ | / /  / / / / __ \/   |  / / 
	  / /|_/ / /| | / / / / __/     / //  |/ /  / / / / /_/ / /| | / /  
	 / /  / / ___ |/ /_/ / /___   _/ // /|  /  / /_/ / _, _/ ___ |/ /___
	/_/  /_/_/  |_/_____/_____/  /___/_/ |_/   \____/_/ |_/_/  |_/_____/
                                                                                        
	*/

    private static SunLight instance;

    private Config config;
    private Lang   lang;

    private SunUserData dataHandler;
    private UserManager userManager;

    private CommandRegulator commandRegulator;

    private ModuleCache moduleCache;

    private SunEditorHandler sunEditorHandler;
    private SunNMS           sunNMS;

    public SunLight() {
        instance = this;
    }

    @NotNull
    public static SunLight getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        if (!this.setupInternalNMS()) {
            this.error("Could not setup internal NMS handler. Shutdown now...");
            this.getPluginManager().disablePlugin(this);
            return;
        }
        this.registerActions();

        this.configManager.extractFullPath(this.getDataFolder() + "/custom_text/", "txt", false);

        this.commandRegulator = new CommandRegulator(this);
        this.commandRegulator.setup();

        this.sunEditorHandler = new SunEditorHandler(this);
        this.sunEditorHandler.setup();

        this.moduleCache = new ModuleCache(this);
        this.moduleCache.setup();

        if (Config.DATA_CONVERSION_ESSENTIALS && Hooks.hasPlugin(HookId.ESSENTIALS)) {
            EssentialsConverter.convert();
            Config.DATA_CONVERSION_ESSENTIALS = false;
        }
    }

    @Override
    public void disable() {
        if (this.sunEditorHandler != null) {
            this.sunEditorHandler.shutdown();
            this.sunEditorHandler = null;
        }
        if (this.moduleCache != null) {
            this.moduleCache.shutdown();
            this.moduleCache = null;
        }
        if (this.commandRegulator != null) {
            this.commandRegulator.shutdown();
            this.commandRegulator = null;
        }
    }

    private boolean setupInternalNMS() {
        Class<?> nmsClazz = Reflex.getClass(SunNMS.class.getPackage().getName(), Version.CURRENT.name());
        if (nmsClazz == null) return false;

        try {
            this.sunNMS = (SunNMS) nmsClazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return this.sunNMS != null;
    }

    @Override
    public boolean setupDataHandlers() {
        try {
            this.dataHandler = SunUserData.getInstance();
            this.dataHandler.setup();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        this.userManager = new UserManager(this);
        this.userManager.setup();

        return true;
    }

    private void registerActions() {
        VaultHK vault = this.getVault();
        if (vault != null) {
            this.getActionsManager().registerExecutor(new AVaultAdd(this, vault));
        }
        if (this.cfg().isModuleEnabled(ModuleId.MENU)) {
            this.getActionsManager().registerExecutor(new AOpenGUI(this));
        }
    }

    @Override
    public void registerCmds(@NotNull IGeneralCommand<SunLight> mainCommand) {

    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<SunLight> mainCommand) {

    }

    @Override
    public void registerHooks() {

    }

    @Override
    public void setConfig() {
        this.config = new Config(this);
        this.config.setup();

        this.lang = new Lang(this);
        this.lang.setup();
    }

    @Override
    public boolean useNewCommandManager() {
        return true;
    }

    @Override
    @NotNull
    public Config cfg() {
        return this.config;
    }

    @Override
    @NotNull
    public Lang lang() {
        return this.lang;
    }

    @Override
    @NotNull
    public SunUserData getData() {
        return this.dataHandler;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public CommandRegulator getCommandRegulator() {
        return commandRegulator;
    }

    @NotNull
    public ModuleCache getModuleCache() {
        return moduleCache;
    }

    @NotNull
    public SunEditorHandler getSunEditorHandler() {
        return sunEditorHandler;
    }

    @NotNull
    public SunNMS getSunNMS() {
        return this.sunNMS;
    }
}
