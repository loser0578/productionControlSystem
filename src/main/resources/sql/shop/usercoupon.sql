
#sql("queryList")
		select uc.*,c.name as coupon_name,u.nickname as user_name from shop_user_coupon  uc 
		LEFT JOIN shop_user u ON uc.user_id = u.id
		LEFT JOIN shop_coupon c ON uc.coupon_id = c.id
        WHERE 1=1
        #if(userName != null && userName.trim() != '')
          and u.username like concat('%',#para(userName),'%')
        #end
         #if(userId != null )
          and uc.user_id = #para(userId)
        #end      
        #if(couponName != null && couponName.trim() != '')
          and uc.name like concat('%',#para(couponName),'%')
        #end
        
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select * from shop_coupon where id = #para(0)
#end