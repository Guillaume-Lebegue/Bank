package world.bentobox.bank.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;

@Table(name = "PlayerBank")
public class PlayerBank implements DataObject {
	
	@Expose
	private String uniqueId;
	
	@Expose
	private Double startingBalance;
	
	@Expose
	private Double generalAccount;
	
	@Expose
	private Map<String, Double> accountPerWorld;
	
	public PlayerBank() {}
	
	public PlayerBank(String uniqueId, Double startingBalance, Double generalAccount, Map<String, Double> accountPerWorld) {
		this.uniqueId = uniqueId;
		this.startingBalance = startingBalance;
		this.generalAccount = generalAccount;
		this.accountPerWorld = accountPerWorld; 
	}
	
	public PlayerBank(String uniqueId, Double startingBalance) {
		this(uniqueId, startingBalance, startingBalance, new HashMap<>());
	}
	
	@Override
	public String getUniqueId() {
		return this.uniqueId;
	}
	
	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public void setStartingBalance(Double startingBalance) {
		this.startingBalance = startingBalance;
	}
	
	public Double getStartingBalance() {
		return startingBalance;
	}
	
	public Double getBalance(String world) {
		if (world == null)
			return this.generalAccount;
		this.accountPerWorld.putIfAbsent(world, this.startingBalance);
		return this.accountPerWorld.get(world);
	}
	
	public void setBalance(String world, Double amount) {
		if (world == null)
			this.generalAccount = amount;
		else
			this.accountPerWorld.put(world, amount);
	}
	
	public void resetBalance(String world) {
		if (world == null)
			this.generalAccount = this.startingBalance;
		else
			this.accountPerWorld.put(world, this.startingBalance);
	}

}
