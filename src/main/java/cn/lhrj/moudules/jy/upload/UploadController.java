package cn.lhrj.moudules.jy.upload;

import java.io.File;
import java.text.SimpleDateFormat;

import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.RandomUtil;
import cn.lhrj.component.base.BaseProjectController;

public class UploadController extends BaseProjectController{
	public void index() throws Exception {
			
			UploadFile uploadFile = getFile();                                                                                                                                                                                                                                                                                                                                                                                                                                       
			File oldFile = uploadFile.getFile();
			
			String originalName = oldFile.getName();
			//截取源文件的文件类型
			String originalType = originalName.substring(originalName.lastIndexOf("."));
			//定义新的文件名 日期+3位随机数
			SimpleDateFormat format	= new SimpleDateFormat("yyyyMMddHHmmss");
			String dateName = format.format(DateUtils.getNowDate());
			String random = RandomUtil.genNumberRandomCode(3);
			File newFile = new File("/www/wwwroot/jy.lcz.fun/jy/webapp/statics/img/"+dateName+random+originalType);	
			boolean flag = oldFile.renameTo(newFile);
			log.info("flag:"+flag);
			renderJson(dateName+random+originalType);
		}
	}
