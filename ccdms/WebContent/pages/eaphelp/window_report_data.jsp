<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/ccdms/extui/resources/css/ext-all.css">
<script type="text/javascript" src="/ccdms/extui/ext-all.js"></script>
<script type="text/javascript" src="/ccdms/extui/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
	var agentid = '<%=request.getParameter("agentId")%>';
</script>
<script type="text/javascript">
	var store_report_name_data;
	var store_report_column_data;
	var store_script_options_data;
	
	//初始化
	Ext.onReady(function() {
		Ext.define('model_report_name', {
			extend : 'Ext.data.Model',
			fields : [{
				name : 'id',
				type : 'int'
			}, {
				name : 'script_id',
				type : 'int'
			}, {
				name : 'script_name',
				type : 'string'
			}]
		});
		store_report_name_data = Ext.create('Ext.data.Store', {
			pageSize : 10,
			model : model_report_name,
			autoLoad : true,
			proxy : {
				type : 'ajax',
				url : '/ccdms/listReportNameWindow.do',
				reader : {
					type : 'json',
					root : 'rows',
					totalProperty : 'total'
				}
			}
		});
		//------------------------------------------
		Ext.define('model_report_column', {
			extend : 'Ext.data.Model',
			fields : [{
				name : 'id',
				type : 'int'
			}, {
				name : 'callout_time',
				type : 'string'
			}, {
				name : 'agent_id',
				type : 'string'
			}, {
				name : 'callout_phone',
				type : 'string'
			}, {
				name : 'script_name',
				type : 'string'
			}, {
				name : 'script_id',
				type : 'int'
			}, {
				name : 'result_describe',
				type : 'string'
			}]
		});
		store_report_column_data = Ext.create('Ext.data.Store', {
			pageSize : 20,
			model : model_report_column,
			autoLoad : true,
			proxy : {
				type : 'ajax',
				url : '/ccdms/listCalloutResultWindow.do',
				actionMethods: {
			        read : 'post'
			    },
				reader : {
					type : 'json',
					root : 'rows',
					totalProperty : 'total'
				}
			}
		});
		//-------------------------------------
		Ext.define('model_script_options', {
			extend : 'Ext.data.Model',
			fields : [{
				name : 'id',
				type : 'int'
			}, {
				name : 'describe',
				type : 'string'
			}]
		});
		store_script_options_data = Ext.create('Ext.data.Store', {
			model : model_script_options,
			autoLoad : false,
			proxy : {
				type : 'ajax',
				url : '/ccdms/listScriptOptionsWindow.do',
				reader : {
					type : 'json',
					root : 'rows'
				}
			}
		});
		//---------------------------------------
		var toolbar_search = Ext.create('Ext.toolbar.Toolbar',{
			items:[
			    '脚本名称:', {
			    	xtype : 'combo',
			    	id : 'input_script',
			    	displayField : 'script_name',
					valueField : 'id',
					queryMode: 'local',
					store : store_report_name_data,
					flex : 1,
					onChange : function(value, beforeValue) {
						store_script_options_data.on('beforeload', function(store) {
							Ext.apply(store.proxy.extraParams, {
								'script_id' : value
							});
						});
						Ext.getCmp('input_list_result').setValue("");
						store_script_options_data.reload();
					},
					listeners : {
	                    beforequery : function(e){
	                    	var combo = e.combo;
	                		if(!e.forceAll){
	                			var value = e.query;
	                			combo.store.filterBy(function(record,id){
	                				var text = record.get(combo.displayField);
	                				return (text.indexOf(value)!=-1);
	                			});
	                			combo.expand();
	                			return false;
	                		}
	                    }
	                }
			    }, '执行结果:', {
			    	xtype : 'combo',
			    	id : 'input_list_result',
			    	displayField : 'describe',
					valueField : 'describe',
					store : store_script_options_data,
					queryMode: 'local',
					flex : 1,
					listeners : {
	                    beforequery : function(e){
	                    	var combo = e.combo;
	                		if(!e.forceAll){
	                			var value = e.query;
	                			combo.store.filterBy(function(record,id){
	                				var text = record.get(combo.displayField);
	                				return (text.indexOf(value)!=-1);
	                			});
	                			combo.expand();
	                			return false;
	                		}
	                    }
	                }
			    }, '工号:', {
			    	xtype : 'field',
			    	id : 'input_agent_id',
			    	width : 60,
			    	readOnly : true,
			    	value : agentid
			    }, '号码:', {
			    	xtype : 'field',
			    	id : 'input_callout_phone',
			    	width : 110
			    },'日期:', {
			    	xtype : 'datefield',
			    	id: 'input_start_date',
			    	format : 'Y-m-d',
			    	flex : 1
			    }, '至', {
			    	xtype : 'datefield',
			    	id: 'input_end_date',
			    	format : 'Y-m-d',
			    	flex : 1
			    }, {
			    	text:'搜索',
			    	handler:function(){
						search();
					}
			    }
			]
		});
		//------------------------------------------
		var grid_report = Ext.create('Ext.grid.Panel', {
			tbar : toolbar_search,
			stripeRows : true,
			store : store_report_column_data,
			columnLines : true,
			loadMask : true,
		    columns : [{
		    	text : 'ID',
		    	dataIndex : 'id',
		    	width : 80
		    }, {
		    	text : '时间',
		    	dataIndex : 'callout_time',
		    	width : 180
		    }, {
		    	text : '脚本ID',
		    	dataIndex : 'script_id',
		    	width : 80
		    }, {
		    	text : '脚本名称',
		    	dataIndex : 'script_name',
		    	width : 200
		    }, {
		    	text : '工号',
		    	dataIndex : 'agent_id',
		    	width : 150
		    }, {
		    	text : '用户号码',
		    	dataIndex : 'callout_phone',
		    	width : 150
		    }, {
		    	text : '结果',
		    	dataIndex : 'result_describe',
		    	flex : 1
		    }, {
		    	text : '操作',
		    	align : 'right',
		    	width : 150,
		    	renderer : function(value, cellmeta, record, rowIndex, columnIndex, store) {
					var str = '';
					str += '<a href="#" onClick=show_window('+ record.data["id"] +',"'+ record.data["callout_phone"] +'")>修改</a>';
					return str;
				}
		    }],
		    bbar : Ext.create('Ext.toolbar.Paging', {
				store : store_report_column_data,
				displayInfo : true
			})
		});
		//------------------------------------------
		var viewport_main = Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : [ grid_report ],
			renderTo : Ext.getBody()
		});
	});
	
	//查询
	function search(){
		var script_id = Ext.getCmp('input_script').getValue();
		var agent_id = Ext.getCmp('input_agent_id').getValue();
		var start_date = Ext.getCmp('input_start_date').getValue();
		var end_date = Ext.getCmp('input_end_date').getValue();
		var callout_phone = Ext.getCmp('input_callout_phone').getValue();
		start_date = Ext.util.Format.date(start_date, 'Y-m-d');
		end_date = Ext.util.Format.date(end_date, 'Y-m-d');
		var options = Ext.getCmp('input_list_result').getValue();
		
		store_report_column_data.proxy.extraParams = {
			"script_id"     : script_id,
			"agent_id"      : agent_id,
			"start_date"    : start_date,
			"end_date"      : end_date,
			"callout_phone" : callout_phone,
			'options'       : options
		};
		store_report_column_data.reload();
	}
	
	//显示表单
	function show_window(result_id, user_phone) {
		Ext.Ajax.request({
			url : '/ccdms/showScriptDataInfoWindow.do',
			params : {
				'result_id' : result_id,
				'channel_bean.origcalled' : user_phone
			},
			success : function(resp, ops) {
				var result = Ext.JSON.decode(resp.responseText);
				if (result.resp == 'success') {
					var html_form = "<form name='form_result' method='post' action=''>" + result.html + "</form>";
					var window_view_script = Ext.create('Ext.window.Window', {
						title : result.callout_result.script_name + ":" + result.callout_result.callout_phone,
						width : 700,
						height : 450,
						autoScroll : true,
						modal : true,
						html : html_form,
						resizable : false,
						bodyPadding : 10,
						buttonAlign : 'center',
						buttons : [{
							id : 'saveBtn',
							text : '提交',
							handler : function() {
								Ext.getCmp('saveBtn').setDisabled(true);
								var form = document.form_result;
								var result_describe = "";
								var options = document.getElementsByName("option_result");
								var option_result = options.length > 0 ? options[0].value : "";
								
								for(var i=0; i<form.length; i++) {
									if(form[i].name != null && form[i].name != "" && form[i].name != "undifend") {
										var result_param = "";
										
										if (form[i].type == 'radio') {
											var opts = document.getElementsByName(form[i].name);
											for (var j=0; j<opts.length; j++) {
												if (opts[j].checked) {
													result_param = opts[j].value;
												}
												i++;
											}
											
											if(opts.length > 0) i--;
										} else if (form[i].type == 'checkbox') {
											var opts = document.getElementsByName(form[i].name);
											for (var j=0; j<opts.length; j++) {
												if (opts[j].checked) {
													result_param += (opts[j].value + "|");
												}
												i++;
											}
											
											if(opts.length > 0) i--;
										} else if (form[i].type == 'text') {
											result_param = form[i].value;
										}
										
										//执行结果
										if(form[i].nodeName == "SELECT") continue;
										result_param = result_param.replace(/;/g,"；");//替换成中文符号
										result_param = result_param.replace(/:/g,"：");
										result_describe += result_param + ";";
									}
								}
								
								Ext.Ajax.request({
									url : '/ccdms/saveCalloutResultWindow.do',
									params : {
										'callout_result.id'                 : result_id,
										'callout_result.result_describe'    : result_describe,
										'options'                           : option_result
									},
									success : function(resp, ops) {
										var result = Ext.JSON.decode(resp.responseText);
										if (result.resp == 'success') {
											alert(result.describe);
											store_report_column_data.reload();
											window_view_script.close();
										} else {
											alert(result.describe);
										}
									}
								});
							}
						}]
					}).show();
				} else {
					alert(result.describe);
				}
			}
		});
	}
</script>
</head>
<body>
</body>
</html>