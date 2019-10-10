var vm = new Vue({
    el: '#rrapp',
    data: {
    	selectList:[],
    	query:{
    		queryList:[],
    		year:'2019',
    	},	
    },
    created:function(){
    	
    },
    mounted () {
    	this.getSelectList();
    	this.getProblem();
    },
	methods:{
		getProblem:function(){
			var problemChart = echarts.init(document.getElementById('problem'));
			problemChart.setOption({
				title: {
					text: '生产质量问题比例',
			        x:'center'
	             },
	             tooltip : {
	                 trigger: 'item',
	                 formatter: "{a} <br/>{b} : {c} ({d}%)"
	             },
	             series : [
	                 {
	                     name: '公司质量问题百分比',
	                     type: 'pie',
	                     radius: '70%',
	                     data:[]
	                 }
	             ]
			});
			Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/problem/getAllProblem",
                contentType: "application/json",
                successCallback: function (r) {
                	problemChart.setOption({
        				title: {
        					text: '生产质量问题比例(全体)',
        			        x:'center'
        	             },
        	             tooltip : {
        	                 trigger: 'item',
        	                 formatter: "{a} <br/>{b} : {c} ({d}%)"
        	             },
        	             series : [
        	                 {
        	                	 name: '公司质量问题百分比',
        	                     type: 'pie',
        	                     radius: '70%',
        	                     data:r.list
        	                 }
        	             ]
        			});
                }
            });
		},
		select:function(){
			Ajax.request({
                type: "POST",
                async: true,
                dataType: 'json',
                url: "../jy/problem/getProblem",
                contentType: "application/json",
                params: JSON.stringify(vm.query),
                successCallback: function (r) {
                	var problemChart = echarts.init(document.getElementById('problem'));
                	problemChart.setOption({
        				title: {
        					text: '生产质量问题比例',
        			        x:'center'
        	             },
        	             tooltip : {
        	                 trigger: 'item',
        	                 formatter: "{a} <br/>{b} : {c} ({d}%)"
        	             },
        	             series : [
        	                 {
        	                	 name: '公司质量问题百分比',
        	                     type: 'pie',
        	                     radius: '70%',
        	                     data:r.list
        	                 }
        	             ]
        			});
                }
            });
		},
		timeChange:function(time){
			this.query.year = time;
		},
		getSelectList:function(){
			Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/problem/selectList",
                contentType: "application/json",
                successCallback: function (r) {
                	console.log(r);
                	vm.selectList = r.selectList;
                }
            });
		},
	},
});