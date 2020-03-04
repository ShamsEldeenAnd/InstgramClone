package com.example.developer.instgramclone.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User  implements Parcelable {
    private String user_id ;
    private String user_name;
    private String email;
    private long phone_number ;

    public User(String user_id, String user_name, String email, long phone_number) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public User() {
    }

    protected User(Parcel in) {
        user_id = in.readString();
        user_name = in.readString();
        email = in.readString();
        phone_number = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(user_name);
        parcel.writeString(email);
        parcel.writeLong(phone_number);
    }
}
