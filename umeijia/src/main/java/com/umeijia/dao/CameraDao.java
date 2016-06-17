package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Camera;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效。
 * 应当是直接修改字段，用update操作
 *
 *
 */
@Scope("prototype")
@Repository("cameradao")
public class CameraDao {
    public CameraDao(){

    }


    public Camera queryCamera(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.id=%ld",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Camera camera = (Camera) list.get(0);
            return camera;
        }else {
            return null;
        }
    }

    /**
     * 班级私有摄像头列表
     * **/
    public List queryPrivateCamerasList(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.class_id=%ld",class_id);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    /**
     * 一个幼儿园公共区域摄像头列表
     * **/
    public List queryPublicCamerasList(long garten_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.garten_id=%ld and ca.is_public=1",garten_id);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    public boolean addCamera(Camera camera) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(camera);
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

    public boolean updateCamera(Camera camera) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(camera);
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


    public boolean deleteCamera(long id) {
        boolean result=false;
        Camera camera = queryCamera(id);
        if(camera!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(camera);
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
