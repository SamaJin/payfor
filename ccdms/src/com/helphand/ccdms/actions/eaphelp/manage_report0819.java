package com.helphand.ccdms.actions.eaphelp;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.report_column_bean;
import com.helphand.ccdms.beans.report_model_bean;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.tables.eaphelp.agentidvsname;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_script;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.helphand.ccdms.tables.eaphelp.callout_script_options;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 报表统计管理
 */
@SuppressWarnings("serial")
public class manage_report0819 extends ActionSupport {
	private Log log = LogFactory.getLog(manage_report0819.class);
	private HibernateDAO daos = new HibernateDAO();
	private common_tools tools = new common_tools();
	private Session dbs_session = null;
	private Transaction dbs_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private int limit, page;
	private List<Object> list = null;
	private String hql = null;

	private JSONObject result_json = null;
	private String result_string = null;
	private callout_result callout_result = null;

	private String tempStr = null;

	private String script_id;
	private report_model_bean model_bean = null;
	private report_column_bean column_bean = null;
	private List<report_model_bean> modelBeanList = null;
	private List<report_column_bean> columnBeanList = null;
	private Map<Integer, String> resultMap = null;// 通道对应的通道答案
	private Map<Integer, String> describeMap = null;// 问题是描述
	private Map<Integer, String> questionMap = null;// 问题是简答

	private String agent_id;
	private String start_date;
	private String end_date;
	private String callout_phone;
	private String options;

	private String callout_time;
	private int callNum;
	private int openNum;
	private String username;
	private String teamname;
	private String opens;

	private agentidvsname adn = null;

	public String init_report_list() {
		log.info("Entry manage_report[ init_report_list()]");
		return SUCCESS;
	}

