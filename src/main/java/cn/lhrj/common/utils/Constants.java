/**
 * Copyright 2015-2025 FLY的狐狸(email:jflyfox@sina.com qq:369191470).
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


/**
 * 常量类
 * 
 * @author flyfox 2012.08.08
 * @email 330627517@qq.com
 */
public class Constants {
    /**
     * 超级管理员ID
     */
    public static final long SUPER_ADMIN = 1;

    /**
     * ORACLE、MYSQL
     */
    public static final String USE_DATA = "MYSQL";
    /**
     * 分页条数
     */
    public static final int pageSize = 10;
    /**
     * 权限前缀
     */
    public static final String PERMS_LIST = "permsList";
    /**
     * 云存储配置KEY
     */
    public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
    /**
     * 短信配置KEY
     */
    public final static String SMS_CONFIG_KEY = "SMS_CONFIG_KEY";
    /**
     * 权限前缀
     */
    public static final String SESSION_KEY = "SESSIONID_";

    /**
     * 排序方式名称 asc:正序 | desc:倒序
     */
    public static final String SORT_ORDER = "sortOrder";
    /**
     * 当前登录用户
     */
    public static final String CURRENT_USER = "curUser";
    /**
     * 默认密码
     */
    public static final String DEFAULT_PASS_WORD = "888888";

    /**
     * 系统缓存前缀
     */
    public static final String SYS_CACHE = "SYS_CACHE:";
	/**
	 * 底层调试标示
	 */
	public static final boolean DEBUG = Config.getToBoolean("CONSTANTS.DEBUG");
	/**
	 * 测试标示
	 */
	public static final boolean TEST = Config.getToBoolean("CONSTANTS.TEST");
	/**
	 * 默认分页
	 */
	public static final int DEFAULT_PAGE_SIZE = Config.getToInt("CONSTANTS.DEFAULT_PAGE_SIZE");
	 /**
	  * 菜单类型
     *
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        private MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}