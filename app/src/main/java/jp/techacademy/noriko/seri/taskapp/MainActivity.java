package jp.techacademy.noriko.seri.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;
import java.util.Date;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.RealmQuery;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_TASK = "jp.techacademy.noriko.seri.taskapp.TASK";

    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener(){
        @Override
        public void onChange(Object element){
            //　カテゴリ表示
            reloadCategory();
            //　一覧表示
            reloadListView();

        }
    };

    private ListView mListView;
    private  TaskAdapter mTaskAdapter;

    private ArrayAdapter<Category> mCategoryAdapter;
    private Spinner mSpinner;
    private long mCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        // Realmの設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView)findViewById(R.id.listView1);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                   //入力・編集する画面に遷移させる

                    Task task = (Task) parent.getAdapter().getItem(position);

                    Intent intent = new Intent(MainActivity.this, InputActivity.class);
                    intent.putExtra(EXTRA_TASK, task.getId());

                    startActivity(intent);
                }



        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する
                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();


                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }


        });

        // カテゴリ読み込み
        reloadCategory();

        // リスト読み込み
        reloadListView();


    }

    // リスト読み込み
    private void reloadListView(){

        RealmResults<Task> taskRealmResults;

        if( mCategoryId >= 0) {
            RealmQuery<Task> query = mRealm.where(Task.class);

            query.equalTo("categoryId", mCategoryId);

            taskRealmResults = query.findAllSorted("date", Sort.DESCENDING);

        } else {

            // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
            taskRealmResults = mRealm.where(Task.class).findAllSorted("date", Sort.DESCENDING);
        }


        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));
        // TaskのListView用のアダプタに渡す
        mListView.setAdapter(mTaskAdapter);
        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged();


    }


    // カテゴリドロップボックスを作成
    private void reloadCategory(){
        // Realmデータベースから、「全てのデータを取得してid順に並べた結果」を取得
        RealmResults<Category> categoryRealmResults = mRealm.where(Category.class).findAllSorted("id", Sort.ASCENDING);
        // 上記の結果を、カテゴリリスト としてセットする
        List list = mRealm.copyFromRealm(categoryRealmResults);
        Category firstCategory = new Category();
        firstCategory.setId(-1);
        firstCategory.setCategoryName("");
        list.add(0,firstCategory);
        mCategoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinnerにadapterをセット
        mSpinner = (Spinner)findViewById(R.id.category);
        mSpinner.setAdapter(mCategoryAdapter);

        // リスナーを登録
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                Category category =  (Category)spinner.getSelectedItem();
                mCategoryId = category.getId();

                // 選択されたカテゴリのタスクを抽出
                reloadListView();

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }


}
