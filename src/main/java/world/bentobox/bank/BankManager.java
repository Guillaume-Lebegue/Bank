package world.bentobox.bank;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.World;

import world.bentobox.bentobox.api.addons.GameModeAddon;

public class BankManager {
	
	public BankManager(BankAddon addon) {
		this.addon = addon;
		this.gameModes = new HashSet<>();
	}
	
	public void addGameMode(List<String> gameModes) {
		this.gameModes.addAll(gameModes);
	}
	
	public boolean isActivateInWorld(World world) {
		Optional<GameModeAddon> addon = this.addon.getPlugin().getIWM().getAddon(world);
		
		return addon.isPresent() && this.gameModes.contains(addon.get().getDescription().getName());
	}
	
	public String format(double amount) {
		DecimalFormat formatter = new DecimalFormat("#,#00.00");
		String formatted = formatter.format(amount);
		
		if (formatted.endsWith("."))
			formatted = formatted.substring(0, formatted.length() - 1);
		return formatted;
	}
	
	public String getCurrencyName(boolean plural) {
		return plural ? "Dollars" : "Dollar";
	}
	
	private BankAddon addon;
	
	private Set<String> gameModes;

}
