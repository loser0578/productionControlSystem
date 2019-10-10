$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
	    	principalListSize:'',
	    	principalColumns: [
		        {
		            title: '负责人名称',
		            key: 'principal_name'
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
										vm.delect(params.row.principal_id,params.index);
									}
								}
							},
							'删除'),
						]);
					}
				}
		    ],
		    principalPageList: [],
	    	showAdd:false,
	    	ruleValidate: {
	    		principal_name: [
	                { required: true, message: '负责人名称不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	principal_id:'',
	        	principal_name: '',
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
	                url: "../jy/inprincipal/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.principalPageList = r.page.list;
	                    vm.principalListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   delect:function(principalid,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/inprincipal/delect",
	               contentType: "application/json",
	               params:  {
	               	id:principalid
	               },
	               successCallback: function () {
	               	vm.principalPageList.splice(index, 1);
	               }
	           });
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增负责人';
	    	   vm.addItem.principal_id = '';
	    	   vm.addItem.principal_name = '';
	    	   vm.addItem.sort = '1';
	       },
	       edit: function(params){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '修改负责人';
	    	   vm.addItem.principal_id = params.principal_id;
	    	   vm.addItem.principal_name = params.principal_name;
	    	   vm.addItem.sort = params.sort;
	       },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   //this.$Message.success('成功');
	                   vm.success(valid);
	                   var url = vm.addItem.principal_id == "" ? "../jy/inprincipal/save" : "../jy/inprincipal/update";
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