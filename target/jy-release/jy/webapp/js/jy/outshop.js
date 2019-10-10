$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
	    	outListSize:'',
	    	outColumns: [
	        	{
					title:'操作',
					key:'action',
					align: 'center',
					render:(h,params)=>{
						return h('div',[
							h('i-button',{
								props:{
									type:'warning'
								},
								style: {
	                                marginRight: '10px'
	                            },
								on:{
									click:() => {
										vm.edit(params.row.id,params.index);
									}
								}
							},
							'撤销'),
						]);
					}
				},
		        {
		            title: '下单日期',
		            key: 'orderTime'
		        },
		        {
		            title: '客户',
		            key: 'client'
		        },
		        {
		            title: '订单号',
		            key: 'order_id'
		        },
		        {
		            title: '生产批号',
		            key: 'batch_id'
		        },
		        {
		            title: '生产负责人',
		            key: 'principal'
		        },
		        {
		            title: '出库日期',
		            key: 'outTime'
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
		    outPageList: [],
	    },
	    created:function(){
	    	this.pageList();
	    },
	    methods:{
	    	pageList: function () {
	    		this.showTable = true;
	    	    Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/outshop/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.outPageList = r.page.list;
	                    vm.outListSize = r.page.totalRow
	                }
	            });
	      },
	      invoice:function(id,value){
	    	  console.log(value);
	    	  Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/outshop/invoice",
	                contentType: "application/json",
	                params:{
	                	id:id,
	                	invoice:value
	                },
	                successCallback: function (r) {
	                	
	                }
	            });
	      },
	      edit:function(orderId,index){
	    	  Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/outshop/edit",
	                contentType: "application/json",
	                params:{
	                	id:orderId
	                },
	                successCallback: function (r) {
	                	 vm.outPageList.splice(index, 1);
	                	 vm.success();
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
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
	       }
	    }
	});
});