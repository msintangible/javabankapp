package dao;

import model.BankUserDTO;
import model.Bank;
// Removed unused db.Database import
import java.sql.SQLException;
import org.junit.jupiter.api.Test; // Use JUnit 5 Test annotation
import static org.junit.jupiter.api.Assertions.*; // Use wildcard import for assertions

public class TestBankUserDaoImpl {

  private String plainPin = "1234"; // PIN used for hashing

    @Test
    public void testInsertRetrieveAndDeleteUser() throws SQLException {
        BankUserDAOImpl dao = new BankUserDAOImpl();
        BankUserDTO testUser = new BankUserDTO("Test", "User", "testingssss@example.com", "1234567890");

        // === INSERT USER ===
        int insertResult = dao.insert(testUser, plainPin);
        assertTrue(insertResult > 0, "Insert should return rows affected > 0");
        assertTrue(testUser.getId() > 0, "User ID should be generated and set after insert.");
        System.out.println("Inserted user ID: " + testUser.getId());

        // === INSERT BANK ACCOUNT ===
        Bank testAccount = new Bank(testUser.getId());
        int bankInsertResult = dao.insertBank(testAccount);
        assertTrue(bankInsertResult > 0, "Bank account insert should return rows affected > 0");
        assertTrue(testAccount.getAccountId() > 0, "Bank account ID should be generated and set after insert.");
        System.out.println("Inserted account ID: " + testAccount.getAccountId());

        // === RETRIEVE USER ===
        BankUserDTO foundUser = dao.get(testUser.getId());
        assertNotNull(foundUser, "User should be found by ID: " + testUser.getId());
        assertEquals(testUser.getEmail(), foundUser.getEmail(), "Found user's email should match.");

        // === CLEANUP: DELETE USER ===
        int deleteResult = dao.delete(testUser);
        System.out.println("Deleted rows: " + deleteResult);
        assertTrue(deleteResult > 0, "User should be deleted successfully.");

        // Optional: Verify deletion
        BankUserDTO deletedUser = dao.get(testUser.getId());
        assertNull(deletedUser, "User should no longer exist in the database.");
    }

    @Test
    public void testUpdate() throws SQLException {

        BankUserDAOImpl dao = new BankUserDAOImpl();
        BankUserDTO testUser = new BankUserDTO("Test", "User", "testingss5@example.com", "1234567890");

        // === INSERT USER ===
        int insertResult = dao.insert(testUser, plainPin);
        assertTrue(insertResult > 0, "Insert should return rows affected > 0");
        assertTrue(testUser.getId() > 0, "User ID should be generated and set after insert.");
        System.out.println("Inserted user ID: " + testUser.getId());

        // === INSERT BANK ACCOUNT ===
        Bank testAccount = new Bank(testUser.getId());  
        int bankInsertResult = dao.insertBank(testAccount);
        assertTrue(bankInsertResult > 0, "Bank account insert should return rows affected > 0");
        assertTrue(testAccount.getAccountId() > 0, "Bank account ID should be generated and set after insert.");
        System.out.println("Inserted account ID: " + testAccount.getAccountId());

        // === UPDATE USER ===
        testUser.setFname("lol");
        int result = dao.update(testUser);
        assertTrue(result > 0, "Update should return rows affected > 0");
        System.out.println("Updated rows: " + result);

        // === CLEANUP: DELETE USER ===
        int deleteResult = dao.delete(testUser);
        System.out.println("Deleted rows: " + deleteResult);
        assertTrue(deleteResult > 0, "User should be deleted successfully.");

        // Optional: Verify deletion
        BankUserDTO deletedUser = dao.get(testUser.getId());
        assertNull(deletedUser, "User should no longer exist in the database.");
 }
@Test
 public void testfinduserbyemail() throws SQLException {
    
    BankUserDAOImpl dao = new BankUserDAOImpl();
    BankUserDTO testUser = new BankUserDTO("Test", "User", "trestingsss@example.com", "1234567890");

    // === INSERT USER ===
    int insertResult = dao.insert(testUser, plainPin);
    assertTrue(insertResult > 0, "Insert should return rows affected > 0");
    assertTrue(testUser.getId() > 0, "User ID should be generated and set after insert.");
    System.out.println("Inserted user ID: " + testUser.getId());

    // === INSERT BANK ACCOUNT ===
    Bank testAccount = new Bank(testUser.getId());  
    int bankInsertResult = dao.insertBank(testAccount);
    assertTrue(bankInsertResult > 0, "Bank account insert should return rows affected > 0");
    assertTrue(testAccount.getAccountId() > 0, "Bank account ID should be generated and set after insert.");
    System.out.println("Inserted account ID: " + testAccount.getAccountId());
    
    BankUserDTO foundUser = dao.findUserByEmailForLogin(testUser.getEmail());
    assertNotNull(foundUser, "User should be found by email: " + testUser.getEmail());
    // === CLEANUP: DELETE USER ===
    int deleteResult = dao.delete(testUser);
    System.out.println("Deleted rows: " + deleteResult);
    assertTrue(deleteResult > 0, "User should be deleted successfully.");

    // Optional: Verify deletion
    BankUserDTO deletedUser = dao.get(testUser.getId());
    assertNull(deletedUser, "User should no longer exist in the database.");
  }

  @Test
  public void testGetAndUpdateBalance() throws SQLException {
    BankUserDAOImpl dao = new BankUserDAOImpl();
    BankUserDTO testUser = new BankUserDTO("Test", "User", "tsestingsss@example.com", "1234567890");

    // === INSERT USER ===
    int insertResult = dao.insert(testUser, plainPin);
    assertTrue(insertResult > 0, "Insert should return rows affected > 0");
    assertTrue(testUser.getId() > 0, "User ID should be generated and set after insert.");
    System.out.println("Inserted user ID: " + testUser.getId());

    // === INSERT BANK ACCOUNT ===
    Bank testAccount = new Bank(testUser.getId());  
    int bankInsertResult = dao.insertBank(testAccount);
    assertTrue(bankInsertResult > 0, "Bank account insert should return rows affected > 0");
    assertTrue(testAccount.getAccountId() > 0, "Bank account ID should be generated and set after insert.");
    System.out.println("Inserted account ID: " + testAccount.getAccountId());

      // Assert initial balance (should be 0 after insertBank)
      double initialBalance = dao.getBalanceByUserId(testUser.getId());
      assertEquals(0.0, initialBalance, 0.001, "Initial balance should be 0.0.");

      // Act 1: Update the balance
      double newBalance = 123.45;
      int updateResult = dao.updateBalanceByUserId(testUser.getId(), newBalance);

      // Assert 1: Check update result
      assertTrue(updateResult > 0, "Balance update should affect rows.");

      // Act 2: Get the balance again
      double updatedBalance = dao.getBalanceByUserId(testUser.getId());

      // Assert 2: Check if the balance was updated correctly
      assertEquals(newBalance, updatedBalance, 0.001, "Balance should be updated.");
      // === CLEANUP: DELETE USER ===
      int deleteResult = dao.delete(testUser);
      System.out.println("Deleted rows: " + deleteResult);
      assertTrue(deleteResult > 0, "User should be deleted successfully.");

      // Optional: Verify deletion
      BankUserDTO deletedUser = dao.get(testUser.getId());
      assertNull(deletedUser, "User should no longer exist in the database.");
  }


}
