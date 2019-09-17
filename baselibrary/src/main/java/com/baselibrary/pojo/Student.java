package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created By pq
 * on 2019/9/17
 */
@Entity
public class Student {
    @Id(autoincrement = true)
    Long sId;
    @Unique
    int studentNo;
//    @Property
    String name;
    String address;
    @Generated(hash = 662735562)
    public Student(Long sId, int studentNo, String name, String address) {
        this.sId = sId;
        this.studentNo = studentNo;
        this.name = name;
        this.address = address;
    }
    @Generated(hash = 1556870573)
    public Student() {
    }
    public Long getSId() {
        return this.sId;
    }
    public void setSId(Long sId) {
        this.sId = sId;
    }
    public int getStudentNo() {
        return this.studentNo;
    }
    public void setStudentNo(int studentNo) {
        this.studentNo = studentNo;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "sId=" + sId +
                ", studentNo=" + studentNo +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
