
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import mns.ConnectionProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.TableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author mulan
 */
public class Report extends javax.swing.JFrame {
    boolean isDailyReport = true; 
   //private String userName = "";
    /**
     * Creates new form daily_Report
     */
    public Report() {
        initComponents();
        // Top Panel for Date Selection
        
    }
    private void generateDailyReport() {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);

        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date!");
            return;
        }

        String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());
        String query = "SELECT medicine_name, SUM(quantity) AS quantity, AVG(price) AS price,SUM(quantity * price) AS total_cost, supplier FROM sales WHERE sale_date = ? group by medicine_name,supplier ";

        try (Connection con = ConnectionProvider.getCon();PreparedStatement pst = con.prepareStatement(query)) {
            java.util.Date today=new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(today.getTime());
           //s pst.setDate(6, sqlDate);
            pst.setString(1, selectedDate);
            ResultSet rs = pst.executeQuery();
            System.out.println("SQL Query: " + pst.toString());
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("medicine_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total_cost"),
                    rs.getString("supplier")
                });
            }
             
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    
    /*private void loadMonthlyReports(String month,String year)
    {
        DefaultTableModel model = (DefaultTableModel)mreporttable.getModel();
        model.setRowCount(0);
        try
        {
            Connection con =ConnectionProvider.getCon();
            String query = "SELECT medicine_name, quantity, price, total_cost, supplier FROM sales where month(sale_date) =? AND year(sale_date) =?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,month);
            pst.setString(2,year);

            ResultSet rs = pst.executeQuery();
            while(rs.next())
            {
                model.addRow(new Object[]{rs.getString("medicine_name"),rs.getInt("quantity"),rs.getDouble("price"),rs.getDouble("total_cost"),rs.getString("supplier")});
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Generating Report: " + e.getMessage());
        }
    }*/
     // Fetch and Display Monthly Report
    private void generateMonthlyReport() {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);

        int month = comboMonth.getSelectedIndex() + 1;
        int year = Integer.parseInt(comboYear.getSelectedItem().toString());

        String query = "SELECT medicine_name, SUM(quantity) AS total_quantity, price, SUM(quantity * price) AS total_cost, supplier " +
                       "FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ? GROUP BY medicine_name, price, supplier";

        try ( Connection con = ConnectionProvider.getCon();PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, month);
            pst.setInt(2, year);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("medicine_name"),
                    rs.getInt("total_quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total_cost"),
                    rs.getString("supplier")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    

