package fr.neatmonster.nocheatplus.permissions;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cache for permission checks
 *
 * @author phase
 * @since 4/14/19
 */
public class PermissionCache {

    public static boolean INITIALIZED = false;
    private static Map<String, Map<UUID, PermCache>> HAS_PERM_CACHE;

    public static void init() {
        INITIALIZED = true;
        HAS_PERM_CACHE = new HashMap<>();
    }

    public static void close() {
        INITIALIZED = false;
        HAS_PERM_CACHE.clear();
    }

    public static boolean hasPermission(Player player, String permission) {
        if (!HAS_PERM_CACHE.containsKey(permission)) {
            HAS_PERM_CACHE.put(permission, new HashMap<>());
        }

        PermCache permCache = HAS_PERM_CACHE.get(permission).get(player.getUniqueId());
        if (permCache != null && System.currentTimeMillis() - permCache.lastCheckMillis < 30000L) {
            return permCache.hasPermission;
        }

        boolean hasPermission = player.hasPermission(permission);

        permCache = new PermCache(System.currentTimeMillis(), hasPermission);
        HAS_PERM_CACHE.get(permission).put(player.getUniqueId(), permCache);

        return hasPermission;
    }

    public static void clearPlayer(Player player) {
        for (Map<UUID, PermCache> uuidPermCacheMap : HAS_PERM_CACHE.values()) {
            uuidPermCacheMap.remove(player.getUniqueId());
        }
    }

    public static class PermCache {
        private final Long lastCheckMillis;
        private final Boolean hasPermission;

        public PermCache(Long lastCheckMillis, Boolean hasPermission) {
            this.lastCheckMillis = lastCheckMillis;
            this.hasPermission = hasPermission;
        }
    }

}