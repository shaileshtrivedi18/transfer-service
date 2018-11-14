package com.ingenico.transfer_service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ingenico.transfer_service.model.Account;

@Repository
public class AccountDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void create(Account account){
		jdbcTemplate.update("INSERT INTO Account(name, balance) VALUES (?, ?)", account.getName(), account.getBalance());
	}

	public List<Account> retrieve(){
		return jdbcTemplate.query("SELECT name, balance FROM Account", 
				(rs, column) -> new Account(rs.getString("name"), rs.getBigDecimal("balance")));
	}

	//@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.SERIALIZABLE)
	public Account retrieve(String name){
		List<Account> accounts = jdbcTemplate.query("SELECT name, balance FROM Account WHERE name = ?", 
				(rs, column) -> new Account(rs.getString("name"), rs.getBigDecimal("balance"))
				,name);

		return accounts.isEmpty() ? null : accounts.get(0);
	}

	//@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.SERIALIZABLE)
	public void update(Account account){
		jdbcTemplate.update("UPDATE Account SET balance = ? WHERE name = ?", account.getBalance(), account.getName());
	}
}