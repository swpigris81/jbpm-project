Ext.ux.TaskQueue = Ext.extend(Ext.util.Observable, {
	constructor: function(o) {
		this.reset();
		this.initEvents();
		if(o) {
			this.init(o);
		}
	},
	init: function(o) {
		this.reset();

		//init queue
		this.queue = Ext.isArray(o.queue) ? o.queue : [o.queue];

		//init other params
		this.copyTo(this, o, 'onCancel,stepTimeout,onStepTimeout,step,success,failure,scope,eventName,beforeStep,afterStep,stepScope');
		if(this.stepTimeout) {
			this.initStepSchedule();
		}
		return this;
	},
    copyTo : function(dest, source, names){
        if(typeof names == 'string'){
            names = names.split(/[,;\s]/);
        }
        Ext.each(names, function(name){
            if(source.hasOwnProperty(name)){
                dest[name] = source[name];
            }
        }, this);
        return dest;
    },
	initStepSchedule: function() {
		this.stepSchedule = new Ext.util.DelayedTask(function() {
			this.cancel();
			if(this.onStepTimeout) {
				this.onStepTimeout.call(this.stepScope);
			} else {
				alert('error, timeout');
			}
		}, this);
	},
	isRunning: function() {
		return this.running;
	},
	isFinished: function() {
		return !this.isRunning();
	},
	killAjax: function() {
		if(this.transId) {
			Ext.Ajax.abort(this.transId);
			delete this.transId;
		}
	},
	setTransId: function(transId) {
		this.transId = transId;
	},
	initEvents: function() {
		// init onReady event
		this.eventName = this.eventName || 'next';
		this.addEvents(this.eventName);

		this.on(this.eventName, function() {
			this.run();
		}, this);
	},
	next: function() {
		this.doAfterStep();
		this.currentIndex++;
		this.run();
	},
	isCanceled: function() {
		return !!this.canceled;
	},
	cancel: function() {
		if(!this.isRunning()) {
			return;
		}
		this.canceled = true;
		this.killAjax();
		this.running = false;
		clearTimeout(this.stepThread);
		this.doCancel();
	},
	reset: function(o) {
		this.canceled = false;
		this.stepThread = null;
		this.transId = null;
		this.currentIndex = 0;
		this.running = false;
		if(o) {
			Ext.apply(this, o);
		}
	},
	restart: function() {
		this.cancel();
		this.reset();
		this.run();
	},
	
	start: function() {
		if(this.isRunning()) {
			return false;
		} else {
			this.reset();
			this.running = true;
			this.run();
			return true;
		}
	},
	finish: function() {
		this.doCallback();
		this.reset();
	},
	run: function() {
		if(this.canceled) {
			return;
		}
		if(this.currentIndex >= this.queue.length) { // finished, run the success callback;
			this.finish();
		} else {
			var _this = this;
			this.stepThread = setTimeout(function() { // create a new thread, prevent stack overflow
				var i = _this.currentIndex;
				var item = _this.queue[i];
				_this.doBeforeStep();
				_this.transId = _this.step.call(_this.scope, item, i, _this);
			}, 0);
		}
	},
	doBeforeStep: function() {
		var fn = this.beforeStep;
		if(fn) {
			var i = this.currentIndex;
			var item = this.queue[i];
			fn.call(this.stepScope, item, i, this.queue, this);
		}
		if(this.stepSchedule) {
			this.stepSchedule.delay(this.stepTimeout);
		}
	},
	doAfterStep: function() {
		if(this.stepSchedule) {
			this.stepSchedule.cancel();
		}
		var fn = this.afterStep;
		if(fn) {
			var i = this.currentIndex;
			var item = this.queue[i];
			fn.call(this.stepScope, item, i, this.queue, this);
		}
	},
	doCallback: function() {
		var fn = this.success;
		if(fn) {
			fn.call(this.scope);
		}
	},
	doCancel: function() {
		var fn = this.onCancel;
		if(fn) {
			fn.call(this.scope);
		}
	}
});




