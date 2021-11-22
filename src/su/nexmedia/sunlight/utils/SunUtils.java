package su.nexmedia.sunlight.utils;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class SunUtils {

    public static final List<String> ENTITY_TYPES = new ArrayList<>();
    public static final List<String> MATERIAL_NAMES;

    static {
        for (EntityType entityType : EntityType.values()) {
            if (entityType == EntityType.PLAYER
                || entityType == EntityType.AREA_EFFECT_CLOUD
                || entityType == EntityType.UNKNOWN) continue;
            ENTITY_TYPES.add(entityType.name());
        }

        MATERIAL_NAMES = new ArrayList<>();
        for (Material material : Material.values()) {
            MATERIAL_NAMES.add(material.name());
        }
    }
}
