package org.happymtb.unofficial.item;

import java.io.Serializable;

/**
 * Created by Adrian on 09/02/2016.
 */
public class Person implements Serializable {

    private String mIdLink;
    private String mName;
    private String mPhone;
    private String mEmail;
    private String mPM;
    private String mMemberSince;

    public Person (String name, String phone, String memberSince, String idLink, String pm, String email) {
        mName = name;
        mPhone = phone;
        mMemberSince = memberSince;
        mIdLink = idLink;
        mPM = pm;
        mEmail = email;
    }

    public String getName(){
        return mName;
    }

    public String getPhone(){
        return mPhone;
    }

    public String getMemberSince() {
        return mMemberSince;
    }

    public String getIdLink() {
        return mIdLink;
    }

    public String getPmLink() {
        return mPM;
    }

    public String getEmailLink() {
        return mEmail;
    }

}
