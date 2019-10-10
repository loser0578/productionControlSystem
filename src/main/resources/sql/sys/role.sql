
#sql("queryList")
		select r.*, (select d.name from sys_dept d where d.dept_id = r.dept_id) deptName from sys_role r
        WHERE 1=1
        #if(roleName != null && roleName.trim() != '')
          and r.roleName like concat('%',#para(roleName),'%')
        #end
        #if(createUserId != null )
          and r.create_user_id = #para(createUserId)
        #end
        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by r.role_id desc
       	#end
       	#if(offset != null && limit != null)
       	 limit #{offset}, #{limit}
       	#end
#end

#sql("queryObject")
	select * from sys_role where role_id = #para(0)
#end