package world.bentobox.bank.command;

import java.util.List;

import world.bentobox.bank.BankAddon;
import world.bentobox.bank.dataobjects.IslandBank;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

public class AdminIslandResetCommand extends CompositeCommand {
	
	public AdminIslandResetCommand(BankAddon addon, CompositeCommand cmd) {
		super(addon, cmd, "reset");
		this.addon = addon;
	}

	@Override
	public void setup() {
		this.inheritPermission();
		this.setDescription("bank.commands.adminislandreset");
		this.setOnlyPlayer(false);
	}
	
	@Override
	public boolean canExecute(User user, String label, List<String> args) {
		if (args.size() != 1) {
			this.showHelp(this, user);
			return false;
		}
		
		User targetuser = getPlayers().getUser(args.get(0));
		if (targetuser == null) {
			user.sendMessage("general.erros.unknown-player", TextVariables.NAME, args.get(0));
			return false;
		}
		
		if (!(getIslands().hasIsland(this.getWorld(), targetuser) || getIslands().inTeam(this.getWorld(), targetuser.getUniqueId()))) {
			user.sendMessage("general.errors.player-has-no-island");
            return false;
		}
		
		return super.canExecute(user, label, args);
	}

	@Override
	public boolean execute(User user, String label, List<String> args) {
		User targetUser = getPlayers().getUser(args.get(0));
		Island island = getIslands().getIsland(this.getWorld(), targetUser);
		IslandBank islandBank = this.addon.getIslandBank(island.getUniqueId());
		
		if (islandBank != null) {
			islandBank.resetBalance();
		} else {
			this.addon.createIslandBank(island.getUniqueId());
		}
		
		user.sendMessage("bank.commands.adminislandreset.answer");
		return true;
	}
	
	private BankAddon addon;

}
