package com.huaxin.library.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class LabelDetail implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String avatar;

    public LabelDetail(String name) {
        this.name = name;
    }

    protected LabelDetail(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LabelDetail> CREATOR = new Creator<LabelDetail>() {
        @Override
        public LabelDetail createFromParcel(Parcel in) {
            return new LabelDetail(in);
        }

        @Override
        public LabelDetail[] newArray(int size) {
            return new LabelDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;

        if (obj instanceof LabelDetail) {

            LabelDetail other = (LabelDetail) obj;

            return this.name.equals(other.name);

        }

        return false;
    }
}
