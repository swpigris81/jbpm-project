/**
 * Android消息推送服务器端
 */
function notification(){
    /**
     * 用户信息
     */
    var userReader = new Ext.data.JsonReader({
        totalProperty : "totalCount",
        root : "userList"
    },[
       {name:"id"},
       {name:"username"},
       {name:"password"},
       {name:"email"},
       {name:"name"},
       {name:"createdDate"},
       {name:"updatedDate"},
       {name:"online"}
    ]);
    
    /**
     * Session信息
     */
    var sessionReader = new Ext.data.JsonReader({
        totalProperty : "totalCount",
        root : "sessionList"
    },[
       {name:"username"},
       {name:"resource"},
       {name:"status"},
       {name:"presence"},
       {name:"clientIP"},
       {name:"createdDate"}
    ]);
    
    /**
     * 用户信息数据集
     */
    var userStore = new Ext.data.Store({
        proxy:new Ext.data.HttpProxy({
            url: path + "/pnserver/pnserverUserList.action?method=userList"
        }),
        reader:userReader,
        //倒叙排序
        sortInfo:{field: 'online', direction: 'DESC'},
        baseParams:{start:0, limit:999999999},
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
     * Session信息数据集
     */
    var sessionStore = new Ext.data.Store({
        proxy:new Ext.data.HttpProxy({
            url: path + "/pnserver/pnserverSessionList.action?method=sessionList"
        }),
        reader:sessionReader,
        //倒叙排序
        sortInfo:{field: 'createdDate', direction: 'DESC'},
        baseParams:{start:0, limit:999999999},
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
    function getSM(){
        var sm = new Ext.grid.CheckboxSelectionModel();
        return sm
    }
    
    /**
     * 用户展现
     */
    var userCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), getSM(), {
        header:"用户编号",
        dataIndex:"id",
        width:30
    },{
        header:"用户唯一标识",
        dataIndex:"username",
        width:90
    },{
        header:"用户密码",
        dataIndex:"password",
        hidden:true,
        hideable:false,
        width:70
    },{
        header:"用户EMAIL",
        dataIndex:"email",
        width:70
    },{
        header:"用户名",
        dataIndex:"name",
        width:50
    },{
        header:"创建日期",
        dataIndex:"createdDate",
        width:50
    },{
        header:"更新日期",
        dataIndex:"updatedDate",
        width:50
    },{
        header:"是否在线",
        dataIndex:"online",
        renderer:function(val){
            if(val){
                if(val == "00"){
                    return "离线<img src='"+path+"/images/user-offline.png'/>";
                }else if(val == "01"){
                    return "在线<img src='"+path+"/images/user-online.png'/>";
                }
            }
        },
        width:40
    }]);
    
    /**
     * session展现
     */
    var sessionCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), getSM(), {
        header:"用户唯一标识",
        dataIndex:"username",
        width:70
    },{
        header:"用户客户端",
        dataIndex:"resource",
        width:90
    },{
        header:"Session状态",
        dataIndex:"status",
        width:70
    },{
        header:"用户状态",
        dataIndex:"presence",
        width:70
    },{
        header:"用户IP",
        dataIndex:"clientIP",
        width:70
    },{
        header:"创建日期",
        dataIndex:"createdDate",
        width:70
    }]);
    
    //暂不提供查询
    /**
     * 用户列表
     */
    var userGrid = new Ext.grid.GridPanel({
        //id:"cashGrid",
        title:"用户列表",
        region:'center',
        collapsible:false,//是否可以展开
        animCollapse:true,//展开时是否有动画效果
        autoScroll:true,
        //width:Ext.get("channel_main_div").getWidth(),
        //height:Ext.get("notification_div").getHeight() - 150,
        loadMask:true,//载入遮罩动画（默认）
        //view: new Ext.grid.GridView({ forceFit:true }),
        //plugins: [new Ext.ux.ColumnWidthCalculator()],
        frame:true,
        autoShow:true,
        store:userStore,
        cm:userCM,
        sm:getSM(),
        //renderTo:"channel_main_div",
        viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
        split: true,
        stripeRows: true,
        bbar:new Ext.PagingToolbar({
            pageSize:9999999999,//每页显示数
            store:userStore,
            displayInfo:true,
            displayMsg:"显示{0}-{1}条记录，共{2}条记录",
            nextText:"下一页",
            prevText:"上一页",
            emptyMsg:"无相关记录"
        })
    });
    
    /**
     * Session列表
     */
    var sessionGrid = new Ext.grid.GridPanel({
        //id:"cashGrid",
        title:"用户会话列表",
        region:'center',
        collapsible:false,//是否可以展开
        animCollapse:true,//展开时是否有动画效果
        autoScroll:true,
        //width:Ext.get("channel_main_div").getWidth(),
        //height:Ext.get("channel_main_div").getHeight(),
        loadMask:true,//载入遮罩动画（默认）
        //view: new Ext.grid.GridView({ forceFit:true }),
        //plugins: [new Ext.ux.ColumnWidthCalculator()],
        frame:true,
        autoShow:true,
        store:sessionStore,
        cm:sessionCM,
        sm:getSM(),
        //renderTo:"channel_main_div",
        viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
        split: true,
        stripeRows: true,
        bbar:new Ext.PagingToolbar({
            pageSize:999999999,//每页显示数
            store:sessionStore,
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
    var mainPanel = new Ext.Panel({
        layout:"border",
        height:Ext.get("notification_div").getHeight(),
        width:Ext.get("notification_div").getWidth(),
        renderTo:"notification_div",
        items:[{
            region:"center",
            xtype:"tabpanel",
            activeTab:0,
            deferredRender:true,
            items:[{
                title:"用户列表",
                layout:"fit",
                items:[userGrid],
                tbar:[{
                    text:"发送Android推送消息",
                    iconCls:"table_gear",
                    tooltip:"发送Android推送消息",
                    handler:function(){
                        var gridSelectionModel = userGrid.getSelectionModel();
                        var gridSelection = gridSelectionModel.getSelections();
                        if(gridSelection.length < 1){
                            Ext.MessageBox.alert('提示','请选择用户推送消息！');
                            return false;
                        }
                        var userArray = new Array();
                        for(var i=0; i<gridSelection.length; i++){
                            var isOnline = gridSelection[i].get("online");
                            /*
                            if(isOnline == "00"){
                                Ext.MessageBox.alert('提示','暂不支持向离线用户推送消息！');
                                return false;
                            }
                            */
                            userArray.push(gridSelection[i].get("username"));
                        }
                        showNotificationForm(userArray.join(","));
                    }
                },"-"]
            },{
                title:"用户会话列表",
                layout:"fit",
                items:[sessionGrid]
            }],
            listeners :{
                "tabchange":function(thiz, tab){
                    tab.items.get(0).store.load();
                }
            }
        }]
    });
    
    /**
     * 按钮存储器，尚未执行查询
     */
    var buttonRightStore = buttonRight();
    /**
     * 执行权限按钮加载, 并且加载列表数据, 显示权限按钮
     * see buttonRight.js
     * loadButtonRight(buttonStore, mainDataStore, dataGrid, pageDiv, params)
     */
    loadButtonRight(buttonRightStore, userStore, mainPanel, "notification_div", null, null, function(){
    });
    
    /**
     * 显示推送消息对话框
     * @param users 用户
     */
    function showNotificationForm(users){
        var url = path + "/pnserver/pnserverNotificationSend.action?method=notificationSend";
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
                    columnWidth:.90,
                    layout:'form',
                    items:[getTextField("username", "推送用户", false, true, users), getHiddenField("broadcast", "N")]
                }]
            },{
                layout:'column',
                height:40,
                items:[{
                    columnWidth:.90,
                    layout:'form',
                    items:[getTextField("title", "推送标题", false, false)]
                }]
            },{
                layout:'column',
                height:70,
                items:[{
                    columnWidth:.90,
                    layout:'form',
                    items:[getTextAreaField("message", "推送消息", false, false)]
                }]
            },{
                layout:'column',
                height:40,
                items:[{
                    columnWidth:.90,
                    layout:'form',
                    items:[getTextField("uri", "推送消息响应URL", true, false)]
                }]
            }]
        });
        var buttons = [{
            text:"提交",
            handler:function(){
                if(requestForm.form.isValid()){
                    doNotification(requestForm, "showNotificationWindow");
                }
            }
        },{
            text:"取消",
            handler:function(){
                var w = Ext.getCmp("showNotificationWindow");
                if(w) w.close();
            }
        }];
        showAllWindow("showNotificationWindow", "推送消息", 500, 300, requestForm, null, buttons);
    }
    
    /**
     * 推送消息
     */
    function doNotification(form, windowId){
        Ext.MessageBox.show({
            msg:"正在推送消息，请稍候...",
            progressText:"正在推送消息，请稍候...",
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
                        var msg = "消息推送成功！";
                        if(result.msg){
                            msg = result.msg;
                        }
                        Ext.Msg.alert('系统提示信息', msg, function(btn, text) {
                            if (btn == 'ok') {
                                Ext.getCmp(windowId).close();
                            }
                        });
                    }else if(!result.success){
                        var msg = "消息推送失败！";
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
                var msg = "消息推送失败，请检查您的网络连接或者联系管理员！";
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
    notification();
});