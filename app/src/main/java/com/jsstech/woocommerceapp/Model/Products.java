package com.jsstech.woocommerceapp.Model;

public class Products {
    private String pname,des,price,pid,image,category,date,time;

    public Products(String pname,String des,String price,String pid,String image,String category,String date,String time) {
        this.pname = pname;
        this.des = des;
        this.price = price;
        this.pid = pid;
        this.image = image;
        this.category = category;
        this.date = date;
        this.time = time;
    }

    public Products() {
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
