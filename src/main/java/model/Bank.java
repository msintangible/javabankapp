package model;
import java.sql.SQLException;
import dao.BankUserDAOImpl;

public class Bank{
   
   private double  balance ;
   private int  userId;
   private double accountId; 
   

       // Constructor for setting initial state (used in tests)
    public Bank(int userId, double accountId, double initialBalance) {
        this.userId = userId;
        this.accountId = accountId;
        this.balance = initialBalance;
    }

   public Bank(int userId,double balance) throws SQLException
   { 
       
      this.userId = userId;
      this.balance = balance;
      try{
         BankUserDAOImpl dao = new BankUserDAOImpl();
         this.balance = dao.getBalanceByUserId(userId);
         this.accountId = dao.getAccountIdByUserId(userId);
      }
      catch (SQLException ex) {
         ex.printStackTrace();
         throw ex;
      }
   }
   public Bank(int userId) throws SQLException
   { 
      
      this.userId = userId;
      try{
         BankUserDAOImpl dao = new BankUserDAOImpl();
         this.balance = dao.getBalanceByUserId(userId);
         this.accountId = dao.getAccountIdByUserId(userId);
      }
      catch (SQLException ex) {
         ex.printStackTrace();
         throw ex;
      }
   }     


   public double getAccountId()
   {
      return this.accountId;
   }
   public int getId()
   {
      return this.userId;
   }
   public double getBalance()
   {
      return this.balance;
   }
  
   public boolean deposit(double input) throws SQLException {
      if (input > 0) {
         this.balance += input;
         BankUserDAOImpl dao = new BankUserDAOImpl();
         dao.updateBalanceByUserId(userId, this.balance);
         return true;
      }
      return false;
   }

   public boolean withdraw(double output) throws SQLException {
      if (output > 0 && output <= balance) {
         this.balance -= output;
              BankUserDAOImpl dao = new BankUserDAOImpl();
            dao.updateBalanceByUserId(userId, this.balance);
         return true;
      }
      return false;
   }

   public boolean transfer(Bank otherBankAccount, double amount) throws SQLException
   {
      if(amount>0 && amount<this.balance)
      {
         otherBankAccount.deposit(amount);
         this.withdraw(amount);
         return true;
      }
      return false;
   }
 
   
   @Override
   public String toString()
   {
      return "AccountId: "+this.getAccountId()+"\n UserID: "+this.getId()+"\nBalance: "+this.getBalance();
   }
   public void setAccountId(long generatedAccId) {
      
      this.accountId = generatedAccId;
   }
   }
  
