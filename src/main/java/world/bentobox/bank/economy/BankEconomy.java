package world.bentobox.bank.economy;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import world.bentobox.bank.BankAddon;
import world.bentobox.bank.dataobjects.IslandBank;
import world.bentobox.bank.dataobjects.PlayerBank;
import world.bentobox.bentobox.database.objects.Island;

public class BankEconomy implements Economy {
	
	private BankAddon addon;
	
	private String name = "BentoBoxBank";
	
	public BankEconomy(BankAddon addon) {
		this.addon = addon;
	}

	@Override
	public boolean isEnabled() {
		return this.addon != null;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean hasBankSupport() {
		return false;
	}
	
	@Override
	public int fractionalDigits() {
		return 2;
	}
	
	@Override
	public String format(double amount) {
		return this.addon.getBankManager().format(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return this.addon.getBankManager().getCurrencyName(true);
	}
	
	@Override
	public String currencyNameSingular() {
		return this.addon.getBankManager().getCurrencyName(false);
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		this.addon.logWarning("hasAccount bad");
		PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
		return playerBank != null;
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		this.addon.logWarning("hasAccount good");
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return false;
			
			IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
			
			return islandBank != null;
			
		} else {
			PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
			
			return playerBank != null;
		}
	}
	
	@Override
	public double getBalance(OfflinePlayer player) {
		this.addon.logWarning("getBalance bad");
		PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
		
		if (playerBank == null)
			return 0;
		
		return playerBank.getBalance(null);
	}
	
	@Override
	public double getBalance(OfflinePlayer player, String worldName) {
		this.addon.logWarning("getBalance good");
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return 0;
			
			IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
			
			return islandBank != null ? islandBank.getBalance() : 0;
			
		} else {
			PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
			
			return playerBank != null ? playerBank.getBalance(worldName) : 0;
		}
	}
	
	@Override
	public boolean has(OfflinePlayer player, double amount) {
		this.addon.logWarning("has bad");
		PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
		
		if (playerBank == null)
			return false;
		
		return playerBank.getBalance(null) >= amount;
	}
	
	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		this.addon.logWarning("has good");
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return false;
			
			IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
			
			return islandBank != null ? islandBank.getBalance() >= amount : false;
			
		} else {
			PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
			
			return playerBank != null ? playerBank.getBalance(worldName) >= amount : false;
		}
	}
	
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		if (amount < 0)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		
		PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
		
		if (playerBank == null)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist");
		
		double playerBalance = playerBank.getBalance(null);
		if (playerBalance < amount)
			return new EconomyResponse(0, playerBalance, ResponseType.FAILURE, "Insufficient found");
		
		double newBalance = playerBalance - amount;
		playerBank.setBalance(null, newBalance);
		return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		if (amount < 0)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player don't have any island");
			
			IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
			
			if (islandBank == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "That island account does not exist");
			
			double islandBalance = islandBank.getBalance();
			if (islandBalance < amount)
				return new EconomyResponse(0, islandBalance, ResponseType.FAILURE, "Insufficient found");
			
			double newBalance = islandBalance - amount;
			islandBank.setBalance(newBalance);
			return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
		} else {
			PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
			
			if (playerBank == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist");
			
			double playerBalance = playerBank.getBalance(worldName);
			if (playerBalance < amount)
				return new EconomyResponse(0, playerBalance, ResponseType.FAILURE, "Insufficient found");
			
			double newBalance = playerBalance - amount;
			playerBank.setBalance(worldName, newBalance);
			return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		if (amount < 0)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		
		PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
		
		if (playerBank == null)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist");
		
		double playerBalance = playerBank.getBalance(null);
		double newBalance = playerBalance + amount;
		
		playerBank.setBalance(null, newBalance);		
		return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		if (amount < 0)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player don't have any island");
			
			IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
			
			if (islandBank == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "That island account does not exist");
			
			double islandBalance = islandBank.getBalance();
			double newBalance = islandBalance + amount;
			
			islandBank.setBalance(newBalance);
			return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
		} else {
			PlayerBank playerBank = this.addon.getPlayerBank(player.getUniqueId());
			
			if (playerBank == null)
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist");
			
			double playerBalance = playerBank.getBalance(worldName);
			double newBalance = playerBalance + amount;
			
			playerBank.setBalance(worldName, newBalance);
			return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
		}
	}
	
	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		this.addon.logWarning("createPlayerAccount bad");
		PlayerBank playerBank = this.addon.createPlayerBank(player.getUniqueId());
		return playerBank != null;
	}
	
	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		this.addon.logWarning("createPlayerAccount good");
		World world = Bukkit.getWorld(worldName);
		
		if (this.addon.getBankManager().isActivateInWorld(world)) {
			Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());
			
			if (island == null)
				return false;
			
			IslandBank islandBank = this.addon.createIslandBank(island.getUniqueId());
			return islandBank != null;
		} else {
			PlayerBank playerBank = this.addon.createPlayerBank(player.getUniqueId());
			return playerBank != null;
		}
	}
	
	@Override
	public EconomyResponse createBank(String name, String player) {
		return null;
	}
	
	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return null;
	}
	
	@Override
	public EconomyResponse deleteBank(String name) {
		return null;
	}
	
	@Override
	public EconomyResponse bankBalance(String name) {
		return null;
	}
	
	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return null;
	}
	
	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return null;
	}
	
	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return null;
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return null;
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return null;
	}
	
	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return null;
	}
	
	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return null;
	}
	
	@Override
	public List<String> getBanks() {
		return null;
	}
	
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName) {
		return this.createPlayerAccount(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer());
	}
	
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return this.createPlayerAccount(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), worldName);
	}
	
	@Deprecated
	@Override
	public boolean hasAccount(String playerName) {
		return this.hasAccount(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer());
	}
	
	@Deprecated
	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return this.hasAccount(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), worldName);
	}
	
	@Deprecated
	@Override
	public double getBalance(String playerName, String world) {
		return this.getBalance(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), world);
	}
	
	@Deprecated
	@Override
	public double getBalance(String playerName) {
		return this.getBalance(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer());
	}
	
	@Deprecated
	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return this.has(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), worldName, amount);
	}
	
	@Deprecated
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return this.withdrawPlayer(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), amount);
	}
	
	@Deprecated
	@Override
	public boolean has(String playerName, double amount) {
		return this.has(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), amount);
	}
	
	@Deprecated
	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return this.withdrawPlayer(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), worldName, amount);
	}
	
	@Deprecated
	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return this.depositPlayer(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), worldName, amount);
	}
	
	@Deprecated
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return this.depositPlayer(this.addon.getPlugin().getPlayers().getUser(playerName).getOfflinePlayer(), amount);
	}
	
}
