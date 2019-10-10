
#sql("queryList")
	  select gs.*,g.name as goods_name,s.name as specification_name
        from shop_goods_specification gs
        
        LEFT JOIN shop_goods g ON g.id = gs.goods_id
        
        LEFT JOIN shop_specification s ON s.id = gs.specification_id
        
        WHERE 1=1
        #if(goodsName != null && goodsName.trim() != '')
          AND g.name like concat('%',#para(goodsName),'%')
        #end
        #if(goodsId != null )
          AND gs.goods_id = #(goodsId)
        #end       
        #if(specificationId != null )
          AND gs.specification_id = #(specificationId)
        #end
        #if(ids != null )
          AND gs.id in  (
          #for(x : ids)
		     #(x) #(for.last ? " " : ",")
		  #end
		  )
        #end  
        #if(sidx != null && sidx.trim() != '')
            order by gs.#(sidx) #(order)
        #else
        	 order by gs.id asc
       	#end
       	#if(offset != null && limit != null)
       	 limit #(offset), #(limit)
       	#end
#end

#sql("queryObject")
	select * from shop_goods_specification where id = #para(0)
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end

#sql("ApiqueryList")
	  select gs.*,g.name as goods_name,s.name as specification_name
       
	  from shop_goods_specification gs
        
        LEFT JOIN shop_goods g ON g.id = gs.goods_id
        
        LEFT JOIN shop_specification s ON s.id = gs.specification_id
        
        WHERE 1=1
        #if(goodsName != null && goodsName.trim() != '')
          AND g.name like concat('%',#para(goodsName),'%')
        #end
        #if(goodsId != null )
          AND gs.goods_id = #para(goodsId)
        #end
        #if(specificationId != null )
          AND gs.specification_id = #para(specificationId)
        #end
        GROUP BY gs.specification_id
        #if(sidx != null && sidx.trim() != '')
            order by gs.#(sidx) #(order)
        #else
        	 order by gs.id asc
       	#end
       	#if(offset != null && limit != null)
       	 limit #(offset), #(limit)
       	#end
#end
