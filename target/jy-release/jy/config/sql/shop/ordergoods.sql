
#sql("queryList")
		select * from shop_order_goods
		WHERE 1=1
        #if(orderId != null)
          and order_id =#para(orderId)
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
 		select o.*,o.shipping_status*1 as shipping_status,o.pay_status*1 as pay_status,o.shipping_id as shipping_id,u.username as username
        from shop_order o
        left join shop_user u on o.user_id = u.id
        where o.id = #para(0)
#end
