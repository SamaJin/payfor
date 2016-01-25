
function show_callin_logs_list(area_code,user_phone) {
	Ext.define('model_callin_logs', {
		extend : 'Ext.data.Model',
		fields : [ {
			name : 'id',
			type : 'int'
		}, {
			name : 'area_code',
			type : 'string'
		}, {
			name : 'user_number',
			type : 'string'
		}, {
			name : 'agent_code',
			type : 'string'
		}, {
			name : 'operate_time',
			type : 'string'
		}, {
			name : 'operate_type',
			type : 'string'
		}, {
			name : 'operate_result',
			type : 'string'
		}, {
			name : 'remarks',
			type : 'string'
		} ]
	});
	//--
	var callin_logs_store = Ext.create('Ext.data.Store', {
		model : 'model_callin_logs',
		autoLoad : true,
		proxy : {
			type : 'ajax',
			url : '/ccdms/listCallinOrderLogs.do?area_code='+area_code+'&user_phone='+user_phone,
			reader : {
				type : 'json',
				root : 'rows',
				totalProperty : 'total'
			}
		}
	});
	//--
	var grid_report_list = Ext.create('Ext.grid.Panel', {
		store : callin_logs_store,
		stripeRows : true,
		columnLines : true,
		loadMask : true,
//		tbar : Ext.create('Ext.toolbar.Toolbar', {
//			items : [ {
//				xtype : 'datefield',
//				id : 'input_logs_start_time',
//				fieldLabel : '&nbsp;开始时间',
//				format : 'Y-m-d',
//				labelWidth : 60,
//				selectOnFocus : true
//			}, {
//				xtype : 'datefield',
//				id : 'input_logs_end_time',
//				fieldLabel : '&nbsp;结束时间',
//				format : 'Y-m-d',
//				labelWidth : 60,
//				selectOnFocus : true
//			}, '->', {
//				text : '查询',
//				width : 50,
//				icon : '/ccdms/extui/resources/icons/magnifier.png',
//				handler : function() {
//					callin_logs_store.on('beforeload', function() {
//						Ext.apply(callin_logs_store.proxy.extraParams, {
//							'start_time' : Ext.getCmp('input_logs_start_time').getRawValue(),
//							'end_time' : Ext.getCmp('input_logs_end_time').getRawValue()
//						});
//					});
//					callin_logs_store.loadPage(1);
//				}
//			} ]
//		}),
		columns : [ {
			text : '区域代码',
			dataIndex : 'area_code'
		}, {
			text : '用户号码',
			dataIndex : 'user_number'
		}, {
			text : '工号',
			dataIndex : 'agent_code'
		}, {
			text : '操作时间',
			dataIndex : 'operate_time'
		}, {
			text : '操作类型',
			dataIndex : 'operate_type'
		}, {
			text : '操作结果',
			dataIndex : 'operate_result'
		}, {
			text : '备注',
			dataIndex : 'remarks'
		} ],
		bbar : Ext.create('Ext.toolbar.Paging', {
			store : callin_logs_store,
			displayInfo : true
		})
	});
	
	var window_report_logs_list = Ext.create('Ext.window.Window', {
		layout : 'fit',
		width : 800,
		height : 600,
		autoShow : true,
		modal : true,
		items : [ grid_report_list ]
	}).show();
}
