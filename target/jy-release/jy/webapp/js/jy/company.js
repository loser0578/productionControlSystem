var vm = new Vue({
    el: '#rrapp',
    data: {
    	query:{
    		year:'2019',
    	},
    	/*monthList:[{id:1,name:'一月份'},{id:2,name:'二月份'},{id:3,name:'三月份'},{id:4,name:'四月份'},
    		{id:5,name:'五月份'},{id:6,name:'六月份'},{id:7,name:'七月份'},{id:8,name:'八月份'},
    		{id:9,name:'九月份'},{id:10,name:'十月份'},{id:11,name:'十一月份'},{id:12,name:'十二月份'}]*/
    },
    created:function(){
    	
    },
    mounted () {
    	// 公司准时交货率方法 折线图
    	this.getCompany();
    },
	methods:{
		getCompany:function(){
			var companyChart = echarts.init(document.getElementById('company'));
			companyChart.setOption({
				title: {
	                 text: '公司准时交货率(百分比)'
	             },
	             xAxis: {
	            	 type: 'category',
	                 data: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
	             },
	             yAxis: {
	            	type:'value',
	            	max:100,
	             },
	             series: [{
	                 type: 'line',
	                 data: []
	             }]
			});
			Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/company/select",
                contentType: "application/json",
                params:  {
                	year:this.query.year
                },
                successCallback: function (r) {
                	companyChart.setOption({
        				title: {
        	                 text: '公司准时交货率(百分比)'
        	             },
        	             xAxis: {
        	            	 type: 'category',
        	                 data: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
        	             },
        	             yAxis: {
        	            	type:'value',
        	            	max:100,
        	             },
        	             series: [{
        	                 type: 'line',
        	                 data: r.list
        	             }]
        			});
                }
            });
		},
		select:function(){
			Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/company/select",
                contentType: "application/json",
                params:  {
                	year:vm.query.year,
                },
                successCallback: function (r) {
                	var companyChart = echarts.init(document.getElementById('company'));
                	companyChart.setOption({
        				title: {
        	                 text: '公司准时交货率(百分比)'
        	             },
        	             xAxis: {
        	            	 type: 'category',
        	                 data: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
        	             },
        	             yAxis: {
        	            	type:'value',
        	            	max:100,
        	             },
        	             series: [{
        	                 type: 'line',
        	                 data: r.list
        	             }]
        			});
                }
            });
		},
		timeChange:function(time){
			this.query.year = time;
		},
	},
});