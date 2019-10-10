
#sql("queryList")
		select * from shop_coupon 
        WHERE 1=1
        #if(name != null && name.trim() != '')
          and name like concat('%',#para(name),'%')
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

#sql("queryUserCoupons")
        select a.*,b.coupon_number,b.user_id,b.coupon_status,b.id user_coupon_id
        from shop_coupon a
        left join shop_user_coupon b on a.id = b.coupon_id
        where 1 = 1
        #if(user_id != null )
          and b.`user_id` = #para(user_id)
        #end
        #if(couponId != null )
          and b.`id` = #para(couponId)
        #end
        #if(send_type != null )
          and a.`send_type` = #para(send_type)
        #end
        #if(coupon_status != null )
          and b.`coupon_status` = #para(coupon_status)
        #end
        #if(coupon_number != null )
          and b.`coupon_number` = #para(coupon_number)
        #end
        #if(source_key != null && source_key.trim() != '')
          and b.`source_key` = #para(source_key)
        #end
        #if(good_price != null )
          and a.`min_goods_amount` <= #para(good_price)
        #end
        #if( notUsed != null &&  notUsed == true)
          and b.used_time is null and (b.order_id  is null or b.order_id =0)
        #end
         order by b.id desc
#end

