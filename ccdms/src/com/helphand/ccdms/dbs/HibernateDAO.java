package com.helphand.ccdms.dbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateDAO {

	private Query query = null;
	private List<Object> list = null;

	@SuppressWarnings("unchecked")
	public Object select_object(Session session, String hql, Object o) {
		try {
			if (StringUtils.startsWith(hql, "select")) {
				query = session.createSQLQuery(hql);
			} else {
				query = session.createQuery(hql);
			}
			query.setProperties(o);
			list = query.list();
			return list.get(0);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> select_list(Session session, String hql, Object o) {
		try {
			if (StringUtils.startsWith(hql, "select")) {
				query = session.createSQLQuery(hql);
			} else {
				query = session.createQuery(hql);
			}
			query.setProperties(o);
			list = query.list();
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> select_list(Session session, String hql) {
		try {
			if (StringUtils.startsWith(hql, "select")) {
				query = session.createSQLQuery(hql);
			} else {
				query = session.createQuery(hql);
			}
			list = query.list();
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	public int get_count(Session session, String hql) {
		try {
			query = session.createQuery(hql);
			return ((Number) query.uniqueResult()).intValue();
		} catch (Exception e) {
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_pages(Session session, String hql,
			int page_number, int page_size, Object o) {
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			query = session.createSQLQuery("select count(*) " + hql);
			query.setProperties(o);
			pages.setTotal_record_count(Integer.parseInt(query.list().get(0)
					.toString()));
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			query = session.createQuery(hql);
			query.setProperties(o);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_pages_lm(Session session, String hql,
			int page_number, int page_size, Object o) {
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			query = session
					.createSQLQuery("select count(distinct(agent_id)) from "
							+ hql.split("from")[1]);
			query.setProperties(o);
			pages.setTotal_record_count(Integer.parseInt(query.list().get(0)
					.toString()));
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			query = session.createQuery(hql);
			query.setProperties(o);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_page_lm(Session session, String hql,
			int page_number, int page_size, Object o) {
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			query = session
					.createSQLQuery("select count(distinct(agent_code)) from "
							+ hql.split("from")[1]);
			query.setProperties(o);
			pages.setTotal_record_count(Integer.parseInt(query.list().get(0)
					.toString()));
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			query = session.createQuery(hql);
			query.setProperties(o);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_pages_special(Session session, String hql,
			int page_number, int page_size) {
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			String sql = "select count(distinct(agent_id)) from "
					+ hql.split("from")[1];
			// sql = sql.split("group")[0];
			query = session.createSQLQuery(sql);
			pages.setTotal_record_count(Integer.parseInt(query.list().get(0)
					.toString()));
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			query = session.createSQLQuery(hql);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_pages(Session session, String hql,
			int page_number, int page_size) {
		List list = new ArrayList(); 
		int totalDb = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			query = session.createSQLQuery("select count(*) " + hql);
			pages.setTotal_record_count(Integer.parseInt(query.list().get(0)
					.toString()));
			// query.setProperties(o);
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			// query.setProperties(o);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public HibernatePageDemo select_sql_pages(Session session, String hql_head,
			String hql, int page_number, int page_size) {
		try {
			page_number = page_number <= 0 ? 1 : page_number;
			page_size = page_size <= 0 ? 15 : page_size;
			HibernatePageDemo pages = new HibernatePageDemo();
			query = session.createSQLQuery("select count(*) " + hql);
			// query.setProperties(o);
			List<Object> list = query.list();
			if (list == null || list.isEmpty()) {
				pages.setTotal_record_count(0);
			} else {
				if (list.size() > 1) {
					pages.setTotal_record_count(list.size());
				} else {
					pages.setTotal_record_count(Integer.parseInt(list.get(0)
							.toString()));
				}
			}
			pages.setPage_size(page_size);
			int i = 0;
			if (pages.getTotal_record_count() % pages.getPage_size() == 0)
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size());
			else
				pages.setTotal_page_count(pages.getTotal_record_count()
						/ pages.getPage_size() + 1);
			if (page_number > pages.getTotal_page_count())
				page_number = i;
			pages.setPage_number(page_number);
			query = session.createQuery(hql_head + hql);
			// query.setProperties(o);
			query.setFirstResult((pages.getPage_number() - 1)
					* pages.getPage_size());
			query.setMaxResults(pages.getPage_size());
			pages.setList(query.list());
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean execute_sql(Session session, String hql, Object o) {
		try {
			query = session.createSQLQuery(hql);
			query.setProperties(o);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean execute_sql(Session session, String hql) {
		try {
			query = session.createSQLQuery(hql);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void close_session(Session session) {
		try {
			session.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void commit_transaction(Transaction transaction) {
		try {
			transaction.commit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void rollback_transaction(Transaction transaction) {
		try {
			transaction.rollback();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
