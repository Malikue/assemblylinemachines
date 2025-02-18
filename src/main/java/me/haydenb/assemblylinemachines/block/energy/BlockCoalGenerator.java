package me.haydenb.assemblylinemachines.block.energy;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import mcjty.theoneprobe.api.*;
import me.haydenb.assemblylinemachines.block.helpers.*;
import me.haydenb.assemblylinemachines.block.helpers.AbstractMachine.ContainerALMBase;
import me.haydenb.assemblylinemachines.block.helpers.BlockTileEntity.BlockScreenBlockEntity;
import me.haydenb.assemblylinemachines.block.helpers.EnergyMachine.ScreenALMEnergyBased;
import me.haydenb.assemblylinemachines.plugins.PluginTOP.PluginTOPRegistry.TOPProvider;
import me.haydenb.assemblylinemachines.registry.ConfigHandler.ConfigHolder;
import me.haydenb.assemblylinemachines.registry.Registry;
import me.haydenb.assemblylinemachines.registry.utils.FormattingHelper;
import me.haydenb.assemblylinemachines.registry.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

public class BlockCoalGenerator extends BlockScreenBlockEntity<BlockCoalGenerator.TECoalGenerator>{

	private static final VoxelShape SHAPE_N = Stream.of(
			Block.box(0, 9, 3, 2, 16, 13),
			Block.box(14, 9, 3, 16, 16, 13),
			Block.box(3, 9, 14, 13, 16, 16),
			Block.box(0, 0, 0, 16, 9, 16),
			Block.box(2, 9, 2, 14, 16, 14)
			).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get();
	
	private static final VoxelShape SHAPE_S = Utils.rotateShape(Direction.NORTH, Direction.SOUTH, SHAPE_N);
	private static final VoxelShape SHAPE_E = Utils.rotateShape(Direction.NORTH, Direction.EAST, SHAPE_N);
	private static final VoxelShape SHAPE_W = Utils.rotateShape(Direction.NORTH, Direction.WEST, SHAPE_N);
	
