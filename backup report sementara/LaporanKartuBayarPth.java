/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LaporanTransaksiHarian.java
 *
 * Created on 07 Agu 12, 13:03:28
 */

package paperman.report.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.joda.time.DateTime;
import paperman.Main;
import paperman.model.setoran;

/**
 *
 * @author Nurul Chusna
 */
public class LaporanKartuBayarPth extends javax.swing.JInternalFrame {

    private static LaporanKartuBayarPth laporanKartuPutih;

    public static void inisialisasi() {
        laporanKartuPutih = new LaporanKartuBayarPth();
    }

    public static void destroy() {
        laporanKartuPutih.dispose();
        laporanKartuPutih = null;
        System.gc();
    }

    public static LaporanKartuBayarPth getKartuPembayaranPutih() {
        return laporanKartuPutih;
    }
    /** Creates new form LaporanTransaksiHarian */
    public LaporanKartuBayarPth() {
        initComponents();
        Date tglkerja = Main.getSistemService().sistemRecord().getTglKerja();
        dateAwal.setDate(new DateTime(tglkerja.getTime()).withDayOfMonth(new DateTime(tglkerja.getTime()).dayOfMonth().getMinimumValue()).toDate());
        dateAkhir.setDate(new DateTime(tglkerja.getTime()).withDayOfMonth(new DateTime(tglkerja.getTime()).dayOfMonth().getMaximumValue()).toDate());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTitle = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dateAwal = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        btnMuat = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        pnlReport = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dateAkhir = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtLambung = new javax.swing.JTextField();

        setTitle("Laporan Rekap Kartu Pembayaran - Armada Putih");

        pnlTitle.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Laporan Kartu Pembayaran");

        javax.swing.GroupLayout pnlTitleLayout = new javax.swing.GroupLayout(pnlTitle);
        pnlTitle.setLayout(pnlTitleLayout);
        pnlTitleLayout.setHorizontalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(552, Short.MAX_VALUE))
        );
        pnlTitleLayout.setVerticalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("Tgl SPO Awal");

        btnMuat.setText("Muat");
        btnMuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMuatActionPerformed(evt);
            }
        });

        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        pnlReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout pnlReportLayout = new javax.swing.GroupLayout(pnlReport);
        pnlReport.setLayout(pnlReportLayout);
        pnlReportLayout.setHorizontalGroup(
            pnlReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 807, Short.MAX_VALUE)
        );
        pnlReportLayout.setVerticalGroup(
            pnlReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 249, Short.MAX_VALUE)
        );

        jLabel3.setText("S/D  Tgl SPO Akhir");

        jLabel4.setText("No. Lambung");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLambung, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMuat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnKeluar)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnKeluar, btnMuat});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dateAkhir, dateAwal});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtLambung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnMuat)
                                .addComponent(btnKeluar))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dateAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                            .addComponent(dateAwal, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(pnlReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnKeluar, btnMuat, dateAwal, jLabel2, jLabel3, txtLambung});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        // TODO add your handling code here:
        destroy();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnMuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuatActionPerformed
            // TODO add your handling code here:
            JasperPrint getReport = Main.getReportService().getKartuPembayaranWhiteReport(new Integer(txtLambung.getText()), dateAwal.getDate(), dateAkhir.getDate());
            JRViewer reportInstance = new JRViewer(getReport);
            reportInstance.setFitWidthZoomRatio();
            reportInstance.setBounds(this.getBounds());
            reportInstance.setSize(pnlReport.getSize());
            reportInstance.setVisible(true);
            pnlReport.removeAll();
            pnlReport.add(reportInstance, BorderLayout.CENTER);
            pnlReport.updateUI();
//            getContentPane().add(reportInstance);
//            reportInstance.setBounds(this.getBounds());
//            reportInstance.setVisible(true);
//            setSize(this.getSize());
//            updateUI();

    }//GEN-LAST:event_btnMuatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnMuat;
    private com.toedter.calendar.JDateChooser dateAkhir;
    private com.toedter.calendar.JDateChooser dateAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel pnlReport;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JTextField txtLambung;
    // End of variables declaration//GEN-END:variables

}
