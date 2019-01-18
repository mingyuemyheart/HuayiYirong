package com.huayi.shawn.yirong.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.huayi.shawn.yirong.common.CONST;

public class ShawnDto implements Parcelable {

    public String title,imgPath,filePath,content,time,state,source;
    public String id;//对应文件或文件夹id
    public String pid;//对应文件或文件夹父id
    public String columnId;//资源库个人库、公共库id
    public String fileType;//1图片、2视频、3音频、4文档、5文件夹
    public boolean isSelected;
    public long fileSize;//文件大小
    public int loadState = CONST.loadPercent;//下载或者上传状态，1完成、-1未完成
    public int percent;

    public ShawnDto() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.imgPath);
        dest.writeString(this.filePath);
        dest.writeString(this.content);
        dest.writeString(this.time);
        dest.writeString(this.state);
        dest.writeString(this.source);
        dest.writeString(this.id);
        dest.writeString(this.pid);
        dest.writeString(this.columnId);
        dest.writeString(this.fileType);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeLong(this.fileSize);
        dest.writeInt(this.loadState);
        dest.writeInt(this.percent);
    }

    protected ShawnDto(Parcel in) {
        this.title = in.readString();
        this.imgPath = in.readString();
        this.filePath = in.readString();
        this.content = in.readString();
        this.time = in.readString();
        this.state = in.readString();
        this.source = in.readString();
        this.id = in.readString();
        this.pid = in.readString();
        this.columnId = in.readString();
        this.fileType = in.readString();
        this.isSelected = in.readByte() != 0;
        this.fileSize = in.readLong();
        this.loadState = in.readInt();
        this.percent = in.readInt();
    }

    public static final Creator<ShawnDto> CREATOR = new Creator<ShawnDto>() {
        @Override
        public ShawnDto createFromParcel(Parcel source) {
            return new ShawnDto(source);
        }

        @Override
        public ShawnDto[] newArray(int size) {
            return new ShawnDto[size];
        }
    };
}
