
#sql("queryList")
		select u.*
        from shop_user u 
        WHERE 1=1
        #if(username != null && username.trim() != '')
          and u.username like concat('%',#para(username),'%')
        #end

        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by u.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select * from shop_user where id = #para(0)
#end