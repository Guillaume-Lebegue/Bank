package world.bentobox.bank.command;

import java.util.List;

import world.bentobox.bank.BankAddon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;

public class BankAdminCommand extends CompositeCommand{

	public BankAdminCommand(BankAddon addon, CompositeCommand cmd) {
		super(addon, cmd, "bank");
	}
	
	@Override
	public void setup() {
		this.setOnlyPlayer(false);
		
		new AdminIslandResetCommand(this.getAddon(), this);
	}
	
	@Override
	public boolean execute(User user, String label, List<String> args) {
		this.showHelp(this, user);
		return false;
	}
	
}
