Ext.ux.ColumnWidthCalculator = Ext.extend(Ext.emptyFn, {
	stepLength: 50, 
	constructor: function() {
		this.task = new Ext.ux.TaskQueue();
		this.canceled = false;
		this.initTemplates();
	},
	init: function(grid) {
		this.grid = grid;

		var v = grid.view;

		v.renderUI = v.renderUI.createSequence(this.renderUI, this);
		grid.store.on('load', this.onStoreLoad, this);//if store reload, data may changed, so do reCalculate
		grid.store.on('update', this.onStoreUpdate, this);//record.commit() causes data changed, do reCalculate
	},
	//handle record.set()
	onStoreUpdate: function(store) {
		var ms = store.getModifiedRecords();
		var fields = [];
		Ext.each(ms, function(m) {
			for(var f in m.modified) {
				if(fields.indexOf(f) === -1) {
					fields.push(f);
				}
			}
		});
		var cols = [], cm = this.grid.colModel;
		Ext.each(fields, function(f) {
			var i = cm.findColumnIndex(f);
			if(!cm.config[i].hidden && cm.config[i].autoAdjust === 'content') {
				cols.push(cm.config[i]);
			}
		});

		// code above is to get all modified fields, is there any better way?
		this.initTask(cols).start();
	},
	//handle store.load
	onStoreLoad: function(ds) {
		if(ds.getCount() === 0) {
			return;
		}
		var g =this.grid;

		var cfg = g.colModel.config;
		var cs = [];
		for(var i=0, len=cfg.length; i<len; i++) {
			if(!cfg[i].hidden && cfg[i].autoAdjust === 'content') {
				cs.push(cfg[i]);
			}
		}
		if(cs.length > 0) {
			this.initTask(cs).start();
		}
	},
	renderUI: function() {
		var g = this.grid,
			v = g.view;
		var _this = this;

		if(v.hmenu) {
			
			var autoAdj = {
				itemId: 'autoAdj',
				text: '自动调整列宽',
				iconCls: '',
				scope: _this,
				handler: _this.onAutoAdjustClick
			};

			var adjCol = {
				itemId: 'adjCol',
				hideOnClick: false,
				text: '以...调整列宽',
				iconCls: '',
				menu: [
					{
						itemId: 'adjColHead',
						text: '标题',
						scope: _this,
						handler: _this.onByHeaderClick
					}, {
						itemId: 'adjColContent',
						text: '内容',
						scope: _this,
						handler: _this.onByContentClick
					}
				]
			};
			var adjAllCol = {
				itemId: 'adjAllCol',
				hideOnClick: false,
				text: '以...调整所有列宽',
				iconCls: '',
				menu: [
					{
						itemId: 'adjAllColHead',
						text: '标题',
						scope: _this,
						handler: _this.onAllByHeaderClick
					}, {
						itemId: 'adjAllColContent',
						text: '内容',
						scope: _this,
						handler: _this.onAllByContentClick
					}
				]
			};
			v.hmenu.add(autoAdj, adjCol, adjAllCol);
		}
	},
	// 1 col by header
	onByHeaderClick: function() {
		var cm = this.grid.colModel,
			colIndex = this.grid.view.hdCtxIndex,
			col = cm.config[colIndex];
		
		col.autoAdjust = 'resizeByHeader';

		this.initTask(col).start();
	},
	// all col by header
	onAllByHeaderClick: function() {
		var cfg = this.grid.colModel.config;
		var cs = [];
		Ext.each(cfg, function(c) {
			if(!c.fixed) {
				c.autoAdjust = 'resizeByHeader';
				cs.push(c);
			}
		});
		if(cs.length > 0) {
			this.initTask(cs).start();
		}
	},
	// 1 col by Content
	onByContentClick: function() {
		var g = this.grid;
		var dsCount = g.store.getCount();
		var cm = g.colModel,
			colIndex = g.view.hdCtxIndex,
			c = cm.config[colIndex];

		c.autoAdjust = 'resizeByContent';

		if(dsCount > 0) {
			this.initTask(c).start();
		}
	},
	// all col by Content
	onAllByContentClick: function() {
		var dsCount = this.grid.store.getCount();
		var cfg = this.grid.colModel.config;
		var cs = [];
		Ext.each(cfg, function(c) {
			if(c.fixed) {
				return true;
			}
			c.autoAdjust = 'resizeByContent';
			if(dsCount > 0) {
				cs.push(c);
			}
		});

		if(cs.length > 0) {
			this.initTask(cs).start();
		}
	},
	// Like all col by Content, but don't resize smaller than header
	onAutoAdjustClick: function() {
		var dsCount = this.grid.store.getCount();
		var cfg = this.grid.colModel.config;
		var cs = [];
		Ext.each(cfg, function(c) {
			if(c.fixed) {
				return true;
			}
			c.autoAdjust = 'resizeByContentSmart';
			if(dsCount > 0) {
				cs.push(c);
			}
		});

		if(cs.length > 0) {
			this.initTask(cs).start();
		}
	},
	mask: function(msg) {
		if(this.maskEl) {
			this.maskEl.removeAllListeners();
			Ext.fly(this.maskEl.dom.nextSibling).removeAllListeners();
		}
		this.maskEl = this.grid.el.mask(msg, 'x-mask-loading');
		this.maskEl.on('click', this.onMaskClick, this);
		Ext.fly(this.maskEl.dom.nextSibling).on('click', this.onMaskClick, this);
	},
	unMask: function() {
		if(this.maskEl) {
			this.maskEl.un('click', this.onMaskClick, this);
			Ext.fly(this.maskEl.dom.nextSibling).un('click', this.onMaskClick, this);
			this.grid.el.unmask();
			delete this.maskEl;
		}
	},
	onMaskClick: function() {
		this.task.cancel();
	},
	// the el is appended to the grid, it is used to calculate the text width,
	// is there any better way to do the text width calculation?
	createEl: function() {
		if(!this.el) {
			this.el = this.grid.el.createChild({
				'white-space': 'nowrap',
				overflow: 'hidden',
				// font: '11px/13px arial,tahoma,helvetica,sans-serif',
				height: 0
			});
		}
	},
	// after (all done || canceled), clean up the el
	cleanEl: function() {
		if(this.el) {
			this.el.remove();
			delete this.el;
		}
	},
	initTemplates: function() {
		this.maskTemplate = new Ext.Template(
			'正在调整列宽, 请稍候({percent}%) ...' + 
			'<p style="text-align:center;"><font color=#737573>点击任意地方取消调整</font></p>'
		);
	},
	initTask: function(cols) {
		return this.task.init({
			queue: cols,
			scope: this,
			success: function() {
				this.unMask();
				this.cleanEl();
			},
			stepScope: this,
			beforeStep: function(col, i, queue, task) {
				this.mask(this.maskTemplate.apply({
					header: col.header,
					percent: Math.round(i*100/queue.length)
				}));
				this.createEl();
			},
			step: function(col, i, task) {
				var type = col.autoAdjust;
				var fn = this[type];
				fn.call(this, col.dataIndex, function() {
					return task.isCanceled();
				}, function() {
					task.next();
				});
			},
			onCancel: function() {
				this.unMask();
				this.cleanEl();
			}
		});
	},
	resizeByContent: function(colIndex, isCanceled, callback) {
		var g = this.grid, cm = g.colModel, v = g.view,
			ds = g.store, rowCount = ds.getCount();

		if(typeof colIndex != 'number') {// support both colIndex & dataIndex
			colIndex = cm.findColumnIndex(colIndex);
		}

		var col = cm.config[colIndex];

		var max = 0, w, row = 0;
		var cache = {};
		var _this = this;

		(function() {
			if(isCanceled()) {
				return;
			}
			for(var x=0; x<_this.stepLength; x++) {
				if(row < rowCount) {
					var rawText = ds.getAt(row).get(col.dataIndex);
					if(cache[rawText] !== undefined) {
						w = cache[rawText];
					} else {
						if(col.dataIndex != "RN"){  // Don't get renderer on RN
							var text = col.renderer(rawText);
						}
						else{
							var text = rawText;
						}
						w = _this.el.getTextWidth(text) + 12;// 12 = 8+getPadding('lr')+getMargins('lr')
						cache[rawText] = w;
					}
					max = Math.max(max, w);
				} else {
					//finish
					v.onColumnSplitterMoved(colIndex, max);
					if(callback) {
						callback();
					}
					return;
				}
				row++;
			}
			// loop the closure, create a thread to prevent freezing the GUI
			// because of creating thread also costs time, so there is a stepLength (default 50)
			// process 50 rows then create a thread
			setTimeout(arguments.callee, 0);
		}).call(this);
	},
	resizeByContentSmart: function(colIndex, isCanceled, callback) {
		var g = this.grid, cm = g.colModel, v = g.view,
			ds = g.store, rowCount = ds.getCount();

		if(typeof colIndex != 'number') {// support both colIndex & dataIndex
			colIndex = cm.findColumnIndex(colIndex);
		}

		var col = cm.config[colIndex];

		// Don't resize smaller than the header 
		var c = Ext.fly(v.getHeaderCell(colIndex));
		var max = c.getPadding('lr') + 8 + c.getMargins('lr') + c.getPadding('lr')
				  + c.getTextWidth(Ext.util.Format.stripTags(c.innerHTML));
		
		var w, row = 0;
		var cache = {};
		var _this = this;

		(function() {
			if(isCanceled()) {
				return;
			}
			for(var x=0; x<_this.stepLength; x++) {
				if(row < rowCount) {
					var rawText = ds.getAt(row).get(col.dataIndex);
					if(cache[rawText] !== undefined) {
						w = cache[rawText];
					} else {
						if(col.dataIndex != "RN"){  // Don't get renderer on RN
							var text = col.renderer(rawText);
						}
						else{
							var text = rawText;
						}
						w = _this.el.getTextWidth(text) + 12;// 12 = 8+getPadding('lr')+getMargins('lr')
						cache[rawText] = w;
					}
					max = Math.max(max, w);
				} else {
					//finish
					v.onColumnSplitterMoved(colIndex, max);
					if(callback) {
						callback();
					}
					return;
				}
				row++;
			}
			// loop the closure, create a thread to prevent freezing the GUI
			// because of creating thread also costs time, so there is a stepLength (default 50)
			// process 50 rows then create a thread
			setTimeout(arguments.callee, 0);
		}).call(this);
	},
	resizeByHeader: function(colIndex, isCanceled, callback) {
		var g = this.grid, cm = g.colModel;
		var v = g.view;
		var c, w;

		if(typeof colIndex != 'number') {// support both colIndex & dataIndex
			colIndex = cm.findColumnIndex(colIndex);
		}

		c = Ext.fly(v.getHeaderCell(colIndex));
		w = c.getPadding('lr');
		c = c.first('.x-grid3-hd-inner');
		w += 8 + c.getMargins('lr') + c.getPadding('lr') + c.getTextWidth(Ext.util.Format.stripTags(c.innerHTML));
		v.onColumnSplitterMoved(colIndex, w);

		if(callback) {
			callback();
		}
	}
});