	public BlockCoalGenerator() {
		super(Block.Properties.of(Material.METAL).strength(3f, 15f).sound(SoundType.METAL), 
				"coal_generator", null, true, Direction.NORTH, TECoalGenerator.class);
		this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
	}
	
	
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		
		Direction d = state.getValue(HorizontalDirectionalBlock.FACING);
		if(d == Direction.WEST) {
			return SHAPE_W;
		}else if(d == Direction.SOUTH) {
			return SHAPE_S;
		}else if(d == Direction.EAST) {
			return SHAPE_E;
		}else {
			return SHAPE_N;
		}
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(HorizontalDirectionalBlock.FACING);
	}
	
	public static class BlockNaphthaTurbine extends Block{

		
		private static final VoxelShape SHAPE_N = Stream.of(
				Block.box(1, 1, 3, 2, 2, 4),Block.box(1, 1, 5, 2, 2, 6),Block.box(1, 1, 10, 2, 2, 11),Block.box(1, 1, 12, 2, 2, 13),
				Block.box(3, 1, 14, 4, 2, 15),Block.box(5, 1, 14, 6, 2, 15),Block.box(10, 1, 14, 11, 2, 15),Block.box(12, 1, 14, 13, 2, 15),
				Block.box(14, 1, 12, 15, 2, 13),Block.box(14, 1, 10, 15, 2, 11),Block.box(14, 1, 5, 15, 2, 6),Block.box(14, 1, 3, 15, 2, 4),
				Block.box(12, 1, 1, 13, 2, 2),Block.box(10, 1, 1, 11, 2, 2),Block.box(5, 1, 1, 6, 2, 2),Block.box(3, 1, 1, 4, 2, 2),
				Block.box(0, 1, 3, 1, 10, 4),Block.box(0, 1, 5, 1, 10, 6),Block.box(0, 1, 10, 1, 10, 11),Block.box(0, 1, 12, 1, 10, 13),
				Block.box(3, 1, 15, 4, 10, 16),Block.box(5, 1, 15, 6, 10, 16),Block.box(10, 1, 15, 11, 10, 16),Block.box(12, 1, 15, 13, 10, 16),
				Block.box(15, 1, 12, 16, 10, 13),Block.box(15, 1, 10, 16, 10, 11),Block.box(15, 1, 5, 16, 10, 6),Block.box(15, 1, 3, 16, 10, 4),
				Block.box(12, 1, 0, 13, 10, 1),Block.box(10, 1, 0, 11, 10, 1),Block.box(5, 1, 0, 6, 10, 1),Block.box(3, 1, 0, 4, 10, 1),Block.box(0, 10, 0, 16, 16, 16),Block.box(2, 0, 2, 14, 10, 14)
				).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		
		private static final VoxelShape SHAPE_S = Utils.rotateShape(Direction.NORTH, Direction.SOUTH, SHAPE_N);
		private static final VoxelShape SHAPE_E = Utils.rotateShape(Direction.NORTH, Direction.EAST, SHAPE_N);
		private static final VoxelShape SHAPE_W = Utils.rotateShape(Direction.NORTH, Direction.WEST, SHAPE_N);
		
		public BlockNaphthaTurbine() {
			super(Block.Properties.of(Material.STONE).strength(3f, 30f).sound(SoundType.STONE));
			this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
		}
		
		
		
		@Override
		protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
			pBuilder.add(HorizontalDirectionalBlock.FACING);
		}
		
		@Override
		public BlockState getStateForPlacement(BlockPlaceContext context) {
			return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,
					context.getHorizontalDirection().getOpposite());
		}
		
		@Override
		public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
			
			Direction d = state.getValue(HorizontalDirectionalBlock.FACING);
			if (d == Direction.WEST) {
				return SHAPE_W;
			} else if (d == Direction.SOUTH) {
				return SHAPE_S;
			} else if (d == Direction.EAST) {
				return SHAPE_E;
			} else {
				return SHAPE_N;
			}
		}

	}
	
	public static class TECoalGenerator extends EnergyMachine<ContainerCoalGenerator> implements ALMTicker<TECoalGenerator>, TOPProvider{

		
		private int genper = 0;
		private int timeremaining = 0;
		private int timer = 0;
		private Integer multiplier = null;
		private Integer naphthaTimeIncrease = null;
		private boolean naphthaActive = false;
		
		public TECoalGenerator(final BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
			super(tileEntityTypeIn, 1, new TranslatableComponent(Registry.getBlock("coal_generator").getDescriptionId()), Registry.getContainerId("coal_generator"), ContainerCoalGenerator.class, new EnergyProperties(false, true, 20000), pos, state);
		}
		
		public TECoalGenerator(BlockPos pos, BlockState state) {
			this(Registry.getBlockEntity("coal_generator"), pos, state);
		}

		@Override
		public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState state, IProbeHitData data) {
			if(genper == 0) {
				probeInfo.horizontal().item(new ItemStack(Items.REDSTONE)).vertical().text(new TextComponent("Idle").withStyle(ChatFormatting.RED)).text(new TextComponent("0 FE/t"));
			}else {
				probeInfo.horizontal().item(new ItemStack(Items.REDSTONE)).vertical().text(new TextComponent("Generating...").withStyle(ChatFormatting.GREEN)).text(new TextComponent("+" + Math.round((float)genper / 2f) + " FE/t").withStyle(ChatFormatting.GREEN));
			}
			
			
		}
		
		@Override
		public void load(CompoundTag compound) {
			super.load(compound);
			
			genper = compound.getInt("assemblylinemachines:initgen");
			timeremaining = compound.getInt("assemblylinemachines:remgen");
			naphthaActive = compound.getBoolean("assemblylinemachines:naphtha");
		}
		
		@Override
		public void saveAdditional(CompoundTag compound) {
			
			compound.putInt("assemblylinemachines:initgen", genper);
			compound.putInt("assemblylinemachines:remgen", timeremaining);
			compound.putBoolean("assemblylinemachines:naphtha", naphthaActive);
			super.saveAdditional(compound);
		}
		@Override
		public boolean isAllowedInSlot(int slot, ItemStack stack) {
			if(ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) != 0) {
				return true;
			}
			return false;
		}

		@Override
		public void tick() {
			if(timer++ == 2) {
				timer = 0;
				if(amount < properties.getCapacity()) {
					boolean sendUpdates = false;
					if(timeremaining <= 0) {
						
						if(contents.get(0) != ItemStack.EMPTY) {
							
							
							if(getLevel().getBlockState(getBlockPos().above()).getBlock() == Registry.getBlock("naphtha_turbine") && getLevel().getBlockState(getBlockPos().relative(Direction.UP, 2)).getBlock() == Registry.getBlock("naphtha_fire")) {
								getLevel().removeBlock(getBlockPos().relative(Direction.UP, 2), false);
								getLevel().playSound(null, getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25f, 1f);
								naphthaActive = true;
							}else {
								naphthaActive = false;
							}
							if(multiplier == null) multiplier = ConfigHolder.getServerConfig().coalGeneratorMultiplier.get();
							int burnTime = Math.round((float) ForgeHooks.getBurnTime(contents.get(0), RecipeType.SMELTING) * multiplier);
							if(burnTime != 0) {
								contents.get(0).shrink(1);
								genper = Math.round((float)(burnTime * 3f) / 90f);
								if(naphthaActive) {
									if(naphthaTimeIncrease == null) naphthaTimeIncrease = ConfigHolder.getServerConfig().naphthaTurbineMultiplier.get();
									timeremaining = 60 * 4;
								}else {
									timeremaining = 60;
								}
								
								sendUpdates = true;
								
							}
						}
					}
					
					if(timeremaining > 0) {
						
						timeremaining--;
						amount += genper;
						if(amount > properties.getCapacity()) {
							amount = properties.getCapacity();
						}
						if(timeremaining == 0) {
							genper = 0;
						}
						sendUpdates = true;
					}
					
					if(sendUpdates) {
						sendUpdates();
					}
					
				}
			}
		}
		
	}
	
	public static class ContainerCoalGenerator extends ContainerALMBase<TECoalGenerator>{

		private static final Pair<Integer, Integer> UPGRADE_POS = new Pair<>(75, 34);
		private static final Pair<Integer, Integer> PLAYER_INV_POS = new Pair<>(8, 84);
		private static final Pair<Integer, Integer> PLAYER_HOTBAR_POS = new Pair<>(8, 142);
		
		public ContainerCoalGenerator(final int windowId, final Inventory playerInventory, final TECoalGenerator tileEntity) {
			super(Registry.getContainerType("coal_generator"), windowId, tileEntity, playerInventory, PLAYER_INV_POS, PLAYER_HOTBAR_POS, 0);
			
			this.addSlot(new AbstractMachine.SlotWithRestrictions(this.tileEntity, 0, UPGRADE_POS.getFirst(), UPGRADE_POS.getSecond(), tileEntity));
		}
		
		
		public ContainerCoalGenerator(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
			this(windowId, playerInventory, Utils.getBlockEntity(playerInventory, data, TECoalGenerator.class));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ScreenCoalGenerator extends ScreenALMEnergyBased<ContainerCoalGenerator>{
		
		TECoalGenerator tsfm;
		
		public ScreenCoalGenerator(ContainerCoalGenerator screenContainer, Inventory inv,
				Component titleIn) {
			super(screenContainer, inv, titleIn, new Pair<>(176, 166), new Pair<>(11, 6), new Pair<>(11, 73), "coal_generator", false, new Pair<>(14, 17), screenContainer.tileEntity, false);
			tsfm = screenContainer.tileEntity;
		}
		
		@Override
		protected void renderTooltip(PoseStack mx, ItemStack stack, int mouseX, int mouseY) {
			int x = (this.width - this.imageWidth) / 2;
			int y = (this.height - this.imageHeight) / 2;
			if(mouseX >= x+74 && mouseY >= y+33 && mouseX <= x+91 && mouseY <= y+50) {
				List<Component> tt = getTooltipFromItem(stack);
				if(tsfm.multiplier == null) tsfm.multiplier = ConfigHolder.getServerConfig().coalGeneratorMultiplier.get();
				int burnTime = Math.round((float) ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * tsfm.multiplier);
				float mul;
				if(tsfm.naphthaActive) {
					if(tsfm.naphthaTimeIncrease == null) tsfm.naphthaTimeIncrease = ConfigHolder.getServerConfig().naphthaTurbineMultiplier.get();
					mul = 60f * tsfm.naphthaTimeIncrease;
				}else {
					mul = 60f;
				}
				tt.add(1, new TextComponent("Approx. " + FormattingHelper.GENERAL_FORMAT.format((((float)burnTime * 3f) / 90f) * mul) + " FE Total").withStyle(ChatFormatting.YELLOW));
				tt.add(1, new TextComponent(FormattingHelper.GENERAL_FORMAT.format(Math.round((float)(burnTime * 3) / 180f)) + " FE/t").withStyle(ChatFormatting.GREEN));
				super.renderComponentTooltip(mx, tt, mouseX, mouseY);
				return;
			}
			super.renderTooltip(mx, stack, mouseX, mouseY);
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
			int x = (this.width - this.imageWidth) / 2;
			int y = (this.height - this.imageHeight) / 2;
			
			if(tsfm.timeremaining != 0) {
				int prog2;
				if(tsfm.naphthaActive) {
					if(tsfm.naphthaTimeIncrease == null) tsfm.naphthaTimeIncrease = ConfigHolder.getServerConfig().naphthaTurbineMultiplier.get();
					prog2 = Math.round(((float) tsfm.timeremaining / (60f * tsfm.naphthaTimeIncrease)) * 12F);
					super.blit(x+77, y+19 + (12 - prog2), 189, 52 + (12 - prog2), 13, prog2);
				}else {
					prog2 = Math.round(((float) tsfm.timeremaining / 60f) * 12F);
					super.blit(x+77, y+19 + (12 - prog2), 176, 52 + (12 - prog2), 13, prog2);
				}
				
			}
			
			if(tsfm.naphthaActive) {
				super.blit(x+75, y+52, 176, 64, 16, 16);
			}
			
			if(tsfm.genper == 0) {
				this.drawCenteredString(this.font, "0/t", x+111, y+38, 0xffffff);
			}else {
				this.drawCenteredString(this.font, "+" + Math.round((float)tsfm.genper / 2f) + "/t", x+111, y+38, 0x76f597);
			}
			
			
			
		}
		
		
	}
}
