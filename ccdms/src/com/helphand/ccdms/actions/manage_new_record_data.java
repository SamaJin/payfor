package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_media_converts;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.*;
import com.helphand.ccdms.tables.*;
import com.opensymphony.xwork2.*;

@SuppressWarnings("serial")
public class manage_new_record_data extends ActionSupport {
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private int page, limit;
	private String sql = null;

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
    private String search_flag = null;//查询模块标示 
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
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			
			//删除没有时间的录音
			/*sql = "delete from commons_records_list where (end_time = ' ' or start_time = ' ')";
			daos.execute_sql(system_session, sql);*/
			
			sql = " from recordfilelist where 1=1 ";
			if (StringUtils.isNotBlank(agent_code)) sql += "and agentid='" + agent_code + "' ";   //工号
			if (StringUtils.isNotBlank(caller_number)) sql += "and callerid='" + caller_number + "' ";  // 主叫
			if (StringUtils.isNotBlank(orig_called_number)) sql += "and origcalledid='" + orig_called_number + "' ";//源主叫
			
			if (StringUtils.isNotBlank(called_number)) {
				if(search_flag.equals("2")){
					String[] tempArrayStrings = called_number.split(",");
					sql += " and to_number(agentid) >= " + tempArrayStrings[0] + " ";  // 被叫
					sql += " and to_number(agentid) <= " + tempArrayStrings[1] + " ";  // 被叫
					
				}else{
					sql += " and calledid='" + called_number + "' ";
				} 
			}
			if (StringUtils.isNotBlank(orig_caller_number)) sql += "and origcallerid='" + orig_caller_number + "' ";  //源被叫
			if (StringUtils.isNotBlank(start_time)) sql += "and starttime>'" + start_time + "' ";   //开始时间
			if (StringUtils.isNotBlank(end_time)) sql += "and starttime<'" + end_time + "' ";      //结束时间
			if (StringUtils.isNotBlank(orig_called_number_trait)) sql += "and origcalledid like '" + orig_called_number_trait + "%' ";//源被叫的前缀特征
			if (StringUtils.isNotBlank(caller_number_trait)) sql += "and callerid like '" + caller_number_trait + "%' ";
			if (StringUtils.isNotBlank(called_number_trait)) sql += "and calledid like '" + called_number_trait + "%' ";
			
			String sql_time_length = "";
			if (start_length != null && end_length != null) {
				sql_time_length = "and round(to_number(to_date(endtime,'yyyy-mm-dd hh24:mi:ss') - to_date(starttime,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) between "+start_length+" and "+end_length+" ";
			}else if(start_length != null) {
				sql_time_length = "and round(to_number(to_date(endtime,'yyyy-mm-dd hh24:mi:ss') - to_date(starttime,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) >="+start_length+" ";
			}else if(end_length != null) {
				sql_time_length = "and round(to_number(to_date(endtime,'yyyy-mm-dd hh24:mi:ss') - to_date(starttime,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) <="+end_length+" ";
			}
			sql += sql_time_length;
			sql += "order by agentid desc"; 
			page_object = daos.select_pages(system_session, sql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			list = list == null ? new ArrayList<Object>() : list;
			result_json.put("rows", list);
			result_json.put("total", page_object.getTotal_record_count());
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
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
			out_file_path = tools.get_init_param("records_virtual_path") + "/";
			out_file_path += file_path.substring(0, file_path.lastIndexOf(".")) + ".wav";
			result_json.put("return_status", "success");
			result_json.put("out_file_path", out_file_path);
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
			out_file_path += tools.get_init_param("records_virtual_path") + "/";
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

	public String getSearch_flag() {
		return search_flag;
	}

	public void setSearch_flag(String search_flag) {
		this.search_flag = search_flag;
	}
 
	
	
}
