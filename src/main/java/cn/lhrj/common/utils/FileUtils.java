/**
3 * Copyright 2015-2025 FLY的狐狸(email:jflyfox@sina.com qq:369191470).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package cn.lhrj.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jfinal.kit.PathKit;

public class FileUtils {
	
	public static final String uploadTempPath = "/upload/temp";
	
	/**
	 * 读取文件，返回byte[] 如果不存在，返回null
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(String path) throws IOException {
		int base_size = 1024;
		File file = new File(path);
		// 不存在创建
		if (!file.exists()) {
			return null;
		}

		FileInputStream fis = new FileInputStream(file);
		int len = 0;
		byte[] dataByte = new byte[base_size];

		ByteArrayOutputStream out = new ByteArrayOutputStream(base_size);
		while ((len = fis.read(dataByte)) != -1) {
			out.write(dataByte, 0, len);
		}
		byte[] content = out.toByteArray();

		fis.close();
		out.close();

		// 没有读取到数据
		if (content.length == 0) {
			return null;
		}

		return content;
	}

	/**
	 * 写文件，如果存在，删除
	 * 
	 * @param path
	 * @param data
	 * @throws IOException
	 */
	public static void write(String path, byte[] data) throws IOException {
		File file = new File(path);
		// 不存在，创建
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
	}

	/**
	 * 查找当前文件下所有properties文件
	 * 
	 * @param baseDirName
	 *            查找的文件夹路径
	 */
	public static List<String> findFiles(String baseDirName) {
		List<String> files = new ArrayList<String>();
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			System.err.println("search error：" + baseDirName + "is not a dir！");
		} else {
			String[] filelist = baseDir.list();
			for (String fileName : filelist) {
				files.add(fileName);
			}
		}
		return files;
	}

	/**
	 * 查找当前文件下所有properties文件
	 * 
	 * @param baseDirName
	 *            查找的文件夹路径
	 */
	public static List<String> findFileNames(String baseDirName, FileFilter fileFilter) {
		List<String> files = new ArrayList<String>();
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			System.err.println("search error：" + baseDirName + "is not a dir！");
		} else {
			File[] filelist = baseDir.listFiles(fileFilter);
			for (File file : filelist) {
				if (file.isFile())
					files.add(file.getName());
			}
		}
		return files;
	}
	/**
     * 获取COS组件的分片
     * 
     * @param userId 用户ID
     * @param name 文件名
     * @param total 文件大小
     * @param chunk 分片序号
     * 
     * @return 分片文件
     */
    public static File getChunkFile(String userId, String name, Long total, Integer chunk) {
		String body = null;
		int dot = name.lastIndexOf(".");
		if (dot != -1) {
			body = name.substring(0, dot);
		} else {
			body = name;
		}
    	StringBuilder sb = new StringBuilder();
    	sb.append(PathKit.getWebRootPath());
    	sb.append(File.separator);
    	sb.append(uploadTempPath);
    	sb.append(File.separator);
    	sb.append(userId);
    	sb.append(File.separator);
    	sb.append(body + "_" + total);
    	sb.append(File.separator);
    	sb.append(chunk);
    	File file=new File(sb.toString());
    	return file;
    }
    
	/**
     * 创建好上传目录准备接收上传
     * 
     * @param userId 用户ID
     * @param name 文件名
     * @param total 文件大小
     * @param chunk 分片序号
     * 
     * @return 分片文件
     */
    public static File MirFile(String userId, String name, int total) {
		String body = null;
		int dot = name.lastIndexOf(".");
		if (dot != -1) {
			body = name.substring(0, dot);
		} else {
			body = name;
		}
    	StringBuilder sb = new StringBuilder();
    	sb.append(PathKit.getWebRootPath());
    	sb.append(File.separator);
    	sb.append(uploadTempPath);
    	sb.append(File.separator);
    	sb.append(userId);
    	sb.append(File.separator);
    	sb.append(body + "_" + total);

    	File file=new File(sb.toString());
       	if (!file.exists()) {
       		file.mkdirs();
        }
    	return file;
    }   
    /**
     * 重命名分片
     * @param f 文件
     * @return 文件
     */
    public static File renameChunk(File f) {
    	//默认第一个为0
    	f = new File(f.getParent(), "0");
 
    	//后续累加
		int count = 0;
		while (f.exists() && count < 9999) {
			count++;
			f = new File(f.getParent(), String.valueOf(count));
		}
 
		return f;
	}
    
    /**
     * 重命名分片
     * @param f 文件
     * @param chunk 分片序号
     * @param name 文件名
     * @param size 文件大小
     * @return 文件
     */
    public static File renameChunk(String key,File f, Integer chunk, String name, Long size) {
    	
    	//新的父文件
		String body = null;
		int dot = name.lastIndexOf(".");
		if (dot != -1) {
			body = name.substring(0, dot);
		} else {
			body = name;
		}
    	StringBuilder sb = new StringBuilder();
    	sb.append(f.getParent());
    	sb.append(File.separator);
    	sb.append(key);
    	sb.append(File.separator);
    	sb.append(body + "_" + size);
    	File parent = new File(sb.toString());
    	if (!parent.exists()) {
			if (!parent.mkdirs()) {
				return null;
			}
    	}
    	
    	return new File(parent, String.valueOf(chunk));
	}
    
    /**
     * 合并分片文件
     * @param dir 分片文件夹
     * @param output 目标文件
     * @throws IOException
     */
    @SuppressWarnings("resource")
	public static void mergeChunk(File dir, File output) throws IOException {
    	File[] fileArray = dir.listFiles(new FileFilter(){  
            //排除目录只要文件  
            @Override  
            public boolean accept(File pathname) {  
                if(pathname.isDirectory()){  
                    return false;
                }  
                return true;  
            }  
        });  
          
        //转成集合，便于排序  
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));  
        Collections.sort(fileList,new Comparator<File>() {  
            @Override  
            public int compare(File o1, File o2) {  
                if(Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())){  
                    return -1;  
                }  
                return 1;  
            }  
        });
        
        // 目标文件安排
        if(output.exists()) { 
        	output.delete();
        }
        output.createNewFile();  
        
        //输出流  
        FileChannel outChnnel = new FileOutputStream(output).getChannel();  
        //合并  
        FileChannel inChannel;  
        for(File file : fileList) {
            inChannel = new FileInputStream(file).getChannel();  
            inChannel.transferTo(0, inChannel.size(), outChnnel);  
            inChannel.close();
            file.delete();
        }
        outChnnel.close();
        
        //清除文件夹
        if(dir.isDirectory() && dir.exists()){  
        	dir.delete();  
        }
    }	

    public static byte[] fileToBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);
        
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            
            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
              
            } finally{
                try {
                    if(null!=fis){
                        fis.close();
                    }
                } catch (IOException ex) {
                   
                }
            }
        }
        
        return buffer;
    }

}
