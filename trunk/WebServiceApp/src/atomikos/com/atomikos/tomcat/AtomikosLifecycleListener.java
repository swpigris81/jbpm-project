package com.atomikos.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.system.Configuration;

public class AtomikosLifecycleListener implements LifecycleListener
{
   private UserTransactionManager utm;

   public void lifecycleEvent(LifecycleEvent event)
   {
      try {
         if (Lifecycle.START_EVENT.equals(event.getType())) {
            if (utm == null) {
               utm = new UserTransactionManager();
            }
               utm.init();
         } else if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
            if (utm != null) {
               utm.close();
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
}