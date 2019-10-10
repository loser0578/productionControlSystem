$(function () {
    $("#jqGrid").Grid({
        url: '../shop/blessing/list',
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '祝福语', name: 'name', index: 'name', width: 180},
            {label: '排序', name: 'sort_order', index: 'sort_order', width: 80}]
    });
});
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        Blessing: {},
        ruleValidate: {
            question: [
                {required: true, message: '问题不能为空', trigger: 'blur'}
            ]
        },
        q: {
            question: ''
        }
    },
    methods: {
        query: function () {
        	
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.Blessing = {};
        },
        update: function (event) {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            var url = vm.Blessing.id == null ? "../shop/blessing/save" : "../shop/blessing/update";

            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.Blessing),
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
                    url: "../shop/blessing/delete",
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
            Ajax.request({
                url: "../shop/blessing/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.Blessing = r.blessing;
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'question': vm.q.question, 'goodsName': vm.q.goodsName},
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
        }
    }
});