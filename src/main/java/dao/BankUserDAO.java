package dao;
import java.sql.*;

import model.Bank;
import model.BankUserDTO;

public interface BankUserDAO extends DAO<BankUserDTO>
{
          int insertBank(Bank user)throws SQLException;   
}