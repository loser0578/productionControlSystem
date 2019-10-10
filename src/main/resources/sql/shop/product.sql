
#sql("queryList")
		select p.*,g.name as goods_name from shop_product p
		LEFT JOIN shop_goods g ON p.goods_id = g.id
        WHERE 1=1
        #if(goodsName != null && goodsName.trim() != '')
          and g.name like concat('%',#para(goodsName),'%')
        #end
        
        #if(goodsId != null )
          and p.goods_id = #para(goodsId)
        #end
        
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by p.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select * from shop_product where id = #para(0)
#end

#sql("queryApiObject")
		select a.*, b.name as goods_name, b.list_pic_url as list_pic_url
		from nideshop_product a left join nideshop_goods b on a.goods_id = b.id
		where a.id = #para(0)
#end
