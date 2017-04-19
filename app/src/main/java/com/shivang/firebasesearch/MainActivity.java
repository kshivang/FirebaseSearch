package com.shivang.firebasesearch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by kshivang on 20/11/16.
 *
 */

public class MainActivity extends AppCompatActivity{

    private EditText etUrl;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private int orderType = 0, filterType = 0;
    private EditText tvOrder, tvFilter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setTitle(getString(R.string.firebase_database));
        }
        etUrl = (EditText) findViewById(R.id.etUrl);
        etUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etUrl.getText())) {
                    etUrl.setText(R.string.base_url_init);
                }
            }
        });
        etUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Access(findViewById(R.id.bt_access));
                return true;
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Spinner spOrderType = (Spinner) findViewById(R.id.selectOrderType);
        Spinner spFilter = (Spinner) findViewById(R.id.selectFilter);
        tvOrder = (EditText) findViewById(R.id.order);
        tvFilter = (EditText) findViewById(R.id.filter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(this,
                R.array.orderQuery, android.R.layout.simple_spinner_dropdown_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrderType.setAdapter(orderAdapter);
        spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvOrder.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                orderType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filterType, android.R.layout.simple_spinner_dropdown_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvFilter.setVisibility(i > 0 ? View.VISIBLE : View.GONE);
                filterType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void Access(final View view) {
        if (TextUtils.isEmpty(etUrl.getText())) {
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference(etUrl.getText().toString());
        }
        progressBar.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);

        Query query = databaseReference;
        switch (orderType) {
            case (1):
                query = databaseReference.orderByKey();
                break;
            case (2):
                query = databaseReference.orderByValue();
                break;
            default:
                if (!TextUtils.isEmpty(tvOrder.getText())) {
                    query = databaseReference.orderByChild(tvOrder.getText().toString());
                }
        }

        switch (filterType) {
            case 1:
                if (TextUtils.isDigitsOnly(tvFilter.getText()))
                    query.limitToLast(Integer.parseInt(tvFilter.getText().toString()));
                break;
            case 2:
                if (TextUtils.isDigitsOnly(tvFilter.getText()))
                    query.limitToFirst(Integer.parseInt(tvFilter.getText().toString()));
                break;
            case 3:
                query.startAt(Integer.parseInt(tvFilter.toString()));
                break;
            case 4:
                query.endAt(Integer.parseInt(tvFilter.toString()));
                break;
            case 5:
                query.equalTo(Integer.parseInt(tvFilter.toString()));
                break;
            default:

        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                (new AlertDialog.Builder(MainActivity.this))
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setMessage(dataSnapshot.toString())
                        .create().show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });



    }
}
