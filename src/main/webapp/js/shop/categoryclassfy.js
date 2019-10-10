$(function () {
    $("#jqGrid").Grid({
        url: '../shop/categoryclassify/list',
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '分类名称', name: 'name', index: 'name', width: 180},
            {label: '排序', name: 'sort_order', index: 'sort_order', width: 80},
            {label: '所属栏目', name: 'category_name', index: 'sort_order', width: 80}]
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        categoryclassfy: { 

        },
        ruleValidate: {
            name: [
                {required: true, message: '规格名称不能为空', trigger: 'blur'}
            ]
        },
        categoryList: [],
        q: {
            name: '',
            sort_order:''
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.categoryclassfy = {name: '',sort_order:1};
            this.getParentCategory();
        },
        update: function (event) {
            var id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            this.getParentCategory(); 
            vm.getInfo(id)
        },
        getParentCategory: function () {
            Ajax.request({
                url: "../shop/category/getCategorySelectL1",
                async: true,
                successCallback: function (r) {
                    vm.categoryList = r.list;
                }
            });
        },
        saveOrUpdate: function (event) {
            var url = vm.categoryclassfy.id == null ? "../shop/categoryclassify/save" : "../shop/categoryclassify/update";
            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.categoryclassfy),
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
                    url: "../shop/categoryclassify/delete",
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
                url: "../shop/categoryclassify/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.categoryclassfy = r.categoryclassify;
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'name': vm.q.name},
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