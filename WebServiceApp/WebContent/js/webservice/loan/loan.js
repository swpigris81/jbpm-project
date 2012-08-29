/**
 * 请款
 */
function cashAdvance(){
	/**
	 * 渠道信息数据解析器
	 */
	var cashReader = new Ext.data.JsonReader({
		totalProperty : "totalCount",
		root : "cashList"
	},[
	   {name:"id"},
	   {name:"cardId"},
	   {name:"cashAmount"},
	   {name:"cashDate"},
	   {name:"cashReason"},
	   {name:"cashRemark"},
	   {name:"cashUserId"},
	   {name:"cashUserName"},
	   {name:"cashCheckUserId"},
	   {name:"cashCheckUserName"},
	   {name:"cashCheckDate"},
	   {name:"cashCheckResult"},
	   {name:"cashApprovalUserId"},
	   {name:"cashApprovalUserName"},
	   {name:"cashApprovalDate"},
	   {name:"cashApprovalResult"},
	   {name:"processTaskId"},
	   {name:"cashStatus"}
	]);
	/**
	 * 渠道信息数据集
	 */
	var cashDataStore = new Ext.data.Store({
		proxy:new Ext.data.HttpProxy({
			url: path + "/loan/myRequestCashAdvance.action?method=myRequest"
		}),
		reader:cashReader
	});
	//数据展现样式
	var cashSM = new Ext.grid.CheckboxSelectionModel();
	//展示样式
	var cashCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),cashSM,{
		header:"卡号",
		dataIndex:"cardId",
		width:150
	},{
		header:"请款金额",
		dataIndex:"cashAmount",
		width:150,
	},{
		header:"请款日期",
		dataIndex:"cashDate",
		width:100
	},{
		header:"请款原因",
		dataIndex:"cashReason",
		width:100
	},{
		header:"请款人",
		dataIndex:"cashUserName",
		width:100
	},{
		header:"审核人",
		dataIndex:"cashCheckUserName",
		width:100
	},{
		header:"审批人",
		dataIndex:"cashApprovalUserName",
		width:100
	},{
		header:"当前状态",
		dataIndex:"cashStatus",
		width:100
	},{
		dataIndex:"id",
		hidden:true,
		hideable:false
	}]);
	
	/**
	 * 查询表单
	 */
	var searchPanel = new Ext.form.FormPanel({
		id:"searchPanel",
		border:true,
		frame:true,
		labelWidth:60,
		//autoHeight:true,
		height:80,
		layout:'column',
		region:'north',
		items:[{
			columnWidth:.30,
			layout:'form',
			items:[getTextField("cashAdvanceInfo.id", "请款编号", true, false)]
		},{
			columnWidth:.30,
			layout:'form',
			items:[getTextField("cashAdvanceInfo.cardId", "入账卡号", true, false)]
		},{
			columnWidth:.30,
			layout:'form',
			items:[getNumberField("cashAdvanceInfo.cashDate", "请款金额", true, false)]
		}],
		buttons:[{
			text:"查询",
			handler:function(){
				var params = searchPanel.getForm().getValues();
				params.start = 0;
				params.limit = 50;
				cashDataStore.baseParams = params;
				cashDataStore.load();
			}
		},{
			text:"重置",
			handler:function(){
				searchPanel.form.reset();
				var params = {};
				params.start = 0;
				params.limit = 50;
				cashDataStore.baseParams = params;
			}
		}]
	});
	
	var cashGrid = new Ext.grid.GridPanel({
		id:"cashGrid",
		title:"请款管理",
		region:'center',
		collapsible:false,//是否可以展开
		animCollapse:true,//展开时是否有动画效果
		autoScroll:true,
		
		//width:Ext.get("channel_main_div").getWidth(),
		//height:Ext.get("channel_main_div").getHeight(),
		loadMask:true,//载入遮罩动画（默认）
		frame:true,
		autoShow:true,		
		store:cashDataStore,
		cm:cashCM,
		sm:cashSM,
		//renderTo:"channel_main_div",
		viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
		split: true,
		stripeRows: true,
		bbar:new Ext.PagingToolbar({
			pageSize:50,//每页显示数
			store:cashDataStore,
			displayInfo:true,
			displayMsg:"显示{0}-{1}条记录，共{2}条记录",
			nextText:"下一页",
			prevText:"上一页",
			emptyMsg:"无相关记录"
		}),
		tbar:[]
	})
	/**
	 * 主面板
	 */
	var mainPanel = new Ext.Panel({
		layout:'border',
		height:Ext.get("loan_div").getHeight(),
		renderTo:"loan_div",
		items:[searchPanel, cashGrid]
	})
	
	/**
	 * 加载数据
	 */
	/*cashGrid.load({
		params:{start:0, limit:50}
	});*/
	/**
	 * 按钮存储器，尚未执行查询
	 */
	var buttonRightStore = buttonRight();
	/**
	 * 执行权限按钮加载, 并且加载列表数据, 显示权限按钮
	 * see buttonRight.js
	 * loadButtonRight(buttonStore, mainDataStore, dataGrid, pageDiv, params)
	 */
	loadButtonRight(buttonRightStore, cashDataStore, mainPanel, "loan_div");

}
/**
 * 程序主入口
 */
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	cashAdvance();
});