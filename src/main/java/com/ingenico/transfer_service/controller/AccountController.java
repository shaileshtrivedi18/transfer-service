package com.ingenico.transfer_service.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ingenico.transfer_service.dao.AccountDao;
import com.ingenico.transfer_service.model.Account;
import com.ingenico.transfer_service.model.TransferRequest;

/**
 * Controller class for handling the account operations
 * @author Shailesh Trivedi
 *
 */
@RestController
public class AccountController {

	@Autowired
	AccountDao accountDao;
	
	/**
	 * creates new account with the name given in the request, with x initial balance
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/accounts", method = POST)
	public ResponseEntity<?> create(@Valid @RequestBody Account account){
		//setting the initial account balance
		account.setBalance(new BigDecimal(30));
		accountDao.create(account);
		return ResponseEntity.ok(account);
	}
	
	/**
	 * transfers the given amount from one Account to another and returns the httpStatus based on the outcome
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/accounts/transfer", method = POST)
	public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request){
		return processTransfer(request);
	}
	
	/**
	 * this method takes care of the actual transfer of amount between the specified accounts
	 * The method is declared as synchronized to maintain the integrity of the account data in database in case of parallel transactions
	 * @param request
	 * @return
	 */
	//@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.SERIALIZABLE)
	private synchronized ResponseEntity<?> processTransfer(TransferRequest request){

		Account fromAccount = accountDao.retrieve(request.getFromAccount().getName());
		Account toAccount = accountDao.retrieve(request.getToAccount().getName());

		if(fromAccount == null || toAccount == null){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		//process the transfer only if the request is valid
		if(fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0 || request.getAmount().compareTo(BigDecimal.ZERO) <= 0 
				|| fromAccount.getBalance().compareTo(request.getAmount()) < 0){
			return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
		}

		toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
		fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));

		accountDao.update(fromAccount);
		accountDao.update(toAccount);
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/accounts", method = GET, produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> retrieve(){
		List<Account> accounts = accountDao.retrieve();
		
		return ResponseEntity.ok(accounts);	
	}
	
	/**
	 * returns the account data for a given account name
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/accounts/{name}", method = GET, produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> retrieve(@PathVariable String name){
		Account account = accountDao.retrieve(name);
		
		return account != null ? ResponseEntity.ok(account) : new ResponseEntity(HttpStatus.NOT_FOUND);	
	}
}