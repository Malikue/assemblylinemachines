package me.haydenb.assemblylinemachines.world;

import java.util.Collection;
import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.haydenb.assemblylinemachines.AssemblyLineMachines;
import me.haydenb.assemblylinemachines.plugins.PluginPatchouli;
import me.haydenb.assemblylinemachines.registry.ConfigHandler.ConfigHolder;
import me.haydenb.assemblylinemachines.registry.PacketHandler;
import me.haydenb.assemblylinemachines.registry.PacketHandler.PacketData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = AssemblyLineMachines.MODID)
public class CapabilityBooks {

	public static final Capability<IBookDistroCapability> BOOK_DISTRO_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	@SubscribeEvent
	public static void registerBookDistroCapability(RegisterCapabilitiesEvent event) {
		event.register(IBookDistroCapability.class);
	}
	
	@SubscribeEvent
	public static void attachBookDistroCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof Player player) {
			BookDistroCapability bookDistro = new BookDistroCapability(player);
			LazyOptional<IBookDistroCapability> lazyBookDistro = LazyOptional.of(() -> bookDistro);
			ICapabilitySerializable<CompoundTag> serializableProvider = new ICapabilitySerializable<>() {
				
				@Override
				public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
					if(cap == CapabilityBooks.BOOK_DISTRO_CAPABILITY) {
						return lazyBookDistro.cast();
					}
					return LazyOptional.empty();
				}
				
				@Override
				public CompoundTag serializeNBT() {
					return bookDistro.save();
				}
				
				@Override
				public void deserializeNBT(CompoundTag nbt) {
					bookDistro.load(nbt);
				}
			};
			event.addCapability(new ResourceLocation(AssemblyLineMachines.MODID, "book_distro"), serializableProvider);
		}
	}
	
	//The Forge event to register a Client connection on the clientside only, and send a request to the server to give book.
	@SubscribeEvent
	public static void clientSendGuideBookRequest(LoggedInEvent event) {
		if(ConfigHolder.getClientConfig().receiveGuideBook.get()) {
			AssemblyLineMachines.LOGGER.debug("Sending request for guide to server from player " + event.getPlayer().getDisplayName().getString() + ".");
			PacketData pd = new PacketData("request_book");
			pd.writeUUID("uuid", event.getPlayer().getUUID());
			PacketHandler.INSTANCE.sendToServer(pd);
			return;
		}
	}
	
	//Receives packet request from server to give book to player by UUID.
	public static void guideBookServerRequestHandler(UUID uuid) {
		if(ConfigHolder.getServerConfig().distributeGuideBook.get()) {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
			if(player == null) throw new NullPointerException("UUID-based player lookup failed.");
			AssemblyLineMachines.LOGGER.debug("Received request to dispense book for player " + player.getDisplayName().getString() + ".");
			player.getCapability(CapabilityBooks.BOOK_DISTRO_CAPABILITY).ifPresent((cap) -> cap.giveBook());
			return;
		}
		AssemblyLineMachines.LOGGER.debug("Consuming request to dispense guide from " + uuid.toString() + ".");
	}
	
	public static interface IBookDistroCapability{
		
		/**
		 * Gives a copy of "Assembly Lines & You" to the Player as long as it is enabled in the config to do so.
		 * @return Whether or not a book was successfully given to the Player.
		 */
		public boolean giveBook();
	}
	
	public static class BookDistroCapability implements IBookDistroCapability{
		
		boolean givenBook = false;
		private final Player player;
		
		public BookDistroCapability(Player player) {
			this.player = player;
		}
		
		public CompoundTag save() {
			CompoundTag tag = new CompoundTag();
			
			tag.putBoolean("assemblylinemachines:given_book", givenBook);
			return tag;
		}
		
		public void load(CompoundTag tag) {
			givenBook = tag.getBoolean("assemblylinemachines:given_book");
		}
		
		@Override
		public boolean giveBook() {
			if(givenBook == false) {
				ItemStack book = PluginPatchouli.INTERFACE.get().getBookItem();
				if(!book.isEmpty()) {
					givenBook = true;
					player.addItem(book);
					player.shouldBeSaved();
					AssemblyLineMachines.LOGGER.debug("Guide dispensed successfully.");
					return true;
				}
				
				AssemblyLineMachines.LOGGER.debug("Patchouli is not installed on the server.");
				return false;
			}
			AssemblyLineMachines.LOGGER.debug("Guide has already been dispensed to this player.");
			return false;
		}
	}
	
	@EventBusSubscriber(bus = Bus.FORGE, modid = AssemblyLineMachines.MODID)
	public static class GuidebookCommand{
		
		
		@SubscribeEvent
		public static void register(RegisterCommandsEvent event) {
			event.getDispatcher().register(Commands.literal("assembly-lines-and-you").requires((css) -> css.hasPermission(2))
					.then(Commands.argument("targets", EntityArgument.players()).executes(GuidebookCommand::giveBookToAll)));
		}
		
		private static int giveBookToAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException{
			CommandSourceStack source = context.getSource();
			ItemStack bookStack = PluginPatchouli.INTERFACE.get().getBookItem();
			if(bookStack.isEmpty()) {
				source.sendFailure(new TextComponent("Could not perform command, Patchouli is not installed."));
				return 0;
			}
			Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
			for(ServerPlayer player : players) {
				ItemStack copy = bookStack.copy();
				boolean given = player.getInventory().add(copy);
				if(given && copy.isEmpty()) {
					copy.setCount(1);
					ItemEntity entity = player.drop(copy, false);
					if(entity != null) entity.makeFakeItem();
					player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				}else {
					ItemEntity entity = player.drop(copy, false);
					if(entity != null) {
						entity.setNoPickUpDelay();
						entity.setOwner(player.getUUID());
					}
				}
			}
			
			source.sendSuccess(new TextComponent("Gave a copy of Assembly Lines & You to " + players.size() + " player(s)."), false);
			return players.size();
		}
	}
}
