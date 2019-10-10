$(function () {
    let goodsId = getQueryString("goodsId");
    let url = '../shop/product/list';
    if (goodsId) {
        url += '?goods_id=' + goodsId;
    }
    $("#jqGrid").Grid({
        url: url,
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '商品', name: 'goods_name', index: 'goods_id', width: 120},
            {
                label: '商品规格',
                name: 'specificationValue',
                index: 'goods_specification_ids',
                width: 100,
                formatter: function (value, options, row) {
                    return value.replace(row.goods_name + " ", '');
                }
            },
            {label: '商品序列号', name: 'goods_sn', index: 'goods_sn', width: 80},
            {label: '商品库存', name: 'goods_number', index: 'goods_number', width: 80},
            {label: '零售价格(元)', name: 'retail_price', index: 'retail_price', width: 80},
            {label: '市场价格(元)', name: 'market_price', index: 'market_price', width: 80}]
    });
});

let vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        product: {},
        ruleValidate: {
            name: [
                {required: true, message: '名称不能为空', trigger: 'blur'}
            ]
        },
        q: {
            goodsName: ''
        },
        goodss: [],
        attribute: [],
        color: [], guige: [], weight: [],

        chosespecifications: [],
        specifications:[],
        specificationclass:[],
        goodsspecification:[],
        type: ''
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.product = {goods_number:1,retail_price:1,market_price:1};
            vm.getGoodss();
            vm.getSpecification();
            vm.type = 'add';
        },
        update: function (event) {
            let id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.type = 'update';
            vm.getSpecification();
            vm.getInfo(id)
        },
        changeGoods: function (opt) {
        	let vm = this;
            let goodsId = opt.value;
            if(!goodsId)return;

            Ajax.request({
                url: "../shop/goods/info/" + goodsId,
                async: true,
                successCallback: function (r) {
                    if (vm.type == 'add') {
                        vm.product.goodsSn = r.goods.goodsSn;
                        vm.product.goodsNumber = r.goods.goodsNumber;
                        vm.product.retailPrice = r.goods.retailPrice;
                        vm.product.marketPrice = r.goods.marketPrice;
                    }                
                    for(i = 0; i < vm.specifications.length; i++){
                        Ajax.request({
                            url: "../shop/goodsspecification/queryAll?goods_id=" + goodsId + "&specification_id="+vm.specifications[i].id,
                            async: false,
                            successCallback: function (r) {
                            	if(r.list != null || r.list !='undefined '){
                            		
                            		Vue.set(vm.specificationclass,i,r.list) 
                            	}                      	                        
                            }
                        });   
                    } 
                    
                }
            });
        },
        saveOrUpdate: function (event) {
            let url = vm.product.id == null ? "../shop/product/save" : "../shop/product/update";
            console.log(vm.goodsspecification)
            vm.product.goodsSpecificationIds=JSON.stringify(vm.goodsspecification);

           
            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.product),
                successCallback: function (r) {
                    alert('操作成功', function (index) {
                        vm.reload();
                    });
                }
            });


        },
        del: function (event) {
            let ids = getSelectedRows("#jqGrid");
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                Ajax.request({
                    type: "POST",
                    url: "../shop/product/delete",
                    contentType: "application/json",
                    params: JSON.stringify(ids),
                    successCallback: function (r) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    }
                });


            });
        },
        getInfo: function (id) {
            vm.attribute = [];
            Ajax.request({
                url: "../shop/product/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.product = r.product;
                    //这个人选了 2_4 是 goodspecification 里面的 2 和 4 
                    let goodsSpecificationIds = vm.product.goods_specification_ids.split("_");
                        goodsSpecificationIds.forEach((goodsSpecificationId, index) => {
                        let specificationIds = goodsSpecificationId.split(",").filter(id => !!id).map(id => Number(id));

                        if(!specificationIds.isEmpty() ){
                            Ajax.request({
                                url: "../shop/goodsspecification/getSpecificationId?specification_id="+specificationIds,
                                async: false,
                                successCallback: function (r) {
                                	if(r.data != null || r.data !='undefined '){
                                		let key=r.data;
                                		vm.goodsspecification[key].value1=specificationIds
         
    	                                if (specificationIds.length > 0) {
                                           vm.attribute.push(key);
    	                                }
                                	}
                                	
                                	                        
                                }
                            });
                        }else{
                        	
                        }

                    });

                    vm.getGoodss();
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            let page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'goodsName': vm.q.goodsName},
                page: page
            }).trigger("reloadGrid");
            vm.handleReset('formValidate');
        },
        handleSubmit: function (name) {
            handleSubmitValidate(this, name, function () {
                vm.saveOrUpdate()
            });
        },
        handleReset: function (name) {
            handleResetForm(this, name);
        },
        getGoodss: function () {
            Ajax.request({
                url: "../shop/goods/queryAll/",
                async: true,
                successCallback: function (r) {
                    vm.goodss = r.list;
                }
            });
        },
        getSpecification: function () {
            Ajax.request({
                url: "../shop/specification/queryAll/",
                async: false,
                successCallback: function (r) {
                    vm.specifications = r.list;
                    vm.specifications.forEach(v=>{  
                      console.log("zhixingle");
                      var item = {value1: []};
                      vm.goodsspecification.push(item);
                	});
                  
                }
            });
        },
        checkAllGroupChange (data) {
        	
        	console.log(vm.product.goods_id);
        	console.log(data);
        	console.log(typeof data);
        	console.log("array最后一个"+data[data.length-1]);
        	data.forEach(v=>{  
        	    console.log(vm.specifications[v].id+vm.specifications[v].name);  
        	});

        }
        
    }
});