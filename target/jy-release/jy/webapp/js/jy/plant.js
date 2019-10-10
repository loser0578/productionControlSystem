$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
		    plantListSize:'',
		    plantColumns: [
		        {
		            title: '公司名称',
		            key: 'name'
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
		    plantPageList: [],
	    	showAdd:false,
	    	ruleValidate: {
	        	name: [
	                { required: true, message: '公司名称不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	id:'',
	        	name: '',
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
	                url: "../jy/plant/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	console.log(r);
	                	vm.plantPageList = r.page.list;
	                    vm.plantListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
				this.page = index;
				this.pageList();
		   },
		   delect:function(id,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/plant/delect",
	               contentType: "application/json",
	               params:  {
	               	id:id
	               },
	               successCallback: function () {
	               	vm.plantPageList.splice(index, 1);
	               }
	           });
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增公司';
	    	   vm.addItem.id = '';
	    	   vm.addItem.name = '';
	       },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   //this.$Message.success('成功');
	                   vm.success(valid);
	                   var url = vm.addItem.id == "" ? "../jy/plant/save" : "../jy/plant/update";
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