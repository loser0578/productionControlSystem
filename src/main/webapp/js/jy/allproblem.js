$(function () {
var vm = new Vue({
    el: '#rrapp',
    data:{
    	showTable:true,
    	title:'',
    	problemList:[],
    	problemColumns:[
    		{
    			type: 'expand',
        		title:'查看详细数据',
        		width:115,
        		render: (h, params) => {
        			return h('div',[
						h('i-table',{
							props:{
								columns:vm.allProblemColumns,
								data:params.row.partsList
							},
						}),
					]);
        		}
    		},
	        {
	            title: '负责人',key: 'principal'
	        },
	        {
				title:'产品名称',key: 'product_name'
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
	                  placement: 'left',
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
		],
		allProblemColumns:[],
    },
    created:function(){
    	this.list();
    	this.getAllProblemColumns();
    },
    methods:{
    	list:function(){
    		this.showTable = true;
            Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/allproblem/problemList",
                contentType: "application/json",
                successCallback: function (r) {
                  vm.problemList = r.list;
                }
            });
    	},
    	getAllProblemColumns:function(){
    		Ajax.request({
                type: "GET",
                async: true,
                dataType: 'json',
                url: "../jy/allproblem/getAllProblemColumns",
                contentType: "application/json",
                successCallback: function (r) {
                  vm.allProblemColumns = r.list;
                   
                }
            });
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