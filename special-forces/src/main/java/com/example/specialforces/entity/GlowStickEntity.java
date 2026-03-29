package com.example.specialforces.entity;

import com.example.specialforces.init.SFEntityTypes;
import com.example.specialforces.init.SFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class GlowStickEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(GlowStickEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_LANDED =
            SynchedEntityData.defineId(GlowStickEntity.class, EntityDataSerializers.BOOLEAN);

    // 30 minutes = 36000 ticks
    private static final int MAX_LIFETIME_TICKS = 30 * 60 * 20;

    private int age;
    private BlockPos lightPos;

    public GlowStickEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public GlowStickEntity(Level level, double x, double y, double z, int color) {
        super(SFEntityTypes.GLOW_STICK.get(), x, y, z, level,
                new ItemStack(SFItems.GLOW_STICK.get()));
        entityData.set(DATA_COLOR, color);
    }

    @Override
    protected Item getDefaultItem() {
        return SFItems.GLOW_STICK.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, 0xCCFF00); // default yellow-green
        builder.define(DATA_LANDED, false);
    }

    public int getColor() {
        return entityData.get(DATA_COLOR);
    }

    public boolean isLanded() {
        return entityData.get(DATA_LANDED);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        land();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        land();
    }

    private void land() {
        setDeltaMovement(0, 0, 0);
        entityData.set(DATA_LANDED, true);
        if (!level().isClientSide()) {
            placeLight();
        }
    }

    private void placeLight() {
        BlockPos pos = blockPosition();
        for (BlockPos candidate : new BlockPos[]{pos, pos.above(), pos.below(),
                pos.north(), pos.south(), pos.east(), pos.west()}) {
            if (level().getBlockState(candidate).isAir()) {
                level().setBlock(candidate, Blocks.LIGHT.defaultBlockState()
                        .setValue(LightBlock.LEVEL, 15), 3);
                lightPos = candidate;
                return;
            }
        }
    }

    private void removeLight() {
        if (lightPos != null && !level().isClientSide()) {
            BlockState state = level().getBlockState(lightPos);
            if (state.is(Blocks.LIGHT)) {
                level().removeBlock(lightPos, false);
            }
            lightPos = null;
        }
    }

    @Override
    public void tick() {
        if (isLanded()) {
            age++;
            if (!level().isClientSide() && age >= MAX_LIFETIME_TICKS) {
                removeLight();
                discard();
                return;
            }
            if (level().isClientSide() && level().getRandom().nextInt(3) == 0) {
                spawnColorParticles();
            }
        } else {
            super.tick();
            // Move light block along with the entity while in flight
            if (!level().isClientSide()) {
                updateFlightLight();
            }
            if (level().isClientSide() && level().getRandom().nextInt(2) == 0) {
                spawnColorParticles();
            }
        }
    }

    private void updateFlightLight() {
        BlockPos current = blockPosition();
        // Only update if the entity moved to a new block
        if (current.equals(lightPos)) return;
        removeLight();
        if (level().getBlockState(current).isAir()) {
            level().setBlock(current, Blocks.LIGHT.defaultBlockState()
                    .setValue(LightBlock.LEVEL, 15), 3);
            lightPos = current;
        }
    }

    private void spawnColorParticles() {
        level().addParticle(
                new DustParticleOptions(getColor(), 1.0f),
                getX() + (random.nextDouble() - 0.5) * 0.5,
                getY() + (random.nextDouble() - 0.5) * 0.5 + 0.25,
                getZ() + (random.nextDouble() - 0.5) * 0.5,
                0, 0.02, 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("Color", getColor());
        out.putInt("Age", age);
        out.putBoolean("Landed", isLanded());
        if (lightPos != null) {
            out.putInt("LightX", lightPos.getX());
            out.putInt("LightY", lightPos.getY());
            out.putInt("LightZ", lightPos.getZ());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        entityData.set(DATA_COLOR, in.getIntOr("Color", 0xCCFF00));
        age = in.getIntOr("Age", 0);
        entityData.set(DATA_LANDED, in.getBooleanOr("Landed", false));
        in.getInt("LightX").ifPresent(lx -> {
            lightPos = new BlockPos(lx, in.getIntOr("LightY", 0), in.getIntOr("LightZ", 0));
        });
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        removeLight();
        super.remove(reason);
    }
}
