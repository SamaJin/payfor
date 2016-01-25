package com.helphand.ccdms.commons;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.catalina.Session;

import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.tables.eaphelp.callout_rdlc;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.helphand.ccdms.thread.MyTask;

import freemarker.template.SimpleDate;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/*
 * 生成访问记录Excel表格工具类
 */
public class arexcelUtil {
	private static WritableCellFormat wcf_value; // 表格数据样式
	private static WritableCellFormat wcf_value_left;
	private static WritableCellFormat wcf_key; // 表头样式
	private static WritableCellFormat wcf_name_left; // 表名样式
	private static WritableCellFormat wcf_name_right; // 表名样式
	private static WritableCellFormat wcf_name_center; // 表名样式
	private static WritableCellFormat wcf_title; // 页名称样式
	private static WritableCellFormat wcf_percent_float;
	private File file;
	private static String pathStr = null; // 将导出Excel表格的路径设置为桌面
	private Map<Integer, String> resultMap = null;// 通道对应的通道答案
	private Map<Integer, String> describeMap = null;// 问题是描述
	private Map<Integer, String> questionMap = null;// 问题是简答

	public void start(List<List<Object>> list, List<String> lname,
			List<String> ltotal, org.hibernate.Session session_41,
			HibernateDAO daos) throws WriteException, IOException {
		URL path2 = MyTask.class.getClassLoader().getResource("");
		String str = path2.toString().substring(6, path2.toString().length());
		str = str.replaceAll("%20", " ");
		int num1 = str.indexOf("ccdms");
		str = str.substring(0, num1 + "ccdms".length()) + "/report/yw/";
		pathStr = str;
		next();
		try {
			// 接通率
			int[] jtl = new int[list.size()];
			// 开通数量
			String[] length = new String[list.size()];
			WritableWorkbook wb = Workbook.createWorkbook(file);
			List<Object> li = null;

			for (int i = 0; i < list.size(); i++) {
				li = list.get(i);
				jtl[i] = li.size();
				callout_result cr1 = (callout_result) li.get(0);
				Iterator it = li.iterator();
				WritableSheet ws = wb.createSheet(cr1.getScript_name(), 0);
				int startRowNum = 0; // 起始行
				int startColNum = 0; // 起始列
				// int maxColSize = 4; // 最大列数
				ws.setColumnView(0, 20);
				ws.setColumnView(1, 20);
				ws.setColumnView(2, 20);
				ws.setColumnView(3, 20);
				// ws.mergeCells(startColNum, startRowNum, startColNum
				// + maxColSize - 1, startRowNum); // 合并单元格，合并(1,0)到(1,9)
				startColNum = 0;
				startRowNum++;
				// 第1行，绘制表头
				ws.addCell(new Label(startColNum, startRowNum, "日期", wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, "工号", wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, "用户号码", wcf_key));
				startColNum++;
				// 获取列对应的hashmap
				gethashmap(cr1, session_41, daos);
				// 根据script_id查询需要显示的描述列
				String hql = "from callout_script_detail where content_type!='describe'  and script_id="
						+ cr1.getScript_id() + " order by id";
				List<Object> result = daos.select_list(session_41, hql);
				Iterator irt = result.iterator();
				while (irt.hasNext()) {
					callout_script_detail csd = (callout_script_detail) irt
							.next();
					ws.addCell(new Label(startColNum, startRowNum, csd
							.getContent_detail(), wcf_key));
					startColNum++;
				}
				ws.addCell(new Label(startColNum, startRowNum, "执行结果", wcf_key));
				startColNum++;
				// 将行数加1，列数重置为0
				startRowNum++;
				startColNum = 0;
				// 添加记录
				int num = 0;
				while (it.hasNext()) {
					callout_result cr = (callout_result) it.next();
					if (cr.getAgent_id() == null || cr.getAgent_id().equals(""))
						break;
					ws.addCell(new Label(startColNum, startRowNum, String
							.valueOf(cr.getCallout_time()), wcf_key));
					startColNum++;
					ws.addCell(new Label(startColNum, startRowNum, String
							.valueOf(cr.getAgent_id()), wcf_key));
					startColNum++;
					ws.addCell(new Label(startColNum, startRowNum, String
							.valueOf(cr.getCallout_phone()), wcf_key));
					startColNum++;

					// 根据结果集分割,重新封装结果
					String[] qn_nums = cr.getResult_describe().split(";");
					for (int j = 0; j < qn_nums.length; j++) {
						String qn = qn_nums[j];
						qn = qn.substring(qn.indexOf(":") + 1, qn.length());// qn2:3
						String[] checkboxs = qn.split("\\|");// 3|2
						int num2 = j + 1;// 问题顺序从1开始

						// 描述不显示
						if (describeMap.containsKey(num2)) {
							continue;
						}

						// 单选、多选
						if (resultMap.containsKey(num2)) {
							if (!"".equals(qn)) {
								String detail = resultMap.get(num2);
								String[] answer = detail.split("//");
								String[] qn_valus = answer[1].split("\\|\\|");

								String qn_value = "";
								if (checkboxs.length > 1) {
									for (int index = 0; index < checkboxs.length; index++) {
										int check_num = Integer
												.parseInt(checkboxs[index]);
										int qn_num = check_num - 1;
										qn_value += qn_valus[qn_num] + ",";
									}
								} else {
									qn_value = qn_valus[Integer.parseInt(qn) - 1];
								}
								ws.addCell(new Label(startColNum, startRowNum,
										qn_value, wcf_key));
								startColNum++;
							} else {
								ws.addCell(new Label(startColNum, startRowNum,
										qn, wcf_key));
								startColNum++;
							}
						}
						// 简答
						else if (questionMap.containsKey(num)) {
							ws.addCell(new Label(startColNum, startRowNum, qn,
									wcf_key));
							startColNum++;
						}
						// 执行结果
						else {
							ws.addCell(new Label(startColNum, startRowNum, qn,
									wcf_key));
							startColNum++;
						}
					}

					String mail = qn_nums[qn_nums.length - 1];
					// ws.addCell(new Label(startColNum, startRowNum, mail,
					// wcf_key));
					// startColNum++;
					// 将行数加1，列数重置为0
					startRowNum++;
					startColNum = 0;
					if (mail.equals("同意开通") || mail.equals("同意办理")
							|| mail.equals("回访成功") || mail.equals("有意向")) {
						num++;
					}
				}
				length[i] = String.valueOf(num);
			}

			WritableSheet ws = wb.createSheet("分析", 0);
			int startRowNum = 0; // 起始行
			int startColNum = 0; // 起始列
			int maxColSize = 8; // 最大列数
			// 设置列宽
			ws.setColumnView(0, 20);
			ws.setColumnView(1, 20);
			ws.setColumnView(2, 20);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.mergeCells(startColNum, startRowNum, startColNum + maxColSize
					- 1, startRowNum); // 合并单元格，合并(1,0)到(1,9)
			startColNum = 0;
			startRowNum++;
			// 第1行，绘制表头
			ws.addCell(new Label(startColNum, startRowNum, "日期", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "业务名称", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "总外呼量", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "成功接通量", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "成功接入台席数", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "接通率", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "日开通率", wcf_key));
			startColNum++;
			ws.addCell(new Label(startColNum, startRowNum, "日开通量", wcf_key));
			startColNum++;
			// 将行数加1，列数重置为0
			startRowNum++;
			startColNum = 0;

			// 添加记录
			String[] lg = new String[ltotal.size()];
			for (int k = 0; k < ltotal.size(); k++) {
				lg[k] = ltotal.get(k);
			}
			for (int i = 0; i < lname.size(); i++) {
				String a = lg[i];
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String newdate = format.format(new Date());
				ws.addCell(new Label(startColNum, startRowNum, newdate, wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, lname.get(i)
						.toString(), wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, a, wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, String
						.valueOf(jtl[i]), wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, String
						.valueOf(jtl[i]), wcf_key));
				startColNum++;
				NumberFormat formatter = new DecimalFormat("0.000");
				Double x = new Double(
						Double.parseDouble(String.valueOf(jtl[i]))
								/ Double.parseDouble(a) * 100);
				String xx = formatter.format(x);
				ws.addCell(new Label(startColNum, startRowNum, xx + "%",
						wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, length[i],
						wcf_key));
				startColNum++;
				Double ktv = new Double(Double.parseDouble(length[i])
						/ Double.parseDouble(String.valueOf(jtl[i])) * 100);
				String ktl = formatter.format(ktv);
				ws.addCell(new Label(startColNum, startRowNum, ktl + "%",
						wcf_key));
				startColNum++;
				// 将行数加1，列数重置为0
				startRowNum++;
				startColNum = 0;
			}
			wb.write(); // 生成Excel工作簿
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void next() throws WriteException, IOException {
		/****** 定义表格格式start *****/
		WritableFont wf_key = new jxl.write.WritableFont(
				WritableFont.createFont("微软雅黑"), 10, WritableFont.BOLD);
		WritableFont wf_value = new jxl.write.WritableFont(
				WritableFont.createFont("微软雅黑"), 10, WritableFont.NO_BOLD);
		// 设置单元格样式
		wcf_value = new WritableCellFormat(wf_value); // 单元格字体样式

		wcf_value.setAlignment(jxl.format.Alignment.CENTRE); // 单元格水平对齐样式
		wcf_value.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); // 单元格垂直对齐样式
		wcf_value.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN); // 单元格边框样式
		wcf_value_left = new WritableCellFormat(wf_value);
		wcf_value_left.setAlignment(jxl.format.Alignment.CENTRE);
		wcf_value_left
				.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcf_value_left.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN);
		wcf_value_left.setWrap(true);

