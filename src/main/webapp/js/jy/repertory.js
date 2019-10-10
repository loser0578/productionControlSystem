$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	showModel:false,
	    	showLog:false,
	    	title:'',
	    	logpage:1,
	    	page:1,
	    	limit:10,
	    	repertoryListSize:'',
	    	repertoryColumns: [
		        {
		            title: '产品名称',
		            key: 'product_name'
		        },
		        {
		            title: '库存数量',
		            key: 'residue'
		        },
		        {
					title:'操作',
					key:'action',
					align: 'center',
					render:(h,params)=>{
						return h('div',[
							h('i-button',{
								props:{
									type:'success'
								},
								style: {
	                                marginRight: '10px'
	                            },
								on:{
									click:() => {
										vm.edit(params.row.id);
									}
								}
							},
							'出货'),
						]);
					}
				}
		    ],
		    repertoryPageList: [],
		    clientList:[],
		    modelItem:{
		    	id:'',
		    	client_id:'',
		    	number:0,
		    },
		    logColumns:[
		    	{
		            title: '产品名称',
		            key: 'product_name'
		        },
		        {
		            title: '客户名称',
		            key: 'client_name'
		        },
		        {
		            title: '客户名称',
		            key: 'client_name'
		        },
		        {
		            title: '出库日期',
		            key: 'create_time'
		        },
		        {
		            title: '出库数量',
		            key: 'number'
		        },
		        {
					title:'是否开票',
					render:(h,params)=>{
						return h('div', [
	                    	h('Checkbox',{
								props:{
									size:'large',
									trueValue:1,
									falseValue:0,
									value:params.row.is_invoice
								},
								style: {
									
	                            },
	                            on:{
	                            	'on-change':(value)=>{
	                            		vm.invoice(params.row.id,value);
	                            	}
	                            }
							}),
						]);
					}
				}
		    ],
		    logPageList:[],
		    logListSize:'',
	    },
	    created:function(){
	    	this.pageList();
	    	this.getClientList();
	    },
	    methods:{
	    	pageList: function () {
	    		this.showTable = true;
	    	    Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.repertoryPageList = r.page.list;
	                    vm.repertoryListSize = r.page.totalRow
	                }
	            });
	      },
	      getClientList:function(){
	    	  Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/getClientList",
	                contentType: "application/json",
	                successCallback: function (r) {
	                	console.log(r);
	                	vm.clientList = r.list;
	                }
	            });
	      },
	      invoice:function(id,value){
	    	  console.log(value);
	    	  Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/invoice",
	                contentType: "application/json",
	                params:{
	                	id:id,
	                	invoice:value
	                },
	                successCallback: function (r) {
	                	
	                }
	            });
	      },
	      edit:function(id){
	    	  this.showModel = true;

	    	  Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/getClientList",
	                contentType: "application/json",
	                successCallback: function (r) {
	                	vm.clientList = r.list;
	                	vm.modelItem.id = id;
	                }
	          });
	      },
	      save:function(){
	    	  Ajax.request({
	                type: "POST",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/save",
	                contentType: "application/json",
	                params: JSON.stringify(vm.modelItem),
	                successCallback: function (r) {
	                	console.log(r);
	                }
	          });
	      },
	      lookLog:function(){
	    	  this.showLog = true;
	    	  this.showTable = false;
		      this.showModel = false;
		      Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/repertory/lookLog",
	                contentType: "application/json",
	                params:{
	                	page:this.logpage,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.logPageList = r.page.list;
	                    vm.logListSize = r.page.totalRow
	                }
	           });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   pageChangeLog :function(index){
			   this.logpage = index;
				this.lookLog();
		   },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   //this.$Message.success('成功');
	                   vm.success(valid);
	                   var url = vm.addItem.process_id == "" ? "../jy/process/save" : "../jy/process/update";
		       			Ajax.request({
		                      type: "POST",
		                      url: url,
		                      contentType: "application/json",
		                      params: JSON.stringify(vm.addItem),
		                      successCallback: function (r) {
		                    	  vm.pageList();
		                      }
		                  });
	               } else {
	            	   vm.error(valid);
	                   //this.$Message.error('失败');
	               }
	           })
	       },
	       success (nodesc) {
	           this.$Notice.success({
	               title: '成功',
	           });
	       },
	       error (nodesc) {
	           this.$Notice.error({
	               title: '失败',
	              
	           });
	       },
	       ok () {
	            this.$Message.info('已提交出货');
	            vm.save();
	            this.back();
	        },
	        cancel () {
	            this.$Message.info('已取消出货');
	        },
	        //返回方法
	        back:function(){
	        	location.reload();
	        }
	    }
	});
});