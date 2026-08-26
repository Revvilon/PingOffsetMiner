package pom.rewrite.utility.stats;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import pom.rewrite.events.clientTick;
import pom.rewrite.events.packetReceived;
import pom.rewrite.features.PingOffsetMiner;
import pom.rewrite.features.debug.CustomStats;
import pom.rewrite.features.debug.PrecisionMining;
import pom.rewrite.features.render.MsbRender;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockObject;
import pom.rewrite.utility.server.IslandUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class MiningStats {
    private ItemStack item = ItemStack.EMPTY;
    private int miningSpeed = 0;

    private boolean msbActive = false;
    private int msbCooldown = 0;
    private float msbMultiplier = 1.0f;

    private long precisionParticleTime = 0;
    private boolean aimingAtPrecisionParticle = false;

    private double particleX = 0;
    private double particleY = 0;
    private double particleZ = 0;
    private boolean hasParticle = false;
    private long particleSpawnTime = 0;

    private static final MiningStats instance = new MiningStats();
    public static MiningStats instance() {return instance;}
    private MiningStats() {}

    public void setItem(ItemStack item) { this.item = (item == null) ? ItemStack.EMPTY : item; }
    public ItemStack getItem() { return this.item; }

    public void setMiningSpeed(int miningSpeed) { this.miningSpeed = miningSpeed; }
    public int getMiningSpeed() {
        int baseSpeed = CustomStats.instance.isEnabled() ? CustomStats.customSpeed.getInt() : miningSpeed;
        if (PrecisionMining.precisionMining.isEnabled() && isAimingAtPrecisionParticle()) {
            return (int) (baseSpeed * 1.30);
        }
        return baseSpeed;
    }
    public int getBaseMiningSpeed() { return miningSpeed; }

    public void setMsbActive(boolean msbActive) { this.msbActive = msbActive; }
    public boolean getMsbActive() {return msbActive; }

    public void setMsbCooldown(int msbCooldown) { this.msbCooldown = msbCooldown; }
    public int getMsbCooldown() { return msbCooldown; }

    public void setMsbMultiplier(float msbMultiplier) { this.msbMultiplier = msbMultiplier; }
    public float getMsbMultiplier() { return msbMultiplier; }

    public void setAimingAtPrecisionParticle(boolean active) {
        this.aimingAtPrecisionParticle = active;
        if (active) {
            this.precisionParticleTime = System.currentTimeMillis();
        }
    }

    public boolean isAimingAtPrecisionParticle() {
        if (this.aimingAtPrecisionParticle) {
            if (System.currentTimeMillis() - this.precisionParticleTime > 250) { // 5 ticks = 250ms
                this.aimingAtPrecisionParticle = false;
            }
        }
        return this.aimingAtPrecisionParticle;
    }

    @EventHandler
    public void onTick(clientTick tick) {
        if (!PrecisionMining.precisionMining.isEnabled()) {
            aimingAtPrecisionParticle = false;
            hasParticle = false;
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !hasParticle) {
            aimingAtPrecisionParticle = false;
            return;
        }

        if (System.currentTimeMillis() - particleSpawnTime > 250) { // 5 ticks
            hasParticle = false;
            aimingAtPrecisionParticle = false;
            return;
        }

        checkLook(mc);
    }

    private void checkLook(Minecraft mc) {
        if (mc.player == null) return;
        Vec3 eyePos = mc.player.getEyePosition(1.0f);
        Vec3 lookVec = mc.player.getViewVector(1.0f);
        Vec3 particlePos = new Vec3(particleX, particleY, particleZ);

        Vec3 toParticle = particlePos.subtract(eyePos);
        double projectionLength = toParticle.dot(lookVec);

        if (projectionLength >= 0) {
            Vec3 closestPoint = eyePos.add(lookVec.scale(projectionLength));
            double distanceToRay = particlePos.distanceTo(closestPoint);

            if (distanceToRay <= 0.3) {
                setAimingAtPrecisionParticle(true);
            } else {
                setAimingAtPrecisionParticle(false);
            }
        } else {
            setAimingAtPrecisionParticle(false);
        }
    }

    @EventHandler
    public void onPacketReceived(packetReceived event) {
        if (!PrecisionMining.precisionMining.isEnabled()) return;
        if (event.value instanceof ClientboundLevelParticlesPacket packet) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            if (!mc.options.keyAttack.isDown()) return;

            if (packet.getParticle().getType() == ParticleTypes.HAPPY_VILLAGER ||
                packet.getParticle().getType() == ParticleTypes.CRIT) {

                if (mc.hitResult instanceof BlockHitResult hr && hr.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = hr.getBlockPos();
                    double px = packet.getX();
                    double py = packet.getY();
                    double pz = packet.getZ();

                    if (px > pos.getX() - 0.12 && px < pos.getX() + 1.12 &&
                        py > pos.getY() - 0.12 && py < pos.getY() + 1.12 &&
                        pz > pos.getZ() - 0.12 && pz < pos.getZ() + 1.12) {

                        particleX = px;
                        particleY = py;
                        particleZ = pz;
                        hasParticle = true;
                        particleSpawnTime = System.currentTimeMillis();

                        checkLook(mc);
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return this.item != ItemStack.EMPTY;
    }

    public boolean shouldRender() {
        if (!PingOffsetMiner.instance.isEnabled()) return false;
        if (CustomStats.instance.isEnabled() && BlockData.getInstance().getCurrentBlock() instanceof BlockObject) return true;

        if (!(BlockData.getInstance().getCurrentBlock() instanceof BlockObject) || !IslandUtils.isIsland() || TickStats.instance().ticksNeeded() <= 0) {
            return false;
        }

        if (!isActive()) return false;

        if (MsbRender.instance.isEnabled()) {
            return getMsbActive() == MsbRender.msbToggle.getBool();
        }

        return true;
    }

    public <T> void ifReady(T value, Predicate<T> check, Consumer<T> task) {
        if (this.isActive() && check.test(value)) {
            task.accept(value);
        }
    }

    public void reset() {
        this.item = ItemStack.EMPTY;
        this.miningSpeed = 0;
        this.msbActive = false;
        this.msbCooldown = 0;
        this.msbMultiplier = 1.0f;
        this.aimingAtPrecisionParticle = false;
        this.precisionParticleTime = 0;
        this.hasParticle = false;
        this.particleX = 0;
        this.particleY = 0;
        this.particleZ = 0;
        this.particleSpawnTime = 0;
    }
}
