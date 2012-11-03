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
	 * 流程定义
	 */
	var processReader = new Ext.data.JsonReader({
		totalProperty : "totalCount",
		root : "processList"
	},[
	   {name:"id"},
	   {name:"deploymentId"},
	   {name:"key"},
	   {name:"name"},
	   {name:"version"},
	   {name:"description"}
	]);
	/**
	 * 流程数据存储
	 */
	var processStore = new Ext.data.Store({
		proxy:new Ext.data.HttpProxy({
			url: path + "/loan/processListCashAdvance.action?method=processList"
		}),
		reader:processReader,
		baseParams:{start:0, limit:50}
	});
	
	/**
	 * 渠道信息数据集
	 */
	var cashDataStore = new Ext.data.Store({
		proxy:new Ext.data.HttpProxy({
			url: path + "/loan/myRequestCashAdvance.action?method=myRequest"
		}),
		reader:cashReader,
		//倒叙排序
		sortInfo:{field: 'cashDate', direction: 'DESC'},
		listeners:{
			"loadexception":function(loader, node, response){
				try{
					if(response.status == "200"){
						try{
							var re = Ext.decode(response.responseText);
							if(re){
								Ext.Msg.alert('错误提示',re.msg, function(btn){
								});
							}
						}catch(e){
							Ext.Msg.alert('错误提示',"系统错误！错误代码："+e, function(btn){
							});
						}
					}else{
						httpStatusCodeHandler(response.status);
					}
				}catch(e){
					Ext.Msg.alert('错误提示',"系统错误！错误代码："+e, function(btn){
					});
				}
			}
		}
	});
	
	/**
	 * 待办任务数据集
	 */
	var cashTaskStore = new Ext.data.Store({
		proxy:new Ext.data.HttpProxy({
			url: path + "/loan/todoTaskCashAdvance.action?method=todoTask"
		}),
		reader:cashReader,
		//倒叙排序
		sortInfo:{field: 'cashDate', direction: 'DESC'},
		listeners:{
			"loadexception":function(loader, node, response){
				try{
					if(response.status == "200"){
						try{
							var re = Ext.decode(response.responseText);
							if(re){
								Ext.Msg.alert('错误提示',re.msg, function(btn){
								});
							}
						}catch(e){
							Ext.Msg.alert('错误提示',"系统错误！错误代码："+e, function(btn){
							});
						}
					}else{
						httpStatusCodeHandler(response.status);
					}
				}catch(e){
					Ext.Msg.alert('错误提示',"系统错误！错误代码："+e, function(btn){
					});
				}
			}
		}
	});
	
	/**
	 * 数据展现样式
	 */
	function getCashSM(){
		var cashSM = new Ext.grid.CheckboxSelectionModel();
		return cashSM
	}
	/**
	 * 流程展现
	 */
	var processCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), getCashSM(), {
		header:"流程编号",
		dataIndex:"id",
		width:70
	},{
		header:"流程部署编号",
		dataIndex:"deploymentId",
		width:90
	},{
		header:"流程关键字",
		dataIndex:"key",
		width:70
	},{
		header:"流程名称",
		dataIndex:"name",
		width:70
	},{
		header:"流程版本",
		dataIndex:"version",
		width:70
	},{
		header:"流程描述",
		dataIndex:"description",
		width:70
	}]);
	
	/**
	 * 请款展示样式
	 */
	var cashCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),getCashSM(),{
		dataIndex:"id",
		hidden:true,
		hideable:false
	},{
		header:"任务编号",
		dataIndex:"processTaskId",
		hidden:true
	},{
		header:"卡号",
		dataIndex:"cardId",
		hidden:true,
		hideable:false,
		width:100
	},{
		header:"请款金额",
		dataIndex:"cashAmount",
		width:70,
	},{
		header:"请款日期",
		dataIndex:"cashDate",
		//renderer:new Ext.util.Format.dateRenderer("Y-m-d"),
		renderer:function(val){
			if(val){
				if(val.indexOf("-") > -1){
					val = val.replace(/-/g, "/");
				}
				var dt = new Date(val);
				return dt.format('Y-m-d');
			}
		},
		sortable:true,
		width:80
	},{
		header:"请款原因",
		dataIndex:"cashReason",
		width:100
	},{
		header:"请款人",
		dataIndex:"cashUserName",
		width:70
	},{
		header:"审核人",
		dataIndex:"cashCheckUserName",
		width:70
	},{
		header:"审批人",
		dataIndex:"cashApprovalUserName",
		width:70
	},{
		header:"请款审批状态",
		dataIndex:"cashStatus",
		renderer:cashStatusRender,
		sortable:true,
		width:70
	}]);
	//查询参数
	var params = {};
	/**
	 * 我的请款查询表单
	 */
	var searchPanel = new Ext.form.FormPanel({
		//id:"searchPanel",
		buttonAlign:"right",
		labelAlign:"right",
		border:true,
		frame:true,
		labelWidth:80,
		//autoHeight:true,
		height:80,
		layout:'column',
		region:'north',
		items:[{
			columnWidth:.20,
			layout:'form',
			items:[getDateField("cashAdvanceInfo.cashDate", "请款日期", true, false)]
		},{
			columnWidth:.20,
			layout:'form',
			items:[getComboBoxField("cashAdvanceInfo.cashStatus", statusStore ,"请款审批状态", "dataKey", "dataValue", true, false)]
		}],
		buttons:[{
			text:"查询",
			handler:function(){
				var baseParams = searchPanel.getForm().getValues();
				baseParams.start = 0;
				baseParams.limit = 50;
				baseParams["cashAdvanceInfo.cashUserName"] = userName;
				baseParams["cashAdvanceInfo.cashUserId"] = userId;
				cashDataStore.baseParams = baseParams;
				loadCashDataStore();
			}
		},{
			text:"重置",
			handler:function(){
				searchPanel.form.reset();
				var baseParams = {};
				baseParams.start = 0;
				baseParams.limit = 50;
				baseParams["cashAdvanceInfo.cashUserName"] = userName;
				baseParams["cashAdvanceInfo.cashUserId"] = userId;
				cashDataStore.baseParams = baseParams;
			}
		}]
	});
	/**
	 * 待办任务查询面板
	 */
	var searchTodoPanel = new Ext.form.FormPanel({
		//id:"searchPanel",
		buttonAlign:"right",
		labelAlign:"right",
		border:true,
		frame:true,
		labelWidth:80,
		autoHeight:true,
		//height:80,
		layout:'column',
		region:'north',
		items:[{
			columnWidth:.20,
			layout:'form',
			items:[getDateField("cashAdvanceInfo.cashDate", "请款日期", true, false)]
		},{
			columnWidth:.20,
			layout:'form',
			items:[getComboBoxField("cashAdvanceInfo.cashStatus", statusStore ,"请款审批状态", "dataKey", "dataValue", true, false)]
		}],
		buttons:[{
			text:"查询",
			handler:function(){
				var baseParams = searchTodoPanel.getForm().getValues();
				baseParams.start = 0;
				baseParams.limit = 50;
				baseParams["cashAdvanceInfo.cashUserName"] = userName;
				baseParams["cashAdvanceInfo.cashUserId"] = userId;
				cashTaskStore.baseParams = params;
				loadCashDataStore();
			}
		},{
			text:"重置",
			handler:function(){
				searchTodoPanel.form.reset();
				var baseParams = {};
				baseParams.start = 0;
				baseParams.limit = 50;
				baseParams["cashAdvanceInfo.cashUserName"] = userName;
				baseParams["cashAdvanceInfo.cashUserId"] = userId;
				cashTaskStore.baseParams = baseParams;
			}
		}]
	});
	
	/**
	 * 我的请款列表
	 */
	var cashGrid = new Ext.grid.GridPanel({
		//id:"cashGrid",
		title:"请款管理",
		region:'center',
		collapsible:false,//是否可以展开
		animCollapse:true,//展开时是否有动画效果
		autoScroll:true,
		//width:Ext.get("channel_main_div").getWidth(),
		//height:Ext.get("channel_main_div").getHeight(),
		loadMask:true,//载入遮罩动画（默认）
		view: new Ext.grid.GridView({ forceFit:true }),
		plugins: [new Ext.ux.ColumnWidthCalculator()],
		frame:true,
		autoShow:true,
		store:cashDataStore,
		cm:cashCM,
		sm:getCashSM(),
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
		})
	});
	
	/**
	 * 待办任务列表
	 */
	var cashTodoGrid = new Ext.grid.GridPanel({
		//id:"cashGrid",
		title:"请款管理",
		region:'center',
		collapsible:false,//是否可以展开
		animCollapse:true,//展开时是否有动画效果
		autoScroll:true,
		//width:Ext.get("channel_main_div").getWidth(),
		//height:Ext.get("channel_main_div").getHeight(),
		loadMask:true,//载入遮罩动画（默认）
		view: new Ext.grid.GridView({ forceFit:true }),
		plugins: [new Ext.ux.ColumnWidthCalculator()],
		frame:true,
		autoShow:true,
		store:cashTaskStore,
		cm:cashCM,
		sm:getCashSM(),
		//renderTo:"channel_main_div",
		viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
		split: true,
		stripeRows: true,
		bbar:new Ext.PagingToolbar({
			pageSize:50,//每页显示数
			store:cashTaskStore,
			displayInfo:true,
			displayMsg:"显示{0}-{1}条记录，共{2}条记录",
			nextText:"下一页",
			prevText:"上一页",
			emptyMsg:"无相关记录"
		})
	});
	/**
	 * 主显示面板
	 */
	var mainTabPanel = new Ext.TabPanel({
		height:Ext.get("loan_div").getHeight(),
		width:Ext.get("loan_div").getWidth(),
		renderTo:"loan_div",
		activeTab:0,
		border:false,
		deferredRender:false,
		layoutOnTabChange:true,
		items:[{
			title:"我的请款信息",
			//layout:'border',
			html:"<div id='myRequest' style='width:100%; height:100%'></div>"
		},{
			title:"我的待办任务",
			//layout:'border',
			html:"<div id='todoRequest' style='width:100%; height:100%'></div>"
		}],
		listeners :{
			"tabchange":function(thiz, tab){
				if(Ext.get("todoRequest").getHeight() > 0){
//					var gtbar = todoPanel.getTopToolbar();
//					var tbar = new Ext.Toolbar();;
//					if(gtbar){
//						todoPanel.tbar.update("");
//						tbar.render(todoPanel.tbar);
//					}
//					
//					tbar.add({
//						text:"ads"
//					});
					todoPanel.setHeight(Ext.get("todoRequest").getHeight());
					todoPanel.render();
				}
			}
		}
	});
	
	/**
	 * 请款面板
	 */
	var reqPanel = new Ext.Panel({
		layout:'border',
		border:false,
		height:Ext.get("myRequest").getHeight()-18,
		renderTo:"myRequest",
		items:[searchPanel, cashGrid],
		tbar:[]
	});
	
	/**
	 * 待办面板
	 */
	var todoPanel = new Ext.Panel({
		layout:'border',
		border:false,
		height:Ext.get("todoRequest").getHeight(),
		renderTo:"todoRequest",
		items:[searchTodoPanel, cashTodoGrid],
		tbar:[{
			text:"通过",
			iconCls:"table_gear",
			tooltip:"通过请款申请",
			handler:function(){
				doWithRequest("1");
			}
		},"-",{
			text:"驳回",
			iconCls:"table_gear",
			tooltip:"驳回请款申请",
			handler:function(){
				doWithRequest("0");
			}
		},"-",{
			text:"再发起",
			iconCls:"table_gear",
			tooltip:"再发起已被驳回的请款申请",
			handler:function(){
				doWithRequest("99");
			}
		}]
	});
	
	
	
	/**
	 * 加载数据
	 */
	function loadCashDataStore(){
		cashDataStore.load({
			params:params
		});
		loadTodoTask();
	}
	function loadTodoTask(){
		cashTaskStore.load({
			params:params
		});
	}
	
	/**
	 * 加载数据参数
	 */
	function loadStoreParams(){
		params.start = 0;
		params.limit = 50;
		params["cashAdvanceInfo.cashUserName"] = userName;
		params["cashAdvanceInfo.cashUserId"] = userId;
		cashDataStore.baseParams = params;
		cashTaskStore.baseParams = params;
	}
	loadStoreParams();
	/**
	 * 按钮存储器，尚未执行查询
	 */
	var buttonRightStore = buttonRight();
	/**
	 * 执行权限按钮加载, 并且加载列表数据, 显示权限按钮
	 * see buttonRight.js
	 * loadButtonRight(buttonStore, mainDataStore, dataGrid, pageDiv, params)
	 */
	loadButtonRight(buttonRightStore, cashDataStore, reqPanel, "myRequest", null, null, function(){
		//加载待办任务
		loadTodoTask();
	});
	/**
	 * 加载请款下拉框
	 */
	statusStore.load({params:{codeId:"297e27f139755679013975a50522007d",codeName:"请款状态"}});
	/**
	 * 解析列表上的当前状态
	 */
	function cashStatusRender(value){
		if(value && value != ""){
			return getCodeNameFromStore(value, statusStore, "dataKey", "dataValue");
		}
		return value;
	}
	/**
	 * 新增请款
	 */
	this.addLoanRequest = function(url){
		var reqForm = getLoanRequestForm(url, false, false);
		var buttons = [{
			text:"暂时保存",
			handler:function(){
				if(reqForm.form.isValid()){
					//00-申请请款
					reqForm.form.findField("cashAdvanceInfo.cashStatus").setValue("00");
					saveLoadRequest("addLoanRequestWindow", reqForm);
				}
			}
		},{
			text:"直接提交",
			handler:function(){
				if(reqForm.form.isValid()){
					//01-发起审核
					reqForm.form.findField("cashAdvanceInfo.cashStatus").setValue("01");
					saveLoadRequest("addLoanRequestWindow", reqForm);
				}
			}
		},{
			text:"取消请款",
			handler:function(){
				var w = Ext.getCmp("addLoanRequestWindow");
				if(w){
					w.close();
				}
			}
		}];
		showAllWindow("addLoanRequestWindow", "新增请款", 500, 300, reqForm, null, buttons);
	};
	/**
	 * 修改请款
	 */
	this.editLoanRequest = function(url, grid, callback){
		var selectGrid;
		if(grid){
			selectGrid = grid;
		}else{
			selectGrid = cashGrid;
		}
		var gridSelectionModel = selectGrid.getSelectionModel();
		var gridSelection = gridSelectionModel.getSelections();
		if(gridSelection.length != 1){
			Ext.MessageBox.alert('提示','请选择一条请款信息！');
		    return false;
		}
		if(gridSelection[0].get("cashStatus") != "00" && gridSelection[0].get("cashStatus") != "03" && gridSelection[0].get("cashStatus") != "06"){
			Ext.MessageBox.alert('提示','您只能修改处于【申请请款】,【审核驳回】,【审批驳回】的请款信息！');
		    return false;
		}
		if(gridSelection[0].get("cashCheckDate") && !callback){
			Ext.MessageBox.alert('提示','当前请款信息不能被修改，只能通过【我的待办任务】进行再发起！');
		    return false;
		}
		var reqForm = getLoanRequestForm(url, false, false);
		var buttons = [{
			text:"暂时保存",
			handler:function(){
				if(reqForm.form.isValid()){
					//00-申请请款
					reqForm.form.findField("cashAdvanceInfo.cashStatus").setValue("00");
					saveLoadRequest("editLoanRequestWindow", reqForm);
				}
			}
		},{
			text:"直接提交",
			handler:function(){
				if(reqForm.form.isValid()){
					//01-发起审核
					reqForm.form.findField("cashAdvanceInfo.cashStatus").setValue("01");
					if(callback){
						reqForm.form.findField("cashAdvanceInfo.cashStatus").setValue("00");
					}
					saveLoadRequest("editLoanRequestWindow", reqForm, callback);
				}
			}
		},{
			text:"取消请款",
			handler:function(){
				var w = Ext.getCmp("editLoanRequestWindow");
				if(w){
					w.close();
				}
			}
		}];
		showAllWindow("editLoanRequestWindow", "修改请款", 500, 300, reqForm, null, buttons);
		setFormValues(reqForm, gridSelection);
	};
	/**
	 * 修改表单值
	 * @param reqForm
	 * @param gridSelection
	 */
	function setFormValues(reqForm, gridSelection){
		reqForm.form.findField("cashAdvanceInfo.cardId").setValue(gridSelection[0].get("cardId"));
		reqForm.form.findField("cashAdvanceInfo.id").setValue(gridSelection[0].get("id"));
		reqForm.form.findField("cashAdvanceInfo.cashAmount").setValue(gridSelection[0].get("cashAmount"));
		reqForm.form.findField("cashAdvanceInfo.cashUserName").setValue(gridSelection[0].get("cashUserName"));
		var cashDate = gridSelection[0].get("cashDate");
		if(cashDate){
			cashDate = cashDate.replace(/-/g, "/");
			cashDate = new Date(cashDate);
			reqForm.form.findField("cashAdvanceInfo.cashDate").setValue(cashDate.format('Y-m-d'));
		}
		//reqForm.form.findField("cashAdvanceInfo.cashDate").setValue(gridSelection[0].get("cashDate"));
		reqForm.form.findField("cashAdvanceInfo.cashUserId").setValue(gridSelection[0].get("cashUserId"));
		reqForm.form.findField("cashAdvanceInfo.cashReason").setValue(gridSelection[0].get("cashReason"));
		reqForm.form.findField("cashAdvanceInfo.cashRemark").setValue(gridSelection[0].get("cashRemark"));
		reqForm.form.findField("cashAdvanceInfo.cashCheckUserId").setValue(gridSelection[0].get("cashCheckUserId"));
		reqForm.form.findField("cashAdvanceInfo.cashCheckUserName").setValue(gridSelection[0].get("cashCheckUserName"));
		reqForm.form.findField("cashAdvanceInfo.cashCheckDate").setValue(gridSelection[0].get("cashCheckDate"));
		reqForm.form.findField("cashAdvanceInfo.cashCheckResult").setValue(gridSelection[0].get("cashCheckResult"));
		reqForm.form.findField("cashAdvanceInfo.cashApprovalUserId").setValue(gridSelection[0].get("cashApprovalUserId"));
		reqForm.form.findField("cashAdvanceInfo.cashApprovalUserName").setValue(gridSelection[0].get("cashApprovalUserName"));
		reqForm.form.findField("cashAdvanceInfo.cashApprovalDate").setValue(gridSelection[0].get("cashApprovalDate"));
		reqForm.form.findField("cashAdvanceInfo.cashApprovalResult").setValue(gridSelection[0].get("cashApprovalResult"));
		reqForm.form.findField("cashAdvanceInfo.processTaskId").setValue(gridSelection[0].get("processTaskId"));
	}
	
	/**
	 * 删除请款
	 */
	this.delLoanRequest = function(url){
		var gridSelectionModel = cashGrid.getSelectionModel();
		var gridSelection = gridSelectionModel.getSelections();
		if(gridSelection.length < 1){
			Ext.MessageBox.alert('提示','请至少选择一条请款信息！');
		    return false;
		}
		var idArray = new Array();
		for(var i=0; i<gridSelection.length; i++){
			if(gridSelection[i].get("cashStatus") != "00"){
				Ext.MessageBox.alert('提示','您只能删除处于【申请请款】的请款信息！');
			    return false;
			}
			idArray.push(gridSelection[i].get("id"));
		}
		var idStr = idArray.join(",");
		Ext.Msg.confirm("系统提示", "确定要删除您的请款信息？删除之后无法恢复！", function(btn){
			if(btn == 'yes' || btn == "ok"){
				Ext.Ajax.request({
					params:{loanIds:idStr},
					timeout:60000,
					url: url,
					success:function(response,options){
						Ext.MessageBox.hide();
						try{
							var msg = Ext.util.JSON.decode(response.responseText);
							if(msg && msg.msg){
								Ext.Msg.alert("提示信息",msg.msg);
								loadCashDataStore();
							}else{
								Ext.Msg.alert("提示信息","已成功删除您的请款信息！");
								loadCashDataStore();
							}
						}catch(e){
							Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
						}
					},failure:function(response,options){
						Ext.Msg.hide();
						try{
							var msg = Ext.util.JSON.decode(response.responseText);
							if(msg && msg.msg){
								Ext.Msg.alert("提示信息",msg.msg);
							}else{
								Ext.Msg.alert("提示信息","删除您的请款信息已失败！");
							}
						}catch(e){
							Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
						}
						return;
					}
				});
			}
		});
	};
	/**
	 * 部署请款流程
	 */
	this.deployLoan = function(url){
		var processSM = getCashSM();
		var processGrid = getProcessGrid(processStore, processCM, processSM, url);
		showAllWindow("deployLoanWindow", "部署请款流程", 500, 300, processGrid, null, null);
		Ext.getCmp("unDeployProcess").hide();
		processStore.load();
	};
	/**
	 * 卸载请款流程
	 */
	this.unDeployLoan = function(url){
		var processSM = getCashSM();
		var processGrid = getProcessGrid(processStore, processCM, processSM, url);
		showAllWindow("unDeployLoanWindow", "卸载请款流程", 500, 300, processGrid, null, null);
		Ext.getCmp("deployNewProcess").hide();
		processStore.load();
	};
	/**
	 * 流程表格
	 * @param dataStore
	 * @param processCM
	 * @param processSM
	 * @returns {Ext.grid.GridPanel}
	 */
	function getProcessGrid(dataStore, processCM, processSM, url){
		var processGrid = new Ext.grid.GridPanel({
			collapsible:false,//是否可以展开
			animCollapse:true,//展开时是否有动画效果
			autoScroll:true,
			loadMask:true,//载入遮罩动画（默认）
			view: new Ext.grid.GridView({ forceFit:true }),
			//plugins: [new Ext.ux.ColumnWidthCalculator()],
			frame:true,
			autoShow:true,
			store:dataStore,
			cm:processCM,
			sm:processSM,
			viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
			split: true,
			stripeRows: true,
			bbar:new Ext.PagingToolbar({
				pageSize:50,//每页显示数
				store:dataStore,
				displayInfo:true,
				displayMsg:"显示{0}-{1}条记录，共{2}条记录",
				nextText:"下一页",
				prevText:"上一页",
				emptyMsg:"无相关记录"
			}),
			tbar:[{
				text:"部署新流程",
				id:"deployNewProcess",
				handler:function(){
					var form = getUploadProcessForm(url);
					var button = [{
						text:"确定部署",
						handler:function(){
							if(form.form.isValid()){
								doDeployProcess(form, "deployNewProcessWindow");
							}
						}
					},{
						text:"取消部署",
						handler:function(){
							var w = Ext.getCmp("deployNewProcessWindow");
							if(w){
								w.close();
							}
						}
					}];
					showAllWindow("deployNewProcessWindow", "请选择流程文件(仅支持XML格式)", 500, 110, form, null, button);
				}
			},"-",{
				text:"卸载流程",
				id:"unDeployProcess",
				handler:function(){
					var gridSelectionModel = processGrid.getSelectionModel();
					var gridSelection = gridSelectionModel.getSelections();
					if(gridSelection.length < 1){
						Ext.MessageBox.alert('提示','请至少选择一条流程信息！');
					    return false;
					}
					var processArray = new Array();
					for(var i=0; i<gridSelection.length; i++){
						processArray.push(gridSelection[i].get("deploymentId"));
					}
					var processId = processArray.join(",");
					Ext.Msg.confirm("系统提示", "确认要卸载所选流程？卸载之后流程相关信息将被删除，无法恢复！", function(btn){
						if(btn == "ok" || btn == "yes"){
							doUnDeployProcess(url, processId);
						}
					});
				}
			}]
		});
		return processGrid;
	}
	/**
	 * 部署流程，上传流程图
	 * @param url
	 */
	function getUploadProcessForm(url){
		var importForm = new Ext.form.FormPanel({
			url:url,
			autoScroll:true,
			labelAlign: 'right',
			labelWidth:50,
			frame:true,
			waitMsgTarget:true,
			fileUpload: true,
			defaults: {
	            msgTarget: 'side'
	        },
			items:[{
				xtype: 'fileuploadfield',
	            id: 'form-file',
	            width:250,
	            emptyText: '请选择请款流程文件（仅支持XML格式文件）',
	            fieldLabel: '文件',
	            name: 'process',
	            allowBlank:false,
	            buttonCfg: {
	                text: '选择文件'
	            },
	            listeners:{
	            	"fileselected":function(fb,v){
	            		var extName = v.substr(v.lastIndexOf(".")+1);
	            		if(extName!="XML" && extName != "xml"){
	            			Ext.Msg.alert("提示信息","请您选择XML格式文件！");
	            			importForm.getForm().reset();
	            			return;
	            		}
	            	}
	            }
			}]
		});
		return importForm;
	}
	
	/**
	 * 新增/修改/详细表单
	 * @param url 表单提交地址
	 * @param isNull 是否允许空
	 * @param readOnly 只读
	 */
	function getLoanRequestForm(url,isNull, readOnly){
		var requestForm = new Ext.form.FormPanel({
			labelAlign:"right",
			border:true,
			frame:true,
			url:url,
			labelWidth:80,
			items:[{
				layout:'column',
				height:40,
				items:[{
					columnWidth:.50,
					layout:'form',
					items:[getTextField("cashAdvanceInfo.cardId", "入账卡号", isNull, readOnly), getHiddenField("cashAdvanceInfo.id", "")]
				},{
					columnWidth:.50,
					layout:'form',
					items:[getNumberField("cashAdvanceInfo.cashAmount", "请款金额(元)", isNull, readOnly), getHiddenField("cashAdvanceInfo.cashUserName", userName)]
				}]
			},{
				layout:'column',
				height:40,
				items:[{
					columnWidth:.50,
					layout:'form',
					items:[getDateField("cashAdvanceInfo.cashDate", "请款日期", isNull, readOnly, new Date()), getHiddenField("cashAdvanceInfo.cashUserId", userId)]
				}]
			},{
				layout:'column',
				height:70,
				items:[{
					columnWidth:.90,
					layout:'form',
					items:[getTextAreaField("cashAdvanceInfo.cashReason", "请款原因", isNull, readOnly)]
				}]
			},{
				layout:'column',
				height:70,
				items:[{
					columnWidth:.90,
					layout:'form',
					items:[getTextAreaField("cashAdvanceInfo.cashRemark", "备注", true, readOnly)]
				}]
			},
			getHiddenField("cashAdvanceInfo.cashCheckUserId"), getHiddenField("cashAdvanceInfo.cashCheckUserName"),
			getHiddenField("cashAdvanceInfo.cashCheckDate"), getHiddenField("cashAdvanceInfo.cashCheckResult"),
			getHiddenField("cashAdvanceInfo.cashApprovalUserId"), getHiddenField("cashAdvanceInfo.cashApprovalUserName"),
			getHiddenField("cashAdvanceInfo.cashApprovalDate"), getHiddenField("cashAdvanceInfo.cashApprovalResult"),
			getHiddenField("cashAdvanceInfo.processTaskId"), getHiddenField("cashAdvanceInfo.cashStatus")
			]
		})
		return requestForm;
	}
	/**
	 * 处理请款请求
	 * @param taskIds
	 */
	function doWithRequest(type){
		var gridSelectionModel = cashTodoGrid.getSelectionModel();
		var gridSelection = gridSelectionModel.getSelections();
		if(gridSelection.length < 1){
			Ext.MessageBox.alert('提示','请至少选择一条请款信息！');
		    return false;
		}
		var taskArray = new Array();
		var loanArray = new Array();
		for(var i=0; i<gridSelection.length; i++){
			var status = gridSelection[i].get("cashStatus");
			if(type == "99"){
				//再发起请求
				if(status != "03" && status != "06"){
					Ext.MessageBox.alert('提示','只能处理审核驳回或者是审批驳回的请款请求！');
					return false;
				}
			}else{
				//通过或者驳回
				if(status != "01" && status != "02"){
					Ext.MessageBox.alert('提示','只能处理发起审核或者是审核通过的请款请求！');
					return false;
				}
			}
			taskArray.push(gridSelection[i].get("processTaskId"));
			loanArray.push(gridSelection[i].get("id"));
		}
		
		if(type == "0"){
			//驳回
			Ext.Msg.prompt("系统提示", "请输入驳回请款原因：", function(btn, text){
				if(btn == "ok"){
					type = setDoType(gridSelection, type);
					sendRequest(taskArray, loanArray, type, text);
				}
			}, null, true);
		}else if(type == "1"){
			//通过
			Ext.Msg.prompt("系统提示", "请输入审批意见：", function(btn, text){
				if(btn == "ok"){
					type = setDoType(gridSelection, type);
					sendRequest(taskArray, loanArray, type, text);
				}
			}, null, true);
		}else if(type == "99"){
			//再发起
			if(gridSelection.length != 1){
				Ext.MessageBox.alert('提示','只能选择一条请款信息再发起！');
			    return false;
			}
			if(gridSelection[0].get("cashUserId") != userId){
				Ext.MessageBox.alert('提示','不能再发起别人的请款请求！');
			    return false;
			}
			var url = path + "/loan/editRequestCashAdvance.action?method=editRequest";
			editLoanRequest(url, cashTodoGrid, function(){
				sendRequest(taskArray, loanArray, type, "");
			});
		}
	}
	/**
	 * 设置type
	 * @param gridSelection
	 * @returns
	 */
	function setDoType(gridSelection, type){
		if(gridSelection[0].get("cashStatus") == "01"){
			//审核
			type = "0"+ type;
		}else if(gridSelection[0].get("cashStatus") == "02"){
			//审批
			type = "1"+ type;
		}
		return type;
	}
	/**
	 * 执行卸载部署
	 */
	function doUnDeployProcess(url, processId){
		Ext.MessageBox.show({
			msg:"正在卸载请款流程，请稍候...",
			progressText:"正在卸载请款流程，请稍候...",
			width:300,
			wait:true,
			waitConfig: {interval:200},
			icon:Ext.Msg.INFO
		});
		Ext.Ajax.request({
			params:{processId: processId},
			timeout:60000,
			url: url,
			success:function(response,options){
				Ext.MessageBox.hide();
				try{
					var msg = Ext.util.JSON.decode(response.responseText);
					if(msg && msg.msg){
						Ext.Msg.alert("提示信息",msg.msg);
					}else{
						Ext.Msg.alert("提示信息","已成功卸载请款流程！");
					}
					processStore.reload();
				}catch(e){
					Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
				}
			},failure:function(response,options){
				Ext.Msg.hide();
				try{
					var msg = Ext.util.JSON.decode(response.responseText);
					if(msg && msg.msg){
						Ext.Msg.alert("提示信息",msg.msg);
					}else{
						Ext.Msg.alert("提示信息","卸载请款流程已失败！");
					}
				}catch(e){
					Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
				}
				return;
			}
		});
	}
	/**
	 * 上传文件，执行部署
	 */
	function doDeployProcess(form, windowId){
		Ext.MessageBox.show({
			msg:"正在部署请款流程，请稍候...",
			progressText:"正在部署请款流程，请稍候...",
			width:300,
			wait:true,
			waitConfig: {interval:200},
			icon:Ext.Msg.INFO
		});
		form.getForm().submit({
			timeout:60000,
			success: function(form, action) {
				Ext.Msg.hide();
				try{
					var result = Ext.decode(action.response.responseText);
					if(result && result.success){
						var msg = "请款流程部署成功！";
						if(result.msg){
							msg = result.msg;
						}
						Ext.Msg.alert('系统提示信息', msg, function(btn, text) {
							if (btn == 'ok') {
								Ext.getCmp(windowId).close();
								processStore.reload();
							}
						});
					}else if(!result.success){
						var msg = "请款流程部署失败！";
						if(result.msg){
							msg = result.msg;
						}
						Ext.Msg.alert('系统提示信息', msg);
					}
				}catch(e){
					Ext.Msg.alert('系统提示信息', "系统错误：" + e);
				}
			},
			failure: function(form, action) {//action.result.errorMessage
				Ext.Msg.hide();
				var msg = "请款流程部署失败，请检查您的网络连接或者联系管理员！";
				try{
					var result = Ext.decode(action.response.responseText);
					if(result.msg){
						msg = result.msg;
					}
				}catch(e){
					msg = "系统错误：" + e;
				}
				Ext.Msg.alert('系统提示信息', msg);
			}
		});
	}
	
	/**
	 * 发起请求
	 */
	function sendRequest(taskArray, loanArray, type, text){
		var taskIds = taskArray.join(",");
		var loanIds = loanArray.join(",");
		Ext.MessageBox.show({
			msg:"正在处理您的任务，请稍候...",
			progressText:"正在处理您的任务，请稍候...",
			width:300,
			wait:true,
			waitConfig: {interval:200},
			icon:Ext.Msg.INFO
		});
		Ext.Ajax.request({
			params:{taskIds:taskIds, loanIds:loanIds, doType:type, currentUserId:userId, currentUserName:userName, reason: text},
			timeout:60000,
			url: path + "/loan/doRequestTaskCashAdvance.action?method=doRequestTask",
			success:function(response,options){
				Ext.MessageBox.hide();
				try{
					var msg = Ext.util.JSON.decode(response.responseText);
					if(msg && msg.msg){
						Ext.Msg.alert("提示信息",msg.msg);
						cashTaskStore.reload();
					}else{
						Ext.Msg.alert("提示信息","已成功处理您的任务！");
						cashTaskStore.reload();
					}
				}catch(e){
					Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
				}
			},failure:function(response,options){
				Ext.Msg.hide();
				try{
					var msg = Ext.util.JSON.decode(response.responseText);
					if(msg && msg.msg){
						Ext.Msg.alert("提示信息",msg.msg);
					}else{
						Ext.Msg.alert("提示信息","处理您的任务已失败！");
					}
				}catch(e){
					Ext.Msg.alert("提示信息","系统错误，错误原因：" + e);
				}
				return;
			}
		});
	}
	
	/**
	 * 保存请款
	 * @param windowId
	 * @param form
	 */
	function saveLoadRequest(windowId, form, callback){
		Ext.MessageBox.show({
			msg:"正在保存请款信息，请稍候...",
			progressText:"正在保存请款信息，请稍候...",
			width:300,
			wait:true,
			waitConfig: {interval:200},
			icon:Ext.Msg.INFO
		});
		form.getForm().submit({
			timeout:60000,
			success: function(form, action) {
				Ext.Msg.hide();
				try{
					var result = Ext.decode(action.response.responseText);
					if(result && result.success){
						var msg = "请款信息保存成功！";
						if(result.msg){
							msg = result.msg;
						}
						Ext.Msg.alert('系统提示信息', msg, function(btn, text) {
							if (btn == 'ok') {
								loadCashDataStore();
							}
						});
						Ext.getCmp(windowId).close();
						if(callback && (typeof(callback) == "function")){
							callback();
						}
						
					}else if(!result.success){
						var msg = "系统消息发送失败！";
						if(result.msg){
							msg = result.msg;
						}
						Ext.Msg.alert('系统提示信息', msg);
					}
				}catch(e){
					Ext.Msg.alert('系统提示信息', "系统错误：" + e);
				}
			},
			failure: function(form, action) {//action.result.errorMessage
				Ext.Msg.hide();
				var msg = "新增请款失败，请检查您的网络连接或者联系管理员！";
				try{
					var result = Ext.decode(action.response.responseText);
					if(result.msg){
						msg = result.msg;
					}
				}catch(e){
					msg = "系统错误：" + e;
				}
				Ext.Msg.alert('系统提示信息', msg);
			}
		});
	}
}
/**
 * 程序主入口
 */
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	cashAdvance();
});