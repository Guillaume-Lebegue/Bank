package world.bentobox.bank.command;

import java.util.List;

import world.bentobox.bank.BankAddon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

public class IslandBalanceCommand extends CompositeCommand {
	
	public IslandBalanceCommand(BankAddon addon, CompositeCommand cmd) {
		super(addon, cmd, "balance");
		this.addon = addon;
	}

	@Override
	public void setup() {
		this.setDescription("bank.commands.islandbalance.description");
		this.setOnlyPlayer(true);
	}

	@Override
	public boolean canExecute(User user, String label, List<String> args) {
		Island island = getIslands().getIsland(this.getWorld(), user);
		
		if (island == null) {
			user.sendMessage("general.errors.no-island");
			return false;
		}
		
		if (!this.addon.getVault().getEconomy().hasAccount(user.getOfflinePlayer(), this.getWorld().getName()) ) {
			this.addon.logError("Island has no account");
			user.sendMessage("bank.error.islandnoaccount");
			return false;
		}
		
		return super.canExecute(user, label, args);
	}
	
	@Override
	public boolean execute(User user, String label, List<String> args) {
		if (args.size() == 0) {
			Island island = getIslands().getIsland(this.getWorld(), user);
			
			if (island == null) {
				user.sendMessage("general.errors.no-island");
				return false;
			}
			
			Double balance = this.addon.getVault().getEconomy().getBalance(user.getOfflinePlayer(), this.getWorld().getName());
			String formatted = this.addon.getVault().getEconomy().format(balance);
			
			user.sendMessage("bank.commands.islandbalance.answer", "[balance]", formatted);
			return true;
		}
		this.showHelp(this, user);
		return false;
	}
	
	private BankAddon addon;

}
