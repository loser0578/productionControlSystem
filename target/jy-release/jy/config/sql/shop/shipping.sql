


#sql("queryList")
		select * from shop_shipping
		WHERE 1=1
        #if(name != null && name.trim() != '')
          and name like concat('%',#para(name),'%')
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
			select
    		`id`,
    		`name`,
    		`keywords`,
    		`front_desc`,
    		`parent_id`,
    		`sort_order`*1 as `sort_order`,
    		`show_index`*1 as `show_index`,
    		`is_show`,
    		`video_url`,
    		`present_url`,
    		`img_url`,
    		`video_img_url`,
    		`cover_url`,
    		`level`,
    		`type`,
    		`front_name`,
			`is_show` as `show`
		from shop_category
		where id = #para(0)
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end
