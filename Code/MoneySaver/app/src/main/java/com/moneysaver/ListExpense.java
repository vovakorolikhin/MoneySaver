package com.moneysaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class ListExpense extends AppCompatActivity {
    ListView vListView;
    ArrayList<Expense> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_expense);
        vListView = findViewById(R.id.ExpenselistView);
        showListView(list);

        Button button_add = findViewById(R.id.button_add);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListExpense.this, AddExpense.class);
                startActivityForResult(i, 2);
            }
        };
        button_add.setOnClickListener(listener);
    }

    private void showListView(ArrayList<Expense> list){
        final AdapterExpense a = new AdapterExpense(list);
        vListView.setAdapter(a);
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Expense item = (Expense)a.getItem(position);
                Intent intent = new Intent(view.getContext(), Expense.class);
                intent.putExtra(Expense.class.getSimpleName(), item);
                startActivity(intent);
            }
        });
    }
}
