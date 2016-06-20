package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Agent;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 */
@Scope("prototype")
@Repository("agentdao")
public class AgentDao {
    public AgentDao(){

    }

    public Agent queryAgent(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.id=%ld", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }

    public Agent queryAgent(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }

    public Agent queryAgentByEmail(String email) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }
/*
    public boolean loginCheck(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select count(*) from Agent as u where u.phone_num=\'%s\' and u.pwd_md=\'%s\'",phoneNumber,passwd);
        Query query = session.createQuery(sql);
        int count = ((Long) query.uniqueResult()).intValue();
        session.close();
        return count > 0;

    }*/

    public boolean verifyToken(long id,String tkn){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select u.expire from Agent as u where u.id=%ld and u.token=\'%s\'",id,tkn);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Date dead=(Date)list.get(0);
            Date now=new Date();
            if(dead.after(now)){
                return  true;  //还有效
            }
        }
        return  false;
    }
    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public boolean loginCheckByPhone(String phoneNumber, String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Agent agent = (Agent) list.get(0);
            if(passwd.equals(agent.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    agent.setToken(MD5.GetSaltMD5Code(agent.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    agent.setExpire(dead);
                    session.update(agent);
                    session.flush();
                    session.getTransaction().commit();
                    result=true;
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    result=false;
                } finally{
                    session.close();
                    return result;
                }
            }
        }
        return  false;
    }

    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public boolean loginCheckByEmail(String email, String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Agent agent = (Agent) list.get(0);
            if(passwd.equals(agent.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    agent.setToken(MD5.GetSaltMD5Code(agent.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    agent.setExpire(dead);
                    session.update(agent);
                    session.flush();
                    session.getTransaction().commit();
                    result=true;
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    result=false;
                } finally{
                    session.close();
                    return result;
                }
            }
        }
        return  false;
    }

    
    public boolean addAgent(Agent agent) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(agent);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

    public boolean correctAgentPasswd(String phoneNumber,String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Agent agent set agent.pwd_md=\'%s\' where agent.phone_num=\'%s\'",passwd,phoneNumber);
            Query queryupdate=session.createQuery(hql);
            int ret=queryupdate.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            if(ret>=0)
                result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

    public boolean updateAgent(Agent agent) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(agent);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

    public boolean deleteAgent(String phoneNumber) {
        boolean result=false;
        Agent agent = queryAgent(phoneNumber);
        if(agent!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(agent);
                session.flush();
                session.getTransaction().commit();
                result=true;
            } catch (HibernateException e) {
                e.printStackTrace();
                session.getTransaction().rollback();
                result=false;
            } finally{
                session.close();
                return result;
            }
        }
        return false;
    }
}
