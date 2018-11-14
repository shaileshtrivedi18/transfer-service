package com.ingenico.transfer_service.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * model class for account details
 * @author Shailesh Trivedi
 *
 */
public class Account {

	@NotNull
	@Size(min=1)
	private String name;

	private BigDecimal balance;
	
	public Account(String name, BigDecimal balance){
		this.name = name;
		this.balance = balance;
	}
	
	public Account(){
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}