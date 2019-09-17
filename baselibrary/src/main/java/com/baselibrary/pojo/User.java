package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created By pq
 * on 2019/9/9
 */
@Entity
public class User {
    @Id(autoincrement = true)
    Long uId;
    @Unique
    int studentNo;
    int age;
    @Property
    String name;
    String sex;
    String telPhone;
    @Generated(hash = 253787512)
    public User(Long uId, int studentNo, int age, String name, String sex,
                String telPhone) {
        this.uId = uId;
        this.studentNo = studentNo;
        this.age = age;
        this.name = name;
        this.sex = sex;
        this.telPhone = telPhone;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public int getStudentNo() {
        return this.studentNo;
    }
    public void setStudentNo(int studentNo) {
        this.studentNo = studentNo;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getTelPhone() {
        return this.telPhone;
    }
    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    @Override
    public String toString() {
        return "User{" +
                "uId=" + uId +
                ", studentNo=" + studentNo +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", telPhone='" + telPhone + '\'' +
                '}';
    }
}