	// 获取脚本列表
	public String list_report_name() {
		log.info("Entry manage_report[ list_report_name()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script where issue=1 order by create_time desc";
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if (list != null && list.size() > 0) {
				page_json = new HibernatePageJson();
				page_json.setTotal(list.size());
				page_json.setRows(list);
				result_json = JSONObject.fromObject(page_json);
			} else {
				result_json = new JSONObject();
			}

		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}

	// 获取脚本对应的结果列表
	public String list_script_options() {
		log.info("Entry manage_report[ list_script_options()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "select describe from callout_script_options where script_id="
					+ script_id;
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if (list != null && list.size() > 0) {
				String describe = String.valueOf(list.get(0));
				String[] results = StringUtils.split(describe, "||");

				list = new ArrayList<Object>();
				callout_script_options bean = null;
				for (int i = 0; i < results.length; i++) {
					bean = new callout_script_options();
					bean.setId(i);
					bean.setDescribe(results[i]);
					list.add(bean);
				}

				page_json = new HibernatePageJson();
				page_json.setRows(list);
				result_json = JSONObject.fromObject(page_json);
			} else {
				result_json = new JSONObject();
			}

		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}

	public String get_report_column() {
		this.log.info("Entry manage_report[ get_report_column()]");
		try {
			this.dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			this.dbs_transaction = this.dbs_session.beginTransaction();

			if (this.tools.isEmpty(this.script_id)) {
				this.result_string = "";
				return "success";
			}

			this.hql = ("from callout_script_detail where script_id=" + this.script_id);
			this.hql += " order by content_sort";
			this.log.info("exec hql : " + this.hql);
			this.list = this.daos.select_list(this.dbs_session, this.hql);

			this.modelBeanList = new ArrayList();
			this.columnBeanList = new ArrayList();
			if ((this.list != null) && (this.list.size() > 0)) {
				String callout_time = "callout_time";
				String agent_id = "agent_id";
				String called_id = "called_id";
				String callout_phone = "callout_phone";
				String list_result = "list_result";

				this.model_bean = new report_model_bean();
				this.model_bean.setName(callout_time);
				this.model_bean.setType("string");
				this.modelBeanList.add(this.model_bean);
				this.model_bean = new report_model_bean();
				this.model_bean.setName(agent_id);
				this.model_bean.setType("string");
				this.modelBeanList.add(this.model_bean);
				this.model_bean = new report_model_bean();
				this.model_bean.setName(called_id);
				this.model_bean.setType("string");
				this.modelBeanList.add(this.model_bean);
				this.model_bean = new report_model_bean();
				this.model_bean.setName(callout_phone);
				this.model_bean.setType("string");
				this.modelBeanList.add(this.model_bean);

				this.column_bean = new report_column_bean();
				this.column_bean.setText("时间");
				this.column_bean.setDataIndex(callout_time);
				this.column_bean.setWidth(180);
				this.columnBeanList.add(this.column_bean);
				this.column_bean = new report_column_bean();
				this.column_bean.setText("工号");
				this.column_bean.setDataIndex(agent_id);
				this.column_bean.setWidth(100);
				this.columnBeanList.add(this.column_bean);
				this.column_bean = new report_column_bean();
				this.column_bean.setText("用户号码");
				this.column_bean.setDataIndex(callout_phone);
				this.column_bean.setWidth(150);
				this.columnBeanList.add(this.column_bean);

				for (int i = 0; i < this.list.size(); i++) {
					callout_script_detail script = (callout_script_detail) this.list
							.get(i);
					String type = script.getContent_type();
					if ("describe".equals(type)) {
						continue;
					}
					String column = "q" + script.getContent_sort();
					String text = "问题" + script.getContent_sort();
					this.model_bean = new report_model_bean();
					this.model_bean.setName(column);
					this.model_bean.setType("string");
					this.modelBeanList.add(this.model_bean);

					this.column_bean = new report_column_bean();
					this.column_bean.setText(text);
					this.column_bean.setDataIndex(column);
					this.column_bean.setWidth(100);
					this.columnBeanList.add(this.column_bean);
				}

				this.model_bean = new report_model_bean();
				this.model_bean.setName(list_result);
				this.model_bean.setType("string");
				this.modelBeanList.add(this.model_bean);

				this.column_bean = new report_column_bean();
				this.column_bean.setText("执行结果");
				this.column_bean.setDataIndex(list_result);
				this.column_bean.setWidth(180);
				this.columnBeanList.add(this.column_bean);
			}

			this.result_json = new JSONObject();
			this.result_json.put("columnBeanList", this.columnBeanList);
			this.result_json.put("modelBeanList", this.modelBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			this.daos.rollback_transaction(this.dbs_transaction);
		} finally {
			this.daos.commit_transaction(this.dbs_transaction);
			this.daos.close_session(this.dbs_session);
		}

		return "success";
	}

	public String list_report_data() {
		this.log.info("Entry manage_report[ list_report_data()]");
		try {
			this.dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			this.dbs_transaction = this.dbs_session.beginTransaction();

			if (this.tools.isEmpty(this.script_id)) {
				return "success";
			}

			this.hql = ("from callout_script_detail where script_id=" + this.script_id);
			this.log.info("exec hql : " + this.hql);
			this.list = this.daos.select_list(this.dbs_session, this.hql);
			if ((this.list != null) && (this.list.size() > 0)) {
				this.resultMap = new HashMap();
				this.describeMap = new HashMap();
				this.questionMap = new HashMap();
				for (int i = 0; i < this.list.size(); i++) {
					callout_script_detail script_detail = (callout_script_detail) this.list
							.get(i);
					int key = script_detail.getContent_sort();
					String value = script_detail.getContent_detail();
					String type = script_detail.getContent_type();

					if (("radio".equals(type)) || ("checkbox".equals(type))) {
						this.resultMap.put(Integer.valueOf(key), value);
					} else if ("describe".equals(type)) {
						this.describeMap.put(Integer.valueOf(key), value);
					} else if ("question".equals(type)) {
						this.questionMap.put(Integer.valueOf(key), value);
					}
				}
			}

			this.hql = ("from callout_result where script_id=" + this.script_id
					.trim());

			if (!this.tools.isEmpty(this.agent_id)) {
				this.hql = (this.hql + " and agent_id='" + this.agent_id + "'");
			}

			if (!this.tools.isEmpty(this.start_date)) {
				if (!this.tools.isEmpty(this.end_date))
					this.hql = (this.hql + " and callout_time between '"
							+ this.start_date + " 00:00:00' and '"
							+ this.end_date + " 23:59:59'");
				else {
					this.hql = (this.hql + " and callout_time >= '"
							+ this.start_date + " 00:00:00'");
				}

			}

			if (!this.tools.isEmpty(this.callout_phone)) {
				this.hql = (this.hql + " and callout_phone='"
						+ this.callout_phone + "'");
			}

			if (!this.tools.isEmpty(this.options)) {
				this.hql = (this.hql + " and result_describe like '%;"
						+ this.options + ";'");
			}

			this.hql += " order by callout_time";
			System.out.println("正确hql语句:::::::select count(*) " + hql);
			this.log.info("exec hql : " + this.hql);
			this.callout_result = new callout_result();
			this.page_object = this.daos.select_pages(this.dbs_session,
					this.hql, this.page, this.limit, this.callout_result);
			this.page_object = (this.page_object == null ? new HibernatePageDemo()
					: this.page_object);
			this.list = this.page_object.getList();

			if ((this.list != null) && (this.list.size() > 0)) {
				System.out.println("list的大小:::::::::::::::" + list.size());
				List resultList = new ArrayList();
				for (int i = 0; i < this.list.size(); i++) {
					StringBuffer str = new StringBuffer();

					callout_result result = (callout_result) this.list.get(i);
					String callout_time = result.getCallout_time();
					String agent_id = result.getAgent_id();
					String callout_phone = result.getCallout_phone();
					System.out.println("list的i:::::::::::::::" + i);

					str.append("{");
					str.append("callout_time:'" + callout_time + "',");
					str.append("agent_id:'" + agent_id + "',");
					str.append("callout_phone:'" + callout_phone + "',");

					String describe = result.getResult_describe();

					String[] qn_nums = describe.split(";");
					for (int j = 0; j < qn_nums.length; j++) {
						String qn = qn_nums[j];
						qn = qn.substring(qn.indexOf(":") + 1, qn.length());
						String[] checkboxs = qn.split("\\|");
						int num = j + 1;

						if (this.describeMap.containsKey(Integer.valueOf(num))) {
							continue;
						}

						if (this.resultMap.containsKey(Integer.valueOf(num))) {
							if (!"".equals(qn)) {
								String detail = (String) this.resultMap
										.get(Integer.valueOf(num));
								String[] answer = detail.split("//");
								String[] qn_valus = answer[1].split("\\|\\|");

								String qn_value = "";
								if (checkboxs.length > 1)
									for (int index = 0; index < checkboxs.length; index++) {
										int check_num = Integer
												.parseInt(checkboxs[index]);
										int qn_num = check_num - 1;
										qn_value = qn_value + qn_valus[qn_num]
												+ ",";
									}
								else {
									qn_value = qn_valus[(Integer.parseInt(qn) - 1)];
								}
								str.append("q" + num + ":'" + qn_value + "',");
							} else {
								str.append("q" + num + ":'" + qn + "',");
							}

						} else if (this.questionMap.containsKey(Integer
								.valueOf(num))) {
							str.append("q" + num + ":'" + qn + "',");
						} else {
							str.append("list_result:'" + qn + "',");
						}
					}
					str.append("}");
					String temp = str.toString().replaceAll("\\n", "");
					resultList.add(temp);
				}

				this.page_json = new HibernatePageJson();
				System.out.println("list的总页数:::::::"
						+ this.page_object.getTotal_record_count());
				this.page_json.setTotal(this.page_object
						.getTotal_record_count());
				this.page_json.setRows(resultList);
				this.result_json = JSONObject.fromObject(this.page_json);
			} else {
				this.result_json = new JSONObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.daos.rollback_transaction(this.dbs_transaction);
		} finally {
			this.daos.commit_transaction(this.dbs_transaction);
			this.daos.close_session(this.dbs_session);
		}

		return "success";
	}

	public void download_excel() throws Exception {
		this.log.info("Entry manage_report[ download_excel()]");
		try {
			this.dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			this.dbs_transaction = this.dbs_session.beginTransaction();

			if (this.tools.isEmpty(this.script_id)) {
				return;
			}

			this.hql = ("from callout_script_detail where script_id=" + this.script_id);
			this.hql += " order by content_sort";
			List columnList = this.daos.select_list(this.dbs_session, this.hql);
			if ((columnList != null) && (columnList.size() > 0)) {
				this.resultMap = new HashMap();
				this.describeMap = new HashMap();
				for (int i = 0; i < columnList.size(); i++) {
					callout_script_detail script_detail = (callout_script_detail) columnList
							.get(i);
					int key = script_detail.getContent_sort();
					String value = script_detail.getContent_detail();
					String type = script_detail.getContent_type();

					if (("radio".equals(type)) || ("checkbox".equals(type))) {
						this.resultMap.put(Integer.valueOf(key), value);
					} else if ("describe".equals(type)) {
						this.describeMap.put(Integer.valueOf(key), value);
					}
				}

			}

			this.hql = ("from callout_result where script_id=" + this.script_id
					.trim());
			if (!this.tools.isEmpty(this.agent_id)) {
				this.hql = (this.hql + " and agent_id='" + this.agent_id + "'");
			}
			if (!this.tools.isEmpty(this.start_date)) {
				if (!this.tools.isEmpty(this.end_date))
					this.hql = (this.hql + " and callout_time between '"
							+ this.start_date + " 00:00:00' and '"
							+ this.end_date + " 23:59:59'");
				else {
					this.hql = (this.hql + " and callout_time >= '"
							+ this.start_date + " 00:00:00'");
				}
			}
			if (!this.tools.isEmpty(this.callout_phone)) {
				this.hql = (this.hql + " and callout_phone='"
						+ this.callout_phone + "'");
			}

			if (!this.tools.isEmpty(this.options)) {
				String temp = URLDecoder.decode(this.options, "UTF-8");
				this.hql = (this.hql + " and result_describe like '%;" + temp + ";'");
			}

			this.hql += " order by callout_time";
			this.log.info("exec hql : " + this.hql);
			List dataList = this.daos.select_list(this.dbs_session, this.hql);

			String script_name = "";
			if ((dataList != null) && (dataList.size() > 0)) {
				callout_result result = (callout_result) dataList.get(0);
				script_name = result.getScript_name();
			}

			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context
					.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse");

			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());

			OutputStream os = response.getOutputStream();

			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ fileName + ".xls");
			response.setContentType("application/msexcel;charset=UTF-8");

			WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet(script_name, 0);

			Label label = null;
			label = new Label(0, 0, "时间");
			sheet.addCell(label);
			label = new Label(1, 0, "工号");
			sheet.addCell(label);
			label = new Label(2, 0, "用户号码");
			sheet.addCell(label);

			int title_num = 0;
			for (int i = 0; i < columnList.size(); i++) {
				callout_script_detail script_detail = (callout_script_detail) columnList
						.get(i);
				if (this.describeMap.containsKey(Integer.valueOf(script_detail
						.getContent_sort()))) {
					continue;
				}

				label = new Label(title_num + 3, 0,
						script_detail.getContent_detail());
				sheet.addCell(label);
				title_num++;
			}

			label = new Label(title_num + 3, 0, "执行结果");
			sheet.addCell(label);
			title_num++;

			for (int i = 0; i < dataList.size(); i++) {
				callout_result result = (callout_result) dataList.get(i);

				label = new Label(0, i + 1, result.getCallout_time());
				sheet.addCell(label);
				label = new Label(1, i + 1, result.getAgent_id());
				sheet.addCell(label);
				label = new Label(2, i + 1, result.getCallout_phone());
				sheet.addCell(label);

				String describe = result.getResult_describe();

				int cellnum = 3;
				String[] qn_nums = describe.split(";");
				for (int j = 0; j < qn_nums.length; j++) {
					String qn = qn_nums[j];
					qn = qn.substring(qn.indexOf(":") + 1, qn.length());
					String[] checkboxs = qn.split("\\|");
					int number = j + 1;

					if (this.describeMap.containsKey(Integer.valueOf(number))) {
						continue;
					}

					if ((this.resultMap.containsKey(Integer.valueOf(number)))
							&& (!"".equals(qn))) {
						String detail = (String) this.resultMap.get(Integer
								.valueOf(number));
						String[] answer = detail.split("//");
						String[] qn_valus = answer[1].split("\\|\\|");

						String qn_value = "";
						if (checkboxs.length > 1)
							for (int index = 0; index < checkboxs.length; index++) {
								int check_num = Integer
										.parseInt(checkboxs[index]);
								int qn_num = check_num - 1;
								qn_value = qn_value + qn_valus[qn_num] + ",";
							}
						else {
							qn_value = qn_valus[(Integer.parseInt(qn) - 1)];
						}

						label = new Label(cellnum, i + 1, qn_value);
						sheet.addCell(label);
						cellnum++;
					} else {
						label = new Label(cellnum, i + 1, qn);
						sheet.addCell(label);
						cellnum++;
					}
				}
			}
			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			this.daos.rollback_transaction(this.dbs_transaction);
		} finally {
			this.daos.commit_transaction(this.dbs_transaction);
			this.daos.close_session(this.dbs_session);
		}
	}

	public String init_count_report() {
		log.info("Entry manage_report[ init_report_list()]");
		return SUCCESS;
	}

	/**
	 * 导出EXCEL
	 */
	public void download_count_excel() throws Exception {
		log.info("Entry manage_report[ download_excel()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();

			tempStr = "";
			teamname = "";
			username = "";
			callNum = 0;
			openNum = 0;
			opens = "";

			if (tools.isEmpty(script_id)) {
				return;
			}

			hql = "select agent_id from callout_result where script_id="
					+ script_id.trim();

			// 工号
			if (!tools.isEmpty(agent_id)) {
				hql += " and agent_id='" + agent_id + "'";
			}

			// 时间
			if (!tools.isEmpty(start_date)) {
				if (!tools.isEmpty(end_date)) {
					hql += " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
					tempStr = " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
				} else {
					hql += " and callout_time >= '" + start_date + " 00:00:00'";
					tempStr = " and callout_time >= '" + start_date
							+ " 00:00:00'";
				}
			}

			hql += " group by agent_id";
			log.info("exec hql : " + hql);
			List<Object> dataList = daos.select_list(dbs_session, hql);

			String script_name = "";
			hql = "from callout_script where id='" + script_id + "'";
			callout_script sp = new callout_script();
			sp.setId(Integer.parseInt(script_id));
			sp = (callout_script) daos.select_object(dbs_session, hql, sp);
			script_name = sp.getScript_name();

			// EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context
					.get(ServletActionContext.HTTP_RESPONSE);

			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
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
			WritableSheet sheet = workbook.createSheet(script_name, 0);

			// excel工作表的标题
			Label label = null;
			label = new jxl.write.Label(0, 0, "时间");
			sheet.addCell(label);
			label = new jxl.write.Label(1, 0, "工号");
			sheet.addCell(label);
			label = new jxl.write.Label(2, 0, "外呼量");
			sheet.addCell(label);
			label = new jxl.write.Label(3, 0, "开通量");
			sheet.addCell(label);
			label = new jxl.write.Label(4, 0, "姓名");
			sheet.addCell(label);
			label = new jxl.write.Label(5, 0, "团队");
			sheet.addCell(label);
			label = new jxl.write.Label(6, 0, "开通率");
			sheet.addCell(label);

			// 表数据
			List<Object> openList = new ArrayList<Object>();
			List<Object> callList = new ArrayList<Object>();
			List<Object> timeList = new ArrayList<Object>();
			for (int i = 0; i < dataList.size(); i++) {
				String agent_id = (String) dataList.get(i);
				String temps = tempStr + " and agent_id='" + agent_id
						+ "' and script_id='" + script_id.trim() + "' ";
				hql = "select callout_time from callout_result where 1=1 "
						+ temps;
				log.info("exec hql : " + hql);
				timeList = daos.select_list(dbs_session, hql);
				callout_time = timeList.get(0).toString();

				hql = "select count(id) from callout_result where 1=1 " + temps;
				log.info("exec hql : " + hql);
				callList = daos.select_list(dbs_session, hql);
				if (callList != null && callList.size() > 0) {
					callNum = Integer.parseInt(callList.get(0).toString());// 外呼量
				}

				hql = "select count(id) from callout_result where 1=1 "
						+ temps
						+ " and (result_describe like '%成功%' or result_describe like '%;同意%')";
				log.info("exec hql : " + hql);
				openList = daos.select_list(dbs_session, hql);
				if (openList != null && openList.size() > 0) {
					openNum = Integer.parseInt(openList.get(0).toString());// 开通量
				}

				hql = "from agentidvsname where agentid='" + agent_id + "'";
				log.info("exec hql : " + hql);
				adn = new agentidvsname();
				adn.setAgentid(agent_id);
				adn = (agentidvsname) daos.select_object(dbs_session, hql, adn);
				if (adn != null) {
					teamname = adn.getTeamname();
					username = adn.getUsername();
				}

				// 开通率
				opens = "";
				if (callNum == 0 || openNum == 0) {
					opens = "";
				} else {
					try {
						NumberFormat numberFormat = NumberFormat.getInstance();
						numberFormat.setMaximumFractionDigits(2);
						opens = numberFormat.format((float) openNum
								/ (float) callNum * 100);
						opens += "%";
					} catch (Exception e) {
						e.printStackTrace();
						opens = "";
					}
				}

				// 第二行开始
				label = new Label(0, i + 1, callout_time);// 时间
				sheet.addCell(label);
				label = new Label(1, i + 1, agent_id);// 工号
				sheet.addCell(label);
				label = new Label(2, i + 1, callNum + "");
				sheet.addCell(label);
				label = new Label(3, i + 1, openNum + "");
				sheet.addCell(label);
				label = new Label(4, i + 1, username);
				sheet.addCell(label);
				label = new Label(5, i + 1, teamname);
				sheet.addCell(label);
				label = new Label(6, i + 1, opens);
				sheet.addCell(label);
			}
			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
	}

	public String init_count_report_data_agent() {
		return SUCCESS;
	}

	public String count_report_column() {
		this.log.info("Entry manage_report[ count_report_column()]");
		try {
			this.dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			this.dbs_transaction = this.dbs_session.beginTransaction();

			if (this.tools.isEmpty(this.script_id)) {
				this.result_string = "";
				return "success";
			}

			this.modelBeanList = new ArrayList();
			this.columnBeanList = new ArrayList();

			callout_time = "callout_time";
			agent_id = "agent_id";
			String callNum = "callNum";
			String openNum = "openNum";
			username = "username";
			teamname = "teamname";
			opens = "opens";

			this.model_bean = new report_model_bean();
			this.model_bean.setName(callout_time);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(agent_id);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(callNum);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(openNum);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(username);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(teamname);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(opens);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);

			this.column_bean = new report_column_bean();
			this.column_bean.setText("时间");
			this.column_bean.setDataIndex(callout_time);
			this.column_bean.setWidth(180);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("工号");
			this.column_bean.setDataIndex(agent_id);
			this.column_bean.setWidth(100);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("外呼量");
			this.column_bean.setDataIndex(callNum);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);

			this.column_bean = new report_column_bean();
			this.column_bean.setText("开通量");
			this.column_bean.setDataIndex(openNum);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("姓名");
			this.column_bean.setDataIndex(username);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("团队");
			this.column_bean.setDataIndex(teamname);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("开通率");
			this.column_bean.setDataIndex(opens);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);

			this.result_json = new JSONObject();
			this.result_json.put("columnBeanList", this.columnBeanList);
			this.result_json.put("modelBeanList", this.modelBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			this.daos.rollback_transaction(this.dbs_transaction);
		} finally {
			this.daos.commit_transaction(this.dbs_transaction);
			this.daos.close_session(this.dbs_session);
		}

		return "success";
	}

	public String count_report_data() {
		this.log.info("Entry manage_report[ count_report_data()]");
		try {
			tempStr = "";
			teamname = "";
			username = "";
			callNum = 0;
			openNum = 0;
			opens = "";

			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();

			if (tools.isEmpty(script_id)) {
				return "success";
			}

			hql = ("select distinct(agent_id) from callout_result where script_id=" + script_id
					.trim());

			if (!tools.isEmpty(agent_id)) {
				hql += " and agent_id='" + agent_id + "'";
			}

			if (!tools.isEmpty(start_date)) {
				if (!tools.isEmpty(end_date)) {
					hql += " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
					tempStr = " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
				} else {
					hql += " and callout_time >= '" + start_date + " 00:00:00'";
					tempStr = " and callout_time >= '" + start_date
							+ " 00:00:00'";
				}

			}
			System.out.println("count的hql语句:::::::select count(*) " + hql);
			log.info("exec hql : " + hql);
			callout_result = new callout_result();
			page_object = daos.select_pages_lm(dbs_session, hql, page, limit,
					callout_result);
			page_object = (page_object == null ? new HibernatePageDemo()
					: page_object);
			list = page_object.getList();

			if ((list != null) && (list.size() > 0)) {
				List resultList = new ArrayList();
				List openList = new ArrayList<Object>();
				List callList = new ArrayList<Object>();
				List timeList = new ArrayList<Object>();
				for (int i = 0; i < this.list.size(); i++) {
					StringBuffer str = new StringBuffer();

					String agent_id = (String) list.get(i);

					String temps = tempStr + " and agent_id='" + agent_id
							+ "' and script_id='" + script_id.trim() + "' ";

					hql = "select callout_time from callout_result where 1=1 "
							+ temps;
					log.info("exec hql : " + hql);
					timeList = daos.select_list(dbs_session, hql);
					callout_time = timeList.get(0).toString();

					hql = "select count(id) from callout_result where 1=1 "
							+ temps;
					log.info("exec hql : " + hql);
					callList = daos.select_list(dbs_session, hql);
					if (callList != null && callList.size() > 0) {
						callNum = Integer.parseInt(callList.get(0).toString());// 外呼量
					}

					hql = "select count(id) from callout_result where 1=1 "
							+ temps
							+ " and (result_describe like '%成功%' or result_describe like '%;同意%')";
					log.info("exec hql : " + hql);
					openList = daos.select_list(dbs_session, hql);
					if (openList != null && openList.size() > 0) {
						openNum = Integer.parseInt(openList.get(0).toString());// 开通量
					}
					hql = "from agentidvsname where agentid='" + agent_id + "'";
					log.info("exec hql : " + hql);
					adn = new agentidvsname();
					adn.setAgentid(agent_id);
					adn = (agentidvsname) daos.select_object(dbs_session, hql,
							adn);
					if (adn != null) {
						teamname = adn.getTeamname();
						username = adn.getUsername();
					}

					// 开通率
					opens = "";
					if (callNum == 0 || openNum == 0) {
						opens = "";
					} else {
						try {
							NumberFormat numberFormat = NumberFormat
									.getInstance();
							numberFormat.setMaximumFractionDigits(2);
							opens = numberFormat.format((float) openNum
									/ (float) callNum * 100);
							opens += "%";
						} catch (Exception e) {
							e.printStackTrace();
							opens = "";
						}
					}

					str.append("{");
					str.append("callout_time:'" + callout_time + "',");
					str.append("agent_id:'" + agent_id + "',");
					str.append("callNum:'" + callNum + "',");
					str.append("openNum:'" + openNum + "',");
					str.append("username:'" + username + "',");
					str.append("teamname:'" + teamname + "',");
					str.append("opens:'" + opens + "',");
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
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}

		return "success";
	}

	// 员工录音统计
	public String count_report_column_agent() {
		try {
			modelBeanList = new ArrayList();
			columnBeanList = new ArrayList();
			String agent_id = "agent_id";
			String script_name = "script_name";
			String opens = "opens";
			String times = "times";

			this.model_bean = new report_model_bean();
			this.model_bean.setName(agent_id);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(script_name);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(opens);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);
			this.model_bean = new report_model_bean();
			this.model_bean.setName(times);
			this.model_bean.setType("string");
			this.modelBeanList.add(this.model_bean);

			this.column_bean = new report_column_bean();
			this.column_bean.setText("工号");
			this.column_bean.setDataIndex(agent_id);
			this.column_bean.setWidth(100);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("业务名称");
			this.column_bean.setDataIndex(script_name);
			this.column_bean.setWidth(150);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("开通量");
			this.column_bean.setDataIndex(opens);
			this.column_bean.setWidth(180);
			this.columnBeanList.add(this.column_bean);
			this.column_bean = new report_column_bean();
			this.column_bean.setText("时间");
			this.column_bean.setDataIndex(times);
			this.column_bean.setWidth(180);
			this.columnBeanList.add(this.column_bean);

			this.result_json = new JSONObject();
			this.result_json.put("columnBeanList", this.columnBeanList);
			this.result_json.put("modelBeanList", this.modelBeanList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

	public String count_report_data_agent() {
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();

			// select distinct(script_id) from callout_result where
			// agent_id='23174'
			// and callout_time between '2015-06-29 00:00:00' and '2015-07-31
			// 23:59:59'
			String tempStr = "";
			String teamname = "";// 业务名称
			int openNum = 0;// 开通量
			String callout_time = "";

			if (tools.isEmpty(agent_id)) {
				return SUCCESS;
			}

			hql = "select distinct(script_id) from callout_result where agent_id="
					+ agent_id.trim();

			// 时间
			if (!tools.isEmpty(start_date)) {
				if (!tools.isEmpty(end_date)) {
					hql += " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
					tempStr = " and callout_time between '" + start_date
							+ " 00:00:00' and '" + end_date + " 23:59:59'";
				} else {
					hql += " and callout_time >= '" + start_date + " 00:00:00'";
					tempStr = " and callout_time >= '" + start_date
							+ " 00:00:00'";
				}
			}

			hql += " order by callout_time ";
			// list = daos.select_list(dbs_session, hql);
			page_object = daos.select_pages_special(dbs_session, hql, page,
					limit);
			page_object = page_object == null ? new HibernatePageDemo()
					: page_object;
			list = page_object.getList();

			// 数据处理
			if (list != null && list.size() > 0) {
				List<Object> resultList = new ArrayList<Object>();
				List<Object> openList = new ArrayList<Object>();
				List<Object> timeList = new ArrayList<Object>();
				List<Object> scriptList = new ArrayList<Object>();
				for (int i = 0; i < list.size(); i++) {
					StringBuffer str = new StringBuffer();
					String script_id = (String) list.get(i);

					String temps = tempStr + " and agent_id='" + agent_id
							+ "' and script_id='" + script_id.trim() + "' ";

					hql = "select script_name from callout_result where 1=1 "
							+ temps;
					scriptList = daos.select_list(dbs_session, hql);
					teamname = scriptList.get(0).toString();

					hql = "select callout_time from callout_result where 1=1 "
							+ temps;
					timeList = daos.select_list(dbs_session, hql);
					callout_time = timeList.get(0).toString();

					hql = "select count(id) from callout_result where 1=1 "
							+ temps
							+ " and (result_describe like '%成功%' or result_describe like '%;同意%')";
					openList = daos.select_list(dbs_session, hql);
					if (openList != null && openList.size() > 0) {
						openNum = Integer.parseInt(openList.get(0).toString());// 开通量
					}

					str.append("{");
					str.append("agent_id:'" + agent_id + "',");
					str.append("script_name:'" + teamname + "',");
					str.append("openNum:'" + openNum + "',");
					str.append("callout_time:'" + callout_time + "',");
					str.append("}");

					String temp = str.toString().replaceAll("\\n", "");// 符号会导致数据转JSON错误
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
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}

	public String getResult_string() {
		return result_string;
	}

	public void setResult_string(String result_string) {
		this.result_string = result_string;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getCallout_phone() {
		return callout_phone;
	}

	public void setCallout_phone(String callout_phone) {
		this.callout_phone = callout_phone;
	}

	public String getScript_id() {
		return script_id;
	}

	public void setScript_id(String script_id) {
		this.script_id = script_id;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public int getCallNum() {
		return callNum;
	}

	public void setCallNum(int callNum) {
		this.callNum = callNum;
	}

	public int getOpenNum() {
		return openNum;
	}

	public void setOpenNum(int openNum) {
		this.openNum = openNum;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getOpens() {
		return opens;
	}

	public void setOpens(String opens) {
		this.opens = opens;
	}

	/*
	 * // 获取通道对应的通道列名 public String get_report_column() {
	 * log.info("Entry manage_report[ get_report_column()]"); try { dbs_session
	 * = HibernateSessionFactory_cailing3_7.getSession(); dbs_transaction =
	 * dbs_session.beginTransaction();
	 * 
	 * if (tools.isEmpty(script_id)) { result_string = ""; return SUCCESS; }
	 * 
	 * // 获取通道内容字段 hql = "from callout_script_detail where script_id=" +
	 * script_id; hql += " order by content_sort"; log.info("exec hql : " +
	 * hql); list = daos.select_list(dbs_session, hql);
	 * 
	 * // 封装Model、Column modelBeanList = new ArrayList<report_model_bean>();
	 * columnBeanList = new ArrayList<report_column_bean>(); if (list != null &&
	 * list.size() > 0) { String callout_time = "callout_time"; String agent_id
	 * = "agent_id"; String called_id = "called_id"; String callout_phone =
	 * "callout_phone"; String list_result = "list_result";
	 * 
	 * // model model_bean = new report_model_bean();
	 * model_bean.setName(callout_time); model_bean.setType("string");
	 * modelBeanList.add(model_bean); model_bean = new report_model_bean();
	 * model_bean.setName(agent_id); model_bean.setType("string");
	 * modelBeanList.add(model_bean); model_bean = new report_model_bean();
	 * model_bean.setName(called_id); model_bean.setType("string");
	 * modelBeanList.add(model_bean); model_bean = new report_model_bean();
	 * model_bean.setName(callout_phone); model_bean.setType("string");
	 * modelBeanList.add(model_bean);
	 * 
	 * // column column_bean = new report_column_bean();
	 * column_bean.setText("时间"); column_bean.setDataIndex(callout_time);
	 * column_bean.setWidth(180); columnBeanList.add(column_bean); column_bean =
	 * new report_column_bean(); column_bean.setText("工号");
	 * column_bean.setDataIndex(agent_id); column_bean.setWidth(100);
	 * columnBeanList.add(column_bean); column_bean = new report_column_bean();
	 * column_bean.setText("用户号码"); column_bean.setDataIndex(callout_phone);
	 * column_bean.setWidth(150); columnBeanList.add(column_bean);
	 * 
	 * for (int i = 0; i < list.size(); i++) { callout_script_detail script =
	 * (callout_script_detail) list .get(i); String type =
	 * script.getContent_type(); if ("describe".equals(type)) { continue; }
	 * 
	 * String column = "q" + script.getContent_sort(); String text = "问题" +
	 * script.getContent_sort(); model_bean = new report_model_bean();
	 * model_bean.setName(column); model_bean.setType("string");
	 * modelBeanList.add(model_bean);
	 * 
	 * column_bean = new report_column_bean(); column_bean.setText(text);
	 * column_bean.setDataIndex(column); column_bean.setWidth(100);
	 * columnBeanList.add(column_bean); }
	 * 
	 * model_bean = new report_model_bean(); model_bean.setName(list_result);
	 * model_bean.setType("string"); modelBeanList.add(model_bean);
	 * 
	 * column_bean = new report_column_bean(); column_bean.setText("执行结果");
	 * column_bean.setDataIndex(list_result); column_bean.setWidth(180);
	 * columnBeanList.add(column_bean); }
	 * 
	 * result_json = new JSONObject(); result_json.put("columnBeanList",
	 * columnBeanList); result_json.put("modelBeanList", modelBeanList); } catch
	 * (Exception e) { e.printStackTrace();
	 * daos.rollback_transaction(dbs_transaction); } finally {
	 * daos.commit_transaction(dbs_transaction);
	 * daos.close_session(dbs_session); }
	 * 
	 * return SUCCESS; }
	 * 
	 * // 显示详细数据 public String list_report_data() {
	 * log.info("Entry manage_report[ list_report_data()]"); try { dbs_session =
	 * HibernateSessionFactory_cailing3_7.getSession(); dbs_transaction =
	 * dbs_session.beginTransaction();
	 * 
	 * if (tools.isEmpty(script_id)) { return SUCCESS; }
	 * 
	 * // 获取脚本信息 hql = "from callout_script_detail where script_id=" +
	 * script_id; log.info("exec hql : " + hql); list =
	 * daos.select_list(dbs_session, hql); if (list != null && list.size() > 0)
	 * { resultMap = new HashMap<Integer, String>(); describeMap = new
	 * HashMap<Integer, String>(); questionMap = new HashMap<Integer, String>();
	 * for (int i = 0; i < list.size(); i++) { callout_script_detail
	 * script_detail = (callout_script_detail) list .get(i); int key =
	 * script_detail.getContent_sort(); String value =
	 * script_detail.getContent_detail(); String type =
	 * script_detail.getContent_type();
	 * 
	 * // 单选、多选 if ("radio".equals(type) || "checkbox".equals(type)) {
	 * resultMap.put(key, value); } // 描述 else if ("describe".equals(type)) {
	 * describeMap.put(key, value); } // 简答 else if ("question".equals(type)) {
	 * questionMap.put(key, value); } } }
	 * 
	 * hql = "from callout_result where script_id=" + script_id.trim();
	 * 
	 * // 工号 if (!tools.isEmpty(agent_id)) { hql += " and agent_id='" + agent_id
	 * + "'"; }
	 * 
	 * // 时间 if (!tools.isEmpty(start_date)) { if (!tools.isEmpty(end_date)) {
	 * hql += " and callout_time between '" + start_date + " 00:00:00' and '" +
	 * end_date + " 23:59:59'"; } else { hql += " and callout_time >= '" +
	 * start_date + " 00:00:00'"; } }
	 * 
	 * // 号码 if (!tools.isEmpty(callout_phone)) { hql += " and callout_phone='"
	 * + callout_phone + "'"; }
	 * 
	 * // 执行结果 if (!tools.isEmpty(options)) { hql +=
	 * " and result_describe like '%;" + options + ";'"; }
	 * 
	 * hql += " order by callout_time"; log.info("exec hql : " + hql);
	 * callout_result = new callout_result(); page_object =
	 * daos.select_pages(dbs_session, hql, page, limit, callout_result);
	 * page_object = page_object == null ? new HibernatePageDemo() :
	 * page_object; list = page_object.getList();
	 * 
	 * // 数据处理 if (list != null && list.size() > 0) { List<Object> resultList =
	 * new ArrayList<Object>(); for (int i = 0; i < list.size(); i++) {
	 * StringBuffer str = new StringBuffer();
	 * 
	 * callout_result result = (callout_result) list.get(i); String callout_time
	 * = result.getCallout_time(); String agent_id = result.getAgent_id();
	 * String callout_phone = result.getCallout_phone();
	 * 
	 * str.append("{"); str.append("callout_time:'" + callout_time + "',");
	 * str.append("agent_id:'" + agent_id + "',"); str.append("callout_phone:'"
	 * + callout_phone + "',");
	 * 
	 * String describe = result.getResult_describe(); // String describe =
	 * ";;;;;;;;1;3|2;";//例1 // String describe = //
	 * "qn1:;qn2:3;qn3:1;qn4:;qn5:;qn6:;qn7:1;qn8:;qn9:1;qn10:;";//例2 String[]
	 * qn_nums = describe.split(";");// qn2:3;qn3:1; for (int j = 0; j <
	 * qn_nums.length; j++) { String qn = qn_nums[j]; qn =
	 * qn.substring(qn.indexOf(":") + 1, qn.length());// qn2:3 String[]
	 * checkboxs = qn.split("\\|");// 3|2 int num = j + 1;// 问题顺序从1开始
	 * 
	 * // 描述不显示 if (describeMap.containsKey(num)) { continue; }
	 * 
	 * // 单选、多选 if (resultMap.containsKey(num)) { if (!"".equals(qn)) { String
	 * detail = resultMap.get(num); String[] answer = detail.split("//");
	 * String[] qn_valus = answer[1].split("\\|\\|");
	 * 
	 * String qn_value = ""; if (checkboxs.length > 1) { for (int index = 0;
	 * index < checkboxs.length; index++) { int check_num = Integer
	 * .parseInt(checkboxs[index]); int qn_num = check_num - 1; qn_value +=
	 * qn_valus[qn_num] + ","; } } else { qn_value =
	 * qn_valus[Integer.parseInt(qn) - 1]; } str.append("q" + num + ":'" +
	 * qn_value + "',"); } else { str.append("q" + num + ":'" + qn + "',"); } }
	 * // 简答 else if (questionMap.containsKey(num)) { str.append("q" + num +
	 * ":'" + qn + "',"); } // 执行结果 else { str.append("list_result:'" + qn +
	 * "',"); } } str.append("}"); System.out.println("lm数据统计列表值:" +
	 * str.toString()); String temp = str.toString().replaceAll("\\n", "");//
	 * 符号会导致数据转JSON错误 resultList.add(temp); }
	 * 
	 * page_json = new HibernatePageJson();
	 * page_json.setTotal(page_object.getTotal_record_count());
	 * page_json.setRows(resultList); result_json =
	 * JSONObject.fromObject(page_json); } else { result_json = new
	 * JSONObject(); } } catch (Exception e) { e.printStackTrace();
	 * daos.rollback_transaction(dbs_transaction); } finally {
	 * daos.commit_transaction(dbs_transaction);
	 * daos.close_session(dbs_session); }
	 * 
	 * return SUCCESS; } //导出EXCEL public void download_excel() throws Exception
	 * { log.info("Entry manage_report[ download_excel()]"); try { dbs_session =
	 * HibernateSessionFactory_cailing3_7.getSession(); dbs_transaction =
	 * dbs_session.beginTransaction();
	 * 
	 * if (tools.isEmpty(script_id)) { return; }
	 * 
	 * // 获取通道内容字段 hql = "from callout_script_detail where script_id=" +
	 * script_id; hql += " order by content_sort"; List<Object> columnList =
	 * daos.select_list(dbs_session, hql); if (columnList != null &&
	 * columnList.size() > 0) { resultMap = new HashMap<Integer, String>();
	 * describeMap = new HashMap<Integer, String>(); for (int i = 0; i <
	 * columnList.size(); i++) { callout_script_detail script_detail =
	 * (callout_script_detail) columnList .get(i); int key =
	 * script_detail.getContent_sort(); String value =
	 * script_detail.getContent_detail(); String type =
	 * script_detail.getContent_type();
	 * 
	 * // 单选、多选 if ("radio".equals(type) || "checkbox".equals(type)) {
	 * resultMap.put(key, value); } // 描述 else if ("describe".equals(type)) {
	 * describeMap.put(key, value); } } }
	 * 
	 * // 条件 hql = "from callout_result where script_id=" + script_id.trim(); if
	 * (!tools.isEmpty(agent_id)) { hql += " and agent_id='" + agent_id + "'";//
	 * 工号 } if (!tools.isEmpty(start_date)) {// 时间 if (!tools.isEmpty(end_date))
	 * { hql += " and callout_time between '" + start_date + " 00:00:00' and '"
	 * + end_date + " 23:59:59'"; } else { hql += " and callout_time >= '" +
	 * start_date + " 00:00:00'"; } } if (!tools.isEmpty(callout_phone)) {// 号码
	 * hql += " and callout_phone='" + callout_phone + "'"; } // 执行结果 if
	 * (!tools.isEmpty(options)) { String temp = URLDecoder.decode(options,
	 * "UTF-8"); hql += " and result_describe like '%;" + temp + ";'"; }
	 * 
	 * // 条件数据 hql += " order by callout_time"; log.info("exec hql : " + hql);
	 * List<Object> dataList = daos.select_list(dbs_session, hql);
	 * 
	 * String script_name = ""; if (dataList != null && dataList.size() > 0) {
	 * callout_result result = (callout_result) dataList.get(0); script_name =
	 * result.getScript_name(); }
	 * 
	 * // EXCEL ActionContext context = ActionContext.getContext();
	 * HttpServletResponse response = (HttpServletResponse) context
	 * .get(ServletActionContext.HTTP_RESPONSE);
	 * 
	 * Calendar now = Calendar.getInstance(); DateFormat format = new
	 * SimpleDateFormat("yyyy_MM_dd_HH_mm_ss"); String fileName =
	 * format.format(now.getTime());
	 * 
	 * OutputStream os = response.getOutputStream(); // OutputStream os = new
	 * FileOutputStream(new // File("./"+fileName+".xls")); response.reset();//
	 * 清空输出流 response.setHeader("Content-disposition", "attachment; filename=" +
	 * fileName + ".xls");// 设定输出文件头
	 * response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
	 * 
	 * // 添加工作表 WritableWorkbook workbook = Workbook.createWorkbook(os);
	 * WritableSheet sheet = workbook.createSheet(script_name, 0);
	 * 
	 * // excel工作表的标题 Label label = null; label = new jxl.write.Label(0, 0,
	 * "时间"); sheet.addCell(label); label = new jxl.write.Label(1, 0, "工号");
	 * sheet.addCell(label); label = new jxl.write.Label(2, 0, "用户号码");
	 * sheet.addCell(label);
	 * 
	 * // 表头 int title_num = 0; for (int i = 0; i < columnList.size(); i++) {
	 * callout_script_detail script_detail = (callout_script_detail) columnList
	 * .get(i); if (describeMap.containsKey(script_detail.getContent_sort())) {
	 * continue;// 描述不显示 }
	 * 
	 * // Label(列号,行号 ,内容 ) 第4列开始 label = new Label(title_num + 3, 0,
	 * script_detail.getContent_detail()); sheet.addCell(label); title_num++; }
	 * 
	 * label = new Label(title_num + 3, 0, "执行结果"); sheet.addCell(label);
	 * title_num++;
	 * 
	 * // 表数据 for (int i = 0; i < dataList.size(); i++) { callout_result result
	 * = (callout_result) dataList.get(i);
	 * 
	 * // 第二行开始 label = new Label(0, i + 1, result.getCallout_time());// 时间
	 * sheet.addCell(label); label = new Label(1, i + 1,
	 * result.getAgent_id());// 工号 sheet.addCell(label); label = new Label(2, i
	 * + 1, result.getCallout_phone());// 用户号码 sheet.addCell(label);
	 * 
	 * String describe = result.getResult_describe();// 问题回复 // String describe
	 * = ";;;;;;;;1;3|2;";//例1 // String describe = //
	 * "qn1:;qn2:3;qn3:1;qn4:;qn5:;qn6:;qn7:1;qn8:;qn9:1;qn10:;";//例2
	 * 
	 * int cellnum = 3;// 从第4列开始 String[] qn_nums = describe.split(";"); for
	 * (int j = 0; j < qn_nums.length; j++) { String qn = qn_nums[j]; qn =
	 * qn.substring(qn.indexOf(":") + 1, qn.length());// qn2:2; String[]
	 * checkboxs = qn.split("\\|");// 3|2 int number = j + 1;// 问题顺序从1开始
	 * 
	 * // 描述不显示 if (describeMap.containsKey(number)) { continue; }
	 * 
	 * // 单选、多选 if (resultMap.containsKey(number) && !"".equals(qn)) { String
	 * detail = resultMap.get(number); String[] answer = detail.split("//");
	 * String[] qn_valus = answer[1].split("\\|\\|");
	 * 
	 * String qn_value = ""; if (checkboxs.length > 1) { for (int index = 0;
	 * index < checkboxs.length; index++) { int check_num = Integer
	 * .parseInt(checkboxs[index]); int qn_num = check_num - 1; qn_value +=
	 * qn_valus[qn_num] + ","; } } else { qn_value =
	 * qn_valus[Integer.parseInt(qn) - 1]; }
	 * 
	 * label = new Label(cellnum, i + 1, qn_value); sheet.addCell(label);
	 * cellnum++; } else { label = new Label(cellnum, i + 1, qn);
	 * sheet.addCell(label); cellnum++; } } } workbook.write();
	 * workbook.close(); os.close(); } catch (Exception e) {
	 * e.printStackTrace(); daos.rollback_transaction(dbs_transaction); }
	 * finally { daos.commit_transaction(dbs_transaction);
	 * daos.close_session(dbs_session); } }
	 */
}
