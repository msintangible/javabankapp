package dao;
import java.util.List;
import db.Database;
import model.Bank;
import model.BankUserDTO;
import model.PasswordEncryptionService; // Import the service
import java.util.Base64; // Import Base64
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.sql.*;

public class BankUserDAOImpl implements BankUserDAO{
  @Override
  public BankUserDTO get(int id) throws SQLException {
      BankUserDTO bankUser = null;
      // Select hash and salt instead of pin
      String getStatement = "SELECT user_id, first_name, last_name, email, pin_hash, pin_salt, phone_number FROM users WHERE user_id = ?";
      try (Connection con = Database.getConnection();
           PreparedStatement ps = con.prepareStatement(getStatement)) {
          ps.setInt(1, id);
          try (ResultSet rs = ps.executeQuery()) {
              if (rs.next()) {
                  int bankUserId = rs.getInt("user_id");
                  String firstname = rs.getString("first_name");
                  String lastname = rs.getString("last_name");
                  String email = rs.getString("email");
                  String pinHash = rs.getString("pin_hash"); // Get hash
                  String pinSalt = rs.getString("pin_salt"); // Get salt
                  String phoneNo = rs.getString("phone_number");
                  // Use the updated constructor
                  bankUser = new BankUserDTO(bankUserId, firstname, lastname, email, pinHash, pinSalt, phoneNo);
              }
          }
      } catch (SQLException e) {
          e.printStackTrace();
          throw e; // Re-throw or handle
      }
      return bankUser;
  }


   // --- Method to get user data for login 
   public BankUserDTO findUserByEmailForLogin(String email) throws SQLException {
    BankUserDTO bankUser = null;
    // Select columns needed for authentication
    String query = "SELECT user_id, first_name, last_name, email, pin_hash, pin_salt, phone_number FROM users WHERE email = ?";
    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
        ps.setString(1, email);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int bankUserId = rs.getInt("user_id");
                String firstname = rs.getString("first_name");
                String lastname = rs.getString("last_name");
                String userEmail = rs.getString("email");
                String pinHash = rs.getString("pin_hash"); // Get stored hash
                String pinSalt = rs.getString("pin_salt"); // Get stored salt
                String phoneNo = rs.getString("phone_number");
                // Create DTO with retrieved data (including hash and salt)
                bankUser = new BankUserDTO(bankUserId, firstname, lastname, userEmail, pinHash, pinSalt, phoneNo);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e; 
    }
    return bankUser;
}

   @Override
    public List<BankUserDTO> getAll() throws SQLException {
    //get database con    
    
      
     //setup sql statement
      String getAllStatement = "SELECT user_id,first_name,last_name,email,pin,phone_number FROM users";
     
      List<BankUserDTO> bankUsers = new ArrayList<>();
     
     //create statement
     try(
     Connection con = Database.getConnection();
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(getAllStatement))
      {
     //excute the statemen
      while( rs.next()){
         int bankUserId = rs.getInt("user_id");
         String firstname = rs.getString("first_name");
         String lastname = rs.getString("last_name");
         String email = rs.getString("email");
         String pinHash = rs.getString("pin_hash");   // Get hash
         String pinSalt = rs.getString("pin_salt");
         String phoneNo = rs.getString("phone_number");
         bankUsers.add( new BankUserDTO(bankUserId,firstname,lastname,pinHash, pinSalt,email,phoneNo));
      } 
    }catch (SQLException e) {
      e.printStackTrace(); // Optional: handle or log error
      throw e; 
  }
    
           
      return bankUsers;
   }
