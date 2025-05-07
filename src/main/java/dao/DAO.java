package dao;
import java.sql.*;
import java.util.*;
public interface DAO<T>{

    T get(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    int insert(T t,String r) throws SQLException;
    int update(T t)throws SQLException;
    int delete(T t)throws SQLException;     
}