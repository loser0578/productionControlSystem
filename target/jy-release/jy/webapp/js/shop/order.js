$(function () {
    let shippingStatus = getQueryString("shipping_status");
    let payStatus = getQueryString("pay_status");
    let orderStatus = getQueryString("order_status");
    let orderType = getQueryString("order_type");
    let url = '../shop/order/list';
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
            {label: '会员', name: 'nickname', index: 'user_name', width: 80},
//            {
//                label: '订单类型', name: 'order_type', index: 'order_type', width: 80, formatter: function (value) {
//                    if (value == '1') {
//                        return '普通订单';
//                    } else if (value == '2') {
//                        return '团购订单';
//                    } else if (value == '3') {
//                        return '砍价订单';
//                    } else if (value == '4') {
//                        return '立即购买';
//                    }
//                    return '-';
//                }
//            },
            {
                label: '订单状态', name: 'order_status', index: 'order_status', width: 80, formatter: function (value) {
                    if (value == '0') {
                        return '待付款';
                    } else if (value == '101') {
                        return '订单已取消';
                    } else if (value == '102') {
                        return '订单已删除';
                    } else if (value == '201') {
                        return '订单已付款';
                    } else if (value == '300') {
                        return '订单已发货';
                    } else if (value == '301') {
                        return '用户确认收货';
                    } else if (value == '401') {
                        return '退款';
                    } else if (value == '402') {
                        return '完成';
                    }
                    return value;
                }
            },
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
            {
                label: '付款状态', name: 'pay_status', index: 'pay_status', width: 80,
                formatter: function (value) {
                    if (value == '0') {
                        return '未付款';
                    } else if (value == '1') {
                        return '付款中';
                    } else if (value == '2') {
                        return '已付款';
                    }
                    return value;
                }
            },
            {label: '购买数量', name: 'number', index: 'number', width: 80},
            {label: '剩余数量', name: 'number_remain', index: 'number_remain', width: 80},
            {label: '实际支付金额', name: 'actual_price', index: 'actual_price', width: 80},
            {label: '订单总价', name: 'order_price', index: 'order_price', width: 60},
            {label: '商品总价', name: 'goods_price', index: 'goods_price', width: 60},
            {
                label: '下单时间', name: 'add_time', index: 'add_time', width: 80,
                formatter: function (value) {
                    return transDate(value);
                }
            },
            {
                label: '操作', width: 160, align: 'center', sortable: false, formatter: function (value, col, row) {
                    return '<button class="btn btn-outline btn-info" onclick="vm.lookDetail(' + row.id + ')"><i class="fa fa-info-circle"></i>&nbsp;详情</button>';
                    
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
                url: "../shop/order/info/" + id,
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
                    url: "../shop/order/confirm",
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
                url: "../shop/order/sendGoods",
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
                url: "../shop/order/info/" + rowId,
                async: true,
                successCallback: function (r) {
                    vm.order = r.order;
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
                url: "../shop/order/info/" + rowId,
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