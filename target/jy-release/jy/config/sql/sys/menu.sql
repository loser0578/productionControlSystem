#sql("paginate")
	select s.id,
		substring(s.title, 1, 100) as title,
		substring(s.content, 1, 180) as content,
		a.avatar,
		a.id as accountId
	from share s inner join account a on s.accountId = a.id
	where report < #para(0)
	order by s.createAt desc
#end

#sql("findById")
	select s.*, a.avatar, a.nickName 
	from share s inner join account a on s.accountId = a.id
	where s.id = #para(0) and s.report < #para(1) limit 1
#end

#sql("getHotShare")
	select id, title from share where id in (
		select shareId  from (
			select shareId from share_page_view where visitDate > #para(0) order by visitCount desc
		) as t group by shareId
	) and report < #para(1) limit 10
#end

#sql("getReplyPage")
	select sr.*, a.nickName, a.avatar
	from share_reply sr inner join account a on sr.accountId = a.id
	where shareId = #para(0)
#end


#sql("queryList")
	  select m.*,(select p.name from sys_menu p where p.menu_id = m.parent_id) as parentName
        from sys_menu m WHERE 1=1
        #if(menuName != null && menuName.trim() != '')
          and m.name like concat('%',#{menuName},'%')
        #end

        #if(parentName != null && parentName.trim() != '')
          AND exists(SELECT 1 FROM sys_menu WHERE m.parent_id=sys_menu.menu_id AND sys_menu.name LIKE concat('%',#{parentName},'%'))
        #end
        #if(sidx != null && sidx.trim() != '')
            order by m.${sidx} ${order}
        #else
        	 order by m.order_num asc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select m.*,(select p.name from sys_menu p where p.menu_id = m.parent_id) as parentName from sys_menu m
	where  1=1
	#if(roleId != null && roleId.trim() != '')
      and m.id in (select menu_id from sys_role_menu where role_id = #{menuName})
    #end
	
	
	
#end

#sql("queryNotButtonList")
	select m.* from sys_menu m where m.type != 2 AND status = 0 order by m.order_num asc
#end

#sql("queryUserList")
	select distinct m.*,(select p.name from sys_menu p where p.menu_id = m.parent_id) as parentName
		from sys_menu m
		LEFT JOIN sys_role_menu rm on m.menu_id = rm.menu_id 
		
	where rm.role_id = #para(0) order by m.order_num asc	
#end
