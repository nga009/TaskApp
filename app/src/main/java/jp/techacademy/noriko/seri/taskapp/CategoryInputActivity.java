package jp.techacademy.noriko.seri.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryInputActivity extends AppCompatActivity {

    private EditText mEditCategoryName;
//    private Category category;

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            addCategory();
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品
        mEditCategoryName = (EditText)findViewById(R.id.categoryname_edit_text);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);


    }


    // カテゴリ追加
    private void addCategory() {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        // 新規作成
        Category category = new Category();

        RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();

        int identifier;
        if (categoryRealmResults.max("id") != null) {
            identifier = categoryRealmResults.max("id").intValue() + 1;
        } else {
            identifier = 1;
        }
        category.setId(identifier);


        category.setCategoryName(mEditCategoryName.getText().toString());



        realm.copyToRealmOrUpdate(category);
        realm.commitTransaction();

        realm.close();


    }

}
