package cn.lhrj.moudules.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.aliyun.oss.OSSClient;
import com.jfinal.upload.UploadFile;

import cn.lhrj.common.utils.FileUtils;
import cn.lhrj.moudules.admin.system.config.ConfigCache;

/**
 * 阿里云存储
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-26 16:22
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSSClient client;

    public AliyunCloudStorageService() {
        //初始化
        init();
    }

    private void init() {
        client = new OSSClient(ConfigCache.getValue("backup.oss.endpoint"), ConfigCache.getValue("backup.oss.id"),
        		ConfigCache.getValue("backup.oss.key"));
    }

    @Override
    public String upload(UploadFile file) throws Exception {
        String fileName = file.getOriginalFileName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return upload(FileUtils.fileToBytes(file.getFile().getAbsolutePath()), getPath(ConfigCache.getValue("prefix")) + "." + prefix);
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(final InputStream inputStream, final String path) {
        try {
        	new Thread(new Runnable(){
				public void run() {
					client.putObject(ConfigCache.getValue("backup.oss.bucketname"), path, inputStream);			
				}
			}).start();

        } catch (Exception e) {
          System.err.println("上传文件失败，请检查配置信息");
        }

        return ConfigCache.getValue("domain") + "/" + path;
    }
    
    @Override
    public String uploadImg(final InputStream inputStream, final String path) {
        try {
   
			client.putObject(ConfigCache.getValue("backup.oss.bucketname"), path, inputStream);			
				
			

        } catch (Exception e) {
          System.err.println("上传文件失败，请检查配置信息");
        }

        return ConfigCache.getValue("domain") + "/" + path;
    }
    
}