// --- INSERT Method (Updated) ---
    // Needs the plain PIN to hash it
    // Remove the old insert method signature if it only took BankUserDTO
    public int insert(BankUserDTO bankUser, String plainPin) throws SQLException {
      // Insert hash and salt
      String insertStatement = "INSERT INTO users(first_name, last_name, email, pin_hash, pin_salt, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
      int result = 0;

      try {
          // 1. Generate Salt
          byte[] saltBytes = PasswordEncryptionService.generateSalt();
          String saltString = Base64.getEncoder().encodeToString(saltBytes); // Encode salt

          // 2. Encrypt PIN
          byte[] hashBytes = PasswordEncryptionService.getEncryptedPassword(plainPin, saltBytes);
          String hashString = Base64.getEncoder().encodeToString(hashBytes); // Encode hash

          // 3. Store in DB
          try (Connection con = Database.getConnection();
               PreparedStatement ps = con.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {

              ps.setString(1, bankUser.getFname());
              ps.setString(2, bankUser.getLname());
              ps.setString(3, bankUser.getEmail());
              ps.setString(4, hashString); // Store Base64 hash
              ps.setString(5, saltString); // Store Base64 salt
              ps.setString(6, bankUser.getPhone());

              result = ps.executeUpdate();

              // Get generated ID
              try (ResultSet rs = ps.getGeneratedKeys()) {
                  if (rs.next()) {
                      bankUser.setId(rs.getInt(1));
                      // Optionally set hash/salt on DTO if needed immediately after insert
                      bankUser.setPinHash(hashString);
                      bankUser.setPinSalt(saltString);
                  }
              }
          }
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
          // Handle encryption errors
          e.printStackTrace();
          throw new SQLException("PIN encryption failed.", e);
      } catch (SQLException e) {
          e.printStackTrace();
          throw e; // Re-throw DB errors
      }
      return result;
  }



  @Override
  public int update(BankUserDTO bankUser) throws SQLException {
     
      String updateStatement = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE user_id = ?";
      int result = 0;
      try (Connection con = Database.getConnection();
           PreparedStatement ps = con.prepareStatement(updateStatement)) {

          ps.setString(1, bankUser.getFname());
          ps.setString(2, bankUser.getLname());
          ps.setString(3, bankUser.getEmail());
          ps.setString(4, bankUser.getPhone());
          ps.setInt(5, bankUser.getId()); // WHERE clause

          result = ps.executeUpdate();
      } catch (SQLException e) {
          e.printStackTrace();
          throw e; 
      }
      return result;
  }


   @Override
    public int delete(BankUserDTO bankUser) throws SQLException {
        // Method to delete a BankUserDTO
        int rs = 0;
        // Get the connection
      String deleteStatement = "DELETE FROM users WHERE user_id = ?";
      try(
      Connection con = Database.getConnection();
      PreparedStatement ps = con.prepareStatement(deleteStatement)){
     
      ps.setInt(1,bankUser.getId());
     
       rs = ps.executeUpdate();
      }catch (SQLException e) {
        e.printStackTrace(); 
        throw e;
       
    }   

      return rs;
   }

public int insertBank(Bank user) throws SQLException {
    int res =0;

    // account_id is auto-incremented, so we exclude it from the query
    String insertBankAcc = "INSERT INTO bank_accounts(user_id, balance) VALUES (?, ?)";
    try(
    Connection con = Database.getConnection();
    PreparedStatement ps = con.prepareStatement(insertBankAcc, Statement.RETURN_GENERATED_KEYS)){


    ps.setInt(1, user.getId());
    ps.setDouble(2, user.getBalance());
    res = ps.executeUpdate();
   try(
    // Get the generated account_id
    ResultSet rs = ps.getGeneratedKeys()){
    if (rs.next()) {
        long generatedAccId = rs.getLong(1);
        user.setAccountId(generatedAccId); // You need a setter in your Bank class
    }
    }
  }catch (SQLException e) {
    e.printStackTrace(); // Optional: handle or log error
    throw e; 
}   
    return res;
}
  
  
   public double getBalanceByUserId(int userId) throws SQLException {
 
      String sql = "SELECT balance FROM bank_accounts WHERE user_id = ?";
      try(
      Connection con = Database.getConnection();
      PreparedStatement ps = con.prepareStatement(sql))
      {
      ps.setInt(1, userId);

      try(ResultSet rs = ps.executeQuery()){
   
      if (rs.next()) {
         return rs.getDouble("balance");
      }
    }
  }catch (SQLException e) {
    e.printStackTrace(); // Optional: handle or log error
    throw e; 
}   
      return 0.0; // Default if not found
   }
   public int updateBalanceByUserId(int userId, double newBalance)throws SQLException {
    int result = 0;
    String sql = "UPDATE bank_accounts SET balance = ? WHERE user_id = ?";

    try (
        Connection con = Database.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {
        ps.setDouble(1, newBalance);
        ps.setInt(2, userId);
        result = ps.executeUpdate(); // store result
    } catch (SQLException e) {
        e.printStackTrace(); // optional: log this instead
        throw e; 
    }

    return result;
}


   
public long getAccountIdByUserId(int userId)throws SQLException {
  long accountId = -1; // Default if not found
  String sql = "SELECT account_id FROM bank_accounts WHERE user_id = ?";

  try (
      Connection con = Database.getConnection();
      PreparedStatement ps = con.prepareStatement(sql)
  ) {
      ps.setInt(1, userId);

      try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
              accountId = rs.getLong("account_id");
          }
      }
  } catch (SQLException e) {
      e.printStackTrace(); // Optional: log instead
      throw e; 
  }

  return accountId;
 }
}