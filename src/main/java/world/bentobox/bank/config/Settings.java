package world.bentobox.bank.config;

import java.util.HashSet;
import java.util.Set;

import world.bentobox.bank.BankAddon;

public class Settings {
	
	public Settings(BankAddon addon) {
		this.addon = addon;
		this.addon.saveDefaultConfig();
		
		this.disabledGameModes = new HashSet<>(this.addon.getConfig().getStringList("disabled-gamemodes"));
		
		if (this.addon.getConfig().isDouble("starting-balance"))
			this.startingBalance = this.addon.getConfig().getDouble("starting-balance");
		else
			this.addon.logWarning("Config: starting-balance not found, using default " + this.startingBalance.toString());
		
		if (this.addon.getConfig().isDouble("max-money")) {
			double maxMoneyConfig = this.addon.getConfig().getDouble("max-money");
			
			if (maxMoneyConfig > this.maxMoney)
				this.addon.logError("Config: max-money is too big. Skipping...");
			else
				this.maxMoney = maxMoneyConfig;
		} else
			this.addon.logWarning("Config: max-money not found, using default " + this.maxMoney.toString());
		
		if (this.addon.getConfig().isString("currency-symbol")) {
			String rawSymbol = this.addon.getConfig().getString("currency-symbol");
			Character symbol = rawSymbol.charAt(0);
			
			if (symbol != null)
				this.currencySymbol = symbol;
		} else
			this.addon.logWarning("Config: currency-symbol not found, using default " + this.currencySymbol);
		
	}
	
	public Set<String> getDisabledGameModes() {
		return this.disabledGameModes;
	}
	
	/**
	 * @return the startingBalance
	 */
	public Double getStartingBalance() {
		return startingBalance;
	}
	
	/**
	 * @return the maxMoney
	 */
	public Double getMaxMoney() {
		return maxMoney;
	}

	/**
	 * @return the currencySymbol
	 */
	public Character getCurrencySymbol() {
		return currencySymbol;
	}

	private BankAddon addon;
	
	private Set<String> disabledGameModes;
	
	private Double startingBalance = 100.0;
	
	private Double maxMoney = 10000000000000.0;

	private Character currencySymbol = '$';

}
