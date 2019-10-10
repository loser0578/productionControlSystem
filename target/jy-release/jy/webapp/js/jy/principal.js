var vm = new Vue({
    el: '#rrapp',
    data: {
    	query:{
    		year:'',
    		quarter:'',
    		month:'',
    	},
    	quarterList:[{id:1,name:'第一季度'},{id:2,name:'第二季度'},{id:3,name:'第三季度'},{id:4,name:'第四季度'}],
    	monthList:[{id:1,name:'一月份'},{id:2,name:'二月份'},{id:3,name:'三月份'},{id:4,name:'四月份'},
    		{id:5,name:'五月份'},{id:6,name:'六月份'},{id:7,name:'七月份'},{id:8,name:'八月份'},
    		{id:9,name:'九月份'},{id:10,name:'十月份'},{id:11,name:'十一月份'},{id:12,name:'十二月份'}]
    },
    created:function(){
    	
    },
    mounted () {
    	 // 准时交货率的方法柱状图
    	this.getDelivery();
    },
	methods:{
		select:function(){
			Ajax.request({
                type: "POST",
                async: true,
                dataType: 'json',
                url: "../jy/principal/getQueryEcharts",
                contentType: "application/json",
                params: JSON.stringify(vm.query),
                successCallback: function (r) {
                	var deliveryChart = echarts.init(document.getElementById('principal'));
                	deliveryChart.setOption({
               		 xAxis: {
               	            data: r.list.principal
               	        },
               	        series: [{
               	            name: '准时交货率',
               	            data: r.list.ratio
               	        }]
                	});
                }
            });
		},
		//渲染图标
		getDelivery:function(){
			var deliveryChart = echarts.init(document.getElementById('principal'));
			deliveryChart.setOption({
				title: {
	                 text: '生产负责人准时交货率(百分比)'
	             },
	             xAxis: {
	                 data: []
	             },
	             tooltip : {
	                 trigger: 'item',
	                 formatter: "{a} <br/>{b} : {c} ({d}%)"
	             },
	             yAxis: {
	            	
	             },
	             series: [{
	                 name: '准时交货率',
	                 type: 'bar',
	                 data: []
	             }]
			});
			Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/principal/getDeliveryEcharts",
                contentType: "application/json",
                successCallback: function (r) {
                	deliveryChart.setOption({
                		 xAxis: {
                	            data: r.list.principal
                	        },
                	        tooltip : {
           	                 trigger: 'item',
           	                 formatter: "{a} <br/>{b} : {c} %"
           	             	},
                	        series: [{
                	            name: '准时交货率',
                	            data: r.list.ratio
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