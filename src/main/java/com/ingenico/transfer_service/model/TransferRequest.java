package com.ingenico.transfer_service.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

public class TransferRequest {

	@NotNull
	private Account fromAccount;

	@NotNull
	private Account toAccount;

	@NotNull
	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Account getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}
	public Account getToAccount() {
		return toAccount;
	}
	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}
}