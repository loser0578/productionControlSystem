var vm = new Vue({
    el: '#rrapp',
    data: {
    	//刷新
    	flag:'',
    	//筛选
    	q:{
    		orderId:'',
    		batchId:'',
    		principal:'',
    		level:'',
    		status:'',
    	},
    	principalList:[],
    	levelList:[{id:1,name:'高级'},{id:2,name:'中级'},{id:3,name:'低级'}],
    	statusList:[{id:0,name:'未生产'},{id:1,name:'生产中'},{id:2,name:'已完工'}],
    	//
    	showTable: true,
    	showAdd: false,
        title: '',
        page:1,
        limit:10,
        productList:[],
        clientList:[],
        selectPList:[],
        addItem:{
        	client: '',
        	orderNumber: '',
        	batchNumber:'',
            level: '1',
            orderDate: '',
            deliveryDate: '',
            principal:'',
            products:[
        		{
        			product:'',
        			number:0,
        			remark:'',
        			status:1,
        			parts:[],
        			predict_start:'',
        			predict_end:'',
        		},
        	],
        },
        ruleValidate: {
        	orderDate: [
                { required: true, type: 'date', message: '下单日期不能为空', trigger: 'blur' }
            ],
            deliveryDate: [
                { required: true, type: 'date', message: '要求交货日期不能为空', trigger: 'blur' }
            ],
            client: [
                { required: true, message: '客户名不能为空', trigger: 'blur' }
            ],
            orderNumber: [
                { required: true, message: '订单号不能为空', trigger: 'blur' }
            ],
            principal: [
                { required: true, message: '生产负责人不能为空', trigger: 'blur' }
            ],
            product: [
                { required: true, message: '产品不能为空', trigger: 'blur' }
            ],
        },
        orderColums: [
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
                                marginRight: '10px',
                                marginTop: '3px'
                            },
							on:{
								click:() => {
									vm.watchProduct(params.row.id);
									vm.readPartsColumns();
								}
							}
						},
						'查看订单'),
						/*h('i-button',{
							props:{
								type:'warning'
							},
							style: {
                                marginRight: '10px',
                                marginTop: '3px'
                            },
							on:{
								click:() => {
									vm.edit(params.row);
								}
							}
						},
						'修改订单'),*/
					]);
				}
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
            	title: '生产优先级',
            	render: (h, params) => {
            		var levelString = '低级';
            		var type = '';
            		switch(params.row.level){
            			case 1:levelString = '高级';
            					type = "error";
            				break;
            			case 2:levelString = '中级';
            					type = "warning";
        					break;
            			default : levelString = '低级';
            					type = "";
            				break;
            		}
                    return h('div', [
                    	h('i-button',{
                    		props:{
								type:type
							},
							style: {
                                marginRight: '10px',
                            },
						},
						levelString),
                    ]);
                }
            },
            {
                title: '要求交货日期',
                key: 'delivery_time'
            },
            {
                title: '距离交货日期',
                key: 'disTime'
            },
            {
                title: '生产状态',
                render: (h, params) => {
            		var statusString = '未生产';
            		var type = '';
            		switch(params.row._status){
            			case 1:statusString = '生产中';
            					type = "info";
            				break;
            			case 2:statusString = '已完工';
            					type = "success";
        					break;
            			default : statusString = '未生产';
            					type = "warning";
            				break;
            		}
                    return h('div', [
                    	h('i-button',{
                    		props:{
								type:type
							},
							style: {
                                marginRight: '10px',
                            },
						},
						statusString),
                    ]);
                }
            },
            /*{
            	title: '生产进度',
                key: 'realPlan'
            },*/
            {
                title: '生产进度图',
                width: '200px',
                render: (h, params) => {
                    return h('div', [
                    	h('i-progress',{
							props:{
								strokeWidth:20,
								strokeColor:params.row.color,
								percent:params.row.progress
							},
							style: {
								
                            },
						}),
                    ]);
                }
            },
            {
            	title:'出入库',
            	key:'action',
				align: 'center',
            	render:(h,params)=>{
            		console.log(params.row.progress);
            		var type = '';
            		var disabled = true;
            		if(100 == params.row.progress){
            			type = 'success';
            			disabled = false;
            		}else{
            			disabled = true;
            		}
					return h('div',[
						h('i-button',{
							props:{
								type:type,
								disabled:disabled,
							},
							style: {
                                marginRight: '10px',
                                marginTop: '3px'
                            },
							on:{
								click:() => {
									vm.updateShipMent(params.row.id,params.index);
								}
							}
						},
						'库存'),
						h('i-button',{
							props:{
								type:type,
								disabled:disabled,
							},
							style: {
                                marginRight: '10px',
                                marginTop: '3px'
                            },
							on:{
								click:() => {
									vm.updateOutShop(params.row.id,params.index);
								}
							}
						},
						'出货'),
					]);
				}
            },
            {
            	title:'删除',
            	key:'action',
				align: 'center',
            	render:(h,params)=>{
					return h('div',[
						h('i-button',{
							props:{
								type:'error'
							},
							style: {
                                marginRight: '10px',
                                marginTop: '3px'
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
            },
        ],
        orderList: [],
        orderListSize:'',
        //产品表格
        showProduct:false,
        acProductList:[],
        acProductSize:'',
        productPage:1,
        productLimit:10,
        acProductColums: [
        	{
        		type: 'expand',
        		title:'查看产品部件',
        		width:115,
        		render: (h, params) => {
        			return h('div',[
						h('i-table',{
							props:{
								columns:vm.acPartsColums,
								data:params.row.acceptPartsList
							},
						}),
					]);
        		}
        	},
            {
                title: '产品名称',
                key: 'product_name'
            },
            {
	            title: '产品图片',
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
                title: '订单数量',
                key: 'product_number'
            },
            {
                title: '备注',
                key: 'remark'
            },
            {
            	title: '生产状态',
                render: (h, params) => {
            		var statusString = '未生产';
            		var type = '';
            		switch(params.row._status){
            			case 1:statusString = '生产中';
            					type = "info";
            				break;
            			case 2:statusString = '已完工';
            					type = "success";
        					break;
            			default : statusString = '未生产';
            					type = "warning";
            				break;
            		}
                    return h('div', [
                    	h('i-button',{
                    		props:{
								type:type
							},
							style: {
                                marginRight: '10px',
                            },
						},
						statusString),
                    ]);
                }
            },
            {
            	title: '生产进度',
            	width: '200px',
                render: (h, params) => {
                    return h('div', [
                    	h('i-progress',{
							props:{
								strokeWidth:20,
								strokeColor:params.row.color,
								percent:params.row.productPlan
							},
							style: {
								
                            },
						}),
                    ]);
                }
            },
         ], 
         //部件表格
         acPartsList:[],
         acPartsColums: [
             {
                 title: '部件名称',
                 key: 'parts_name',
                 width:100,
                 fixed:'left',
                 className:'demo-table-info-column',
             },
             {
                 title: '查看部件报工',
               	 key:'action',
   				 align: 'center',
                 render:(h,params)=>{
   					return h('div',[
   						h('i-button',{
   							props:{
   								type:'info',
   							},
   							style: {
                                   marginRight: '10px',
                                   marginTop: '3px'
                               },
   							on:{
   								click:() => {
   									vm.reportQ.partsId = params.row.id;
   									vm.getReportPage(params.row.id);
   									
   								}
   							}
   						},
   						'查看报工详情'),
   					]);
   				}
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
                 title: '要求生产数量',
                 key: 'parts_number',
                 className:'demo-table-info-column',
             },
             {
             	title: '生产进度',
             	width: '150px',
                render: (h, params) => {
                    return h('div', [
                    	h('i-progress',{
							props:{
								strokeWidth:20,
								strokeColor:params.row.color,
								percent:params.row.partsPlan
							},
							style: {
								
                            },
						}),
                    ]);
                }
             },
         ],
         //报工
         showReport:false,
         acProcessList:[],
         reportItem:{
        	 process_id:'',
        	 report_time:'',
        	 require_number:0,
        	 scrap_number:0,
        	 delect_number:0,
         },
        //报工表格
        showReportTable:false,
        reportPage:1,
        reportLimit:10,
        reportPageList:[],
        reportListSize:'',
        reportProcessList:[],
        reportQ:{
        	partsId:'',
        	process_id:'',
        },
        reportColumns:[
	        {
	            title: '所属产品名称',
	            key: 'product_name'
	        },
	        {
	            title: '所属部件名称',
	            key: 'parts_name'
	        },
	        {
	            title: '报工工序名称',
	            key: 'process_name'
	        },
	        {
	            title: '报工时间',
	            key: 'report_time'
	        },
	        {
	            title: '报工完成数量',
	            key: 'require_number'
	        },
	        {
	            title: '问题质量数量',
	            key: 'scrap_number'
	        },
	        {
	            title: '报废数量',
	            key: 'delect_number'
	        },
        ],
    },
    created:function(){
    	this.getOrderPageList();
    	//这是获取筛选用的
    	this.getPrincipalList();
    },
	methods:{
		getOrderPageList:function(){
			this.showTable = true;
     	    this.showAdd = false;
     	    this.showReport = false;
     	    this.showProduct = false;
     	   
     	   Ajax.request({
               type: "GET",
               async: true,
               dataType: 'json',
               url: "../jy/order/list",
               contentType: "application/json",
               params:{
               	page:this.page,
               	limit:this.limit
               },
               successCallback: function (r) {
	               	vm.orderList = r.page.list;
	                vm.orderListSize = r.page.totalRow
               }
           });
		},
		getPrincipalList:function(){
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/principalList",
	               contentType: "application/json",
	               successCallback: function (r) {
		               	vm.principalList = r.list;
	               }
	           });
		},
		add:function(){
			vm.showTable = false;
			vm.showAdd = true;	
			vm.title = '新增订单';
			vm.addItem.product_id = '';
			vm.addItem.product_name = '';
			vm.addItem.client = '';
			vm.addItem.orderNumber = '';
			vm.addItem.batchNumber = '';
			vm.addItem.orderDate = '';
			vm.addItem.deliveryDate = '';
			vm.addItem.principal = '';
			vm.addItem.products = [
				{
        			product:'',
        			number:0,
        			remark:'',
        			status:1,
        			parts:[],
        			predict_start:'',
        			predict_end:'',
        		},
			];
	    	vm.addItem.sort = '1';
            Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/product/productList",
                contentType: "application/json",
                successCallback: function (r) {
                   vm.productList = r.list;
                }
            });
            Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/product/clientList",
                contentType: "application/json",
                successCallback: function (r) {
                   vm.clientList = r.list;
                }
            });
            //这是事先准备的数据
            Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/product/principalList",
                contentType: "application/json",
                successCallback: function (r) {
                   vm.selectPList = r.list;
                }
            });
		},
		update:function(event){
			var url = vm.addItem.product_id == null ? "../jy/order/save" : "../jy/order/update";
			Ajax.request({
                type: "POST",
                url: url,
                async: true,
                contentType: "application/json",
                params: JSON.stringify(vm.addItem),
                successCallback: function (r) {
                    alert('操作成功', function (index) {
                        
                    });
                }
            });
		},
		delect:function(orderid,index){
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/delect",
	               contentType: "application/json",
	               params:  {
	               		id:orderid
	               },
	               successCallback: function () {
	               	vm.orderList.splice(index, 1);
	               }
	           });
		},
		updateOutShop:function(event,index){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/updateOutShop",
	               contentType: "application/json",
	               params:{
	               	  id:event
	               },
	               successCallback: function (r) {
	            	   vm.orderList.splice(index, 1);
	            	   vm.getOrderPageList();
	            	   vm.success();
	               }
	           });
		},
		updateShipMent:function(event,index){
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/updateShipMent",
	               contentType: "application/json",
	               params:{
	               	  id:event
	               },
	               successCallback: function (r) {
	            	   vm.orderList.splice(index, 1);
	            	   vm.getOrderPageList();
	            	   vm.success();
	               }
	           });
		},
		submit:function(name){
			this.$refs[name].validate((valid) => {
                if (valid) {
                	vm.success(valid);
                	Ajax.request({
                        type: "POST",
                        async: true,
                        dataType: 'json',
                        url: "../jy/order/save",
                        contentType: "application/json",
                        params: JSON.stringify(vm.addItem),
                        successCallback: function (r) {
                        	vm.getOrderPageList();
                           vm.success();
                        }
                    });
                } else {
                	vm.error(valid);
                }
            })
			
		},
		handleAdd:function(){
			var length = this.addItem.products.length;
			var productLength = this.addItem.products[length-1].product;
			var numberLength = this.addItem.products[length-1].number;
			if(productLength == ""){
				alert("请选择产品");
			}else if(numberLength == 0){
				alert("请输入产品数量");
			}else{
				var object={};
				object['product']='';
				object['number'] = 0;
				object['remark'] = '';
				object['status']=1;
				object['parts']=[];
		        this.addItem.products.push(object);	 
			}
		},
		handleRemove:function(index){
			this.addItem.products[index].status = 0;
			
		},
		pageChange:function(index){
			this.page = index;
			this.pageList();
	    },
		changes:function(value){
			if(""==value){
				
			}else{
				Ajax.request({
	                type: "GET",
	                async: true,
	                dataType: 'json',
	                url: "../jy/parts/partsListByProductId",
	                contentType: "application/json",
	                params: {
	                	id:value,
	                },
	                successCallback: function (r) {
	                	var length = vm.addItem.products.length;
	                	vm.addItem.products[length-1].parts = r.parts;
	                }
	            });
			}
		},
		//产品方法
		watchProduct:function(orderId){
			vm.showTable = false;
			vm.showProduct = true;
			vm.flag = orderId;
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/acProductList",
	               contentType: "application/json",
	               params:{
	               	page:this.productPage,
	               	limit:this.productLimit,
	               	orderId:orderId
	               },
	               successCallback: function (r) {
	            	   vm.acProductList = r.page.list;
		                vm.acProductSize = r.page.totalRow
	               }
	           });
		},
		acProductPageChange:function(index){
			this.productPage = index;
			this.watchProduct();
		},
		//读取部件表头方法
		readPartsColumns:function(){
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/readPartsColumns",
	               contentType: "application/json",
	               successCallback: function (r) {
	            	  for( i = 0;i<r.columnsList.length;i++){
	            		  vm.acPartsColums.push(r.columnsList[i]);
	            	  }
		            	var action =   {
		                  	title:'操作',
		                  	key:'action',
		                  	fixed:'right',
		                  	width:100,
		      				align: 'center',
		                  	render:(h,params)=>{
		      					return h('div',[
		      						h('i-button',{
		      							props:{
		      								type:'success',
		      								
		      							},
		      							style: {
		                                      marginRight: '10px',
		                                      marginTop: '3px'
		                                  },
		      							on:{
		      								'on-change':(value)=>{
			                            		vm.invoice(params.row.id,value);
			                            	},
		      								click:() => {
		      									
		      									vm.report(params.row.id);
		      								}
		      							}
		      						},
		      						'工序报工'),
		      					]);
		      				}
		                  }
		            	vm.acPartsColums.push(action);
	               }
	        });
		},
		//报工方法
		report:function(partsId){
			vm.showReport = true;
			Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/acProcessList",
	               contentType: "application/json",
	               params:{
	               	   partsId:partsId
	               },
	               successCallback: function (r) {
	            	   console.log(r);
	            	   vm.acProcessList = r.list
	               }
	        });
		},
		saveReport:function(){
			Ajax.request({
	               type: "POST",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/saveReport",
	               contentType: "application/json",
	               params:JSON.stringify(vm.reportItem),
	               successCallback: function (r) {
	            	   vm.watchProduct(vm.flag);
	               }
	        });
		},
		reportPageChange:function(index){
			this.reportPage = index;
			this.getReportPage();
	    },
	    getReportProcessList:function(){
	    	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/getReportProcess",
	               contentType: "application/json",
	               successCallback: function (r) {
	            	   vm.reportProcessList = r.list
	               }
	        });
	    },
	    queryReport:function(){
	    	console.log(vm.reportQ);
	    	vm.showTable = false;
     	    vm.showAdd = false;
     	    vm.showProduct = false;
     	    vm.showReport = false;
     	    vm.showReportTable = true;
	    	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/order/getReportQuery",
	               contentType: "application/json",
	               params:{
	            	   partsId:this.reportQ.partsId,
	               	   processId:this.reportQ.process_id,
		               	page:this.reportPage,
		               	limit:this.reportLimit,
	               },
	               successCallback: function (r) {
	            	   vm.reportPageList = r.page.list
	               }
	        });
	    },
		getReportPage:function(partsId){
			vm.showTable = false;
     	    vm.showAdd = false;
     	    vm.showProduct = false;
     	    vm.showReport = false;
     	    vm.showReportTable = true;
     	   Ajax.request({
               type: "GET",
               async: true,
               dataType: 'json',
               url: "../jy/order/getReportPage",
               contentType: "application/json",
               params:{
               	page:this.reportPage,
               	limit:this.reportLimit,
               	partsId:partsId,
               },
               successCallback: function (r) {
	               	vm.reportPageList = r.page.list;
	               	vm.reportListSize = r.page.totalRow
	               	vm.getReportProcessList();
               }
           });
		},
		//查询
		query:function(){
			this.showTable = true;
     	    this.showAdd = false;
     	   Ajax.request({
               type: "POST",
               async: true,
               dataType: 'json',
               url: "../jy/order/query",
               contentType: "application/json",
               params:JSON.stringify(vm.q),
               successCallback: function (r) {
            	   console.log(r);
	               	vm.orderList = r.list;
	                /*vm.orderListSize = r.page.totalRow*/
               }
           });
		},
		//重置查询
		restart:function(){
			vm.q = {
	    		orderId:'',
	    		batchId:'',
	    		principal:'',
	    		level:'',
	    		status:'',
	    	};
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
            this.$Message.info('已提交报工');
            vm.saveReport();
            //window.location.replace();
            /*vm.watchProduct();*/
        },
        cancel () {
            this.$Message.info('已取消报工');
        },
        //返回方法
        back:function(){
        	location.reload();
        }
	}
});