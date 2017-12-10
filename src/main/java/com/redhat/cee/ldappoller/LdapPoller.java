/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.redhat.cee.ldappoller;

import java.io.InputStream;
import java.util.Random;

import org.apache.camel.CamelContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;



/**
 * To generate random orders
 */
public class LdapPoller {

    private int count = 1;
    private Random random = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapPoller.class);

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     
    
    public InputStream ldapPoller(CamelContext camelContext) {
        int number = random.nextInt(5) + 1;
        String name = "data/order" + number + ".xml";
        
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        String sql = "select * from ldap_in_hydra";

        LOGGER.info("query ldap_in_hydra table ...");

        try {
           
            jdbc.execute(sql);
        } catch (Throwable e) {
            // ignore
        }
        LOGGER.info("In Ldap Poller ...  ");

        Connection conn = null;
        Statement stmt = null;
        try {
         
          conn = dataSource.getConnection();

          DatabaseMetaData dbm = conn.getMetaData();

          ResultSet tables = dbm.getTables(null, null, "ldap_in_hydra", null);
          if (tables.next()) {
                LOGGER.info("Table exists.");
            }
          else {
        	  LOGGER.info("Table doesn't exists.");
          }
          

          stmt = conn.createStatement();
          sql = "SELECT * FROM ldap_in_hydra";
          ResultSet rs = stmt.executeQuery(sql);
          while (rs.next()) {
            String last = rs.getString("attribute_value");

            LOGGER.info("Attribute Value: " + last);
          }
          rs.close();
          stmt.close();
          
          conn.close();
        } catch (SQLException se) {
          se.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          try {
            if (stmt != null)
              stmt.close();
            if (conn != null)
              conn.close();
          } catch (SQLException se) {
            se.printStackTrace();
          }
        }
      
        
        return generateOrderWithFileName(camelContext, name);
    }

    public InputStream generateOrderWithFileName(CamelContext camelContext, String name) {
        return camelContext.getClassResolver().loadResourceAsStream(name);
    }

    public String generateFileName() {
        return "order" + count++ + ".xml";
    }
    
    public void create() throws Exception {
        
        try {
        	LOGGER.info("In Ldap Poller create...  ");
        } catch (Throwable e) {
            
        }
    }
    
    public void destroy() throws Exception {
        
        try {
        	LOGGER.info("In Ldap Poller destory...  ");
        } catch (Throwable e) {
            
        }
    }
}
