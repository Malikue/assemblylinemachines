package me.haydenb.assemblylinemachines.item.powertools;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import me.haydenb.assemblylinemachines.AssemblyLineMachines;
import me.haydenb.assemblylinemachines.client.TooltipBorderHandler.ISpecialTooltip;
import me.haydenb.assemblylinemachines.item.ItemTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class ItemPowerAxe extends AxeItem implements IToolWithCharge, ISpecialTooltip {

	private final IToolWithCharge.PowerToolType ptt;
	
	public ItemPowerAxe(ItemTiers tier, Properties properties) {
		super(tier.getItemTier(), 3f, -3.5f, properties);
		this.ptt = tier.getPowerToolType();
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		ItemStack resStack = damageItem(stack, amount);
		return resStack == null ? super.damageItem(stack, amount, entity, onBroken) : super.damageItem(resStack, 0, entity, onBroken);
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos,
			LivingEntity player) {
		if(canUseSecondaryAbilities(stack)) {
			BlockState bs = world.getBlockState(pos);

			if(bs.is(TagKey.create(Keys.BLOCKS, new ResourceLocation(AssemblyLineMachines.MODID, "mystium_axe_mineable")))) {
				int cmax = ptt == IToolWithCharge.PowerToolType.NOVASTEEL ? 50 : 10;
				stack.hurtAndBreak(breakAndBreakConnected(world, bs, 0, cmax, pos, player), player, (p_220038_0_) -> {p_220038_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);});
			}
			return true;
		}
		return super.mineBlock(stack, world, state, pos, player);
		
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return this.defaultInitCapabilities(stack, nbt);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
		this.defaultUse(p_41432_, p_41433_, p_41434_);
		return super.use(p_41432_, p_41433_, p_41434_);
	}
	
	@Override
	public void appendHoverText(ItemStack p_41421_, Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
		super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
		this.addEnergyInfoToHoverText(p_41421_, p_41423_);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		if(!stack.hasTag() || stack.getTag().getInt(ptt.keyName) == 0) return super.isBarVisible(stack);
		return stack.getTag().getInt(ptt.keyName) != this.getMaxPower(stack);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		CompoundTag compound = stack.hasTag() ? stack.getTag() : new CompoundTag();
		int dmg = compound.getInt(ptt.keyName);
		if(dmg == 0) {
			return super.getBarColor(stack);
		}else {
			float v = (float) dmg / (float) getMaxPower(stack);
			return ARGB32.color(255, Math.round(v * 255f), Math.round(v * 255f), 255);
		}
		
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		CompoundTag compound = stack.hasTag() ? stack.getTag() : new CompoundTag();
		int dmg = compound.getInt(ptt.keyName);
		return dmg == 0 ? super.getBarWidth(stack) : Math.round(((float)dmg/ (float) getMaxPower(stack)) * 13.0f);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return ptt.borderTexturePath;
	}

	@Override
	public int getTopColor() {
		return ptt.argbBorderColor;
	}
	
	@Override
	public int getBottomColor() {
		return ptt.getBottomARGBBorderColor().orElse(ISpecialTooltip.super.getBottomColor());
	}

	@Override
	public IToolWithCharge.PowerToolType getPowerToolType() {
		return ptt;
	}
	
	private static int breakAndBreakConnected(Level world, BlockState origState, int ctx, int cmax, BlockPos posx, LivingEntity player) {
		world.destroyBlock(posx, true, player);

		int cost = 2;
		Iterator<BlockPos> iter = BlockPos.betweenClosedStream(posx.below().north().west(), posx.above().south().east()).iterator();

		while(iter.hasNext()) {
			BlockPos posq = iter.next();

			BlockState bs = world.getBlockState(posq);
			if(bs.getBlock() == origState.getBlock() && ctx <= cmax) {
				ctx++;
				cost = cost + breakAndBreakConnected(world, origState, ctx, cmax, posq, player);
			}
		}

		return cost;
	}
}
