<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/ccdms/extui/resources/css/ext-all-gray.css">
<script type="text/javascript" src="/ccdms/extui/ext-all.js"></script>
<script type="text/javascript" src="/ccdms/extui/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="/ccdms/js/timeout.js"></script>
<script type="text/javascript">
	var grid_record;
	var store_grid_record;
	var viewport_main;
	var window_audition_record;
	var window_recording_check;
	//--
	Ext.define('model_record', {
		extend : 'Ext.data.Model',
		fields : [ {
			name : 'id',
			type : 'String'
		}, {
			name : 'agent_code',
			type : 'string'
		}, {
			name : 'caller_number',
			type : 'string'
		}, {
			name : 'orig_caller_number',
			type : 'string'
		}, {
			name : 'called_number',
			type : 'string'
		}, {
			name : 'orig_called_number',
			type : 'string'
		}, {
			name : 'file_name',
			type : 'string'
		}, {
			name : 'start_time',
			type : 'string'
		}, {
			name : 'end_time',
			type : 'string'
		}, {
			name : 'is_check',
			type : 'int'
		} ]
	});
	//--
	Ext.onReady(function() {
		grid_record = Ext.create('Ext.grid.Panel', {
			store : store_grid_record,
			columnLines : true,
			rowLines : true,
			loadMask : true,
			viewConfig : {
				stripeRows : true,
				enableTextSelection : true
			},
			columns : [ {
				text : '工号',
				dataIndex : 'agent_code',
			}, {
				text : '文件名称',
				dataIndex : 'file_name',
				selectable : true,
				width : 300,
			}, {
				text : '源主叫',
				dataIndex : 'orig_caller_number'
			}, {
				text : '主叫',
				dataIndex : 'caller_number'
			}, {
				text : '源被叫',
				dataIndex : 'orig_called_number'
			}, {
				text : '被叫',
				dataIndex : 'called_number'
			}, {
				text : '开始时间',
				dataIndex : 'start_time',
				width : 120,
			}, {
				text : '结束时间',
				dataIndex : 'end_time',
				width : 120,
			}, {
				text : '质检',
				width : 50,
				renderer : function(value, cellmeta, record, rowIndex, columnIndex, store) {
					var result = "<font color='red'>否</font>";
					if(record.data["is_check"] == 1) {
						result = "<font color='greed'>是</font>";
					}
					return result;
				}
			}, {
				text : '操作',
				sortable : false,
				renderer : function(value, cellmeta, record, rowIndex, columnIndex, store) {
					var str = '';
					str += '<a href="#" onClick="open_audition_record_window(\'' + record.data["file_name"] + '\')">试听</a>&nbsp;&nbsp;';
					return str;
				}
			} ],
			bbar : Ext.create('Ext.toolbar.Paging', {
				store : store_grid_record,
				displayInfo : true
			}),
			tbar : Ext.create('Ext.toolbar.Toolbar', {
				store : store_grid_record,
				items : [ {
					xtype : 'textfield',
					id : 'input_agent_code',
					fieldLabel : '工号',
					labelWidth : 30,
					labelAlign : 'right',
					width : 80,
				}, {
					xtype : 'textfield',
					id : 'input_orig_caller_number',
					fieldLabel : '源主叫',
					labelWidth : 40,
					labelAlign : 'right',
					width : 130,
				}, {
					xtype : 'textfield',
					id : 'input_caller_number',
					fieldLabel : '主叫',
					labelWidth : 30,
					labelAlign : 'right',
					width : 120,
				}, {
					xtype : 'textfield',
					id : 'input_orig_called_number',
					fieldLabel : '源被叫',
					labelWidth : 40,
					labelAlign : 'right',
					width : 130,
				}, {
					xtype : 'textfield',
					id : 'input_called_number',
					fieldLabel : '被叫',
					labelWidth : 30,
					labelAlign : 'right',
					width : 120,
				}, {
					xtype : 'datefield',
					id : 'input_start_time',
					fieldLabel : '时间起',
					format : 'Y-m-d',
					labelWidth : 40,
					selectOnFocus : true,
					labelAlign : 'right',
					width : 150,
				}, {
					xtype : 'datefield',
					id : 'input_end_time',
					fieldLabel : '时间止',
					format : 'Y-m-d',
					labelWidth : 40,
					selectOnFocus : true,
					labelAlign : 'right',
					width : 150,
				}, {
					xtype : 'textfield',
					id : 'input_start_length',
					fieldLabel : '通话时长',
					labelWidth : 60,
					labelAlign : 'right',
					width : 100,
				}, {
					xtype : 'textfield',
					id : 'input_end_length',
					fieldLabel : '至',
					labelWidth : 15,
					labelAlign : 'right',
					width : 55,
				}, '->', {
					text : '搜索',
					icon : '/ccdms/extui/resources/icons/magnifier.png',
					handler : function() {
						store_grid_record.on('beforeload', function() {
							Ext.apply(store_grid_record.proxy.extraParams, {
								agent_code : Ext.getCmp('input_agent_code').getValue(),
								orig_caller_number : Ext.getCmp('input_orig_caller_number').getValue(),
								caller_number : Ext.getCmp('input_caller_number').getValue(),
								orig_called_number : Ext.getCmp('input_orig_called_number').getValue(),
								called_number : Ext.getCmp('input_called_number').getValue(),
								start_time : Ext.Date.format(Ext.getCmp('input_start_time').getValue(), 'Y-m-d H:i:s'),
								end_time : Ext.Date.format(Ext.getCmp('input_end_time').getValue(), 'Y-m-d H:i:s'),
								start_length : Ext.getCmp('input_start_length').getValue(),
								end_length : Ext.getCmp('input_end_length').getValue(),
							});
						});
						store_grid_record.loadPage(1);
						//store_grid_record.load();
					}
				}, {
					text : '重置',
					icon : '/ccdms/extui/resources/icons/arrow_rotate_clockwise.png',
					handler : function() {
						Ext.getCmp('input_agent_code').setValue('');
						Ext.getCmp('input_orig_caller_number').setValue('');
						Ext.getCmp('input_caller_number').setValue('');
						Ext.getCmp('input_orig_called_number').setValue('');
						Ext.getCmp('input_called_number').setValue('');
						Ext.getCmp('input_start_time').setValue('');
						Ext.getCmp('input_end_time').setValue('');
						Ext.getCmp('input_start_length').setValue('');
						Ext.getCmp('input_end_length').setValue('');
					},
				} ]
			}),
		});
		grid_record.show();
		//--
		viewport_main = Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : [ grid_record ],
		});
	});
	//--
	store_grid_record = Ext.create('Ext.data.Store', {
		model : 'model_record',
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : '/ccdms/listRecordDataWin.do',
			reader : {
				type : 'json',
				root : 'rows',
				totalProperty : 'total'
			},
		}
	});
	//--

	function open_audition_record_window(fname) {
	//	alert('fname='+fname);
		Ext.Ajax.request({
			url : '/ccdms/auditionRecordDataWin.do?file_path=' + fname,
			success : function(resp, opts) {
				var result = Ext.decode(resp.responseText);
				if (result.return_status == 'success') {
					window_audition_record = Ext.create('Ext.window.Window', {
						title : '录音播放',
						autoShow : true,
						width : 400,
						height : 150,
						//modal : true,
						html : '<embed name="audioplayer" id="audioplayer" src="'+result.out_file_path+'" type="audio/x-wav" hidden="false"  width="400" height="150"  autostart="true" loop="false" /></embed/>',
					});
				}
			}
		});
	}
</script>
</head>
<body>

</body>
</html>