package moe.hertz.side_effects.mixins;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
  @ModifyVariable(method = "argument", at = @At("HEAD"), ordinal = 0)
  private static ArgumentType<?> replaceArgumentType(ArgumentType<?> type) {
    if (type instanceof EntitySummonArgumentType) {
      return IdentifierArgumentType.identifier();
    }
    return type;
  }
}
