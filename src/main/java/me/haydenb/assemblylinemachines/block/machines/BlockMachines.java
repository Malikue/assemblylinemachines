package me.haydenb.assemblylinemachines.block.machines;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import me.haydenb.assemblylinemachines.block.helpers.MachineBuilder;
import me.haydenb.assemblylinemachines.block.helpers.MachineBuilder.MachineBlockEntityBuilder.IMachineDataBridge;
import me.haydenb.assemblylinemachines.block.helpers.MachineBuilder.MachineScreenBuilder.IScreenDataBridge;
import me.haydenb.assemblylinemachines.block.helpers.MachineBuilder.RegisterableMachine;
import me.haydenb.assemblylinemachines.block.helpers.MachineBuilder.RegisterableMachine.Phases;
import me.haydenb.assemblylinemachines.crafting.*;
import me.haydenb.assemblylinemachines.registry.StateProperties;
import me.haydenb.assemblylinemachines.registry.StateProperties.BathCraftingFluids;
import me.haydenb.assemblylinemachines.registry.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockMachines {

	//ALLOY SMELTER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="alloy_smelter")
	public static Block alloySmelter() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
			Block.box(0, 0, 0, 16, 3, 16),Block.box(0, 13, 0, 16, 16, 16),
			Block.box(0, 3, 0, 3, 13, 5),Block.box(13, 3, 0, 16, 13, 5),
			Block.box(0, 3, 11, 16, 13, 16),Block.box(3, 3, 5, 3, 13, 11),
			Block.box(13, 3, 5, 13, 13, 11),Block.box(3, 6, 0, 13, 9, 1),
			Block.box(3, 9, 0, 6, 13, 1),Block.box(10, 9, 0, 13, 13, 1),
			Block.box(4, 3, 0, 7, 6, 1),Block.box(9, 3, 0, 12, 6, 1),
			Block.box(6, 9, 1, 10, 13, 1),Block.box(3, 3, 1, 13, 6, 1),
			Block.box(13, 3, 6, 14, 13, 7),Block.box(2, 3, 6, 3, 13, 7),
			Block.box(2, 3, 9, 3, 13, 10),Block.box(13, 3, 9, 14, 13, 10),
			Block.box(13, 3, 8, 15, 4, 11),Block.box(1, 4, 5, 3, 5, 8),
			Block.box(1, 6, 5, 3, 7, 8),Block.box(1, 8, 5, 3, 9, 8),
			Block.box(1, 10, 5, 3, 11, 8),Block.box(1, 12, 5, 3, 13, 8),
			Block.box(1, 3, 8, 3, 4, 11),Block.box(1, 5, 8, 3, 6, 11),
			Block.box(1, 7, 8, 3, 8, 11),Block.box(1, 9, 8, 3, 10, 11),
			Block.box(1, 11, 8, 3, 12, 11),Block.box(13, 5, 8, 15, 6, 11),
			Block.box(13, 7, 8, 15, 8, 11),Block.box(13, 9, 8, 15, 10, 11),
			Block.box(13, 11, 8, 15, 12, 11),Block.box(13, 4, 5, 15, 5, 8),
			Block.box(13, 6, 5, 15, 7, 8),Block.box(13, 8, 5, 15, 9, 8),
			Block.box(13, 10, 5, 15, 11, 8),Block.box(13, 12, 5, 15, 13, 8)
			).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).build("alloy_smelter");
	}

	@RegisterableMachine(phase=Phases.CONTAINER, blockName="alloy_smelter")
	public static MenuType<?> alloySmelterContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(
				Triple.of(119, 34, true), Triple.of(54, 34, false), Triple.of(75, 34, false), Triple.of(149, 21, false),
				Triple.of(149, 39, false), Triple.of(149, 57, false))).build("alloy_smelter");
	}

	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="alloy_smelter")
	public static BlockEntityType<?> alloySmelterEntity() {
		return MachineBuilder.blockEntity().energy(40000).baseProcessingStats(200, 16).recipeProcessor(Utils.recipeFunction(AlloyingCrafting.ALLOYING_RECIPE)).slotInfo(6, 3).stackManagement((i) -> i >= 1 && i <= 2, null, null).build("alloy_smelter");
	}

	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="alloy_smelter")
	public static void alloySmelterScreen() {
		MachineBuilder.screen().blitLRProgressBar(95, 35, 176, 64, 16, 14).blitWhenActive(76, 53, 176, 52, 13, 12).buildAndRegister("alloy_smelter");
	}
	
	//MKII ALLOY SMELTER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="mkii_alloy_smelter")
	public static Block mkIIAlloySmelter() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
			Block.box(0, 0, 0, 16, 3, 16),Block.box(0, 13, 2, 16, 16, 16),
			Block.box(0, 13, 0, 6, 16, 2),Block.box(10, 13, 0, 16, 16, 2),
			Block.box(6, 15, 0, 10, 16, 2),Block.box(0, 3, 0, 3, 13, 5),
			Block.box(13, 3, 0, 16, 13, 5),Block.box(0, 3, 11, 16, 13, 16),
			Block.box(3, 3, 5, 3, 13, 11),Block.box(13, 3, 5, 13, 13, 11),
			Block.box(3, 6, 0, 13, 9, 2),Block.box(3, 9, 0, 6, 13, 2),
			Block.box(10, 9, 0, 13, 13, 2),Block.box(4, 3, 0, 7, 6, 1),
			Block.box(9, 3, 0, 12, 6, 1),Block.box(6, 9, 2, 10, 13, 2),
			Block.box(3, 3, 1, 13, 6, 1),Block.box(13, 3, 6, 14, 13, 7),
			Block.box(2, 3, 6, 3, 13, 7),Block.box(2, 3, 9, 3, 13, 10),
			Block.box(13, 3, 9, 14, 13, 10),Block.box(13, 3, 8, 15, 4, 11),
			Block.box(1, 4, 5, 3, 5, 8),Block.box(1, 6, 5, 3, 7, 8),
			Block.box(1, 8, 5, 3, 9, 8),Block.box(1, 10, 5, 3, 11, 8),
			Block.box(1, 12, 5, 3, 13, 8),Block.box(1, 3, 8, 3, 4, 11),
			Block.box(1, 5, 8, 3, 6, 11),Block.box(1, 7, 8, 3, 8, 11),
			Block.box(1, 9, 8, 3, 10, 11),Block.box(1, 11, 8, 3, 12, 11),
			Block.box(13, 5, 8, 15, 6, 11),Block.box(13, 7, 8, 15, 8, 11),
			Block.box(13, 9, 8, 15, 10, 11),Block.box(13, 11, 8, 15, 12, 11),
			Block.box(13, 4, 5, 15, 5, 8),Block.box(13, 6, 5, 15, 7, 8),
			Block.box(13, 8, 5, 15, 9, 8),Block.box(13, 10, 5, 15, 11, 8),
			Block.box(13, 12, 5, 15, 13, 8),Block.box(7, 9, 1, 9, 15, 2),
			Block.box(6, 13, 0, 10, 14, 2)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), true).build("mkii_alloy_smelter");
	}
	
	@RegisterableMachine(phase=Phases.CONTAINER, blockName="mkii_alloy_smelter")
	public static MenuType<?> mkIIAlloySmelterContainer(){
		return MachineBuilder.container().shiftMergeableSlots(2, 6).playerInventoryPos(8, 106).playerHotbarPos(8, 164).slotCoordinates(List.of(
				Triple.of(53, 61, true), Triple.of(107, 61, true), Triple.of(42, 22, false), Triple.of(64, 22, false), Triple.of(96, 22, false),
				Triple.of(118, 22, false), Triple.of(149, 21, false), Triple.of(149, 39, false), Triple.of(149, 57, false), Triple.of(167, 21, false), Triple.of(167, 39, false), Triple.of(167, 57, false))).build("mkii_alloy_smelter");
	}
	
	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="mkii_alloy_smelter")
	public static BlockEntityType<?> mkIIAlloySmelterEntity(){
		return MachineBuilder.blockEntity().energy(400000).baseProcessingStats(800, 16).recipeProcessor(Utils.recipeFunction(AlloyingCrafting.ALLOYING_RECIPE)).slotInfo(12, 6).slotIDTransformer((in) -> switch(in) {
		case 1 -> 2;
		case 2 -> 3;
		default -> in;
		}).outputSlots(0, 0, 1).dualProcessorIDTransformer((in) -> switch(in) {
		case 0 -> 1;
		case 1 -> 4;
		case 2 -> 5;
		default -> in;
		}).slotExtractableFunction((slot) -> slot < 2).cycleCountModifier(0.5f).stackManagement((i) -> i >= 2 && i <= 5, null, null).build("mkii_alloy_smelter");
	}
	
	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="mkii_alloy_smelter")
	public static void mkIIAlloySmelterScreen() {
		MachineBuilder.mkIIScreen().blitUDProgressBar(57, 42, 190, 66, 8, 11).blitUDDuplicateBar(111, 42).blitWhenActive(80, 43, 190, 52, 16, 14).buildAndRegister("mkii_alloy_smelter");
	}

	//ELECTRIC FURNACE
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="electric_furnace")
	public static Block electricFurnace() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 0, 0, 16, 3, 16),Block.box(0, 13, 0, 16, 16, 16),
				Block.box(0, 3, 13, 16, 13, 16),Block.box(0, 3, 0, 2, 13, 3),
				Block.box(14, 3, 0, 16, 13, 3),Block.box(2, 6, 0, 14, 13, 3),
				Block.box(0, 3, 3, 16, 5, 13),Block.box(0, 11, 3, 16, 13, 13),
				Block.box(4, 3, 0, 7, 6, 1),Block.box(13, 3, 0, 14, 6, 1),
				Block.box(2, 3, 0, 3, 6, 1),Block.box(9, 3, 0, 12, 6, 1),
				Block.box(2, 3, 1, 14, 6, 1),Block.box(2, 5, 3, 2, 11, 13),
				Block.box(14, 5, 3, 14, 11, 13),Block.box(1, 5, 4, 2, 11, 5),
				Block.box(14, 5, 4, 15, 11, 5),Block.box(1, 5, 6, 2, 11, 7),
				Block.box(14, 5, 6, 15, 11, 7),Block.box(1, 5, 9, 2, 11, 10),
				Block.box(14, 5, 9, 15, 11, 10),Block.box(1, 5, 11, 2, 11, 12),Block.box(14, 5, 11, 15, 11, 12)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).build("electric_furnace");
	}

	@RegisterableMachine(phase=Phases.CONTAINER, blockName="electric_furnace")
	public static MenuType<?> electricFurnaceContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(
				Triple.of(119, 34, true), Triple.of(75, 34, false), Triple.of(149, 21, false), Triple.of(149, 39, false),
				Triple.of(149, 57, false))).build("electric_furnace");
	}

	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="electric_furnace")
	public static BlockEntityType<?> electricFurnaceEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(80, 16).recipeProcessor(Utils.recipeFunction(RecipeType.SMELTING))
				.slotInfo(5, 3).executeOnRecipeCompletion((container, recipe) -> {
					container.getItem(1).shrink(1);
					((IMachineDataBridge) container).setCycles(((SmeltingRecipe) recipe).getCookingTime() / 20f);
				})
				.slotIDTransformer((slotIn) -> {
					return switch(slotIn) {
					case 0 -> 1;
					case 1 -> 0;
					default -> slotIn;
					};
				}).build("electric_furnace");
	}

	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="electric_furnace")
	public static void electricFurnaceScreen() {
		MachineBuilder.screen().blitLRProgressBar(95, 35, 176, 64, 16, 14).blitWhenActive(76, 53, 176, 52, 13, 12).buildAndRegister("electric_furnace");
	}

	//ELECTRIC GRINDER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="electric_grinder")
	public static Block electricGrinder() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 14, 0, 16, 16, 16),Block.box(0, 0, 0, 16, 3, 16),
				Block.box(0, 3, 1, 16, 14, 16),Block.box(0, 6, 0, 16, 14, 1),
				Block.box(0, 3, 0, 3, 6, 1), Block.box(13, 3, 0, 16, 6, 1),
				Block.box(9, 3, 0, 12, 6, 1),Block.box(4, 3, 0, 7, 6, 1)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).build("electric_grinder");
	}

	@RegisterableMachine(phase=Phases.CONTAINER, blockName="electric_grinder")
	public static MenuType<?> electricGrinderContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(
				Triple.of(119, 34, true), Triple.of(72, 34, false), Triple.of(149, 21, false), Triple.of(149, 39, false),
				Triple.of(149, 57, false))).build("electric_grinder");
	}

	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="electric_grinder")
	public static BlockEntityType<?> electricGrinderEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(180, 16).recipeProcessor(Utils.recipeFunction(GrinderCrafting.GRINDER_RECIPE))
				.slotInfo(5, 3).build("electric_grinder");
	}

	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="electric_grinder")
	public static void electricGrinderScreen() {
		MachineBuilder.screen().blitLRProgressBar(92, 35, 176, 52, 19, 14).buildAndRegister("electric_grinder");
	}
	
	//ELECTRIC FLUID MIXER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="electric_fluid_mixer")
	public static Block electricFluidMixer() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 14, 0, 16, 16, 16),Block.box(0, 0, 0, 16, 2, 16),
				Block.box(0, 2, 1, 16, 14, 16),Block.box(0, 5, 0, 16, 14, 1),
				Block.box(0, 2, 0, 3, 5, 1),Block.box(13, 2, 0, 16, 5, 1),
				Block.box(9, 2, 0, 12, 5, 1),Block.box(4, 2, 0, 7, 5, 1),Block.box(7, 4, 0, 9, 5, 1)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true)
				.additionalProperties((state) -> state.setValue(StateProperties.FLUID, BathCraftingFluids.NONE),
						(builder) -> builder.add(StateProperties.FLUID))
				.rightClickAction((state, world, pos, player) -> {
					if(player.getMainHandItem().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && world.getBlockEntity(pos) instanceof IMachineDataBridge) {
						FluidActionResult far = FluidUtil.tryEmptyContainer(player.getMainHandItem(), ((IMachineDataBridge) world.getBlockEntity(pos)).getCraftingFluidHandler(Optional.of(true)), 1000, player, true);
						if(far.isSuccess()) {
							if(player.getMainHandItem().getCount() == 1) {
								player.getInventory().removeItemNoUpdate(player.getInventory().selected);
							}else {
								player.getMainHandItem().shrink(1);
							}
							ItemHandlerHelper.giveItemToPlayer(player, far.getResult());
							return InteractionResult.CONSUME;
						}
					}
					return null;
				}).build("electric_fluid_mixer");
	}
	
	@RegisterableMachine(phase=Phases.CONTAINER, blockName="electric_fluid_mixer")
	public static MenuType<?> electricFluidMixerContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(Triple.of(119, 34, true), Triple.of(54, 34, false),
				Triple.of(75, 34, false), Triple.of(149, 21, false), Triple.of(149, 39, false), Triple.of(149, 57, false))).build("electric_fluid_mixer");
	}
	
	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="electric_fluid_mixer")
	public static BlockEntityType<?> electricFluidMixerEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(60, 16).recipeProcessor(Utils.recipeFunction(BathCrafting.BATH_RECIPE))
				.slotInfo(6, 3).processesFluids(4000, true).specialStateModifier((recipe, state) -> {
					return state.setValue(StateProperties.FLUID, ((BathCrafting) recipe).getFluid());
				}).stackManagement((i) -> i >= 1 && i <= 2, null, null).build("electric_fluid_mixer");
	}
	
	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="electric_fluid_mixer")
	public static void electricFluidMixerScreen() {
		MachineBuilder.screen().renderFluidBar(41, 23, 37, 176, 84).addCustomBackgroundRenderer((screen, x, y) ->{
			IScreenDataBridge data = (IScreenDataBridge) screen;
			if(data.getDataBridge().getCycles() != 0f) {
				BathCraftingFluids bcf = ((BlockEntity)data.getDataBridge()).getBlockState().getValue(StateProperties.FLUID);
				if(bcf != BathCraftingFluids.NONE) {
					int prog = Math.round((data.getDataBridge().getProgress() / data.getDataBridge().getCycles()) * 15f);
					screen.blit(data.getPoseStack(), x+95, y+34, bcf.getElectricBlitPiece().getFirst(), bcf.getElectricBlitPiece().getSecond(), prog, 16);
				}
			}
		}).internalTankSwitchingButton(129, 57, 192, 41, 11, 11).buildAndRegister("electric_fluid_mixer");
	}
	
	//MKII FLUID MIXER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="mkii_fluid_mixer")
	public static Block mkIIFluidMixer() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 14, 0, 16, 16, 16),Block.box(0, 0, 0, 16, 2, 16),
				Block.box(0, 2, 1, 16, 14, 3),Block.box(0, 5, 0, 16, 14, 1),
				Block.box(0, 2, 0, 3, 5, 1),Block.box(13, 2, 0, 16, 5, 1),
				Block.box(9, 2, 0, 12, 5, 1),Block.box(4, 2, 0, 7, 5, 1),
				Block.box(7, 4, 0, 9, 5, 1),Block.box(4, 2, 3, 12, 14, 13),
				Block.box(0, 2, 13, 16, 14, 16),Block.box(1, 4, 5, 2, 12, 6),
				Block.box(2, 4, 5, 4, 5, 6),Block.box(2, 11, 5, 4, 12, 6),
				Block.box(0, 6, 4, 3, 7, 7),Block.box(3, 3, 4, 4, 6, 7),
				Block.box(3, 10, 4, 4, 13, 7),Block.box(0, 9, 4, 3, 10, 7),
				Block.box(3, 10, 9, 4, 13, 12),Block.box(2, 11, 10, 4, 12, 11),
				Block.box(1, 4, 10, 2, 12, 11),Block.box(0, 9, 9, 3, 10, 12),
				Block.box(0, 6, 9, 3, 7, 12),Block.box(3, 3, 9, 4, 6, 12),
				Block.box(2, 4, 10, 4, 5, 11),Block.box(14, 4, 10, 15, 12, 11),
				Block.box(12, 4, 10, 14, 5, 11),Block.box(12, 11, 10, 14, 12, 11),
				Block.box(13, 6, 9, 16, 7, 12),Block.box(12, 3, 9, 13, 6, 12),
				Block.box(12, 10, 9, 13, 13, 12),Block.box(13, 9, 9, 16, 10, 12),
				Block.box(12, 10, 4, 13, 13, 7),Block.box(12, 11, 5, 14, 12, 6),
				Block.box(14, 4, 5, 15, 12, 6),Block.box(13, 9, 4, 16, 10, 7),
				Block.box(13, 6, 4, 16, 7, 7),Block.box(12, 3, 4, 13, 6, 7),Block.box(12, 4, 5, 14, 5, 6)
				).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), true)
				.additionalProperties((state) -> state.setValue(StateProperties.FLUID, BathCraftingFluids.NONE),
						(builder) -> builder.add(StateProperties.FLUID))
				.rightClickAction((state, world, pos, player) -> {
					if(player.getMainHandItem().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && world.getBlockEntity(pos) instanceof IMachineDataBridge) {
						FluidActionResult far = FluidUtil.tryEmptyContainer(player.getMainHandItem(), ((IMachineDataBridge) world.getBlockEntity(pos)).getCraftingFluidHandler(Optional.of(true)), 1000, player, true);
						if(far.isSuccess()) {
							if(player.getMainHandItem().getCount() == 1) {
								player.getInventory().removeItemNoUpdate(player.getInventory().selected);
							}else {
								player.getMainHandItem().shrink(1);
							}
							ItemHandlerHelper.giveItemToPlayer(player, far.getResult());
							return InteractionResult.CONSUME;
						}
					}
					return null;
				}).build("mkii_fluid_mixer");
	}
	
	@RegisterableMachine(phase=Phases.CONTAINER, blockName="mkii_fluid_mixer")
	public static MenuType<?> mkIIFluidMixerContainer(){
		return MachineBuilder.container().shiftMergeableSlots(2, 6).playerInventoryPos(8, 106).playerHotbarPos(8, 164).slotCoordinates(List.of(Triple.of(53, 65, true), Triple.of(107, 65, true), Triple.of(42, 22, false), Triple.of(64, 22, false), Triple.of(96, 22, false),
				Triple.of(118, 22, false), Triple.of(149, 21, false), Triple.of(149, 39, false), Triple.of(149, 57, false), Triple.of(167, 21, false), Triple.of(167, 39, false), Triple.of(167, 57, false))).build("mkii_fluid_mixer");
	}
	
	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="mkii_fluid_mixer")
	public static BlockEntityType<?> mkIIFluidMixerEntity(){
		return MachineBuilder.blockEntity().energy(400000).baseProcessingStats(240, 16).recipeProcessor(Utils.recipeFunction(BathCrafting.BATH_RECIPE)).slotInfo(12, 6).specialStateModifier((recipe, state) -> {
					return state.setValue(StateProperties.FLUID, ((BathCrafting) recipe).getFluid());
				}).slotIDTransformer((in) -> switch(in) {
				case 1 -> 2;
				case 2 -> 3;
				default -> in;
				}).outputSlots(0, 0, 1).dualProcessorIDTransformer((in) -> switch(in) {
				case 0 -> 1;
				case 1 -> 4;
				case 2 -> 5;
				default -> in;
				}).slotExtractableFunction((slot) -> slot < 2).cycleCountModifier(0.5f).processesFluids(8000, true).stackManagement((i) -> i >= 2 && i <= 5, null, null).build("mkii_fluid_mixer");
	}
	
	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="mkii_fluid_mixer")
	public static void mkIIFluidMixerScreen() {
		MachineBuilder.mkIIScreen().renderFluidBar(84, 33, 37, 190, 52).internalTankSwitchingButton(82, 72, 206, 40, 12, 12).addCustomBackgroundRenderer((screen, x, y) ->{
			IScreenDataBridge data = (IScreenDataBridge) screen;
			if(data.getDataBridge().getCycles() != 0f) {
				BathCraftingFluids bcf = ((BlockEntity)data.getDataBridge()).getBlockState().getValue(StateProperties.FLUID);
				if(bcf != BathCraftingFluids.NONE) {
					int prog = Math.round((data.getDataBridge().getProgress() / data.getDataBridge().getCycles()) * 14f);
					screen.blit(data.getPoseStack(), x+53, y+42, bcf.getMKIIBlitPiece().getFirst(), bcf.getMKIIBlitPiece().getSecond(), 16, prog);
					screen.blit(data.getPoseStack(), x+107, y+42, bcf.getMKIIBlitPiece().getFirst(), bcf.getMKIIBlitPiece().getSecond(), 16, prog);
				}
			}
		}).buildAndRegister("mkii_fluid_mixer");
	}
	
	//ELECTRIC PURIFIER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="electric_purifier")
	public static Block electricPurifier() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 0, 0, 16, 2, 16),Block.box(4, 2, 0, 5, 7, 1),
				Block.box(6, 2, 0, 7, 7, 1),Block.box(9, 2, 0, 10, 7, 1),
				Block.box(11, 2, 0, 12, 7, 1),Block.box(0, 7, 0, 16, 16, 16),
				Block.box(0, 2, 0, 2, 7, 16),Block.box(14, 2, 0, 16, 7, 16),Block.box(2, 2, 2, 14, 7, 16)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).additionalProperties((state) -> 
				state.setValue(Utils.PURIFIER_STATES, false), (builder) -> builder.add(Utils.PURIFIER_STATES)).build("electric_purifier");
	}
	
	@RegisterableMachine(phase=Phases.CONTAINER, blockName="electric_purifier")
	public static MenuType<?> electricPurifierContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(Triple.of(119, 34, true),
				Triple.of(51, 21, false), Triple.of(51, 47, false), Triple.of(72, 34, false), Triple.of(149, 21, false),
				Triple.of(149, 39, false), Triple.of(149, 57, false))).build("electric_purifier");
	}
	
	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="electric_purifier")
	public static BlockEntityType<?> electricPurifierEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(100, 16).recipeProcessor(Utils.recipeFunction(PurifierCrafting.PURIFIER_RECIPE))
				.slotInfo(7, 3).specialStateModifier((recipe, state) -> {
					return ((PurifierCrafting) recipe).requiresUpgrade() ? state.setValue(Utils.PURIFIER_STATES, true) : state.setValue(Utils.PURIFIER_STATES, false);
				}).stackManagement((i) -> i >= 1 && i <= 3, 3, (is, be) -> be.getLevel().getRecipeManager().getAllRecipesFor(PurifierCrafting.PURIFIER_RECIPE).stream().anyMatch((rcp) -> rcp.isPrimaryIngredient(is))
				).build("electric_purifier");
	}
	
	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="electric_purifier")
	public static void electricPurifierScreen() {
		MachineBuilder.screen().blitLRProgressBar(70, 26, 176, 52, 43, 32).blitLRFrameData(2, 10).buildAndRegister("electric_purifier");
	}
	
	//MKII PURIFIER
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="mkii_purifier")
	public static Block mkIIPurifier() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 0, 0, 16, 2, 16),
				Block.box(4, 2, 0, 5, 7, 1),
				Block.box(6, 2, 0, 7, 7, 1),
				Block.box(9, 2, 0, 10, 7, 1),
				Block.box(11, 2, 0, 12, 7, 1),
				Block.box(0, 7, 0, 16, 16, 16),
				Block.box(0, 2, 0, 2, 7, 16),
				Block.box(14, 2, 0, 16, 7, 16),
				Block.box(2, 2, 2, 14, 7, 16)
				).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), true).additionalProperties((state) -> state.setValue(Utils.PURIFIER_STATES, false), (builder) -> builder.add(Utils.PURIFIER_STATES)).build("mkii_purifier");
	}
	
	@RegisterableMachine(phase=Phases.CONTAINER, blockName="mkii_purifier")
	public static MenuType<?> mkIIPurifierContainer(){
		return MachineBuilder.container().shiftMergeableSlots(2, 6).playerInventoryPos(8, 106).playerHotbarPos(8, 164).slotCoordinates(List.of(Triple.of(53, 73, true), Triple.of(107, 73, true), Triple.of(53, 42, false),
				Triple.of(107, 42, false), Triple.of(71, 22, false), Triple.of(89, 22, false), Triple.of(149, 21, false), Triple.of(149, 39, false), Triple.of(149, 57, false), Triple.of(167, 21, false), Triple.of(167, 39, false), Triple.of(167, 57, false))).build("mkii_purifier");
	}
	
	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="mkii_purifier")
	public static BlockEntityType<?> mkIIPurifierEntity(){
		return MachineBuilder.blockEntity().energy(400000).baseProcessingStats(400, 16).recipeProcessor(Utils.recipeFunction(PurifierCrafting.PURIFIER_RECIPE)).slotInfo(12, 6).specialStateModifier((recipe, state) -> {
			return ((PurifierCrafting) recipe).requiresUpgrade() ? state.setValue(Utils.PURIFIER_STATES, true) : state.setValue(Utils.PURIFIER_STATES, false);
		}).slotIDTransformer((in) -> switch(in) {
		case 1 -> 4;
		case 2 -> 5;
		case 3 -> 2;
		default -> in;
		}).outputSlots(0, 0, 1).dualProcessorIDTransformer((in) -> switch(in) {
		case 0 -> 1;
		case 1 -> 4;
		case 2 -> 5;
		default -> in;
		}).slotExtractableFunction((slot) -> slot < 2).cycleCountModifier(0.5f).stackManagement((i) -> i >= 2 && i <= 5, null, null).build("mkii_purifier");
	}
	
	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="mkii_purifier")
	public static void mkIIPurifierScreen() {
		MachineBuilder.mkIIScreen().blitUDProgressBar(57, 59, 190, 52, 8, 12).blitUDFrameData(2, 20).blitUDDuplicateBar(111, 59)
		.blitWhenActive(59, 20, 190, 88, 58, 21).blitWhenActiveFrameData(3, 40).buildAndRegister("mkii_purifier");
	}

	//METAL SHAPER

	@RegisterableMachine(phase=Phases.BLOCK, blockName="metal_shaper")
	public static Block metalShaper() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 14, 0, 16, 16, 16),Block.box(0, 0, 0, 16, 3, 16),
				Block.box(1, 3, 2, 15, 14, 16),Block.box(1, 6, 0, 15, 14, 1),
				Block.box(0, 3, 0, 3, 6, 1),Block.box(0, 3, 1, 1, 6, 16),
				Block.box(15, 3, 1, 16, 6, 16),Block.box(15, 10, 0, 16, 14, 16),
				Block.box(0, 10, 0, 1, 14, 16),Block.box(13, 3, 0, 16, 6, 1),
				Block.box(9, 3, 0, 12, 6, 1),Block.box(4, 3, 0, 7, 6, 1),
				Block.box(3, 3, 1, 13, 6, 2),Block.box(1, 6, 1, 15, 12, 2)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).build("metal_shaper");
	}

	@RegisterableMachine(phase=Phases.CONTAINER, blockName="metal_shaper")
	public static MenuType<?> metalShaperContainer(){
		return MachineBuilder.container().shiftMergeableSlots(1, 3).slotCoordinates(List.of(Triple.of(119, 34, true),
				Triple.of(72, 34, false), Triple.of(149, 21, false), Triple.of(149, 39, false), Triple.of(149, 57, false))).build("metal_shaper");
	}

	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="metal_shaper")
	public static BlockEntityType<?> metalShaperEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(90, 16).recipeProcessor(Utils.recipeFunction(MetalCrafting.METAL_RECIPE)).slotInfo(5, 3).build("metal_shaper");
	}

	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="metal_shaper")
	public static void metalShaperScreen() {
		MachineBuilder.screen().blitLRProgressBar(92, 37, 176, 52, 19, 10).buildAndRegister("metal_shaper");
	}

	//LUMBER MILL
	
	@RegisterableMachine(phase=Phases.BLOCK, blockName="lumber_mill")
	public static Block lumberMill() {
		return MachineBuilder.block().hasActiveProperty().voxelShape(Stream.of(
				Block.box(0, 14, 0, 16, 16, 16),Block.box(0, 0, 0, 16, 3, 16),
				Block.box(1, 3, 2, 15, 14, 16),Block.box(1, 6, 0, 15, 14, 1),
				Block.box(0, 3, 0, 3, 6, 1),Block.box(0, 3, 1, 1, 6, 16),
				Block.box(15, 3, 1, 16, 6, 16),Block.box(15, 10, 0, 16, 14, 16),
				Block.box(0, 10, 0, 1, 14, 16),Block.box(13, 3, 0, 16, 6, 1),
				Block.box(9, 3, 0, 12, 6, 1),Block.box(4, 3, 0, 7, 6, 1),
				Block.box(3, 3, 1, 13, 6, 2),Block.box(1, 6, 1, 15, 12, 2)
				).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get(), true).build("lumber_mill");
	}

	@RegisterableMachine(phase=Phases.CONTAINER, blockName="lumber_mill")
	public static MenuType<?> lumberMillContainer(){
		return MachineBuilder.container().shiftMergeableSlots(2, 3).slotCoordinates(List.of(Triple.of(98, 34, true),
				Triple.of(124, 34, false), Triple.of(51, 34, false), Triple.of(149, 21, false), Triple.of(149, 39, false),
				Triple.of(149, 57, false))).build("lumber_mill");
	}

	@RegisterableMachine(phase=Phases.BLOCK_ENTITY, blockName="lumber_mill")
	public static BlockEntityType<?> lumberMillEntity(){
		return MachineBuilder.blockEntity().energy(20000).baseProcessingStats(90, 16).recipeProcessor(Utils.recipeFunction(LumberCrafting.LUMBER_RECIPE))
				.slotInfo(6, 3).slotExtractableFunction((slot) -> slot <= 1).build("lumber_mill");
	}

	@OnlyIn(Dist.CLIENT)
	@RegisterableMachine(phase=Phases.SCREEN, blockName="lumber_mill")
	public static void lumberMillScreen() {
		MachineBuilder.screen().addCustomBackgroundRenderer(new TriConsumer<>() {
			private int f, ff, t, tt = 0;
			@Override
			public void accept(AbstractContainerScreen<AbstractContainerMenu> screen, Integer x, Integer y) {
				if(t++ == 10) {
					t = 0;
					f = f >= 1 ? 0 : f + 1;
				}
				if(tt++ == 20) {
					tt = 0;
					ff = ff >= 7 ? 0 : ff + 1;
				}
				IMachineDataBridge data = ((IScreenDataBridge) screen).getDataBridge();
				PoseStack ps = ((IScreenDataBridge) screen).getPoseStack();
				int prog = Math.round((data.getProgress()/data.getCycles()) * 19f);
				screen.blit(ps, x+71, y+40, 176, 64, prog, 5);
				screen.blit(ps, x+71, y+34, 176, 52 + (6*f), prog, 6);
				screen.blit(ps, x+71, y+34, 196, 52 + (6*ff), prog, 6);
			}
		}).buildAndRegister("lumber_mill");
	}
	

}
