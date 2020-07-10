package world.bentobox.bank.dataobjects;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;

@Table(name = "IslandBank")
public class IslandBank implements DataObject {

	@Expose
	private String uniqueId;
	
	@Expose
	private Double startingBalance;
	
	@Expose
	private Double balance;
	
	public IslandBank(String uniqueId, Double startingBalance, Double balance) {
		this.uniqueId = uniqueId;
		this.startingBalance = startingBalance;
		this.balance = balance;
	}
	
	public IslandBank(String uniqueId, Double startingBalance) {
		this(uniqueId, startingBalance, startingBalance);
	}
	
	@Override
	public String getUniqueId() {
		return this.uniqueId;
	}
	
	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the startingBalance
	 */
	public Double getStartingBalance() {
		return startingBalance;
	}

	/**
	 * @param startingBalance the startingBalance to set
	 */
	public void setStartingBalance(Double startingBalance) {
		this.startingBalance = startingBalance;
	}

	/**
	 * @return the balance
	 */
	public Double getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	public void resetBalance() {
		this.balance = this.startingBalance;
	}
	
}
