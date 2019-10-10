$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
	    	staffListSize:'',
	        staffColumns: [
		        {
		            title: '用户名',
		            key: 'username'
		        },
			    {
					title:'操作',
					key:'action',
					align: 'center',
					render:(h,params)=>{
						return h('div',[
							h('i-button',{
								props:{
									type:'error'
								},
								style: {
	                                marginRight: '10px'
	                            },
								on:{
									click:() => {
										vm.delect(params.row.id,params.index);
									}
								}
							},
							'删除'),
						]);
					}
				}
		    ],
		    staffPageList: [],
	    	showAdd:false,
	    	ruleValidate: {
	        	username: [
	                { required: true, message: '用户名不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	id:'',
	        	username: '',
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
	                url: "../jy/staff/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.staffPageList = r.page.list;
	                    vm.staffListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   delect:function(id,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/staff/delect",
	               contentType: "application/json",
	               params:  {
	               	id:id
	               },
	               successCallback: function () {
	               	vm.processPageList.splice(index, 1);
	               }
	           });
	       	vm.back();
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增员工';
	    	   vm.addItem.id = '';
	    	   vm.addItem.username = '';
	       },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   //this.$Message.success('成功');
	                   vm.success(valid);
	                   var url = vm.addItem.id == "" ? "../jy/staff/save" : "../jy/staff/update";
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
	       //返回方法
	        back:function(){
	        	location.reload();
	        }
	    }
	});
});