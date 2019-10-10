
#sql("queryList")
        select o.*,o.shipping_status*1 as shipping_status,o.pay_status*1 as pay_status,o.shipping_id*1 as shipping_id,u.username as username,u.nickname,og.number,og.number_remain
        from shop_order o
        left join shop_user u on o.user_id = u.id
        left join shop_order_goods og on og.order_id = o.id
        WHERE 1=1
        #if(orderSn != null && orderSn.trim() != '')
          and o.order_sn like concat('%',#para(orderSn),'%')
        #end
        #if(shippingStatus != null )
         and o.shipping_status = #para(shipping_status)
        #end
        #if(payStatus != null )
          and o.pay_status = #para(payStatus)
        #end
        #if(orderStatus != null  )
         and o.order_status = #para(orderStatus)
        #end
        #if(orderType != null && orderType.trim() != '')
         and o.order_type = #para(orderType)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by o.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryListReciverList")
        select ore.*,ore.shipping_status*1 as shipping_status,ore.shipping_id*1 as shipping_id,u.nickname as nickname,u.nickname,og.number,og.number_remain,
        o.pay_status,o.actual_price,o.order_price,og.retail_price
        from shop_order_reciver ore
        left join shop_user u on ore.receiver_id = u.id
        left join shop_order o on ore.parent_id = o.id
        left join shop_order_goods og on og.order_id = ore.parent_id
        WHERE 1=1
        #if(orderSn != null && orderSn.trim() != '')
          and o.order_sn like concat('%',#para(orderSn),'%')
        #end
        #if(shippingStatus != null )
         and o.shipping_status = #para(shipping_status)
        #end
        #if(payStatus != null )
          and o.pay_status = #para(payStatus)
        #end
        #if(orderStatus != null  )
         and o.order_status = #para(orderStatus)
        #end
        #if(orderType != null && orderType.trim() != '')
         and o.order_type = #para(orderType)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by o.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryAdminObject")
 		select o.*,o.shipping_status*1 as shipping_status,o.pay_status*1 as pay_status,o.shipping_id as shipping_id,u.username as username,u.nickname,og.goods_id,og.goods_name,og.goods_sn,og.goods_specifition_name_value
        from shop_order o
         left join shop_user u on o.user_id = u.id
         left join shop_order_goods og  on og.order_id=o.id
        where o.id = #para(0)
#end

#sql("queryObject")
 		select o.*,o.shipping_status*1 as shipping_status,o.pay_status*1 as pay_status,o.shipping_id as shipping_id,u.username as username,u.nickname,og.goods_id,og.goods_name,og.goods_sn,og.goods_specifition_name_value
        from shop_order_reciver ore
         left join shop_order o on o.id = ore.parent_id
         left join shop_user u on o.user_id = u.id
         left join shop_order_goods og  on og.order_id=o.id
        where o.id = #para(0)
#end

#sql("queryApiObject")
 		select o.order_price,c.cover_url,o.actual_price,o.order_sn ,o.id,o.coupon_price,o.add_time,og.list_pic_url,og.goods_name,og.goods_name,og.goods_specifition_name_value,og.number,o.shipping_status*1 as shipping_status,o.pay_status*1 as pay_status,o.shipping_id as shipping_id,u.username as username,u.nickname,og.number,og.number_remain
        from shop_order o
        left join shop_user u on o.user_id = u.id
        left join shop_order_goods og on og.order_id = o.id
        left join shop_category c on c.id=o.category_id
        where o.order_sn = #para(0)
#end

#sql("queryReciverObject")
	select o.id,u.nickname,ore.*, og.goods_name,og.retail_price,og.goods_specifition_name_value,og.list_pic_url,o.order_sn as parent_order_sn from 
	shop_order_goods og 
	left join shop_order o on og.order_id=o.id
	
	left join shop_order_reciver ore on ore.parent_id=o.id
	left join shop_user u on ore.receiver_id = u.id
    where ore.id = #para(0)
#end


#sql("queryApiOrderList")
		select o.order_sn as orderId,og.goods_name,og.goods_specifition_name_value,og.number,og.list_pic_url,o.add_time,og.number,og.number_remain
		from shop_order_goods og 
		left join shop_order o on og.order_id=o.id
	
	    where o.pay_status = 2 and o.user_id = #para(0) order by o.id DESC
#end

#sql("queryApiReciverObject")
	select ore.order_sn as orderId,o.id,u.nickname,ore.shipping_no,ore.parent_id,ore.id,ore.is_location, ore.mobile,ore.shipping_status*1 as shipping_status,u.nickname,ore.add_time, og.goods_name,og.goods_specifition_name_value,ore.number,og.list_pic_url from 
	shop_order_goods og 
	left join shop_order o on og.order_id=o.id
	left join shop_user u on o.user_id = u.id
	left join shop_order_reciver ore on ore.parent_id=o.id
    where ore.order_sn = #para(0)
#end


#sql("queryApiOrderReciverList")
	select ore.order_sn as orderId, u.nickname,og.goods_name,og.goods_specifition_name_value,ore.number,og.list_pic_url from 
	shop_order_goods og 
	left join shop_order o on og.order_id=o.id
	left join shop_order_reciver ore on ore.parent_id=o.id
	left join shop_user u on o.user_id = u.id
    where o.pay_status = 2 and ore.receiver_id = #para(0) order by o.id DESC
#end
