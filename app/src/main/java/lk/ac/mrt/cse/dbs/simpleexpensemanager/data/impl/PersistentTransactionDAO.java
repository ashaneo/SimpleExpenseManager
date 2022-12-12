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

    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
    private final DbHelper dbHelper; //Creating a dpHelper object

    public PersistentTransactionDAO(DbHelper dbHelper){
        this.dbHelper=dbHelper;
    }

    @Override
    //Function to perform transactions
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(dbHelper.Date, date.toString());
        cv.put(dbHelper.Account_Number, accountNo);
        cv.put(dbHelper.Type, expenseType.toString());
        cv.put(dbHelper.Amount, amount);

        database.insert(dbHelper.Transaction_Table, null, cv);
        database.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionArrayList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase(); //Open the database

        String queryString = "SELECT * FROM " + dbHelper.Transaction_Table; //Select all details from transaction table
        Cursor cursor = database.rawQuery(queryString,null); //Creating a cursor object

        if (cursor.moveToFirst()){
            do {
                String accNo = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Number)); //Getting the account number
                String type = cursor.getString(cursor.getColumnIndex(dbHelper.Type)); //Getting the account type
                ExpenseType expenseType;

                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.Date));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = cursor.getDouble(cursor.getColumnIndex(dbHelper.Amount));

                transactionArrayList.add(new Transaction(date, accNo, expenseType, amount));


            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return transactionArrayList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> returnList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryString = "SELECT * FROM " + dbHelper.Transaction_Table + " ORDER BY " + dbHelper.Transaction_ID + " DESC LIMIT " + limit;
        Cursor cursor = database.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(dbHelper.Account_Number));
                String type = cursor.getString(cursor.getColumnIndex(dbHelper.Type));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.Date));
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = cursor.getDouble(cursor.getColumnIndex(dbHelper.Amount));

                returnList.add(new Transaction(date, accountNo, expenseType, amount));


            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return returnList;


    }
}
