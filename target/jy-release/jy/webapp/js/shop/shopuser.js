$(function () {
    $("#jqGrid").Grid({
        url: '../shop/shopuser/list',
        colModel: [{
            label: 'id', name: 'id', index: 'id', key: true, hidden: true
        }, {
            label: '会员名称', name: 'username', index: 'username', width: 80
        }, {
            label: '会员密码', name: 'password', index: 'password', hidden: true
        }, {
            label: '性别', name: 'gender', index: 'gender', width: 40, formatter: function (value) {
                return transGender(value);
            }
        }, {
            label: '出生日期', name: 'birthday', index: 'birthday', width: 80, formatter: function (value) {
                return transDate(value);
            }
        }, {
            label: '注册时间', name: 'register_time', index: 'register_time', width: 80, formatter: function (value) {
                return transDate(value);
            }
        }, {
            label: '最后登录时间', name: 'last_login_time', index: 'last_login_time', width: 80, formatter: function (value) {
                return transDate(value);
            }
        }, {
            label: '最后登录Ip', name: 'last_login_ip', index: 'last_login_ip', hidden: true
        }, {
            label: '微信名', name: 'nickname', index: 'nickname', width: 80
        }, {
            label: '手机号码', name: 'mobile', index: 'mobile', width: 120
        }, {
            label: '注册Ip', name: 'registerIp', index: 'register_ip', hidden: true
        }, {
            label: '头像', name: 'avatar', index: 'avatar', width: 80, formatter: function (value) {
                return transImg(value);
            }
        }, {
            label: '微信Id', name: 'weixinOpenid', index: 'weixin_openid', width: 80, hidden: true
        },{
            label: '操作', width: 70, sortable: false, formatter: function (value, col, row) {
                return '<button class="ivu-btn ivu-btn-primary ivu-btn-circle ivu-btn-small" onclick="vm.publish(' + row.id + ')"><i class="ivu-icon ivu-icon-android-send"></i>充值</button>';
            }
        }]
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
    	showGoods: false,
        showList: true,
        title: null,
        selectData: {},
        users: [],
        denominations:[],
        rid :[],
        user: {
            gender: 1
        },
        ruleValidate: {
            username: [
                {required: true, message: '会员名称不能为空', trigger: 'blur'}
            ]
        },
        q: {
            username: ''
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.user = {gender: '1'};
        },
        update: function (event) {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id);
           
        },
        saveOrUpdate: function (event) {
            var url = vm.user.id == null ? "../shop/shopuser/save" : "../shop/shopuser/update";

            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.user),
                successCallback: function (r) {
                    alert('操作成功', function (index) {
                        vm.reload();
                    });
                }
            });
        },
        del: function (event) {
            var ids = getSelectedRows("#jqGrid");
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                Ajax.request({
                    type: "POST",
                    url: "../shop/shopuser/delete",
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
        getUsers: function () {
            Ajax.request({
                url: "../shop/shopuser/queryAll",
                async: true,
                successCallback: function (r) {
                    vm.users = r.list;
                }
            });
        },

        publish: function (id) {
        	//console.log('+'+id);
            vm.showGoods = true;
            vm.goods = [];
            vm.user = [];
            vm.denominations = [];
            //vm.getInfo();
            vm.getDenomination();
            vm.selectData = id;
            vm.sendSms = false;
            openWindow({
                title: "充值",
                area: ['600px', '350px'],
                content: jQuery("#sendDiv")
            })
        },
        getDenomination:function(){
        	Ajax.request({
                url: "../shop/shopuser/getAmountid",
                async: true,
                successCallback: function (r) {
                	console.log(r)
                    vm.denominations = r;
                }
            });
        },
        publishSubmit: function () {
            confirm('确定充值？', function () {
                Ajax.request({
                    type: "POST",
                    dataType: 'json',
                    url: "../shop/shopuser/reCharge",
                    contentType: "application/json",
                   
                    params:  JSON.stringify({
                    	
                    	uid:vm.selectData,
                    	rid:vm.rid
                    	}),
                    successCallback: function (r) {
                        alert('操作成功', function (index) {
                            vm.reload();
                            vm.showGoods = false;
                            vm.showList = true;
                            layer.closeAll();
                        });
                    }
                });
            });
        },
        exportUser: function () {
            exportFile('#rrapp', '../user/export', {'username': vm.q.username});
        },
        coupon: function () {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            openWindow({
                title: '优惠券',
                type: 2,
                content: '../shop/usercoupon.html?user_id=' + id
            })
        },
        address: function () {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            openWindow({
                title: '收获地址',
                type: 2,
                content: '../shop/address.html?user_id=' + id
            })
        },
        shopCart: function () {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            openWindow({
                title: '购物车',
                type: 2,
                content: '../shop/cart.html?user_id=' + id
            })
        },
        getInfo: function (id) {
            Ajax.request({
                url: "../shop/shopuser/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.user = r.user;
                }
            });
        },
 
        reload: function (event) {
        	 vm.showList = true;
             vm.showCard = false;
             vm.showGoods = false;
             layer.closeAll();
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'username': vm.q.username},
                page: page,
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
        recharge: function(event){
        	var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "充值";

            vm.getInfo(id)
        }
    }
});