$(function () {
    $("#jqGrid").Grid({
        url: '../shop/denomination/list',
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '充值面额名称', name: 'name', index: 'name', width: 180},
            {label: '面额', name: 'denomination', index: 'denomination', width: 80}],
    });
});
var vm = new Vue({
    el: '#rrapp',
    data: {
    	showList: true,
        title: null,
        denomodel:{},
        ruleValidate: {
            question: [
                {required: true, message: '金额不能为空', trigger: 'blur'}
            ]
        },
        q: {
        	question: ''
        },
        
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
        	vm.showList = false;
            vm.title = "新增";
            vm.denomodel = {};
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
            var url = vm.denomodel.id == null ? "../shop/denomination/save" : "../shop/denomination/update";

            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.denomodel),
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
                    url: "../shop/denomination/delete",
                    contentType: "application/json",
                    params: JSON.stringify(ids),
                    successCallback: function (r) {
                        alert('操作成功', function (index) {
                            vm.reload();;
                        });
                    }
                });

            });
        },
        getInfo: function (id) {
            Ajax.request({
                url: "../shop/denomination/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.denomodel = r.denomination;
                    
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
        	console.log('1111');
            handleSubmitValidate(this, name, function () {
                vm.saveOrUpdate()
            });
        },
        handleReset: function (name) {
            handleResetForm(this, name);
        },
    }
});