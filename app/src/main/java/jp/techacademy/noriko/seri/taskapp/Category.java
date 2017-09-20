package jp.techacademy.noriko.seri.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Category extends RealmObject implements Serializable {
    private String categoryName; // カテゴリ名

    @PrimaryKey
    private long id;

    public Category(){
        this.id = 0;
        this.categoryName = "";
    }
    public Category( long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return categoryName;
    }

}
