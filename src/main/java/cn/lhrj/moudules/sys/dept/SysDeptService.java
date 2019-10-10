package cn.lhrj.moudules.sys.dept;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.SysDept;
import cn.lhrj.common.utils.StrUtils;

public class SysDeptService {
    
	public static final SysDeptService me = new SysDeptService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("dept.queryList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}	
    /**
     * 获取子部门ID(包含本部门ID)，用于数据过滤
     */
    public String getSubDeptIdList(Long deptId) {
        //部门及子部门ID列表
        List<Long> deptIdList = new ArrayList<>();

        //获取子部门ID
        SqlPara sPara=Db.getSqlPara("dept.queryDetpIdList", deptId);
        List<Record> records=Db.find(sPara);

        getDeptTreeList(records, deptIdList);

        //添加本部门
        deptIdList.add(deptId);

        String deptFilter = StrUtils.join(deptIdList, ",");
        return deptFilter;
    }
    /**
     * 递归
     */
    public void getDeptTreeList(List<Record> subIdList, List<Long> deptIdList) {
        for (Record deptId : subIdList) {
            //获取子部门ID
            SqlPara sPara=Db.getSqlPara("dept.queryDetpIdList", deptId);
            List<Record> list=Db.find(sPara);
           
            if (list.size() > 0) {
                getDeptTreeList(list, deptIdList);
            }

            deptIdList.add(deptId.getLong("dept_id"));
        }
    }	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("dept.queryList", kv);
		List<Record> list=Db.find(sPara);
		return list;
	}
    /**
     * 查询菜单
     */	
	public Record queryObject(long id) {
	
		SqlPara sPara=Db.getSqlPara("dept.queryObject", id);
		Record list=Db.findFirst(sPara);
		return list;
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	public void save(String string) {
		SysDept dept=FastJson.getJson().parse(string, SysDept.class);
		dept.save();
	}
	public void update(String string) {
		SysDept dept=FastJson.getJson().parse(string, SysDept.class);
		dept.update();
	}
	public void delectByIds(String string) {
		Db.delete("delete from sys_dept where dept_id= ?",string);	
	}
}
