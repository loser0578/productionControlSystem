
#sql("queryList")
		select u.*, (select d.name from sys_dept d where d.dept_id = u.dept_id) deptName from sys_user u
        WHERE 1=1
        #if(createUserId != null && createUserId.trim() != '')
          and u.create_user_id like concat('%',#para(createUserId),'%')
        #end
        #if(username != null && username.trim() != '')
          and u.username like concat('%',#para(username),'%')
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by u.id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select * from sys_user where id = #para(0)
#end