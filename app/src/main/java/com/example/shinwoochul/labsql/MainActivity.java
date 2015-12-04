package com.example.shinwoochul.labsql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;


    // layout object
    EditText mEtName;
    EditText mEtName2;
    Button mBtInsert;
    Button mBtRead;
    Button mBtDelete;
    Button mBtUpdate;
    Button mBtSort;
    Button mBtdTable;


    ArrayAdapter<String> musicAdapter;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create databases
        db = openOrCreateDatabase(dbName,dbMode,null);
        // create table;
        createTable();

        mEtName = (EditText) findViewById(R.id.et_text);
        mEtName2 = (EditText) findViewById(R.id.ed_text2);
        mBtInsert = (Button) findViewById(R.id.bt_insert);
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtDelete = (Button) findViewById(R.id.bt_delete);
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        mBtSort = (Button) findViewById(R.id.bt_sort);
        mBtdTable = (Button) findViewById(R.id.bt_dtable);

        ListView mList = (ListView) findViewById(R.id.list_view);


        // Insert
        mBtInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEtName.getText().toString();
                insertData(name);
            }
        });


        // Read
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();
                selectAll();
                musicAdapter.notifyDataSetChanged();
            }
        });


        // Delete
        mBtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Integer.parseInt(mEtName2.getText().toString());
                removeData(index);
            }
        });

        // Update
        mBtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Integer.parseInt(mEtName.getText().toString());
                String updata = mEtName2.getText().toString();
                updateData(index, updata);
            }
        });

        // Sort
        mBtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();
                sortingData();
                musicAdapter.notifyDataSetChanged();
            }
        });


        // Delete Table
        mBtdTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();
                removeTable();
                createTable();
                musicAdapter.notifyDataSetChanged();
            }
        });


        // Create listview
        nameList = new ArrayList<String>();
        musicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameList);
        mList.setAdapter(musicAdapter);

    }


//    // Database 생성 및 열기
//    public void createDatabase(String dbName, int dbMode) {
//        db = openOrCreateDatabase(dbName, dbMode, null);
//    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer primary key autoincrement, " + "name text not null)";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    // Table 삭제
    public void removeTable() {
        String sql = "drop table " + tableName;
        db.execSQL(sql);
    }

    // Data 추가
    public void insertData(String name) {
        String sql = "insert into " + tableName + " values(NULL, '" + name + "');";
        db.execSQL(sql);
    }

    // Data 업데이트
    public void updateData(int index, String name) {
        String sql = "update " + tableName + " set name = '" + name + "' where id = " + index + ";";
        findExistData(index);
        db.execSQL(sql);

    }
    // Data 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        findExistData(index);
        db.execSQL(sql);

    }

    // Data 읽기(꺼내오기)
    public void selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index + ";";
        Cursor result = db.rawQuery(sql, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String name = result.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();

            Log.d("lab_sqlite", "\"index= \" + id + \" name=\" + name ");
        }
        result.close();
    }


    // 모든 Data 읽기
    public void selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();


        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();
            Log.d("lab_sqlite", "index= " + id + " name=" + name);

            nameList.add("#id :" + id +"       name : " +name);

            results.moveToNext();
        }
        results.close();
    }

    // Data 역순으로 Sorting하기
    public void sortingData() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToLast();


        while (!results.isBeforeFirst()) {
            int id = results.getInt(0);
            String name = results.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();
            Log.d("lab_sqlite", "index= " + id + " name=" + name);

            nameList.add("#id :" + id +"       name : " +name);
            results.moveToPrevious();
        }
        results.close();
    }


    // 해당 Index 값 Data가 존재하는지 확인
    public void findExistData(int index){
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);
        boolean findFlag = false;

        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            if(index == id)
                findFlag = true;
            results.moveToNext();
        }
        if(!findFlag)
            Toast.makeText(this, "Not Found Number of Index: " + index, Toast.LENGTH_LONG).show();

        results.close();
    }
}
