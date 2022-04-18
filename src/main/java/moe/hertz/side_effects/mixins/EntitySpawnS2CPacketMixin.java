package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import moe.hertz.side_effects.IFakeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

@Mixin(EntitySpawnS2CPacket.class)
public abstract class EntitySpawnS2CPacketMixin {
  @Shadow
  @Final
  @Mutable
  private EntityType<?> entityTypeId;

  @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/Entity;)V")
  void patchInit(Entity entity, CallbackInfo ci) {
    if (entity instanceof IFakeEntity fake) {
      entityTypeId = fake.getFakeType();
    }
  }
}
