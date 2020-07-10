package world.bentobox.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import world.bentobox.bank.command.BankAdminCommand;
import world.bentobox.bank.command.IslandBalanceCommand;
import world.bentobox.bank.config.Settings;
import world.bentobox.bank.dataobjects.IslandBank;
import world.bentobox.bank.dataobjects.PlayerBank;
import world.bentobox.bank.economy.BankEconomy;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.hooks.VaultHook;

public class BankAddon extends Addon {

	@Override
	public void onLoad() {
		super.onLoad();
		this.saveDefaultConfig();
		this.settings = new Settings(this);
	}
	
	@Override
	public void onEnable() {
		if (this.getState().equals(State.DISABLED)) {
			this.logWarning("Bank Addon is not available or disabled!");
			return;
		}
		
		List<String> hookedGameModes = new ArrayList<>();
		
		this.getPlugin().getAddonsManager().getGameModeAddons().stream()
			.filter(g -> !settings.getDisabledGameModes().contains(g.getDescription().getName()))
			.forEach(g -> {
				hookedGameModes.add(g.getDescription().getName());
				
				if (g.getPlayerCommand().isPresent()) {
					new IslandBalanceCommand(this, g.getPlayerCommand().get());
				}
				
				if (g.getAdminCommand().isPresent()) {
					new BankAdminCommand(this, g.getAdminCommand().get());
				}
			});
		
		if (hookedGameModes.size() == 0)
			this.logWarning("Bank addon could not hook into any GameMode ans therefore will act as a regular economy plugin");
		
		this.bankManager = new BankManager(this);
		this.bankManager.addGameMode(hookedGameModes);
		
		this.playerDataBase = new Database<>(this, PlayerBank.class);
		this.islandDataBase = new Database<>(this, IslandBank.class);
		this.playerCache = new HashMap<>();
		this.islandCache = new HashMap<>();
		
		Optional<VaultHook> vault = this.getPlugin().getVault();
		if (!vault.isPresent()) {
			this.logError("Vault plugin not found. Bank Addon will be disabled!");
			this.vault = null;
			this.setState(State.DISABLED);
			return;
		} else
			this.vault = vault.get();
		
		Bukkit.getServicesManager().register(Economy.class, new BankEconomy(this), this.vault.getPlugin(), ServicePriority.High);
		
		this.log("Bank addon enabled");
	}
	
	@Override
	public void onDisable() {
		if (this.playerCache != null)
			this.playerCache.values().forEach(this.playerDataBase::saveObjectAsync);
		if (this.islandCache != null)
			this.islandCache.values().forEach(this.islandDataBase::saveObjectAsync);
	}
	
	@Override
	public void onReload() {
		super.onReload();
		
		this.settings = new Settings(this);
		this.log("Bank Addon reloaded");
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public BankManager getBankManager() {
		return bankManager;
	}
	
	public Database<PlayerBank> getPlayerDataBase() {
		return playerDataBase;
	}
	
	public Database<IslandBank> getIslandDataBase() {
		return islandDataBase;
	}
	
	public PlayerBank getPlayerBank(UUID playerId) {
		PlayerBank playerBank = this.playerCache.get(playerId);
		
		if (playerBank != null)
			return playerBank;
		
		if (this.playerDataBase.objectExists(playerId.toString())) {
			PlayerBank data = this.playerDataBase.loadObject(playerId.toString());
			
			if (data != null)
				this.playerCache.put(playerId, data);
			return data;
		} else
			return null;
	}
	
	public PlayerBank createPlayerBank(UUID playerId) {
		PlayerBank playerBank = this.playerCache.get(playerId);
		
		if (playerBank != null)
			return playerBank;
		
		Double startingBalance = this.settings.getStartingBalance();
		PlayerBank data = this.playerDataBase.objectExists(playerId.toString()) ?
			Optional.ofNullable(this.playerDataBase.loadObject(playerId.toString())).orElse(new PlayerBank(playerId.toString(), startingBalance)) :
			new PlayerBank(playerId.toString(), startingBalance);
			
		this.playerCache.put(playerId, data);
		return data;
	}
	
	public IslandBank getIslandBank(String islandId) {
		IslandBank islandBank = this.islandCache.get(islandId);
		
		if (islandBank != null)
			return islandBank;
		
		if (this.islandDataBase.objectExists(islandId)) {
			IslandBank data = this.islandDataBase.loadObject(islandId);
			
			if (data != null)
				this.islandCache.put(islandId, data);
			return data;
		} else
			return null;
	}
	
	public IslandBank createIslandBank(String islandId) {
		IslandBank islandBank = this.islandCache.get(islandId);
		
		if (islandBank != null)
			return islandBank;
		
		Double startingBalance = this.settings.getStartingBalance();
		IslandBank data = this.islandDataBase.objectExists(islandId) ?
			Optional.ofNullable(this.islandDataBase.loadObject(islandId)).orElse(new IslandBank(islandId, startingBalance)) :
			new IslandBank(islandId, startingBalance);
		
		this.islandCache.put(islandId, data);
		return data;
	}
	
	public void uncachePlayerBank(UUID playerId, boolean save) {
		PlayerBank data = this.playerCache.remove(playerId);
		
		if (data == null)
			return;
		if (save)
			this.playerDataBase.saveObjectAsync(data);
	}
	
	public void uncacheIslandBank(String islandId, boolean save) {
		IslandBank data = this.islandCache.remove(islandId);
		
		if (data == null)
			return;
		if (save)
			this.islandDataBase.saveObjectAsync(data);
	}
	
	public VaultHook getVault() {
		return vault;
	}
	
	private Database<PlayerBank> playerDataBase;
	private Database<IslandBank> islandDataBase;
	private Map<UUID, PlayerBank> playerCache;
	private Map<String, IslandBank> islandCache;
	
	private Settings settings;
	private BankManager bankManager;
	
	private VaultHook vault;
	
}
