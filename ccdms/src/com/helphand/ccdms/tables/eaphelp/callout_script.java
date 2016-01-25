package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="callout_script", uniqueConstraints={@UniqueConstraint(columnNames="script_name")})
@SuppressWarnings("serial")
public class callout_script implements Serializable {
	private int id;
	private int issue;// 脚本时候发布，发布后的脚本不能做修改和删除。
	private String script_name;// 脚本名称
	private String create_time;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_callout_script", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_callout_script", sequenceName = "seq_callout_script")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "issue")
	public int getIssue() {
		return issue;
	}

	public void setIssue(int issue) {
		this.issue = issue;
	}

	@Column(name = "script_name")
	public String getScript_name() {
		return script_name;
	}

	public void setScript_name(String script_name) {
		this.script_name = script_name;
	}

	@Column(name = "create_time")
	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}
