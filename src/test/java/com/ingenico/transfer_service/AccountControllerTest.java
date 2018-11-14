package com.ingenico.transfer_service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.ingenico.transfer_service.model.Account;
import com.ingenico.transfer_service.model.TransferRequest;

/**
 * Test class for testing Account related operations
 * @author Shailesh Trivedi
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {

    @LocalServerPort
    private int port;

    private String accountsBaseURL;
    
    private String transferURL;
    
    private String fetchAccountURL;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
    	accountsBaseURL = "http://localhost:" + port + "/accounts";
    	transferURL = accountsBaseURL + "/transfer";
    	fetchAccountURL = accountsBaseURL + "/{name}";
    }
    
    private Account createAccount(String name){
    	Account account = new Account();
    	account.setName(name);
    	return account;
    }
    
    private TransferRequest createTransferRequest(Account fromAccount, Account toAccount, BigDecimal amount){
    	TransferRequest request = new TransferRequest();
    	request.setFromAccount(fromAccount);
    	request.setToAccount(toAccount);
    	request.setAmount(amount);
    	
    	return request;
    }
    
    /**
     * test the parallel transfers for the created accounts and valid the account balance
     */
    @Test
    public void testParallelTransfers() {
    	
    	Account account1 = createAccount("Customer1");
    	Account account2 = createAccount("Customer2");
    	Account account3 = createAccount("Customer3");
    	
    	List<Account> accounts = Arrays.asList(account1, account2, account3);
    	
    	//Each Account is created with initial balance of 30 Euros
    	accounts.stream().forEach(account -> createAccount(account));
    	
    	TransferRequest transfer1 = createTransferRequest(account1, account3, new BigDecimal(10));
    	TransferRequest transfer2 = createTransferRequest(account2, account3, new BigDecimal(10));
    	TransferRequest transfer3 = createTransferRequest(account3, account1, new BigDecimal(5));
    	TransferRequest transfer4 = createTransferRequest(account1, account2, new BigDecimal(5));
    	
    	List<TransferRequest> transferRequests = Arrays.asList(transfer1, transfer2,  transfer3, transfer4);
    	
    	//initiate the transfers in parallel
    	transferRequests.parallelStream().forEach(request -> processTransfer(request));
    	
    	account1 = retrieveAccountDetails(account1.getName());
    	account2 = retrieveAccountDetails(account2.getName());
    	account3 = retrieveAccountDetails(account3.getName());

    	assertTrue(account1.getBalance().doubleValue() == 20.0);
    	assertTrue(account2.getBalance().doubleValue() == 25.0);
    	assertTrue(account3.getBalance().doubleValue() == 45.0);
    }

    /**
     * create account in the system and validate if account created successfully
     * @param account
     */
    private void createAccount(Account account){
    	
    	HttpEntity<?> httpEntity = new HttpEntity(account);
    	
    	assertEquals(HttpStatus.OK, restTemplate.exchange(
    			accountsBaseURL,
                HttpMethod.POST,
                httpEntity,
                Account.class
        ).getStatusCode());
    }
    
    private void processTransfer(TransferRequest transferRequest){
    	
    	HttpEntity<?> httpEntity = new HttpEntity(transferRequest);
    	
    	assertEquals(HttpStatus.OK, restTemplate.exchange(
    			transferURL,
                HttpMethod.POST,
                httpEntity,
                String.class
        ).getStatusCode());
    }

    
    private Account retrieveAccountDetails(String accountName) {
        return restTemplate.getForEntity(fetchAccountURL, Account.class, accountName).getBody();
    }
}