		wcf_key = new WritableCellFormat(wf_key);
		wcf_key.setAlignment(jxl.format.Alignment.CENTRE);
		wcf_key.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN);
		wcf_key.setWrap(true);// 设置自动换行
		wcf_key.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); // 单元格垂直对齐样式

		wcf_name_left = new WritableCellFormat(wf_key);
		wcf_name_left.setAlignment(Alignment.CENTRE);
		wcf_name_left.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		wcf_name_right = new WritableCellFormat(wf_key);
		wcf_name_right.setAlignment(Alignment.CENTRE);

		wcf_name_center = new WritableCellFormat(wf_key);
		wcf_name_center.setAlignment(Alignment.CENTRE);

		jxl.write.NumberFormat wf_percent_float = new jxl.write.NumberFormat(
				"0.00"); // 定义单元浮点数据类型
		wcf_percent_float = new jxl.write.WritableCellFormat(wf_value,
				wf_percent_float);
		wcf_percent_float.setAlignment(jxl.format.Alignment.CENTRE);
		wcf_percent_float
				.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcf_percent_float.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN);

		WritableFont wf_title = new jxl.write.WritableFont(
				WritableFont.createFont("微软雅黑"), 24, WritableFont.NO_BOLD); // 定义标题样式
		wcf_title = new WritableCellFormat(wf_title);
		wcf_title.setAlignment(Alignment.CENTRE);
		wcf_title.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN);
		/****** 定义表格格式end *****/
		// 在指定的路径生成空白的xls文件
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dt);
		calendar.add(calendar.DATE, -1);// 把日期往后增加一天.整数往后推,负数往前移动
		dt = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String filename = pathStr + df.format(dt) + ".xls";
		System.out.println(filename);
		file = new File(filename);
		file.createNewFile();
	}

	public void gethashmap(callout_result cr1,
			org.hibernate.Session session_41, HibernateDAO daos) {
		int script_id = cr1.getScript_id();
		String hql = "from callout_script_detail where script_id=" + script_id
				+ " order by id";
		List<Object> list21 = daos.select_list(session_41, hql);
		if (list21 != null && list21.size() > 0) {
			resultMap = new HashMap<Integer, String>();
			describeMap = new HashMap<Integer, String>();
			questionMap = new HashMap<Integer, String>();
			for (int q = 0; q < list21.size(); q++) {
				callout_script_detail script_detail = (callout_script_detail) list21
						.get(q);
				int key = script_detail.getContent_sort();
				String value = script_detail.getContent_detail();
				String type = script_detail.getContent_type();

				// 单选、多选
				if ("radio".equals(type) || "checkbox".equals(type)) {
					resultMap.put(key, value);
				}
				// 描述
				else if ("describe".equals(type)) {
					describeMap.put(key, value);
				}
				// 简答
				else if ("question".equals(type)) {
					questionMap.put(key, value);
				}
			}
		}
	}

	public void start2(List<String> lname, List<callout_rdlc> rdlc)
			throws WriteException, IOException {
		URL path2 = MyTask.class.getClassLoader().getResource("");
		String str = path2.toString().substring(6, path2.toString().length());
		str = str.replaceAll("%20", " ");
		int num1 = str.indexOf("ccdms");
		str = str.substring(0, num1 + "ccdms".length()) + "/report/rb/";
		pathStr = str;
		next();
		try {
			WritableWorkbook wb = Workbook.createWorkbook(file);
			WritableSheet ws = wb.createSheet("外呼日报工时表", 0);
			int startRowNum = 0; // 起始行
			int startColNum = 0; // 起始列
			startColNum = 0;
			startRowNum++;

			String[] namel = new String[lname.size()];
			// 第1行，绘制表头
			for (int i = 0; i < lname.size(); i++) {
				ws.setColumnView(i, 20);
				namel[i] = lname.get(i);
				ws.addCell(new Label(startColNum, startRowNum, lname.get(i),
						wcf_key));
				startColNum++;
			}
			for (int i = 0; i < lname.size(); i++) {
				ws.addCell(new Label(startColNum, startRowNum, lname.get(i)
						+ "外呼量", wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, lname.get(i)
						+ "开通量", wcf_key));
				startColNum++;
				ws.addCell(new Label(startColNum, startRowNum, lname.get(i)
						+ "开通率", wcf_key));
				startColNum++;
			}
			// 将行数加1，列数重置为0
			startRowNum++;
			startColNum = 0;

			for (int j = 0; j < rdlc.size(); j++) {
				callout_rdlc rd = rdlc.get(j);
				String name = rd.getScript_name();
				int wz=0;
				// 根据脚本名称,判断显示位置
				// 1.获取所有需要显示的行数,
				// 2.获取当前任务在数组中的位置
				for(int g=0;g<namel.length;g++)
				{
					if(name.equals(namel[g]))
					{
						wz=g;
						break;
					}
				}
				ws.addCell(new Label(wz, startRowNum, rd.getAgent_id(), wcf_key));
				startColNum++; 
				ws.addCell(new Label(namel.length+(wz)*3, startRowNum, rd.getWhtotal(), wcf_key));
				startColNum++; 
				ws.addCell(new Label(namel.length+(wz)*3+1, startRowNum, rd.getKttotal(), wcf_key));
				startColNum++; 
				double a=(Double.parseDouble(rd.getKttotal())/Double.parseDouble(rd.getWhtotal()))*100;
				NumberFormat formatter = new DecimalFormat("0.000");
				String value=formatter.format(a);
				ws.addCell(new Label(namel.length+(wz)*3+2, startRowNum, value+"%", wcf_key));
				startColNum++; 
				startRowNum++;
				startColNum = 0;
			}
			wb.write(); // 生成Excel工作簿
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}