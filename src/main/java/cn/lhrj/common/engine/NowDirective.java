package cn.lhrj.common.engine;

import java.util.Date;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

public class NowDirective extends Directive {


	@Override
	public void exec(Env env, Scope scope, Writer writer) {
		write(writer, DateKit.toStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		
	}
}
