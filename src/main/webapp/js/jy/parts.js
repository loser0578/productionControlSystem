$(function () {
	var vm = new Vue({
	    el: '#rrapp',
	    data: {
	    	showTable: true,
	    	title:'',
	    	page:1,
	    	limit:10,
		    partsListSize:'',
	        partsColumns: [
		        {
		            title: '部件名称',
		            key: 'parts_name'
		        },
		        {
		            title: '部件图片',
		            key: 'img',
		            width: 120,
		            render: (h, params) => {
		            	var imgurl = "../statics/img/noimg.jpg";
		            	if(params.row.img == ""||params.row.img == null){
		            		imgurl = "../statics/img/noimg.jpg";
		            	}else{
		            		imgurl = "../statics/img/"+params.row.img;
		            	}
		              return h('Poptip', {
		                props: {
		                  placement: 'right',
		                  trigger: 'hover',
		                },
		 
		              }, [
		                h('img', {
		                  props: {},
		                  style: {
		                    width: '50px',
		                    height: '50px',
		                    cursor: 'pointer'
		                  },
		                  attrs: {
		                    src: imgurl,
		                  },
		                }),
		                h('div', {
		                  slot: 'content',
		                  style: {
		                    textAlign: 'center'
		                  },
		                }, [
		                  h('img', {
		                    attrs: {
		                      src: imgurl,
		                    },
		                    style: {
		                    	width: '100%',
		                    },
		                  })
		                ])
		              ])
		            }
		          },
		        {
		            title: '排序(数字越小越靠前显示)',
		            key: 'sort'
		        },
		        {
		        	title:'部件所需工序',
		        	render:(h,params)=>{
		                var arr = params.row.processNameList;//数据数组
		                var newArr = [];
		                arr.forEach((processName,index)=>{
		                    newArr.push(
								h('i-button',{
									props:{
										type:'info'
									},
									style: {
		                                marginRight: '10px',
		                                marginTop:'5px'
		                            },
								},
								processName)
		                      )
		                })
		                return h('div',newArr);
					}
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
										vm.delect(params.row.parts_id,params.index);
									}
								}
							},
							'删除'),
						]);
					}
				}
		    ],
		    partsPageList: [],
	    	showAdd:false,
	    	processList:[],
	    	ruleValidate: {
	        	parts_name: [
	                { required: true, message: '部件名称不能为空', trigger: 'blur' }
	            ],
	        },
	        addItem:{
	        	parts_id:'',
	        	parts_name: '',
	        	process_id:[],
	        	img:'',
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
	                url: "../jy/parts/list",
	                contentType: "application/json",
	                params:{
	                	page:this.page,
	                	limit:this.limit
	                },
	                successCallback: function (r) {
	                	vm.partsPageList = r.page.list;
	                    vm.partsListSize = r.page.totalRow
	                }
	            });
	      },
	      pageChange:function(index){
	    	    console.log(index);
				this.page = index;
				this.pageList();
		   },
		   delect:function(partsid,index){
	       	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/parts/delect",
	               contentType: "application/json",
	               params:  {
	               	id:partsid
	               },
	               successCallback: function () {
	               	vm.partsPageList.splice(index, 1);
	               }
	           });
	       },
	       add: function(){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '新增部件';
	    	   vm.addItem.parts_id = '';
	    	   vm.addItem.parts_name = '';
	    	   vm.addItem.sort = '1';
	    	   vm.addItem.img = '';
	    	   vm.addItem.process_id = [];
	    	   Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/process/allList",
	               contentType: "application/json",
	               successCallback: function (r) {
	            	   vm.processList = r.processList;
	               }
	           });
	       },
	       edit: function(params){
	    	   vm.showTable = false;
	    	   vm.showAdd = true;
	    	   vm.title = '修改部件';
	    	   vm.addItem.parts_id = params.parts_id;
	    	   vm.addItem.parts_name = params.parts_name;
	    	   vm.addItem.sort = params.sort;
	    	   vm.addItem.process_id = [];
	    	   Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/process/allList",
	               contentType: "application/json",
	               successCallback: function (r) {
	            	   vm.processList = r.processList;
	               }
	           });
	       },
	       publishSubmit: function (name) {
	    	   this.$refs[name].validate((valid) => {
	               if (valid) {
	                   var url = vm.addItem.parts_id == "" ? "../jy/parts/save" : "../jy/parts/update";
		       			Ajax.request({
		                      type: "POST",
		                      url: url,
		                      contentType: "application/json",
		                      params: JSON.stringify(vm.addItem),
		                      successCallback: function (r) {
		                    	  vm.pageList();
		                    	  vm.success(valid);
		                      }
		                  });
	               } else {
	            	   vm.error(valid);
	                   //this.$Message.error('失败');
	               }
	           })
	       },
	       update(file){
	    	   vm.addItem.img = file;
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