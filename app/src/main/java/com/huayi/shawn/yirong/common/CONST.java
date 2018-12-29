package com.huayi.shawn.yirong.common;

import android.os.Environment;

import com.huayi.shawn.yirong.R;

public class CONST {

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

	//showType类型，区分本地类或者图文
	public static final String LOCAL = "local";
	public static final String NEWS = "news";
	public static final String URL = "url";
	public static final String PDF = "pdf";

	public static final String WEB_URL = "web_Url";//网页地址的标示
	public static final String ACTIVITY_NAME = "activity_name";//界面名称
	public static final String LOCAL_ID = "local_id";//local_id

	//通用
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/Yirong";
	public static String DOWNLOAD_ADDR = SDCARD_PATH + "/download/";//下载视频保存的路径

}
