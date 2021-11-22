package su.nexmedia.sunlight.utils.actions.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.hooks.external.VaultHK;
import su.nexmedia.engine.utils.actions.actions.IActionExecutor;
import su.nexmedia.engine.utils.actions.params.IParamResult;
import su.nexmedia.engine.utils.actions.params.IParamType;
import su.nexmedia.engine.utils.actions.params.IParamValue;
import su.nexmedia.sunlight.SunLight;

public class AVaultAdd extends IActionExecutor {

    private VaultHK vault;

    public AVaultAdd(@NotNull SunLight plugin, @NotNull VaultHK vault) {
        super(plugin, "VAULT_ADD");
        this.vault = vault;
    }

    @Override
    @NotNull
    public List<String> getDescription() {
        return Arrays.asList("Adds money to a player.");
    }

    @Override
    public void registerParams() {
        this.registerParam(IParamType.DELAY);
        this.registerParam(IParamType.TARGET);
        this.registerParam(IParamType.AMOUNT);
    }

    @Override
    public boolean mustHaveTarget() {
        return true;
    }

    @Override
    protected void execute(Entity exe, Set<Entity> targets, IParamResult result) {
        IParamValue val = result.getParamValue(IParamType.AMOUNT);
        double amount = val.getDouble(0);
        if (amount == 0) return;

        for (Entity e : targets) {
            if (e.getType() == EntityType.PLAYER) {
                Player p = (Player) e;
                this.vault.give(p, amount);
            }
        }
    }
}
