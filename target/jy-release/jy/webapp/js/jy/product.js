$(function () {
var vm = new Vue({
    el: '#rrapp',
    data:{
    	showTable:true,
    	showAdd:false,
    	title:'',
    	page:1,
    	limit:10,
    	productListSize:'',
    	productColumns:[
	        {
	            title: '产品名称',key: 'product_name'
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
	                      src:  imgurl,
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
				title:'排序',key: 'sort'
		    },
		    {
		    	title:'产品所需部件',
	        	render:(h,params)=>{
	                var arr = params.row.partsNameList;//数据数组
	                var newArr = [];
	                arr.forEach((partsName,index)=>{
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
							partsName)
	                      )
	                })
	                return h('div',newArr);
				}
			},
			{
				title:'操作',key: 'action',align: 'center',
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
									vm.delect(params.row.product_id,params.index);
								}
							}
						},
						'删除'),
					]);
				}
			}	
		],
		productPageList:[],
    	showAdd:false,
    	partsList:[],
    	ruleValidate: {
        	product_name: [
                { required: true, message: '产品名称不能为空', trigger: 'blur' }
            ],
        },
    	addItem: {
    		product_id:'',
            product_name: '',
            img:'',
            parts_id:[],
            sort:1,
            partsList:[
            	{
            		parts_id:'',
            		parts_number:0,
            		status:1,
            	}
            ],
        }
    },
    created:function(){
    	this.pageList()
    },
    methods:{
    	pageList:function(){
    		this.showTable = true;
     	    this.showAdd = false;
            Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/product/list",
                contentType: "application/json",
                params:  {
                	page:this.page,
                	limit:this.limit
                },
                successCallback: function (r) {
                   vm.productPageList = r.page.list;
                   vm.productListSize = r.page.totalRow
                }
            });
    	},
    	pageChange:function(index){
        	console.log(index);
			this.page = index;
			this.pageList();
        },
        delect:function(productid,index){
        	Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/product/delect",
                contentType: "application/json",
                params:  {
                	id:productid
                },
                successCallback: function () {
                	vm.productPageList.splice(index, 1);
                }
            });
        },
        add:function(){
        	vm.showTable = false;
        	vm.showAdd = true;
        	vm.title = '新增产品';
        	vm.addItem.product_id = '';
     	  	vm.addItem.product_name = '';
     	  	vm.addItem.sort = '1';
     	  	vm.addItem.parts_id = [];
     	  	Ajax.request({
	               type: "GET",
	               async: true,
	               dataType: 'json',
	               url: "../jy/parts/allList",
	               contentType: "application/json",
	               successCallback: function (r) {
	            	   vm.partsList = r.partsList;
	               }
	           });
        },
        edit: function(params){
     	   vm.showTable = false;
     	   vm.showAdd = true;
     	   vm.title = '修改产品';
     	   console.log(params);
     	   vm.addItem.product_id = params.product_id;
     	   vm.addItem.product_name = params.product_name;
     	   vm.addItem.sort = params.sort;
     	   //vm.addItem.parts_id = [];
     	  vm.addItem.partsList = [

             	{
             		parts_id:'',
             		parts_number:0,
             		status:1,
             	}

          ];
	     	  Ajax.request({
	              type: "GET",
	              async: true,
	              dataType: 'json',
	              url: "../jy/parts/allList",
	              contentType: "application/json",
	              successCallback: function (r) {
	           	   vm.partsList = r.partsList;
	              }
	          });
        },
        publishSubmit: function (name) {
     	   this.$refs[name].validate((valid) => {
                if (valid) {
                    vm.success(valid);
                    var url = vm.addItem.product_id == "" ? "../jy/product/save" : "../jy/product/update";
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
                }
            })
        },
        handleAdd:function(){
        	//1先去判断this.addItem.products.length 大小 ，去this.addItem.products.product 有没有    ===》 1有的  2.提示一次jiayige
			var length = this.addItem.partsList.length;
			var materLength = this.addItem.partsList[length-1].parts_id;
			var numberLength = this.addItem.partsList[length-1].parts_number;
			if(materLength == ""){
				alert("请选择部件");
			}else if(numberLength == 0){
				alert("请输入部件数量");
			}else{
				var object={};
				object['parts_id']='';
				object['parts_number'] = 0;
				object['status']=1;
		        this.addItem.partsList.push(object);	 
			}
        },
        handleRemove:function(index){
			this.addItem.partsList[index].status = 0;	
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
        },
    }
});
});