package com.atomikos.tomcat;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.ResourceRef;

import com.atomikos.beans.PropertyException;
import com.atomikos.beans.PropertyUtils;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;

public class AtomikosTomcatFactoryFactory implements ObjectFactory
{
    private Log logger = LogFactory.getLog(AtomikosTomcatFactoryFactory.class);
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception
   {
      if (obj instanceof ResourceRef) {
         try {
            Reference ref = (Reference) obj;
            String beanClassName = ref.getClassName();
            Class<?> beanClass = null;
            ClassLoader tcl = Thread.currentThread().getContextClassLoader();

            if (tcl != null) {
               try {
                  beanClass = tcl.loadClass(beanClassName);
               } catch (ClassNotFoundException e) {
                  throw new NamingException("Could not load class " + beanClassName);
               }
            } else {
               try {
                  beanClass = Class.forName(beanClassName);
               } catch (ClassNotFoundException e) {
                  throw new NamingException("Could not load class " + beanClassName);
               }
            }

            if (beanClass == null) {
               throw new NamingException("Class not found: " + beanClassName);
            }

            if (AtomikosDataSourceBean.class.isAssignableFrom(beanClass)) {
               return createDataSourceBean(ref, (AtomikosDataSourceBean) beanClass.newInstance());
            } else if (AtomikosNonXADataSourceBean.class.isAssignableFrom(beanClass)) {
               return createNonXADataSourceBean(ref, (AtomikosNonXADataSourceBean) beanClass.newInstance());
            } else if (AtomikosConnectionFactoryBean.class.isAssignableFrom(beanClass)) {
               return createConnectionFactoryBean(ref, (AtomikosConnectionFactoryBean) beanClass.newInstance());
            } else {
               throw new NamingException(
                     "Class is neither an AtomikosDataSourceBean nor an AtomikosConnectionFactoryBean: "
                           + beanClassName);
            }

         } catch (InstantiationException e) {
            throw (NamingException) new NamingException(
                  "error creating AtomikosDataSourceBean or AtomikosConnectionFactoryBean").initCause(e);
         } catch (IllegalAccessException e) {
            throw (NamingException) new NamingException(
                  "error creating AtomikosDataSourceBean or AtomikosConnectionFactoryBean").initCause(e);
         } catch (JMSException e) {
            throw (NamingException) new NamingException("error creating AtomikosConnectionFactoryBean").initCause(e);
         } catch (AtomikosSQLException e) {
            throw (NamingException) new NamingException("error creating AtomikosDataSourceBean").initCause(e);
         } catch (PropertyException e) {
            throw (NamingException) new NamingException(
                  "error creating AtomikosDataSourceBean or AtomikosConnectionFactoryBean").initCause(e);
         }
      }

      return (null);
   }

   private Object createConnectionFactoryBean(Reference ref, AtomikosConnectionFactoryBean bean) throws JMSException
   {
      Enumeration<RefAddr> en = ref.getAll();

      int i = 0;
      while (en.hasMoreElements()) {
         RefAddr ra = (RefAddr) en.nextElement();
         String propName = ra.getType();

         String value = (String) ra.getContent();

         /**
          * uniqueResourceName is only unique per webapp but has to made
          * unique globally.
          */
         if (propName.equals("uniqueResourceName")) {
            //value = AtomikosLifecycleManager.getInstance().getWebappName() + "/" + value;
         }

         try {
            PropertyUtils.setProperty(bean, propName, value);
            i++;
         } catch (PropertyException pe) {
            System.out.println("Property " + propName + " could not be set. " + pe.getMessage());
         }

      }

      bean.init();
      AtomikosLifecycleManager.getInstance().addResource(bean);
      return (bean);
   }

   private Object createNonXADataSourceBean(Reference ref, AtomikosNonXADataSourceBean bean) throws AtomikosSQLException, PropertyException
   {
      if (logger.isDebugEnabled()) {
         logger.debug("instanciating bean of class " + bean.getClass().getName());
      }

      Enumeration<RefAddr> en = ref.getAll();

      int i = 0;
      while (en.hasMoreElements()) {
         RefAddr ra = (RefAddr) en.nextElement();
         String propName = ra.getType();

         String value = (String) ra.getContent();

         /**
          * uniqueResourceName is only unique per webapp but has to made
          * unique globally.
          */
         if (propName.equals("uniqueResourceName")) {
            value = AtomikosLifecycleManager.getInstance().getWebappName() + "/" + value;
         }

         if (logger.isDebugEnabled()) {
            logger.debug("setting property '" + propName + "' to '" + value + "'");
         }

         try {
            PropertyUtils.setProperty(bean, propName, value);
            i++;
         } catch (PropertyException pe) {
            System.out.println("Property " + propName + "could not be set. " + pe.getMessage());
         }
      }

      bean.init();
      AtomikosLifecycleManager.getInstance().addResource(bean);
      return (bean);
   }

   private Object createDataSourceBean(Reference ref, AtomikosDataSourceBean bean) throws AtomikosSQLException
   {
      if (logger.isDebugEnabled()) {
         logger.debug("instanciating bean of class " + bean.getClass().getName());
      }

      Enumeration<RefAddr> en = ref.getAll();

      int i = 0;

      while (en.hasMoreElements()) {
         RefAddr ra = (RefAddr) en.nextElement();
         String propName = ra.getType();
         System.out.println("propName = " + propName);
         String value = (String) ra.getContent();
         System.out.println("value = " + value);
         /**
          * uniqueResourceName is only unique per webapp but has to made
          * unique globally.
          */
         if (propName.equals("uniqueResourceName")) {
            //value = AtomikosLifecycleManager.getInstance().getWebappName() + "/" + value;
         }

         if (logger.isDebugEnabled()) {
            logger.debug("setting property '" + propName + "' to '" + value + "'");
         }
         System.out.println("setting property '" + propName + "' to '" + value + "'");
         try {
            PropertyUtils.setProperty(bean, propName, value);
            i++;
         } catch (PropertyException pe) {
            System.out.println("Property " + propName + " could not be set. " + pe.getMessage());
         }

      }

      bean.init();
      AtomikosLifecycleManager.getInstance().addResource(bean);
      return (bean);
   }
}