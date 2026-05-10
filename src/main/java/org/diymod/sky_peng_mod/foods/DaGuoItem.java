package org.diymod.sky_peng_mod.foods;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.diymod.sky_peng_mod.Sky_peng_mod;
import org.diymod.sky_peng_mod.entity.PlayerSavedData;
import org.diymod.sky_peng_mod.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;

public class DaGuoItem extends Item {

    public DaGuoItem(Item.Properties settings) {
        super(settings);
    }

    // 食用完成事件
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {

        ItemStack itemStack = super.finishUsingItem(stack, world, user);
        if (user instanceof Player player) {
            // Use dimension-independent saved data (falls back to in-memory if necessary)
            PlayerSavedData savedData = PlayerSavedData.get(world);
            int newCount = savedData.addDaGuoEaten(player.getUUID());

            if (player instanceof ServerPlayer) {
                player.displayClientMessage(Component.literal("已使用大果数量: " + newCount), false);
                String effect = "early";

                if (newCount == 5) {
                    player.displayClientMessage(Component.literal("你因使用过多大果导致进入口腔癌初期！"), false);
                    // 给玩家口腔癌效果，持续 5 分钟（5*60*20 tick）
                    addPlayerEffect(player, ModEffects.EARLY_ORAL_CANCER, 60 * 5);
                    setOralCancerRemaining(player, "early", 60 * 5);
                } else if (newCount == 10) {
                    player.displayClientMessage(Component.literal("你因使用过多大果导致进入口腔癌中期！"), false);

                    if (player.hasEffect(Holder.direct(ModEffects.EARLY_ORAL_CANCER))) {
                        player.removeEffect(Holder.direct(ModEffects.EARLY_ORAL_CANCER));
                    }

                    // 给玩家口腔癌中期效果，持续 5 分钟（5*60*20 tick）
                    addPlayerEffect(player, ModEffects.MIDDLE_ORAL_CANCER, 60 * 5);
                    setOralCancerRemaining(player, "middle", 60 * 5);
                } else if (newCount >= 15) {
                    player.displayClientMessage(Component.literal("你因使用过多大果导致进入口腔癌晚期！"), false);

                    if (player.hasEffect(Holder.direct(ModEffects.MIDDLE_ORAL_CANCER))) {
                        player.removeEffect(Holder.direct(ModEffects.MIDDLE_ORAL_CANCER));
                    }

                    // 给玩家口腔癌晚期效果，持续 5 分钟（5*60*20 tick）
                    addPlayerEffect(player, ModEffects.LATE_ORAL_CANCER, 60 * 5);
                    setOralCancerRemaining(player, "late", 60 * 5);
                }
            }
        }
        return itemStack;
    }

    // 给角色增加效果
    public void addPlayerEffect(Player player, MobEffect effect, int seconds) {
        player.addEffect(new MobEffectInstance(Holder.direct(effect), 20 * seconds, 0));
    }

    // 设置口腔癌时间(存入PlayerSavedData，单位为秒)
    public void setOralCancerRemaining(Player player, String effect, int seconds) {
        PlayerSavedData.get(player.level()).setOralCancerRemaining(player.getUUID(), effect,20 * seconds);
    }
}
