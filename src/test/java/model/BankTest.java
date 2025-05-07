package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;


import org.junit.jupiter.api.Test;
//unit test isolates one piece of code class or test vto verify it does what it does
public class BankTest {

    @Test
    public void testDeposit() throws SQLException {
        Bank bank = new Bank(1, 100.0, 50.0);
        boolean result = bank.deposit(50.0);
        assertTrue(result);
        assertEquals(100.0, bank.getBalance(), 0.001);
    }

    @Test
    public void testWithdraw() throws SQLException {
        Bank bank = new Bank(1, 100.0,100.0);
        boolean result = bank.withdraw(30.0);
        assertTrue(result);
        assertEquals(70.0, bank.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawTooMuch() throws SQLException {
        Bank bank = new Bank(1,100.0, 100.0);
        boolean result = bank.withdraw(150.0);
        assertFalse(result,"withdraw should fail for a negative amount.");
        assertEquals(100.0, bank.getBalance(), 0.001);
    }


@Test
public void testDepositNegativeAmount() throws SQLException {
    Bank bank = new Bank(1,100.0, 100.0);
    boolean result = bank.deposit(-50.0);
    assertFalse(result, "Deposit should fail for a negative amount.");
    assertEquals(100.0, bank.getBalance(), 0.001, "Balance should not change when depositing a negative amount.");
}

/**
     * Tests a successful transfer between two accounts.
     * Covers the core logic of the transfer functionality.
     */
    @Test
    public void testSuccessfulTransfer() throws SQLException {
        Bank bank1 = new Bank(1,100.0,100.0);
        Bank bank2 = new Bank(2,100.2, 200.0);
        double transferAmount = 40.0;
        double initialBank1Balance = bank1.getBalance();
        double initialBank2Balance = bank2.getBalance();

        boolean result = bank1.transfer(bank2, transferAmount);

        assertTrue(result, "Transfer should succeed for a valid transaction.");
        assertEquals(initialBank1Balance - transferAmount, bank1.getBalance(), 0.001, "Sender's balance should decrease.");
        assertEquals(initialBank2Balance + transferAmount, bank2.getBalance(), 0.001, "Receiver's balance should increase.");
    }

    /**
     * Tests a transfer attempt when the sender has insufficient funds.
     * Covers a critical failure path for transfer.
     */
    @Test
    public void testTransferInsufficientFunds() throws SQLException {
        Bank bank1 = new Bank(1, 100.0);
        Bank bank2 = new Bank(2, 200.0);
        double transferAmount = 150.0; // More than bank1's balance
        double initialBank1Balance = bank1.getBalance();
        double initialBank2Balance = bank2.getBalance();

        boolean result = bank1.transfer(bank2, transferAmount);

        assertFalse(result, "Transfer should fail when sender has insufficient funds.");
        assertEquals(initialBank1Balance, bank1.getBalance(), 0.001, "Sender's balance should not change.");
        assertEquals(initialBank2Balance, bank2.getBalance(), 0.001, "Receiver's balance should not change.");
    }

}