package com.example.videoapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/* SP存储  工具类*/
public class SPUtils {
	private static SharedPreferences sp;

	/**   写入boolean 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param value		存储节点的值 boolean
	 */
	public static void putBoolean(Context context,String key,boolean value){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	/** 读取boolean 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param defvalue		没有此节点默认值
	 * return  默认值或者此节点读取到的结果
	 */
	public static boolean getBoolean(Context context,String key,boolean defvalue){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defvalue);
	}

	/**   写入String 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param value		存储节点的值 String
	 */
	public static void putString(Context context,String key,String value){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	/** 读取String 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param defvalue		没有此节点默认值
	 * return  默认值或者此节点读取到的结果
	 */
	public static String getString(Context context,String key,String defvalue){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defvalue);
	}

	/**   写入String 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param value		存储节点的值 String
	 */
	public static void putInt(Context context,String key,int value){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}

	/** 读取String 变量至sp中
	 * @param context 上下文环境
	 * @param key		存储节点名称
	 * @param defvalue		没有此节点默认值
	 * return  默认值或者此节点读取到的结果
	 */
	public static int getInt(Context context,String key,int defvalue){
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defvalue);
	}

	/**   从sp中 移除指定节点
	 * @param context  上下文环境
	 * @param key  需要移除节点的名称
	 */
	public static void remove(Context context, String key) {
		//（存储节点文件名称，读写方式）
		if(sp == null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().remove(key).commit();
	}

	//删除 单个
	public static void deleShare(Context context,String key){
		SharedPreferences sp  = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		sp.edit().remove(key).commit();
	}

	//删除 全部
	public static void deleAll(Context context,String key){
		SharedPreferences sp  = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
}