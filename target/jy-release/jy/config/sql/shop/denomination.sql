#sql("list")
   select * from shop_denomination where 1=1
   #if(name != null && name.trim() != '')
       and name like concat('%',#para(name),'%')
   #end
#end


#sql("queryList")
	select * from shop_denomination 
	#if(sidx != null && sidx.trim() != '')
		order by #para(sidx) #para(order)
	#else
		order by denomination asc
	#end
	#if(offset != null && limit != null)
	    limit #para(offset), #para(limit)
	#end
#end		

#sql("queryObject")
	select * from shop_denomination where id = #para(0)
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end