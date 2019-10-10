


#sql("queryList")
		select
    		`id`,
    		`name`,
    		`keywords`,
    		`front_desc`,
    		`parent_id`,
    		`sort_order`,
    		`show_index`*1 as `show_index`,
    		`is_show`,
    		`video_url`,
    		`present_url`,
    		`img_url`,
    		`video_img_url`,
    		`cover_url`,
    		`level`,
    		`classfy_id`,
    		`type`,
    		`front_name`,
			`is_show` as `show`
		from shop_category
		WHERE 1=1
        #if(name != null && name.trim() != '')
          and m.name like concat('%',#para(name),'%')
        #end
        #if(parent_id != null && parent_id.trim() != '')
         AND `parent_id` = #para(parentId)
        #end
        #if(classfyId != null )
         AND `classfy_id` = #para(classfyId)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by ${sidx} ${order}
        #else
        	 order by show_index desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryIndexClassify")
		select
    		a.`id`,
    		a.`name`,
    		a.`front_desc`,
    		a.`show_index`*1 as `a.show_index`,
			a.`img_url`,
			b.`retail_price`,
			b.`list_pic_url`,
			b.`id` as goodid
		from shop_category a left join shop_goods b on a.goods_id = b.id
		WHERE 1=1
		AND a.`is_show` = 1
		AND a.`level` = "L1"
		AND b.`is_on_sale` = 1
		order by a.`show_index` desc

#end







#sql("queryListL1")
		select
    		`id`,
    		`name`,
    		`keywords`,
    		`front_desc`,
    		`parent_id`,
    		`sort_order`,
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
    		`classfy_id`,
			`is_show` as `show`
		from shop_category
		WHERE 1=1
        #if(name != null && name.trim() != '')
          and m.name like concat('%',#{name},'%')
        #end
        #if(parent_id != null && parent_id.trim() != '')
         AND `parent_id` = #{parentId}
        #end
         AND `level` = "L1"
        #if(sidx != null && sidx.trim() != '')
             order by ${sidx} ${order}
        #else
        	 order by id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
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
    		`goods_id`,
    		`img_url`,
    		`video_img_url`,
    		`cover_url`,
    		`level`,
    		`classfy_id`,
    		`type`,
    		`front_name`,
			`is_show` as `show`
		from shop_category
		where id = #para(0)
		order by show_index desc 
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end


#sql("ApiqueryObject")
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
    		`goods_id`,
    		`img_url`,
    		`video_img_url`,
    		`cover_url`,
    		`level`,
    		`classfy_id`,
    		`type`,
    		`front_name`,
			`is_show` as `show`
		from shop_category
		where `parent_id` = #para(0)
		 order by sort_order desc
#end



