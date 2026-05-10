package org.diymod.sky_peng_mod.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.diymod.sky_peng_mod.Sky_peng_mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class EatTimeManager {

    // 全局时长修改函数（可以随时替换）
    private static float globalDuration = 0f;

    // 玩家独立的时长修改函数（键为玩家UUID）
    private static final Map<UUID, Float> playerMultipliers = new HashMap<>();

    // 特定物品的时长修改函数
    private static final Map<String, Float> itemModifiers = new HashMap<>();

    // 默认倍数
//    private static float defaultMultiplier = 1.0f;
    private static boolean enabled = true;

    /**
     * 获取修改后的食用时长
     * @param stack 食物物品栈
     * @param user 使用者
     * @param originalDuration 原始时长（ticks）
     * @return 修改后的时长（ticks）
     */
    public static int getModifiedEatTime(ItemStack stack, LivingEntity user, int originalDuration) {
        if (!enabled) return originalDuration;

        float useDuration = originalDuration;
        Sky_peng_mod.LOGGER.info("originalDuration: " + useDuration);

        // 1.检查是否是指定物品
        if (!itemModifiers.isEmpty()) {
            String itemId = stack.getItem().toString();
            if (itemModifiers.containsKey(itemId)) {
                useDuration = itemModifiers.get(itemId);
            }
        }

        // 2. 检查玩家特定修改器
        if (user instanceof Player player) {
            UUID uuid = player.getUUID();
            if (playerMultipliers.containsKey(uuid)) {
                useDuration *= playerMultipliers.get(uuid);
            }
        }

        // 3. 检查全局修改器
        if (globalDuration != 0f) {
            useDuration += globalDuration;
        }

        return (int) useDuration;
    }

    /**
     * 设置全局自定义函数
     * @param useDuration 直接设置全局时长（ticks），如果不需要全局修改则设置为0
     */
    public static void setGlobalModifier(float useDuration) {
        globalDuration = useDuration;
    }

    /**
     * 设置特定玩家的自定义函数
     * @param playerUuid 玩家UUID
     * @param useDuration 直接设置玩家时长（ticks），如果不需要修改
     */
    public static void setPlayerMultiplier(UUID playerUuid, float useDuration) {
        playerMultipliers.put(playerUuid, useDuration);
    }

    /**
     * 移除玩家修改器
     * @param playerUuid 玩家UUID
     */
    public static void removePlayerMultiplier(UUID playerUuid) {
        playerMultipliers.remove(playerUuid);
    }

    /**
     * 设置特定物品的时长修改函数
     * @param itemId 物品ID（例如 "minecraft:apple"）
     * @param useDuration 直接设置物品时长（ticks），如果不需要修改则设置为0
     */
    public static void setItemModifier(String itemId, float useDuration) {
        itemModifiers.put(itemId, useDuration);
    }

    /**
     * 清除所有自定义设置
     */
    public static void clearAll() {
        playerMultipliers.clear();
        itemModifiers.clear();
        globalDuration = 0f;
        enabled = true;
    }
}
