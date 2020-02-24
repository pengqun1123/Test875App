package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TestIdCard {
    @Id
    private Long cardId;
    private String cardNo;
    @Generated(hash = 814023755)
    public TestIdCard(Long cardId, String cardNo) {
        this.cardId = cardId;
        this.cardNo = cardNo;
    }
    @Generated(hash = 1889875721)
    public TestIdCard() {
    }
    public Long getCardId() {
        return this.cardId;
    }
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
    public String getCardNo() {
        return this.cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "TestIdCard{" +
                "cardId=" + cardId +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
