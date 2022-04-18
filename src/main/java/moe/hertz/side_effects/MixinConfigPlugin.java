package moe.hertz.side_effects;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class MixinConfigPlugin implements IMixinConfigPlugin {
  private static final FabricLoader LOADER = FabricLoader.getInstance();
  private static final boolean POLYMER = LOADER.isModLoaded("polymer");

  @Override
  public void onLoad(String mixinPackage) {
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    if (POLYMER) {
      return switch (mixinClassName) {
        case "moe.hertz.side_effects.mixins.CommandManagerMixin" -> false;
        case "moe.hertz.side_effects.mixins.SuggestionProvidersMixin" -> false;
        default -> true;
      };
    }
    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
  }

}
