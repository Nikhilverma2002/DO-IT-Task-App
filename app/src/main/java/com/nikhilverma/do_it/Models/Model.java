package com.nikhilverma.do_it.Models;

public class Model {

    private String name;
    private String color;
    private String uid;
    private String title;
    private String description;
    private String star;
    private String members;
    private String date;
    private String pending;
    private String admin;
    private String task;
    private String CheckBox;
    private String pushkey;
    private String total;
    private String complete;

    public Model() {
    }

    public Model(String name, String color, String uid, String title, String description, String star,
                 String members, String date, String pending, String admin, String task, String checkBox, String pushkey,String complete,String total) {
        this.name = name;
        this.color = color;
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.star = star;
        this.members = members;
        this.date = date;
        this.pending = pending;
        this.admin = admin;
        this.task = task;
        this.CheckBox = checkBox;
        this.pushkey = pushkey;
        this.total = total;
        this.complete = complete;
    }

    public String getTask() {
        return task;
    }

    public String getCheckBox() {
        return CheckBox;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getTotal() {
        return total;
    }

    public String getComplete() {
        return complete;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStar() {
        return star;
    }

    public String getMembers() {
        return members;
    }

    public String getDate() {
        return date;
    }

    public String getAdmin() {
        return admin;
    }

    public String getPending() {
        return pending;
    }

    public String getPushkey() {
        return pushkey;
    }
}
