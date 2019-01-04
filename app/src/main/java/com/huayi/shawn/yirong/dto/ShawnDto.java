package com.huayi.shawn.yirong.dto;

public class ShawnDto {

    public String title,imgPath,filePath,content,time,state,source,dataUrl,videoUrl;
    public String id;//对应文件或文件夹id
    public String pid;//对应文件或文件夹父id
    public String fileType;//1图片、2视频、3音频、4文档、5文件夹
    public boolean isSelected;
    public long fileSize;//文件大小

}
