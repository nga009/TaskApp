package jp.techacademy.noriko.seri.taskapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class InputActivity extends AppCompatActivity {

//    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener(){
        @Override
        public void onChange(Object element){
            //　カテゴリ表示
            setSpinner();

        }
    };



    private ArrayAdapter<Category> mCategoryAdapter;


    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mDateButton, mTimeButton;
    private EditText mTitleEdit, mContentEdit;
    private long mCategoryId;
    private Task mTask;
    private Spinner mSpinner;
    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(InputActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
                            mDateButton.setText(dateString);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };

    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
                            mTimeButton.setText(timeString);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTask();
            finish();
        }
    };

    private View.OnClickListener mOnCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //カテゴリ新規作成画面に遷移させる
            Intent intent = new Intent(InputActivity.this, CategoryInputActivity.class);

            startActivity(intent);

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mDateButton = (Button)findViewById(R.id.date_button);
        mDateButton.setOnClickListener(mOnDateClickListener);
        mTimeButton = (Button)findViewById(R.id.times_button);
        mTimeButton.setOnClickListener(mOnTimeClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        findViewById(R.id.category_input).setOnClickListener(mOnCategoryClickListener);
        mTitleEdit = (EditText)findViewById(R.id.title_edit_text);
        mContentEdit = (EditText)findViewById(R.id.content_edit_text);
        mSpinner = (Spinner)findViewById(R.id.category);



        // spinnerセット
        setSpinner();

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        Realm realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        int taskId = intent.getIntExtra(MainActivity.EXTRA_TASK, -1);
        mTask = realm.where(Task.class).equalTo("id", taskId).findFirst();
        realm.close();

        if (mTask == null) {
            // 新規作成の場合
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合
            mTitleEdit.setText(mTask.getTitle());
            mContentEdit.setText(mTask.getContents());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getDate());
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
            mDateButton.setText(dateString);
            mTimeButton.setText(timeString);
            mCategoryId=mTask.getCategoryId();
            for (int i=0;i<mSpinner.getCount();i++){
                if(mSpinner.getAdapter().getItemId(i) == mCategoryId) {
                    mSpinner.setSelection(i);
                    break;
                }
            }



        }
    }


    // タスク追加
    private void addTask() {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if (mTask == null) {
            // 新規作成の場合
            mTask = new Task();

            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mTask.setId(identifier);
        }


        mTask.setTitle(mTitleEdit.getText().toString());
        mTask.setContents(mContentEdit.getText().toString());
        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
        Date date = calendar.getTime();
        mTask.setDate(date);
        mTask.setCategoryId(mCategoryId);


        realm.copyToRealmOrUpdate(mTask);
        realm.commitTransaction();

        realm.close();

        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
        resultIntent.putExtra(MainActivity.EXTRA_TASK, mTask.getId());
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                this,
                mTask.getId(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);

    }


    // カテゴリ表示
    private void setSpinner(){
        //
        Realm realm = Realm.getDefaultInstance();
        realm.addChangeListener(mRealmListener);

        // Realmデータベースから、「全てのデータを取得してid順に並べた結果」を取得
        RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAllSorted("id", Sort.ASCENDING);
        // 上記の結果を、TaskList としてセットする
        List list = realm.copyFromRealm(categoryRealmResults);
        Category firstCategory = new Category();
        firstCategory.setId(-1);
        firstCategory.setCategoryName("");
        list.add(0,firstCategory);
        mCategoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinnerにadapterをセット
        mSpinner = (Spinner)findViewById(R.id.category);
        mSpinner.setAdapter(mCategoryAdapter);

        realm.close();



        // リスナーを登録
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                Category category =  (Category)spinner.getSelectedItem();
                mCategoryId = category.getId();


            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }
}
