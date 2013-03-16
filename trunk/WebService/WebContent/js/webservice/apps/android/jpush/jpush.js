/**
 * JPush推送
 */
function jpush(){
    /**
     * userReader - 用户信息解析器
     */
    var userReader = new Ext.data.JsonReader({
        totalProperty : "totalCount",
        root : "userList"
    },[
        {name:"userId"},//唯一id
        {name:"userCode"},//用户编号
        {name:"userName"},//用户名
        {name:"telphoneNo"},//电话
        {name:"phoneNo"},//手机
        {name:"privence"},//省
        {name:"city"},//城市
        {name:"address"},//地址
        {name:"zip"},//邮编
        {name:"email"},//电子邮件
        {name:"phoneImei"}//电子邮件
    ]);
    
    /**
     * 用户信息数据集
     */
    var userStore = new Ext.data.Store({
        proxy:new Ext.data.HttpProxy({
            url: path + "/jpush/jpushUserList.action?method=userList"
        }),
        reader:userReader,
        //倒叙排序
        baseParams:{start:0, limit:50},
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
    var userCM = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),getSM(),{
        dataIndex:"userId",
        hidden:true,
        hideable:false//不允许将隐藏的字段显示出来
    },{
        header:"用户名称",
        dataIndex:"userName",
        width:150
    },{
        header:"手机号码",
        dataIndex:"phoneNo",
        width:180
    },{
        header:"电子邮件",
        dataIndex:"email",
        width:130
    },{
        header:"设备IMEI号码",
        dataIndex:"phoneImei",
        width:130
    }]);
    
    /**
     * 用户列表
     */
    var userGrid = new Ext.grid.GridPanel({
        title:"用户列表",
        region:'center',
        collapsible:false,//是否可以展开
        animCollapse:true,//展开时是否有动画效果
        autoScroll:true,
        width:Ext.get("jpush_div").getWidth(),
        height:Ext.get("jpush_div").getHeight() - 20,
        loadMask:true,//载入遮罩动画（默认）
        frame:true,
        autoShow:true,
        store:userStore,
        cm:userCM,
        sm:getSM(),
        viewConfig:{forceFit:true},//若父容器的layout为fit，那么强制本grid充满该父容器
        split: true,
        stripeRows: true,
        renderTo:"jpush_div",
        bbar:new Ext.PagingToolbar({
            pageSize:50,//每页显示数
            store:userStore,
            displayInfo:true,
            displayMsg:"显示{0}-{1}条记录，共{2}条记录",
            nextText:"下一页",
            prevText:"上一页",
            emptyMsg:"无相关记录"
        }),
        tbar:[]
    });
    
    /**
     * 按钮存储器，尚未执行查询
     */
    var buttonRightStore = buttonRight();
    /**
     * 执行权限按钮加载, 并且加载列表数据, 显示权限按钮
     * see buttonRight.js
     */
    loadButtonRight(buttonRightStore, userStore, userGrid, "jpush_div");
    /**
     * 推送
     */
    this.push = function(url){
        var gridSelectionModel = userGrid.getSelectionModel();
        var gridSelection = gridSelectionModel.getSelections();
        if(gridSelection.length < 1){
            Ext.MessageBox.alert('提示','请要推送消息的用户！');
            return false;
        }
        var userArray = new Array();
        var imeiArray = new Array();
        for(var i=0; i<gridSelection.length; i++){
            var userName = gridSelection[i].get("userName");
            var imei = gridSelection[i].get("phoneImei");
            userArray.push(userName);
            imeiArray.push(imei);
        }
        var userForm = showPushForm(url, userArray.join(","), imeiArray.join(","));
        var button = [{
            text:"推送消息",
            handler:function(){
                if(userForm.form.isValid()){
                    push("pushWindow", userForm);
                }
            }
        },{
            text:"关闭窗口",
            handler:function(){
                var userWindow = Ext.getCmp("pushWindow");
                if(userWindow){
                    userWindow.close();
                }
            }
        }];
        showAllWindow("pushWindow", "推送消息",500, 320, userForm, null, button);
    };
    /**
     * 推送表单
     * @param url
     * @param users
     * @param imeis
     * @returns {Ext.form.FormPanel}
     */
    function showPushForm(url, users, imeis){
        var userForm = new Ext.form.FormPanel({
            frame: true,
            labelAlign: 'right',
            labelWidth:60,
            autoScroll:false,
            waitMsgTarget:true,
            url:url,
            items:[{
                layout:'column',
                height:40,
                items:[{
                    columnWidth:.90,
                    layout:'form',
                    items:[getTextField("userName", "推送用户", false, true, users), getHiddenField("phoneImei", imeis)]
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
                    items:[getTextAreaField("content", "推送消息", false, false)]
                }]
            }]
        });
        return userForm;
    }
    /**
     * 推送消息
     */
    function push(windowId, form){
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
            params:{pushUserName:userName},
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
    jpush();
});