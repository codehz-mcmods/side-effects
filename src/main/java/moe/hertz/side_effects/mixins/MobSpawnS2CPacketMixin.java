package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import moe.hertz.side_effects.IFakeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.util.registry.Registry;

@Mixin(MobSpawnS2CPacket.class)
public abstract class MobSpawnS2CPacketMixin {
  @Shadow
  @Final
  @Mutable
  private int entityTypeId;

  @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/LivingEntity;)V")
  void patchInit(LivingEntity entity, CallbackInfo ci) {
    if (entity instanceof IFakeEntity fake) {
      entityTypeId = Registry.ENTITY_TYPE.getRawId(fake.getFakeType());
    }
  }
}
