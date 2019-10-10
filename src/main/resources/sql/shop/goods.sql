
#sql("list")
		select g.*,c.name as category_name,ac.name as attribute_category_name,b.name as brand_name
		from 
		shop_goods g 
		LEFT JOIN shop_category c ON g.category_id = c.id
        LEFT JOIN shop_attribute_category ac ON g.attribute_category = ac.id
        LEFT JOIN shop_brand b ON b.id = g.brand_id
		WHERE 1=1
        #if(name != null && name.trim() != '')
          AND g.`name` like concat('%', #para(name), '%')
        #end
        #if(parent_id != null && parent_id.trim() != '')
         AND `parent_id` = #{parentId}
        #end
        AND g.is_Delete = #para(isDelete)
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by g.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end
#sql("queryList")
	select id,name from shop_goods 
#end



#sql("queryObject")
   select s.*,c.name as category_name  from shop_goods s left join shop_category c on s.category_id=c.id  where s.id = #para(0)
#end