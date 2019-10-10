

#sql("queryList")
		select gg.*,g.name as goods_name
		from 
		shop_goods_gallery gg 
		LEFT JOIN shop_goods g ON gg.goods_id = g.id
		WHERE 1=1
        #if(goodsName != null && goodsName.trim() != '')
          and g.name like concat('%',#(goodsName),'%')
        #end
        #if(goodsId != null)
         AND gg.goods_id = #(goodsId)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by gg.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end


#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end

#sql("queryObject")
   select * from shop_goods where id = #para(0)
#end