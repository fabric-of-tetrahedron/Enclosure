package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.EnclosureList;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.Block;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.enclosure.utils.Permission.permissions;
import static net.minecraft.block.CampfireBlock.LIT;
import static net.minecraft.block.CampfireBlock.WATERLOGGED;

@Mixin(PotionEntity.class)
public abstract class MixinPotionEntity extends ThrownItemEntity {
    public MixinPotionEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "extinguishFire", cancellable = true)
    private void onExtinguishFire(BlockPos pos, CallbackInfo ci) {
        if (getWorld().isClient) {
            return;
        }
        Block block = getWorld().getBlockState(pos).getBlock();
        if (block instanceof CampfireBlock) {
            EnclosureList list = ServerMain.INSTANCE.getAllEnclosures((ServerWorld) getWorld());
            EnclosureArea area = list.getArea(pos);
            if (area != null && !area.areaOf(pos).hasPubPerm(permissions.USE_CAMPFIRE)) {
                if (getOwner() instanceof ServerPlayerEntity player) {
                    player.sendMessage(permissions.USE_CAMPFIRE.getNoPermissionMsg(player));
                }
                getWorld().setBlockState(pos, getWorld().getBlockState(pos).with(WATERLOGGED, false).with(LIT, true), 252);
                ci.cancel();
            }
        }
    }
}
