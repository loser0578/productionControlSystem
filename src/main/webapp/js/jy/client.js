$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
	    	clientListSize:'',
	    	clientColumns: [
		        {
		            title: '客户名称',
		            key: 'client_name'
		        },
		        {
		            title: '排序(数字越小越靠前显示)',
		            key: 'sort'
		        },
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
										vm.edit(params.row);
									}
								}
							},
							'修改'),
							h('i-button',{
								props:{
									type:'error'
								},
								style: {
	                                marginRight: '10px'
	                            },
								on:{
									click:() => {
										vm.delect(params.row.client_id,params.index);
									}
								}
							},
							'删除'),
						]);
					}
				}
		    ],
		    clientPageList: [],
	    	showAdd:false,
	    	ruleValidate: {
	        	client_name: [
	                { required: true, message: '客户名称不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	client_id:'',
	        	client_name: '',
	        	sort:1,
	        },
	    },
	    created:function(){
	    	this.pageList();
	    },
	    methods:{
	    	pageList: function () {
	    		this.showTable = true;
	     	    this.showAdd = false;
	    	    Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/client/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.clientPageList = r.page.list;
	                    vm.clientListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   delect:function(clientid,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/client/delect",
	               contentType: "application/json",
	               params:  {
	               	id:clientid
	               },
	               successCallback: function () {
	               	vm.clientPageList.splice(index, 1);
	               }
	           });
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增客户';
	    	   vm.addItem.client_id = '';
	    	   vm.addItem.client_name = '';
	    	   vm.addItem.sort = '1';
	       },
	       edit: function(params){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '修改工序';
	    	   vm.addItem.client_id = params.client_id;
	    	   vm.addItem.client_name = params.client_name;
	    	   vm.addItem.sort = params.sort;
	       },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   //this.$Message.success('成功');
	                   vm.success(valid);
	                   var url = vm.addItem.client_id == "" ? "../jy/client/save" : "../jy/client/update";
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