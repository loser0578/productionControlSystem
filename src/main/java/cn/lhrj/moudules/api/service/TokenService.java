package cn.lhrj.moudules.api.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import cn.hutool.core.util.RandomUtil;
import cn.lhrj.common.model.ShopUser;
import cn.lhrj.common.model.TbToken;


public class TokenService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;
    
    
    public TbToken queryByToken(String token) {
		TbToken tbToken=new TbToken().findFirst("select * from tb_token where token=?",token);
		return tbToken;
	}
    
    public ShopUser queryUserByToken(String token) {
    	TbToken tbToken=new TbToken().findFirst("select * from tb_token where token=?",token);
		ShopUser user=new ShopUser().findById(tbToken.getUserId());
		return user;
	}
    public static Map<String, Object> createToken(long userId) {
        //生成一个token
        String token = RandomUtil.randomString(32);
        //当前时间
        Date now = new Date();

        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否生成过token
        TbToken tokenEntity = new TbToken().findFirst("select * from tb_token where user_id = ?",userId);
        if (tokenEntity == null) {
            TbToken tokenEntity2=new TbToken();
            tokenEntity2.setUserId(userId);
            tokenEntity2.setToken(token);
            tokenEntity2.setUpdateTime(now);
            tokenEntity2.setExpireTime(expireTime);
            //保存token
            tokenEntity2.save();
        } else {
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);
            //更新token
            tokenEntity.update();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", EXPIRE);
        return map;
    }
}
