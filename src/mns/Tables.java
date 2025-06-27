/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mns;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.Statement;

/**
 *
 * @author mulan
 */
public class Tables {

    public static void main(String[] args) {
        try {
            Connection con = ConnectionProvider.getCon();
            Statement st = con.createStatement();
            //st.executeUpdate("create table appuser(appuser_pk int AUTO_INCREMENT primary key,userRole varchar(200),name varchar(200),dob varchar(50),mobileNumber varchar(50),email varchar(200),userName varchar(200),password varchar(50),address varchar(200))");
            //st.executeUpdate("insert into appuser(userRole,name,dob,mobileNumber,email,userName,password,address) values('Admin','Admin','1-1-2004','1111122222','admin1@gmail.com','admin','admin','India')");
            //st.executeUpdate("create table medicine(medicine_pk int AUTO_INCREMENT primary key,uniqueId varchar(200),name varchar(200) UNIQUE NOT NULL,companyName varchar(200),quantity bigint,price bigint,mdate varchar(50),edate varchar(50))");
            //st.executeUpdate("create table bill(bill_pk int AUTO_INCREMENT primary key,billId varchar(200),patientName varchar(50), mobNumber varchar(50), doctorName varchar(50),billDate varchar(50),totalPaid bigint,generatedBy varchar(50))");
              
            //st.executeUpdate("create table patient(patient_pk int AUTO_INCREMENT primary key,uniqueId varchar(50)UNIQUE,patientName varchar(100),mobNumber varchar(50),doctorName varchar(50)");
            //st.executeUpdate("insert into patient(uniqueId,patientName,mobNumber,doctorName) values(2,'mns',12341234,'drrr')");
            st.executeUpdate("CREATE TABLE sales (sales_pk int AUTO_INCREMENT primary key,medicine_name VARCHAR(255),quantity INT,price DECIMAL(10,2),total_cost varchar(100) ,supplier varchar(100),sale_date DATE)");
            //st.executeUpdate("drop table sales");
            JOptionPane.showMessageDialog(null, "Table Created Successfully");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }
}
