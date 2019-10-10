package cn.lhrj.common.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;

public class UploadImgUtil {

	private final static Log log = Log.getLog(UploadImgUtil.class);
	
	private static final String accessKeyId = "LTAIKQE5O8IZhu8n";
	private static final String accessKeySecret = "hnmeQPvtkzAFU8tyDP1cRtSl5xFepF";
	private static final String bucket = "kxcoss";
	//下载
	private static final String allEndPoint = "https://kxcoss.oss-cn-hangzhou.aliyuncs.com";
	//上传
	private static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
	private static final String basePath = "kxc/img/";
	
	public static Ret upload(List<UploadFile> fileList) {
		
		try {
			if (null == fileList||fileList.size()==0) {
				return Ret.fail("msg", "请上传图片");
			}
			JSONArray fileArray = new JSONArray();
			for (int i = 0; i < fileList.size(); i++) {
				File file = fileList.get(i).getFile();
				//获得文件大小
				String fileSize	= file.length()+"";
				//获得原文件名
				String originalName = file.getName();
				//截取源文件的文件类型
				String originalType = originalName.substring(originalName.lastIndexOf("."));
				//定义新的文件名 日期+3位随机数
				SimpleDateFormat format	= new SimpleDateFormat("yyyyMMddHHmmss");
				String dateName = format.format(DateUtils.getNowDate());
				String random = RandomUtil.genNumberRandomCode(3);
				OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
				boolean success = client.doesBucketExist(bucket);
				if (!success) {
					client.createBucket(bucket);
				}
				client.putObject("kxcoss", basePath+dateName+random+originalType, file);
				// 关闭OSSClient。
				client.shutdown();
				JSONObject object = new JSONObject();
				object.put("url", allEndPoint+"/"+basePath+dateName+random+originalType);
				object.put("type", originalType);
				object.put("size", fileSize);
				fileArray.add(object);
			}
			return Ret.ok("data",fileArray);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage(),e);
			return Ret.fail("msg", e.getMessage());
		}
	}
	
public static Ret uploadOne(UploadFile upfile) {
		
			try {
				if (null == upfile) {
					return Ret.fail("msg", "请上传图片");
				}
				File file = upfile.getFile();
				//获得文件大小
				String fileSize	= file.length()+"";
				//获得原文件名
				String originalName = file.getName();
				//截取源文件的文件类型
				String originalType = originalName.substring(originalName.lastIndexOf("."));
				//定义新的文件名 日期+3位随机数
				SimpleDateFormat format	= new SimpleDateFormat("yyyyMMddHHmmss");
				String dateName = format.format(DateUtils.getNowDate());
				String random = RandomUtil.genNumberRandomCode(3);
				OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
				boolean success = client.doesBucketExist(bucket);
				if (!success) {
					client.createBucket(bucket);
				}
				client.putObject("kxcoss", basePath+dateName+random+originalType, file);
				// 关闭OSSClient。
				client.shutdown();
				String url = allEndPoint+"/"+basePath+dateName+random+originalType;
			return Ret.ok("url", url);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage(),e);
			return Ret.fail("msg", e.getMessage());
		}
	}
	
	public static String uploadQrFile(File file) {
				//获得文件大小
				String fileSize	= file.length()+"";
				//获得原文件名
				String originalName = file.getName();
				//截取源文件的文件类型
				String originalType = originalName.substring(originalName.lastIndexOf("."));
				//定义新的文件名 日期+3位随机数
				SimpleDateFormat format	= new SimpleDateFormat("yyyyMMddHHmmss");
				String dateName = format.format(DateUtils.getNowDate());
				String random = RandomUtil.genNumberRandomCode(3);
				OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
				boolean success = client.doesBucketExist(bucket);
				if (!success) {
					client.createBucket(bucket);
				}
				client.putObject("kxcoss", basePath+dateName+random+originalType, file);
				// 关闭OSSClient。
				client.shutdown();
				String url = allEndPoint+"/"+basePath+dateName+random+originalType;
				return url;
	}
}
