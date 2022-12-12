package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataBaseHandler extends SQLiteOpenHelper {

    //Assigning names for required column names, so if we want to change the column name in future changing in here will be sufficient,
    public static final String Account_Holder_Name = "Account_Holder";
    public static final String Bank = "Bank";
    public static final String Account_Table = "Account_Table";
    public static final String Account_Number = "Account_No";
    public static final String Balance = "Balance";
    public static final String Type = "Type";
    public static final String Amount = "Amount";
    public static final String Date = "Date";
    public static final String Transaction_ID = "Transaction_ID";
    public static final String Transaction_Table = "Transaction_Table";


    //Creating the database names 200623P
    public DataBaseHandler(@Nullable Context context) {
        super(context, "200623P.db",null,1);
    }
    @Override


    //Function to create tables
    public void onCreate(SQLiteDatabase dataBase) {

        //Creating the table for transactions
        dataBase.execSQL("CREATE TABLE " + Transaction_Table + " (" +
                Transaction_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Account_Number + " TEXT NOT NULL, " +
                Type + " TEXT NOT NULL, " +
                Amount + " REAL NOT NULL, " +
                Date + " TEXT NOT NULL," +
                "FOREIGN KEY("+ Account_Number + ")REFERENCES "+ Account_Table +"(" + Account_Number +"))");

        //Creating the table for accounts
        dataBase.execSQL("CREATE TABLE " + Account_Table + "(" +
                Account_Number + " TEXT PRIMARY KEY, " +
                Bank + " TEXT NOT NULL, " +
                Account_Holder_Name + " TEXT NOT NULL, " +
                Balance + " REAL NOT NULL )");
    }

    @Override
    //Function to drop a table if needed
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String dropAccountTable = "DROP TABLE IF EXISTS " + Account_Table;
        String dropTransactionTable = "DROP TABLE IF EXISTS " + Transaction_Table;
        db.execSQL(dropAccountTable);
        db.execSQL(dropTransactionTable);
        onCreate(db);
    }
}
