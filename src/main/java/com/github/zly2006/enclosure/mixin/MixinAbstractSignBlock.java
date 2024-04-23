package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock {
    @Inject(method = "openEditScreen", at = @At("HEAD"), cancellable = true)
    private void openEditScreen(PlayerEntity player, SignBlockEntity blockEntity, boolean front, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity) {
            @SuppressWarnings("DataFlowIssue")
            EnclosureArea area = ServerMain.INSTANCE.getSmallestEnclosure((ServerWorld) blockEntity.getWorld(), blockEntity.getPos());
            if (area != null && !area.hasPerm((ServerPlayerEntity) player, permissions.EDIT_SIGN)) {
                player.sendMessage(permissions.EDIT_SIGN.getNoPermissionMsg(player));
                ci.cancel();
            }
        }
    }
}
