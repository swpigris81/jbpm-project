function mongoDB(){
	var url = path + "/mongodb/mongodbAction!showMongoDbTest.action";
	var params = {
			"userId":"111"
	};
	requestAjax(url, params, function(msg){
		alert(msg.msg);
	});
}

/**
 * 程序主入口
 */
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	mongoDB();
});