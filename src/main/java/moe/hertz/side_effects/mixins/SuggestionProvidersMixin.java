package moe.hertz.side_effects.mixins;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.Identifier;

@Mixin(SuggestionProviders.class)
public class SuggestionProvidersMixin {
  @Shadow
  @Final
  private static Identifier ASK_SERVER_NAME;

  @Inject(method = "computeId", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
  private static void onComputeId(SuggestionProvider<CommandSource> provider,
      CallbackInfoReturnable<Identifier> cir) {
    if (cir.getReturnValue().getPath().equals("summonable_entities")) {
      cir.setReturnValue(ASK_SERVER_NAME);
    }
  }
}
