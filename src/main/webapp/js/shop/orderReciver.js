$(function () {
    let shippingStatus = getQueryString("shipping_status");
    let payStatus = getQueryString("pay_status");
    let orderStatus = getQueryString("order_status");
    let orderType = getQueryString("order_type");
    let url = '../shop/orderReciver/list';
    if (shippingStatus) {
        url += '?shipping_status=' + shippingStatus;
    }
    if (payStatus) {
        url += '?pay_status=' + payStatus;
    }
    if (orderStatus) {
        url += '?order_status=' + orderStatus;
    }
    if (orderType) {
        url += '?order_type=' + orderType;
    }
    $("#jqGrid").Grid({
        url: url,
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '订单号', name: 'order_sn', index: 'order_sn', width: 100},
            {label: '会员', name: 'nickname', index: 'nickname', width: 80},
            {
                label: '发货状态',
                name: 'shipping_status',
                index: 'shipping_status',
                width: 60,
                formatter: function (value) {
                    if (value == '0') {
                        return '未发货';
                    } else if (value == '1') {
                        return '已发货';
                    } else if (value == '2') {
                        return '已收货';
                    } else if (value == '4') {
                        return '退货';
                    }
                    return value;
                }
            },
            {label: '快递公司', name: 'shipping_name', index: 'shipping_name', width: 80},
            {label: '快递单号', name: 'shipping_no', index: 'shipping_no', width: 80},
            {label: '收货人', name: 'consignee', index: 'consignee', width: 80},
            {label: '价格', name: 'retail_price', index: 'retail_price', width: 80},
            {label: '电话', name: 'mobile', index: 'mobile', width: 60},
            {
                label: '地址', width: 160, align: 'center', sortable: false, formatter: function (value, col, row) {
                    return '' + row.province + '' + row.city + '' + row.district + '' + row.address + '';
                }
            },
            {
                label: '下单时间', name: 'add_time', index: 'add_time', width: 80,
                formatter: function (value) {
                    return transDate(value);
                }
            },
            {
                label: '操作', width: 160, align: 'center', sortable: false, formatter: function (value, col, row) {
                    return '<button class="btn btn-outline btn-info" onclick="vm.lookDetail(' + row.id + ')"><i class="fa fa-info-circle"></i>&nbsp;详情</button>' +
                        '<button class="btn btn-outline btn-primary"  onclick="vm.printDetail(' + row.id + ')"><i class="fa fa-print"></i>&nbsp;打印</button>'+
                    '<button class="btn btn-outline btn-primary"  onclick="vm.trackDetail(' + row.id + ');"><i class="fa fa-cab"></i>&nbsp;</button>';
                }
            }
        ]
    });
});

let vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        detail: false,
        title: null,
        order: {},
        shippings: [],
        q: {
        	order_sn: '',
        	order_status: '',
        	order_type: ''
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        sendGoods: function (event) {
            let id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "发货";
            Ajax.request({
                url: "../shop/orderReciver/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.order = r.order;
                }
            });
        },
        confirm: function (event) {
            let id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            confirm('确定收货？', function () {
                Ajax.request({
                    type: "POST",
                    url: "../shop/orderReciver/confirm",
                    contentType: "application/json",
                    params: id,
                    successCallback: function (r) {
                    	alert(r.state)
             
                        if (r.state == 'ok') {
                            alert('操作成功', function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            Ajax.request({
                type: "POST",
                url: "../shop/orderReciver/sendGoods",
                contentType: "application/json",
                params: JSON.stringify(vm.order),
                successCallback: function (r) {
                    vm.reload();
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            vm.detail = false;
            let page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {
                    'order_sn': vm.q.order_sn,
                    'order_status': vm.q.order_status,
                    'order_type': vm.q.order_type
                },
                page: page
            }).trigger("reloadGrid");
        },
        lookDetail: function (rowId) { //第三步：定义编辑操作
            vm.detail = true;
            vm.title = "详情";
            Ajax.request({
                url: "../shop/orderReciver/info/" + rowId,
                async: true,
                successCallback: function (r) {
                	
                    vm.order = r.order;
                    vm.order.address=vm.order.province+vm.order.city+vm.order.district+vm.order.address
                }
            });
        },
        printDetail: function (rowId) {
            openWindow({
                type: 2,
                title: '<i class="fa fa-print"></i>打印票据',
                content: '../shop/ordergoods.html?orderId=' + rowId
            })
        },
        trackDetail: function (rowId) {
            Ajax.request({
                url: "../shop/orderReciver/info/" + rowId,
                async: true,
                successCallback: function (r) {
                    vm.order = r.order;
                    if(r.order.shipping_status>0){
                        openWindow({
                            type: 2,
                            title: '<i class="fa fa-print"></i>物流查询',
                            content: '../shop/orderTrack.html?orderId=' + rowId
                        })
                    }else{
                    	 layer.alert("此订单尚未发货", {icon: 5});
                    }
                }
            });

        }
        
    },
    created: function () {
        let vue = this;
        Ajax.request({
            url: "../shop/shipping/queryAll",
            async: true,
            successCallback: function (r) {
                vue.shippings = r.list;
            }
        });
    }
});