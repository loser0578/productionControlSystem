#sql("queryList")
		select c.*,ca.name as category_name from shop_categoryclassfy c
		left join shop_category ca on ca.id=c.category_id
		WHERE 1=1
        #if(name != null && name.trim() != '')
          and c.name like concat('%',#para(name),'%')
        #end
        #if(categoryid != null )
          and c.category_id = #para(categoryid)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #para(sidx) #para(order)
        #else
        	 order by id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #para(offset), #para(limit)
       	#end
#end
#sql("queryObject")
		select c.*,ca.name as category_name from shop_categoryclassfy c
		left join shop_category ca on ca.id=c.category_id
		where c.id = #para(0)
		
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end
