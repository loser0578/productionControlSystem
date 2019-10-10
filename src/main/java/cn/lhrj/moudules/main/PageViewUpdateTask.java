package cn.lhrj.moudules.main;

import java.util.Date;

import com.jfinal.aop.Aop;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;

public class PageViewUpdateTask implements ITask {
	
	MainService pageViewSrv = Aop.get(MainService.class);

	public void run() {
		doUpdate();
	}

	public void stop() {
		doUpdate();
	}

	private void doUpdate() {
		pageViewSrv.init();
		pageViewSrv.getGoodsDate();
		pageViewSrv.getSellDate();
		pageViewSrv.getUserDate();
		pageViewSrv.getVisitDate();
		// 每次调度启动时，向 task_run_log 写日志，用于检查调度的时间是否与预期的一致，避免出现 bug 却不知道
		Record taskRunLog = new Record().set("taskName", "PageViewUpdateTask").set("createAt", new Date());
		Db.save("task_run_log", taskRunLog);
	}
}
