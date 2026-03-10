package com.example.skillcraft.entity;

import com.example.skillcraft.init.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

/**
 * A stationary wandering-trader-style NPC that sells the Lightning Book.
 *
 * <p>Key differences from a vanilla WanderingTrader:
 * <ul>
 *   <li>No movement or wandering AI goals — it stays exactly where it spawned.</li>
 *   <li>Does not despawn naturally (persistence required is set on spawn).</li>
 *   <li>Sells one fixed trade: 63 gold ingots → 1 Lightning Book.</li>
 * </ul>
 */
public class SecretMerchant extends WanderingTrader {

    public SecretMerchant(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    // ---- AI: keep only interaction and look goals, remove all movement ----

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(2, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        // Intentionally no PathfinderGoalMoveTowardsTarget or wander goals
    }

    // ---- Fixed trade list ----

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.getOffers();
        offers.add(new MerchantOffer(
                new ItemCost(Items.GOLD_INGOT, 63),
                Optional.empty(),
                new ItemStack(ModItems.LIGHTNING_BOOK.get()),
                Integer.MAX_VALUE, // unlimited uses
                0,                 // no XP reward
                0.0f               // no price fluctuation
        ));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        // No XP reward for this merchant
    }

    // ---- Prevent despawn ----

    @Override
    public boolean removeWhenFarAway(double distToClosestPlayer) {
        return false;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason spawnType) {
        return true;
    }
}
