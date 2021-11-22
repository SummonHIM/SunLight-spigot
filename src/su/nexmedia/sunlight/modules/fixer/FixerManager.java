package su.nexmedia.sunlight.modules.fixer;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.fixer.config.FixerConfig;
import su.nexmedia.sunlight.modules.fixer.entitylimiter.EntityLimiterManager;
import su.nexmedia.sunlight.modules.fixer.listener.FixerFarmKillerListener;

public class FixerManager extends SunModule {

    private EntityLimiterManager entityLimiterManager;

    public FixerManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.FIXER;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void onLoad() {
        FixerConfig.load(this);
        if (FixerConfig.ENTITY_LIMITER_ENABLED) {
            this.entityLimiterManager = new EntityLimiterManager(this.plugin);
            this.entityLimiterManager.setup();
        }
        this.addListener(new FixerFarmKillerListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.entityLimiterManager != null) {
            this.entityLimiterManager.shutdown();
            this.entityLimiterManager = null;
        }
    }
}
