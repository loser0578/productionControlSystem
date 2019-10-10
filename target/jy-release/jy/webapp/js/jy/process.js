$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
		    processListSize:'',
	        processColumns: [
		        {
		            title: '工序名称',
		            key: 'process_name'
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
										vm.delect(params.row.process_id,params.index);
									}
								}
							},
							'删除'),
						]);
					}
				}
		    ],
		    processPageList: [],
	    	showAdd:false,
	    	ruleValidate: {
	        	process_name: [
	                { required: true, message: '工序名称不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	process_id:'',
	        	process_name: '',
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
	                url: "../jy/process/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.processPageList = r.page.list;
	                    vm.processListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   delect:function(processid,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/process/delect",
	               contentType: "application/json",
	               params:  {
	               	id:processid
	               },
	               successCallback: function () {
	               	vm.processPageList.splice(index, 1);
	               }
	           });
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增工序';
	    	   vm.addItem.process_id = '';
	    	   vm.addItem.process_name = '';
	    	   vm.addItem.sort = '1';
	       },
	       edit: function(params){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '修改工序';
	    	   vm.addItem.process_id = params.process_id;
	    	   vm.addItem.process_name = params.process_name;
	    	   vm.addItem.sort = params.sort;
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