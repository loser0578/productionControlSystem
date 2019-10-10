package cn.lhrj.moudules.upload;

import com.aliyun.oss.OSSClient;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import cn.lhrj.component.base.BaseProjectController;

public class SysOssController extends BaseProjectController{

	
	public void upload() throws Exception {
		
		String url =new AliyunCloudStorageService().upload(getFile());
		renderJson(Ret.by("url", url));
	}
	
	public void uploadImg() throws Exception{
		String url =new AliyunCloudStorageService().upload(getFile());
		renderJson(Ret.by("url", url));
	}
}
