package jp.techacademy.noriko.seri.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Task extends RealmObject implements Serializable {
    private String title; // タイトル
    private String contents; // 内容
    private Date date; // 日時
    private long  categoryId; // カテゴリ

    @PrimaryKey
    private int id;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId( long categoryId) {
        this.categoryId = categoryId;
    }

}
