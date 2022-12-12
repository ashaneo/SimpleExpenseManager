package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
    private final DataBaseHandler dbHandler; //Creating a dpHelper object

    public PersistentTransactionDAO(DataBaseHandler dbHelper){
        this.dbHandler =dbHelper;
    }

    @Override
    //Function to perform transactions
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHandler.Date, date.toString());
        cv.put(dbHandler.Account_Number, accountNo);
        cv.put(dbHandler.Type, expenseType.toString());
        cv.put(dbHandler.Amount, amount);

        database.insert(dbHandler.Transaction_Table, null, cv);
        database.close();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> returnList = new ArrayList<>();
        SQLiteDatabase database = dbHandler.getReadableDatabase();

        String queryString = "SELECT * FROM " + dbHandler.Transaction_Table + " ORDER BY " + dbHandler.Transaction_ID + " DESC LIMIT " + limit;
        Cursor cursor = database.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(dbHandler.Account_Number));
                String type = cursor.getString(cursor.getColumnIndex(dbHandler.Type));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHandler.Date));
                    date = dataFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = cursor.getDouble(cursor.getColumnIndex(dbHandler.Amount));

                returnList.add(new Transaction(date, accountNo, expenseType, amount));
            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return returnList; //Return the created Lists
    }

    @Override
    //Function to get all logs about transactions
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionArrayList = new ArrayList<>();
        SQLiteDatabase database = dbHandler.getReadableDatabase(); //Open the database

        Cursor cursor = database.rawQuery("SELECT * FROM " + dbHandler.Transaction_Table,null); //Creating a cursor object to the database

        if (cursor.moveToFirst()){
            do {
                String accNo = cursor.getString(cursor.getColumnIndex(dbHandler.Account_Number)); //Getting the account number
                String type = cursor.getString(cursor.getColumnIndex(dbHandler.Type)); //Getting the account type
                ExpenseType expenseType;

                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Date date = null;

                try {
                    //Trying to get the date
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHandler.Date));
                    date = dataFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = cursor.getDouble(cursor.getColumnIndex(dbHandler.Amount));

                transactionArrayList.add(new Transaction(date, accNo, expenseType, amount));

            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return transactionArrayList;
    }


}
