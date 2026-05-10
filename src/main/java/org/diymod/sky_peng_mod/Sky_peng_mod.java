package org.diymod.sky_peng_mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diymod.sky_peng_mod.effect.ModEffects;
import org.diymod.sky_peng_mod.entity.EatTimeManager;
import org.diymod.sky_peng_mod.entity.ModItems;
import org.diymod.sky_peng_mod.entity.PlayerSavedData;

public class Sky_peng_mod implements ModInitializer {

    public static String MOD_ID = "sky_peng_mod";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    // 1. 创建物品组的注册键
    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(MOD_ID, "creative_tab")
    );

    // 2. 创建物品组对象
    public static final CreativeModeTab CUSTOM_CREATIVE_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.DIY_DAGUO))
            .title(Component.translatable("creativeTab.test-diy-food"))
            .displayItems((params, output) -> {
                output.accept(ModItems.DIY_DAGUO);
            })
            .build();



    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModEffects.initialize();
        initFoodUseDuration();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_CREATIVE_TAB_KEY, CUSTOM_CREATIVE_TAB);

        // 加入游戏，恢复口腔癌时间
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
           ServerPlayer player =  handler.player;
           CompoundTag playerData = PlayerSavedData.get(player.level()).getOralCancerRemaining(player.getUUID());
           player.displayClientMessage(Component.literal("playerData:" + playerData), false);

           if (playerData == null) return;

           int oral_cancer_remaining = playerData.getIntOr("oral_cancer_remaining", 0);
           String effect = playerData.getString("effect").orElse(null);

           if (oral_cancer_remaining > 0 && effect != null && !effect.isEmpty()) {
              player.displayClientMessage(Component.literal("你还有 " + oral_cancer_remaining + " 秒的口腔癌效果未清除，快喝牛奶吧！"), false);

              switch (effect) {
                case "early":
                    player.displayClientMessage(Component.literal("你进入了口腔癌初期！"), false);
                    player.addEffect(new MobEffectInstance(Holder.direct(ModEffects.EARLY_ORAL_CANCER), oral_cancer_remaining * 20, 0));
                    PlayerSavedData.get(player.level()).removeOralCancerRemaining(player.getUUID());
                    break;
                case "middle":
                    player.addEffect(new MobEffectInstance(Holder.direct(ModEffects.MIDDLE_ORAL_CANCER), oral_cancer_remaining * 20, 0));
                    PlayerSavedData.get(player.level()).removeOralCancerRemaining(player.getUUID());
                    player.displayClientMessage(Component.literal("你进入了口腔癌中期！"), false);
                    break;
                case "late":
                    player.addEffect(new MobEffectInstance(Holder.direct(ModEffects.LATE_ORAL_CANCER), oral_cancer_remaining * 20, 0));
                    PlayerSavedData.get(player.level()).removeOralCancerRemaining(player.getUUID());
                    player.displayClientMessage(Component.literal("你进入了口腔癌晚期！"), false);
                    break;
                default:
                    Log.warn(LogCategory.LOG, "未知的口腔癌效果类型: ", effect);
               }

           }
        });

        // 断开链接记录
        ServerPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            ServerPlayer player =  handler.player;
            MobEffectInstance EARLY_ORAL_CANCER_Instance = player.getEffect(Holder.direct(ModEffects.EARLY_ORAL_CANCER));
            MobEffectInstance MIDDLE_ORAL_CANCER = player.getEffect(Holder.direct(ModEffects.MIDDLE_ORAL_CANCER));
            MobEffectInstance LATE_ORAL_CANCER = player.getEffect(Holder.direct(ModEffects.LATE_ORAL_CANCER));
            if (EARLY_ORAL_CANCER_Instance != null) {
                PlayerSavedData.get(player.level()).setOralCancerRemaining(player.getUUID(), "early",EARLY_ORAL_CANCER_Instance.getDuration() / 20);
            } else if (MIDDLE_ORAL_CANCER != null) {
                PlayerSavedData.get(player.level()).setOralCancerRemaining(player.getUUID(), "middle", MIDDLE_ORAL_CANCER.getDuration() / 20);
            }else if (LATE_ORAL_CANCER != null) {
                PlayerSavedData.get(player.level()).setOralCancerRemaining(player.getUUID(), "late", LATE_ORAL_CANCER.getDuration() / 20);
            } else {
                PlayerSavedData.get(player.level()).removeOralCancerRemaining(player.getUUID());
            }
        });

        // 玩家破坏橡树树叶概率掉落大果
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide() && state.is(Blocks.OAK_LEAVES)) {
                if (world.random.nextFloat() < 0.05f) { // 5%(0.05f) 的概率掉落大果
                    ItemStack daGuoStack = new ItemStack(ModItems.DIY_DAGUO);
                    Block.popResource(world, pos, daGuoStack);
                }
            }
        });
    }

    // 初始化自定义食物的食用时长
    public void initFoodUseDuration() {
        String daGuoItemId = BuiltInRegistries.ITEM.getKey(ModItems.DIY_DAGUO).toString();
        EatTimeManager.setItemModifier(daGuoItemId, 20f);
    }
}
