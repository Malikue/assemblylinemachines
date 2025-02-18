package me.haydenb.assemblylinemachines.item;

import java.util.List;

import me.haydenb.assemblylinemachines.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class ItemReactorOutput extends Item {

	private final Component quality;
	
	public ItemReactorOutput(Component quality) {
		super(new Item.Properties().tab(Registry.CREATIVE_TAB));
		this.quality = quality;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
		tooltipComponents.add(new TextComponent("This is a ").withStyle(ChatFormatting.DARK_GRAY).append(quality).append(new TextComponent(" by-product.").withStyle(ChatFormatting.DARK_GRAY)));
		super.appendHoverText(stack, pLevel, tooltipComponents, pIsAdvanced);
	}
}
