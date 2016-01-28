package com.helphand.ccdms.actions;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.report_column_bean;
import com.helphand.ccdms.beans.report_model_bean;
import com.helphand.ccdms.commons.common_media_converts;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.commons_records_list;
import com.helphand.ccdms.tables.eaphelp.agentidvsname;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_result_count_info;
import com.helphand.ccdms.tables.eaphelp.callout_script;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class manage_record_data extends ActionSupport {
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private List<report_model_bean> modelBeanList = null;
	private List<report_column_bean> columnBeanList = null;
	private int page, limit;
	private String sql = null;

	private report_model_bean model_bean = null;
	private report_column_bean column_bean = null;
	private JSONObject result_json = null;
	private String result_string = null;

	private commons_records_list recording = null;
	private String caller_number = null;
	private String caller_number_trait = null;
	private String called_number_trait = null;
	private String orig_caller_number = null;
	private String called_number = null;
	private String orig_called_number = null;
	private String orig_called_number_trait = null;// 源被叫的前缀特征
	private String agent_code = null;
	private String start_time = null;
	private String end_time = null;
	private String file_path = null;
	private Integer start_length = null;
	private Integer end_length = null;
	
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String init_list_record_data() {
		try {

		} catch (Exception e) {
			// TODO: handle exception
		}
		return SUCCESS;
	}
	

	// 列举录音记录
	public String list_record_data() {
		result_json = new JSONObject();
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();

			// 删除没有时间的录音
			/*
			 * sql =
			 * "delete from commons_records_list where (end_time = ' ' or start_time = ' ')"
			 * ; daos.execute_sql(system_session, sql);
			 */

			sql = "from commons_records_list where 1=1 ";
			if (StringUtils.isNotBlank(agent_code))
				sql += "and agent_code='" + agent_code + "' ";
			if (StringUtils.isNotBlank(called_number))
				sql += "and called_number='" + called_number + "' ";
			if (StringUtils.isNotBlank(orig_called_number))
				sql += "and orig_called_number='" + orig_called_number + "' ";
			if (StringUtils.isNotBlank(caller_number))
				sql += "and caller_number='" + caller_number + "' ";
			if (StringUtils.isNotBlank(orig_caller_number))
				sql += "and orig_caller_number='" + orig_caller_number + "' ";
			if (StringUtils.isNotBlank(start_time))
				sql += "and start_time>'" + start_time + "' ";
			if (StringUtils.isNotBlank(end_time))
				sql += "and start_time<'" + end_time + "' ";
			if (StringUtils.isNotBlank(orig_called_number_trait))
				sql += "and orig_called_number like '"
						+ orig_called_number_trait + "%' ";
			if (StringUtils.isNotBlank(caller_number_trait))
				sql += "and caller_number like '" + caller_number_trait + "%' ";
			if (StringUtils.isNotBlank(called_number_trait))
				sql += "and called_number like '" + called_number_trait + "%' ";

			String sql_time_length = "";
			if (start_length != null && end_length != null) {
				sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) between "
						+ start_length + " and " + end_length + " ";
			} else if (start_length != null) {
				sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) >="
						+ start_length + " ";
			} else if (end_length != null) {
				sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) <="
						+ end_length + " ";
			}
			sql += sql_time_length;
			sql += "order by id desc";
			commons_records_list o=new commons_records_list();
			page_object = daos.select_pages(system_session, sql, page, limit,o);
			page_object = page_object == null ? new HibernatePageDemo()
					: page_object;
			list = page_object.getList();
			list = list == null ? new ArrayList<Object>() : list;