public void exportTableToPDF(JTable table,String reportTitle ) {
    try {
        // Setup document
        Document document = new Document(PageSize.A4.rotate());
        String folderpath = "ReportPDF";

// ✅ Step 2: Create the folder if it doesn't exist
        File folder = new File(folderpath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Format current date for filename
String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

// Clean up the title to be filename-friendly
String cleanTitle = reportTitle.replace(" ", "_");

// Build the full file path using folder and report title
String fileName = folderpath + "/" + cleanTitle + "_" + timestamp + ".pdf";

        
        //String fileName = reportTitle.replace(" ", "_") + "_" +
               // new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Title and date
        Paragraph title = new Paragraph("Medical Management System - " +reportTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph date = new Paragraph("\nDate: " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
        date.setAlignment(Element.ALIGN_LEFT);
        document.add(date);

        document.add(new Paragraph(" ")); // Empty line

        // Create PDF table
        TableModel model = table.getModel();
        PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
        pdfTable.setWidthPercentage(100);

        // Add table headers
        for (int i = 0; i < model.getColumnCount(); i++) {
            PdfPCell headerCell = new PdfPCell(new Phrase(model.getColumnName(i)));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfTable.addCell(headerCell);
        }

        // Add table rows
        for (int rows = 0; rows < model.getRowCount(); rows++) {
            for (int cols = 0; cols < model.getColumnCount(); cols++) {
                pdfTable.addCell(model.getValueAt(rows, cols).toString());
            }
        }

        document.add(pdfTable);

        // Calculate total (if last column is price)
        double total = 0;
        try {
            for (int r = 0; r < model.getRowCount(); r++) {
                total += Double.parseDouble(model.getValueAt(r, model.getColumnCount() - 2).toString());
            }
            Paragraph totalPara = new Paragraph("Total: ₹" + total, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(new Paragraph(" "));
            document.add(totalPara);
        } catch (Exception e) {
            // If total column isn't numeric, skip total
        }

        document.close();
        System.out.println("PDF Generated: " + fileName);
        javax.swing.JOptionPane.showMessageDialog(null, "PDF saved as: " + fileName);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

     // Export Report to PDF
    /*private void exportReportToPDF(boolean isDaily) {
    try {
        Date today = new Date();
        String reportType = isDaily ? "Daily" : "Monthly";
        String fileSuffix = isDaily ? new SimpleDateFormat("yyyyMMdd").format(today) : new SimpleDateFormat("yyyyMM").format(today);
        String filename = reportType + "_Report_" + fileSuffix + ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Date top-left
        String formattedDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(today);
        Paragraph datePara = new Paragraph("Date: " + formattedDate, normalFont);
        datePara.setAlignment(Element.ALIGN_LEFT);
        document.add(datePara);

        // Title
        Paragraph title = new Paragraph("Medical Management System - " + reportType + " Report\n\n", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(reportTable.getColumnCount());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add column headers
        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            table.addCell(new PdfPCell(new Phrase(reportTable.getColumnName(i), boldFont)));
        }

        // Add table data
        double totalCost = 0.0;
        for (int row = 0; row < reportTable.getRowCount(); row++) {
            for (int col = 0; col < reportTable.getColumnCount(); col++) {
                Object val = reportTable.getValueAt(row, col);
                table.addCell(new PdfPCell(new Phrase(val != null ? val.toString() : "", normalFont)));

                if (col == 3) { // Assuming "Total Cost" is at index 3
                    try {
                        totalCost += Double.parseDouble(val.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        document.add(table);

        // Total
        Paragraph totalPara = new Paragraph("Total Cost: ₹ " + String.format("%.2f", totalCost), boldFont);
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        totalPara.setSpacingBefore(10f);
        document.add(totalPara);

        document.close();
        JOptionPane.showMessageDialog(null, reportType + " report saved as " + filename);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error generating PDF: " + e.getMessage());
    }
}*/


    /*private void exportReportToPDF() {
        try {
            String fileName = "Report_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            PdfPTable table = new PdfPTable(reportTable.getColumnCount());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2f, 1f, 1f, 1f, 2f});  // Column widths

            for (int i = 0; i < reportTable.getColumnCount(); i++) {
                table.addCell(new PdfPCell(new Phrase(reportTable.getColumnName(i), FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            }

            for (int row = 0; row < reportTable.getRowCount(); row++) {
                for (int col = 0; col < reportTable.getColumnCount(); col++) {
                    table.addCell(new PdfPCell(new Phrase(reportTable.getValueAt(row, col).toString())));
                }
            }

            document.add(new Paragraph("Medical Management System - Report\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(this, "Report saved as: " + fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
        }
        
    }*/
   
    
    

     
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnGenerateDaily = new javax.swing.JButton();
        lblDate = new javax.swing.JLabel();
        dateChooser = new com.toedter.calendar.JDateChooser();
        lblMonthYear = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reportTable = new javax.swing.JTable();
        comboMonth = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        comboYear = new javax.swing.JComboBox<>();
        btnGenerateMonthly = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/all_pages_background.png"))); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(850, 500));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("REPORT");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 117, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 47, 930, 10));

        btnGenerateDaily.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGenerateDaily.setText("Generate Daily Report");
        btnGenerateDaily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateDailyActionPerformed(evt);
            }
        });
        getContentPane().add(btnGenerateDaily, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, -1, -1));

        lblDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDate.setText("Select Date");
        getContentPane().add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 80, -1));

        dateChooser.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        getContentPane().add(dateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 170, -1));

        lblMonthYear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMonthYear.setText("Select Month");
        getContentPane().add(lblMonthYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 90, -1, -1));

        reportTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        reportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Medicine Name", "Quantity", "Unit Price", "Total Cost", "Supplier"
            }
        ));
        jScrollPane2.setViewportView(reportTable);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 760, 200));

        comboMonth.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        comboMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "Augast", "SEPtember", "October", "November", "December" }));
        getContentPane().add(comboMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 120, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Select Year");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 90, 90, -1));

        comboYear.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        comboYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2025", "2026", "2027", "2028", "2029", "2030" }));
        comboYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboYearActionPerformed(evt);
            }
        });
        getContentPane().add(comboYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 120, -1, -1));

        btnGenerateMonthly.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGenerateMonthly.setText("Generate Monthly Report");
        btnGenerateMonthly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateMonthlyActionPerformed(evt);
            }
        });
        getContentPane().add(btnGenerateMonthly, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 170, -1, 30));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("exportReportToPDF");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 460, 150, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/all_pages_background.png"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerateDailyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateDailyActionPerformed
        isDailyReport = true;
        generateDailyReport(); // method that loads daily data into the table
    }//GEN-LAST:event_btnGenerateDailyActionPerformed

    private void btnGenerateMonthlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateMonthlyActionPerformed
        isDailyReport = false;
         generateMonthlyReport();// method that loads monthly data into the table

    }//GEN-LAST:event_btnGenerateMonthlyActionPerformed

    private void comboYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboYearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboYearActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String reportTitle = isDailyReport ? "Daily Report" : "Monthly Report";
        exportTableToPDF(reportTable, reportTitle);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Report().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateDaily;
    private javax.swing.JButton btnGenerateMonthly;
    private javax.swing.JComboBox<String> comboMonth;
    private javax.swing.JComboBox<String> comboYear;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblMonthYear;
    private javax.swing.JTable reportTable;
    // End of variables declaration//GEN-END:variables

    
}
