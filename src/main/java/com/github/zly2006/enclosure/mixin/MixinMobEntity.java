package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity extends LivingEntity {
    protected MixinMobEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (this instanceof Bucketable && player.getStackInHand(hand).isOf(Items.WATER_BUCKET)) {
            if (!ServerMain.INSTANCE.checkPermission(getWorld(), getBlockPos(), player, permissions.FISH)) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                serverPlayer.networkHandler.sendPacket(createSpawnPacket());
                serverPlayer.sendMessage(permissions.FISH.getNoPermissionMsg(player));
                serverPlayer.currentScreenHandler.syncState();
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }

    @Inject(method = "canBeLeashedBy", at = @At("HEAD"), cancellable = true)
    private void canBeLeashedBy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!ServerMain.INSTANCE.checkPermission(getWorld(), getBlockPos(), player, permissions.LEASH)) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.networkHandler.sendPacket(new EntityAttachS2CPacket(this, null));
            serverPlayer.sendMessage(permissions.LEASH.getNoPermissionMsg(player));
            serverPlayer.currentScreenHandler.syncState();
            cir.setReturnValue(false);
        }
    }
}
