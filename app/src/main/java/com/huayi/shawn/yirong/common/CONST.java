package com.huayi.shawn.yirong.common;

import android.os.Environment;

import com.huayi.shawn.yirong.R;

public class CONST {

	public static final String imageSuffix = ".png";//图标后缀名
	public static final String lastFile = "..";//返回上级目录
	public static final String doc = ".doc";
	public static final String docx = ".docx";
	public static final String ppt = ".ppt";
	public static final String pptx = ".pptx";
	public static final String pdf = ".pdf";
	public static final String xls = ".xls";
	public static final String xlsx = ".xlsx";
	public static final String txt = ".txt";
	public static final String zip = ".zip";
	public static final String rar = ".rar";

	//下拉刷新progresBar四种颜色
	public static final int color1 = R.color.refresh_color1;
	public static final int color2 = R.color.refresh_color2;
	public static final int color3 = R.color.refresh_color3;
	public static final int color4 = R.color.refresh_color4;

	//预警颜色对应规则
	public static String[] blue = {"01", "_blue"};
	public static String[] yellow = {"02", "_yellow"};
	public static String[] orange = {"03", "_orange"};
	public static String[] red = {"04", "_red"};

	public static final String ACTIVITY_NAME = "activity_name";//界面名称
	public static final String WEB_URL = "web_Url";//网页地址的标示
	public static final String IMAGE_PATH = "image_path";//本地图片路径
	public static final String VIDEO_PATH = "video_path";//本地视频路径

	//通用
	private static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/Yirong";
	public static String DOWNLOAD_ADDR = SDCARD_PATH + "/download/";//下载视频保存的路径
	public static String FILETYPE1 = "1",FILETYPE2 = "2",FILETYPE3 = "3",FILETYPE4 = "4",FILETYPE5 = "5";//1图片、2视频、3音频、4文档、5文件夹
	public static int loadComplete = 1, loadPercent = -1;//下载、上传文件完成、未完成状态

	//广播
	public static String BROADCAST_CONTROL = "broadcast_control";//控制资源库下方操作按钮状态
	public static String BROADCAST_DOWNLOAD_PROGRESS = "broadcast_download_progress";//下载文件进度
	public static String BROADCAST_UPLOAD_PROGRESS = "broadcast_upload_progress";//上传文件进度

}