//			result_json.put("rows", list);
//			result_json.put("total", page_object.getTotal_record_count());
			page_json = new HibernatePageJson();
			page_json.setRows(list);
			page_json.setTotal(page_object.getTotal_record_count());
			result_json = JSONObject.fromObject(page_json);
			System.out.println("page_json was be callback");
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	public String inti_list_time_record() {		
		return SUCCESS;
	}
	
	public String time_record_column() {
		// 封装Model、Column
		modelBeanList = new ArrayList<report_model_bean>();
		columnBeanList = new ArrayList<report_column_bean>();
		String start_time = "start_time";
		String end_time = "end_time";
		String agent_code = "agent_code";
		String aparts = "aparts";		
		String orig_caller_number = "orig_caller_number";
		String caller_number = "caller_number";
		String orig_called_number = "orig_called_number";
		String called_number = "called_number";

		// model
		model_bean = new report_model_bean();
		model_bean.setName(agent_code);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(orig_caller_number);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(caller_number);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(orig_called_number);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(called_number);
		model_bean.setType("string");
		modelBeanList.add(model_bean);		
		model_bean = new report_model_bean();
		model_bean.setName(start_time);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(end_time);
		model_bean.setType("string");
		modelBeanList.add(model_bean);
		model_bean = new report_model_bean();
		model_bean.setName(aparts);
		model_bean.setType("string");
		modelBeanList.add(model_bean);

		// column
		column_bean = new report_column_bean();
		column_bean.setText("工号");
		column_bean.setDataIndex(agent_code);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("开始时间");
		column_bean.setDataIndex(start_time);
		column_bean.setWidth(180);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("结束时间");
		column_bean.setDataIndex(end_time);
		column_bean.setWidth(180);
		columnBeanList.add(column_bean);		
		column_bean = new report_column_bean();
		column_bean.setText("源主叫");
		column_bean.setDataIndex(orig_caller_number);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("主叫");
		column_bean.setDataIndex(caller_number);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("源被叫");
		column_bean.setDataIndex(orig_called_number);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("被叫");
		column_bean.setDataIndex(called_number);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);
		column_bean = new report_column_bean();
		column_bean.setText("通话时长");
		column_bean.setDataIndex(aparts);
		column_bean.setWidth(100);
		columnBeanList.add(column_bean);

		result_json = new JSONObject();
		result_json.put("columnBeanList", columnBeanList);
		result_json.put("modelBeanList", modelBeanList);

		return SUCCESS;
	}
	
	public String list_time_record_old() {
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();			
						
			// 获取脚本信息
			sql = "from commons_records_list where 1=1 ";
			if (StringUtils.isNotBlank(agent_code))
				sql += "and agent_code='" + agent_code + "' ";
			if (StringUtils.isNotBlank(called_number))
				sql += "and called_number='" + called_number + "' ";
			if (StringUtils.isNotBlank(orig_called_number))
				sql += "and orig_called_number='" + orig_called_number + "' ";
			if (StringUtils.isNotBlank(caller_number))
				sql += "and caller_number='" + caller_number + "' ";
			if (StringUtils.isNotBlank(orig_caller_number))
				sql += "and orig_caller_number='" + orig_caller_number + "' ";
			if (StringUtils.isNotBlank(start_time))
				sql += "and start_time>'" + start_time + " 00:00:00' ";
			if (StringUtils.isNotBlank(end_time))
				sql += "and start_time<'" + end_time + " 23:59:59' ";
			if (StringUtils.isNotBlank(orig_called_number_trait))
				sql += "and orig_called_number like '"
						+ orig_called_number_trait + "%' ";
			if (StringUtils.isNotBlank(caller_number_trait))
				sql += "and caller_number like '" + caller_number_trait + "%' ";
			if (StringUtils.isNotBlank(called_number_trait))
				sql += "and called_number like '" + called_number_trait + "%' ";
			
			sql += "order by agent_code desc";
			list = daos.select_list(system_session, sql);
			
			recording = new commons_records_list();
			page_object = daos.select_pages(system_session, sql, page, limit,
					recording);
			page_object = (page_object == null ? new HibernatePageDemo()
					: page_object);
			list = page_object.getList();
			if ((list != null) && (list.size() > 0)) {
				List<Object> resultList = new ArrayList<Object>();
				for (int i = 0; i < this.list.size(); i++) {
					StringBuffer str = new StringBuffer();

					recording = (commons_records_list) list.get(i);
					
					Date start_date = df.parse(recording.getStart_time());
					Date end_date = df.parse(recording.getEnd_time());					
					int aparts = (int)(end_date.getTime()-start_date.getTime())/1000;
					
					String orig_caller_number = recording.getOrig_caller_number();
					if(orig_caller_number == null){
						orig_caller_number = "";
					}
					String orig_called_number = recording.getOrig_called_number();
					if(orig_called_number == null){
						orig_called_number = "";
					}
					str.append("{");
					str.append("agent_code:'" + recording.getAgent_code() + "',");
					str.append("orig_caller_number:'" + orig_caller_number + "',");
					str.append("caller_number:'" + recording.getCaller_number() + "',");
					str.append("orig_called_number:'" + orig_called_number + "',");
					str.append("called_number:'" + recording.getCalled_number() + "',");
					str.append("start_time:'" + recording.getStart_time() + "',");
					str.append("end_time:'" + recording.getEnd_time() + "',");
					str.append("aparts:'" + aparts + "',");
					str.append("}");

					String temp = str.toString().replaceAll("\\n", "");
					resultList.add(temp);
				}

				page_json = new HibernatePageJson();
				page_json.setTotal(page_object.getTotal_record_count());
				page_json.setRows(resultList);
				result_json = JSONObject.fromObject(page_json);
			} else {
				result_json = new JSONObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}

		return SUCCESS;
	}
	
	public String list_time_record() {
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();			
						
			// 获取脚本信息
			sql = "select distinct(agent_code) from commons_records_list where 1=1 ";
			String temp = "";
			if (StringUtils.isNotBlank(agent_code))
				sql += "and agent_code='" + agent_code + "' ";
			if (StringUtils.isNotBlank(called_number))
				sql += "and called_number='" + called_number + "' ";
				temp += "and called_number='" + called_number + "' ";
			if (StringUtils.isNotBlank(orig_called_number))
				sql += "and orig_called_number='" + orig_called_number + "' ";
				temp += "and orig_called_number='" + orig_called_number + "' ";
			if (StringUtils.isNotBlank(caller_number))
				sql += "and caller_number='" + caller_number + "' ";
				temp += "and caller_number='" + caller_number + "' ";
			if (StringUtils.isNotBlank(orig_caller_number))
				sql += "and orig_caller_number='" + orig_caller_number + "' ";
				temp += "and orig_caller_number='" + orig_caller_number + "' ";
			if (StringUtils.isNotBlank(start_time))
				sql += "and start_time>'" + start_time + " 00:00:00' ";
				temp += "and start_time>'" + start_time + " 00:00:00' ";
			if (StringUtils.isNotBlank(end_time))
				sql += "and start_time<'" + end_time + " 23:59:59' ";
				temp += "and start_time<'" + end_time + " 23:59:59' ";
			if (StringUtils.isNotBlank(orig_called_number_trait))
				sql += "and orig_called_number like '"
						+ orig_called_number_trait + "%' ";
				temp += "and orig_called_number like '"
				+ orig_called_number_trait + "%' ";
			if (StringUtils.isNotBlank(caller_number_trait))
				sql += "and caller_number like '" + caller_number_trait + "%' ";
				temp += "and caller_number like '" + caller_number_trait + "%' ";
			if (StringUtils.isNotBlank(called_number_trait))
				sql += "and called_number like '" + called_number_trait + "%' ";
				temp += "and called_number like '" + called_number_trait + "%' ";
			
			sql += "order by agent_code desc";
			list = daos.select_list(system_session, sql);
			
			recording = new commons_records_list();
			page_object = daos.select_page_lm(system_session, sql, page, limit,
					recording);
			page_object = (page_object == null ? new HibernatePageDemo()
					: page_object);
			list = page_object.getList();
			if ((list != null) && (list.size() > 0)) {
				List resultList = new ArrayList();
				List tempList = new ArrayList<Object>();
				for (int i=0; i < list.size(); i++) {
					StringBuffer str = new StringBuffer();
					String agent_code = (String) list.get(i);
					sql = "from commons_records_list where agent_code='" + agent_code + "' " + temp;
					recording = new commons_records_list();
					tempList = daos.select_list(system_session, sql, recording);
					if ((tempList != null) && (tempList.size() > 0)) {
						int aparts = 0;						
						for (int j=0; j < tempList.size(); j++) {
							recording = (commons_records_list) tempList.get(i);
							Date start_date = df.parse(recording.getStart_time());
							Date end_date = df.parse(recording.getEnd_time());					
							aparts += (int)(end_date.getTime()-start_date.getTime())/1000;
							orig_caller_number = recording.getOrig_caller_number();
							if(orig_caller_number == null){
								orig_caller_number = "";
							}
							caller_number = recording.getCaller_number();
							orig_called_number = recording.getOrig_called_number();
							if(orig_called_number == null){
								orig_called_number = "";
							}
							called_number = recording.getCalled_number();
							start_time = recording.getStart_time();
							end_time = recording.getEnd_time();
						}
						str.append("{");
						str.append("agent_code:'" + recording.getAgent_code() + "',");
						str.append("orig_caller_number:'" + orig_caller_number + "',");
						str.append("caller_number:'" + recording.getCaller_number() + "',");
						str.append("orig_called_number:'" + orig_called_number + "',");
						str.append("called_number:'" + recording.getCalled_number() + "',");
						str.append("start_time:'" + recording.getStart_time() + "',");
						str.append("end_time:'" + recording.getEnd_time() + "',");
						str.append("aparts:'" + aparts + "',");
						str.append("}");

						String temps = str.toString().replaceAll("\\n", "");
						resultList.add(temp);
					}					
				}

				page_json = new HibernatePageJson();
				page_json.setTotal(page_object.getTotal_record_count());
				page_json.setRows(resultList);
				result_json = JSONObject.fromObject(page_json);
			} else {
				result_json = new JSONObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}

		return SUCCESS;
	}
	
	public void record_time_excel() throws Exception {
		DateFormat format = null;
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();

			sql = "from commons_records_list where 1=1 ";
			if (StringUtils.isNotBlank(agent_code))
				sql += "and agent_code='" + agent_code + "' ";
			if (StringUtils.isNotBlank(called_number))
				sql += "and called_number='" + called_number + "' ";
			if (StringUtils.isNotBlank(orig_called_number))
				sql += "and orig_called_number='" + orig_called_number + "' ";
			if (StringUtils.isNotBlank(caller_number))
				sql += "and caller_number='" + caller_number + "' ";
			if (StringUtils.isNotBlank(orig_caller_number))
				sql += "and orig_caller_number='" + orig_caller_number + "' ";
			if (StringUtils.isNotBlank(start_time))
				sql += "and start_time>'" + start_time + " 00:00:00' ";
			if (StringUtils.isNotBlank(end_time))
				sql += "and start_time<'" + end_time + " 23:59:59' ";
			if (StringUtils.isNotBlank(orig_called_number_trait))
				sql += "and orig_called_number like '"
						+ orig_called_number_trait + "%' ";
			if (StringUtils.isNotBlank(caller_number_trait))
				sql += "and caller_number like '" + caller_number_trait + "%' ";
			if (StringUtils.isNotBlank(called_number_trait))
				sql += "and called_number like '" + called_number_trait + "%' ";

			sql += "order by agent_code desc";
			list = daos.select_list(system_session, sql);

			// EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context
					.get(ServletActionContext.HTTP_RESPONSE);

			Calendar now = Calendar.getInstance();
			format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());

			OutputStream os = response.getOutputStream();
			// OutputStream os = new FileOutputStream(new
			// File("./"+fileName+".xls"));
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename="
					+ fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型

			// 添加工作表
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("录音时长查询记录", 0);

			// excel工作表的标题
			Label label = null;
			label = new jxl.write.Label(0, 0, "工号");
			sheet.addCell(label);
			label = new jxl.write.Label(1, 0, "开始时间");
			sheet.addCell(label);
			label = new jxl.write.Label(2, 0, "结束时间");
			sheet.addCell(label);
			label = new jxl.write.Label(3, 0, "源主叫");
			sheet.addCell(label);
			label = new jxl.write.Label(4, 0, "主叫");
			sheet.addCell(label);
			label = new jxl.write.Label(5, 0, "源被叫");
			sheet.addCell(label);
			label = new jxl.write.Label(6, 0, "被叫");
			sheet.addCell(label);
			label = new jxl.write.Label(7, 0, "通话时长");
			sheet.addCell(label);

			// 表数据
			for (int i = 0; i < list.size(); i++) {
				recording = (commons_records_list) list.get(i);
				
				String orig_caller_number = recording.getOrig_caller_number();
				if(orig_caller_number == null){
					orig_caller_number = "";
				}
				String orig_called_number = recording.getOrig_called_number();
				if(orig_called_number == null){
					orig_called_number = "";
				}
				
				Date start_date = df.parse(recording.getStart_time());
				Date end_date = df.parse(recording.getEnd_time());
				
				int aparts = (int)(end_date.getTime()-start_date.getTime())/1000;
				
				// 第二行开始
				label = new Label(0, i + 1, recording.getAgent_code());
				sheet.addCell(label);
				label = new Label(1, i + 1, recording.getStart_time() + " ");
				sheet.addCell(label);
				label = new Label(2, i + 1, recording.getEnd_time() + " ");
				sheet.addCell(label);
				label = new Label(3, i + 1, orig_caller_number);
				sheet.addCell(label);
				label = new Label(4, i + 1, recording.getCaller_number());
				sheet.addCell(label);
				label = new Label(5, i + 1, orig_called_number);
				sheet.addCell(label);
				label = new Label(6, i + 1, recording.getCalled_number());
				sheet.addCell(label);
				label = new Label(7, i + 1, aparts + "");
				sheet.addCell(label);
			}
			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}

	// 试听录音
	public String audition_record_data() {
		result_json = new JSONObject();
		String in_file_path = null;
		String out_file_path = null;
		try {
			in_file_path = tools.get_init_param("records_real_path");
			in_file_path += file_path;

			new common_media_converts(in_file_path, null, 1, 6000, 1);
			//out_file_path = tools.get_init_param("records_virtual_path") + "/";
			//out_file_path += file_path.substring(0, file_path.lastIndexOf("."))
			//		+ ".wav";
			result_json.put("return_status", "success");
			//result_json.put("out_file_path", out_file_path);
			result_json.put("out_file_path", file_path);
		} catch (Exception e) {
			result_json.put("return_status", "failrue");
			result_json.put("return_describe", e.getMessage());
		}
		return SUCCESS;
	}

	// 下载录音文件
	public String download_record_data() {
		result_json = new JSONObject();
		String out_file_path = null;
		try {
			out_file_path = "";
			String boolen=file_path.substring(0,3);
			if(!boolen.equals("ftp"))
			{
				out_file_path+="172.1.1.171:8090";
				out_file_path += tools.get_init_param("records_virtual_path") + "/";
			}
			out_file_path += file_path;
			result_json.put("return_status", "success");
			result_json.put("out_file_path", out_file_path);
		} catch (Exception e) {
			result_json.put("return_status", "failrue");
			result_json.put("return_describe", e.getMessage());
		}
		return SUCCESS;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}

	public String getCaller_number() {
		return caller_number;
	}

	public void setCaller_number(String caller_number) {
		this.caller_number = caller_number;
	}

	public String getOrig_caller_number() {
		return orig_caller_number;
	}

	public void setOrig_caller_number(String orig_caller_number) {
		this.orig_caller_number = orig_caller_number;
	}

	public String getCalled_number() {
		return called_number;
	}

	public void setCalled_number(String called_number) {
		this.called_number = called_number;
	}

	public String getOrig_called_number() {
		return orig_called_number;
	}

	public void setOrig_called_number(String orig_called_number) {
		this.orig_called_number = orig_called_number;
	}

	public String getAgent_code() {
		return agent_code;
	}

	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public HibernatePageJson getPage_json() {
		return page_json;
	}

	public void setPage_json(HibernatePageJson page_json) {
		this.page_json = page_json;
	}

	public String getResult_string() {
		return result_string;
	}

	public void setResult_string(String result_string) {
		this.result_string = result_string;
	}

	public commons_records_list getRecording() {
		return recording;
	}

	public void setRecording(commons_records_list recording) {
		this.recording = recording;
	}

	public String getOrig_called_number_trait() {
		return orig_called_number_trait;
	}

	public void setOrig_called_number_trait(String orig_called_number_trait) {
		this.orig_called_number_trait = orig_called_number_trait;
	}

	public String getCaller_number_trait() {
		return caller_number_trait;
	}

	public void setCaller_number_trait(String caller_number_trait) {
		this.caller_number_trait = caller_number_trait;
	}

	public Integer getStart_length() {
		return start_length;
	}

	public void setStart_length(Integer start_length) {
		this.start_length = start_length;
	}

	public Integer getEnd_length() {
		return end_length;
	}

	public void setEnd_length(Integer end_length) {
		this.end_length = end_length;
	}

	public String getCalled_number_trait() {
		return called_number_trait;
	}

	public void setCalled_number_trait(String called_number_trait) {
		this.called_number_trait = called_number_trait;
	}

}
