package cn.lhrj.moudules.upload;


import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import cn.lhrj.common.utils.DateUtils;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 云存储
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-25 14:58
 */
public abstract class CloudStorageService {


    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @return 返回上传路径
     */
    public String getPath(String prefix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + DateUtils.format(new Date(), "HHmmssS") + uuid.substring(0, 5);

        if (!StrKit.isBlank(prefix)) {
            path = prefix + "/" + path;
        }

        return path;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回http地址
     */
    public abstract String upload(UploadFile file) throws Exception;

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path        文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    
    //图片上传
	public abstract String uploadImg(InputStream inputStream, String path);

}
