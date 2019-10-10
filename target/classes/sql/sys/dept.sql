
#sql("queryList")
		select d.*,(select p.name from sys_dept p where p.dept_id = d.parent_id) as parentName
		from sys_dept d where d.del_flag = 0
		
		
        #if(deptFilter != null)
          and d.dept_id in (#para(createUserId))
        #end

        #if(sidx != null && sidx.trim() != '')
             order by #(sidx) #(order)
        #else
        	 order by d.dept_id desc
       	#end

#end

#sql("queryObject")
		select d.*,(select p.name from sys_dept p where p.dept_id = d.parent_id) as parentName
		from sys_dept d where d.del_flag = 0 and d.dept_id = #para(0)
#end

#sql("queryDetpIdList")
	select dept_id from sys_dept where parent_id = #para(0) and del_flag = 0
#end