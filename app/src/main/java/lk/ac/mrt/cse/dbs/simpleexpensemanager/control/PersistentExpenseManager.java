package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{

    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        setup(context);
    }

    @Override
    public void setup() {
    }

    @Override
    public void setup(Context context) throws ExpenseManagerException {
        DataBaseHandler dbHelper = new DataBaseHandler(context);
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(dbHelper);
        setAccountsDAO(persistentAccountDAO);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(dbHelper);
        setTransactionsDAO(persistentTransactionDAO);
    }
}
