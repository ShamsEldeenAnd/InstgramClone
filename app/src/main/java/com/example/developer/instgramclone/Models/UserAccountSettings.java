package com.example.developer.instgramclone.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccountSettings implements Parcelable {
    private String description;
    private String display_name;
    private long following;
    private long followers;
    private long posts;
    private String profile_photo;
    private String user_name;
    private String website;
    private String user_id;

    public UserAccountSettings() {
    }

    public UserAccountSettings(String description, String display_name, long following, long followers, long posts, String profile_photo, String user_name, String website, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.following = following;
        this.followers = followers;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.user_name = user_name;
        this.website = website;
        this.user_id = user_id;
    }

    protected UserAccountSettings(Parcel in) {
        description = in.readString();
        display_name = in.readString();
        following = in.readLong();
        followers = in.readLong();
        posts = in.readLong();
        profile_photo = in.readString();
        user_name = in.readString();
        website = in.readString();
        user_id = in.readString();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(display_name);
        parcel.writeLong(following);
        parcel.writeLong(followers);
        parcel.writeLong(posts);
        parcel.writeString(profile_photo);
        parcel.writeString(user_name);
        parcel.writeString(website);
        parcel.writeString(user_id);
    }
}
