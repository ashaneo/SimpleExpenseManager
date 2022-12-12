package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    //Creating a DbHelper object
    private final DbHelper dbHelper;
    public PersistentAccountDAO(DbHelper dbHelper){
        this.dbHelper=dbHelper;
    }


    @Override
    //Function to add a new account
    public void addAccount(Account account) {
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //Opening the database
        ContentValues contentValues = new ContentValues(); //Creating a content value object

        contentValues.put(dbHelper.Account_Number, account.getAccountNo()); //Adding account number to database
        contentValues.put(dbHelper.Bank, account.getBankName()); //Adding bank name to database
        contentValues.put(dbHelper.Account_Holder_Name, account.getAccountHolderName()); //Adding account holders name to database
        contentValues.put(dbHelper.Balance, account.getBalance()); //Adding initial account balance to database

        database.insert(dbHelper.Account_Table, null, contentValues);
        database.close();
    }

    @Override
    //Function to remove an account
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase database = dbHelper.getWritableDatabase(); //Opening the database
        String[] parameters = {accountNo};//Creating a empty array of strings

        String queryString = "DELETE FROM " + dbHelper.Account_Table + " WHERE " + dbHelper.Account_Number + "= ?"; //Deleting data according to the given account number
        Cursor cursor = database.rawQuery(queryString, parameters);
        database.close();
    }

    @Override
    //Function to return a list with all the wanted details
    public List<String> getAccountNumbersList() {

        //Creating a list to save account numbers
        List<String> accountNumbersList = new ArrayList<>();

        //Getting the account number from account details table
        String queryAccNumbers = "SELECT " + dbHelper.Account_Number + " FROM " + dbHelper.Account_Table;

        SQLiteDatabase database = dbHelper.getReadableDatabase(); //Getting a readable database
        Cursor cursor = database.rawQuery(queryAccNumbers, null); //Crating a cursor

        //Moving the cursor through rows in the table
        if (cursor.moveToFirst()){
            do{
                String accNo = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Number)); //Getting account number from table
                accountNumbersList.add(accNo); //Appending account number to our list
            }while
            (cursor.moveToNext()); //Move the cursor to next row
        }
        else{
            //If table is empty
            System.out.print("Can't found any account number");
        }
        cursor.close(); //Close the cursor
        database.close(); //Close the database
        return accountNumbersList; //Return the list including details
    }

    @Override
    public List<Account> getAccountsList() {

        //Creating a list to save account details
        List<Account> accountsList = new ArrayList<>();

        //Getting all details from Account Table
        String queryAccounts = "SELECT * FROM " + dbHelper.Account_Table;

        SQLiteDatabase database = dbHelper.getReadableDatabase(); //Getting a readable database
        Cursor cursor = database.rawQuery(queryAccounts, null); //Creating a cursor object to database

        if (cursor.moveToFirst()){
            do{
                String acc_holder  = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Holder_Name)); //Getting account holders name from the row
                String accNo = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Number)); //Getting account number from the row
                String bank  = cursor.getString(cursor.getColumnIndex(dbHelper.Bank)); //Getting bank name from the row
                Double balance  = cursor.getDouble(cursor.getColumnIndex(dbHelper.Balance)); //Getting account balance from the row

                Account newAcc = new Account(accNo,bank,acc_holder,balance); //Creating a account object to store taken details from the cursor
                accountsList.add(newAcc); //Appending created object to our list of accounts
            }
            while(cursor.moveToNext()); //Move the cursor to next row
        }
        else{
            System.out.println("Can't found any accounts");}
        cursor.close(); //Close the cursor
        database.close(); //Close the database
        return accountsList; //Return the list including details

    }

    @Override
    //Function to get an account object including details about a given account
    public Account getAccount(String accountNo) throws InvalidAccountException {
        //Query to get details from account table where account number equals provided account number
        String queryString = "SELECT * FROM " + dbHelper.Account_Table + " WHERE " + dbHelper.Account_Number + " = " + accountNo;


        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (!cursor.moveToFirst()) {
            //Exception Handling
            String msg = "Invalid account number";
            throw new InvalidAccountException(msg);
        }

        String bankName = cursor.getString(cursor.getColumnIndex(dbHelper.Bank)); //Getting the name of the bank
        String accHolder = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Holder_Name)); //Getting the name of account holder
        Double balance = cursor.getDouble(cursor.getColumnIndex(dbHelper.Balance)); //Getting the balance

        Account account= new Account(accountNo,bankName,accHolder,balance); //Creating a account object to store taken details from the cursor

        cursor.close(); //Close the cursor
        database.close(); //Close the database
        return account; //Return the list including details
    }


    @Override
    //Function to update the balance
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = dbHelper.getWritableDatabase(); //Opening the database
        String[] parameters = {accountNo}; //Creating a empty array of strings

        String queryString = "SELECT " + dbHelper.Balance + " FROM " + dbHelper.Account_Table + " WHERE " + dbHelper.Account_Number + " = ?" ; //Selecting account balance from account table according to the given account number
        Cursor cursor = database.rawQuery(queryString,parameters); //Creating a cursor

        if (!cursor.moveToFirst()) {
            //Exception Handling
            String msg = "Account Number is Invalid";
            throw new InvalidAccountException(msg);
        }

        double balanceNow = cursor.getDouble(cursor.getColumnIndex(dbHelper.Balance)); //Getting the current Balance

//        switch (expenseType){
//            case EXPENSE:
//                balanceNow = balanceNow - amount;
//                break;
//            case INCOME:
//                balanceNow = balanceNow + amount;
//                break;
//
//        }

        if (expenseType.equals("EXPENSE")){
            balanceNow = balanceNow - amount;
        }
        else if (expenseType.equals("INCOME")){
            balanceNow = balanceNow + amount;
        }

        ContentValues cv = new ContentValues();
        cv.put(dbHelper.Balance, balanceNow);
        database.update(dbHelper.Account_Table,cv, dbHelper.Account_Number + " = ?" , parameters); //Update the database
        cursor.close(); //Close the cursor
        database.close(); //Close the database
    }
}
