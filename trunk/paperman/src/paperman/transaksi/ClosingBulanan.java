/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClosingBulanan.java
 *
 * Created on 31 Agu 12, 20:31:03
 */
package paperman.transaksi;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.joda.time.DateTime;
import paperman.Main;
import paperman.OsUtils;
import paperman.dialog.OpenClosedTrans;
import paperman.model.aksiClosing;
import paperman.model.closingBulanan;
import paperman.model.closingBulananPutih;
import paperman.model.isClosedFor;
import paperman.model.isClosedStatus;
import paperman.model.setoran;
import paperman.model.setoranDetail;
import paperman.model.setoranPutih;
import paperman.model.sistem;

/**
 *
 * @author Nurul Chusna
 */
public class ClosingBulanan extends javax.swing.JInternalFrame {

    /** Creates new form ClosingBulanan */
    private static ClosingBulanan formClosing;
    private closingBulanan closingModel;
    private closingBulananPutih closingBulananPutihModel;
    private List<closingBulanan> listClosingBulan = new ArrayList<closingBulanan>();
    private List<setoranDetail> listSetoranDetail = new ArrayList<setoranDetail>();
    private BigDecimal totalAngsuran = BigDecimal.ZERO;
    private BigDecimal totalTabungan = BigDecimal.ZERO;
    private BigDecimal totalKasbon = BigDecimal.ZERO;
    private BigDecimal totalBayarKas = BigDecimal.ZERO;
    private BigDecimal totalOvertime = BigDecimal.ZERO;
    private BigDecimal totalCicilan = BigDecimal.ZERO;
    private BigDecimal totalSetoran = BigDecimal.ZERO;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMyyyy");
    private sistem sys;
    private Object[] data;
    public static boolean closingState = false;

    public static void inisialisasi() {
        formClosing = new ClosingBulanan();
    }

    public static void destroy() {
        formClosing.dispose();
        formClosing = null;
        System.gc();
    }

    public static ClosingBulanan getFormClosing() {
        return formClosing;
    }

    public ClosingBulanan() {
        initComponents();
        Date tglkerja = Main.getSistemService().sistemRecord().getTglKerja();
        bulanClosing.setDate(tglkerja);
        sys = Main.getSistemService().sistemRecord();
    }

    private boolean cekValidClosing() {
        if (Main.getTransaksiService().findAnyClosingBulananValid(sys.getTglKerja(), isClosedFor.CLOSING_BULAN) == null) {
            return true;
        } else if (Main.getTransaksiService().findAnyLeftClosingBulananValid(sys.getTglKerja(), isClosedStatus.AVAILABLE) != null) {
            return true;
        } else {
            return false;
        }
    }

    private void setClosing() {
        if (cekValidClosing()) {
            listClosingBulan = Main.getTransaksiService().closingBulananRecordForSaldoAwal();
//            listSetoranDetail = Main.getTransaksiService().findAvailableSetoran(isClosedStatus.AVAILABLE, bulanClosing.getDate(), bulanClosing.getDate(), cmbJenisArmada.getSelectedItem().toString());
            listSetoranDetail = Main.getTransaksiService().findSetoranNoLambungReturned(bulanClosing.getDate());
            data = listSetoranDetail.toArray();
            Date dateClosing = new DateTime(bulanClosing.getDate()).minusMonths(1).dayOfMonth().withMaximumValue().toDate();
            Date monthClosing = new DateTime(bulanClosing.getDate()).minusMonths(1).toDate();
            if (!listClosingBulan.isEmpty()) {
                for (closingBulanan cb : listClosingBulan) {
                    cb.setClosedFor(isClosedFor.CLOSING_SALDO_BULAN_LALU);
                    Main.getTransaksiService().save(cb);
                }
            }
            for (int i = 0; i <= data.length - 1; i++) {
                closingModel = new closingBulanan();
                closingModel.setRefNoLambung(new Integer(data[i].toString()));
                closingModel.setRefSetoranKe(Main.getTransaksiService().findLastSetoranDetailByLambung(new Integer(data[i].toString())).get(0).getSetor_map().getCounter_setoran());
                closingModel.setTotalSetor(Main.getTransaksiService().sumSetoran(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalSetor()));
                closingModel.setTotalAngsuran(Main.getTransaksiService().sumAngsuran(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalAngsuran()));
                closingModel.setTotalTabungan(Main.getTransaksiService().sumTabungan(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalTabungan()));
                closingModel.setTotalKas(Main.getTransaksiService().sumKasbon(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalKas()));
                closingModel.setTotalBayarKas(Main.getTransaksiService().sumBayarKasbon(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalBayarKas()));
                closingModel.setTotalOvertime(Main.getTransaksiService().sumOvertime(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalOvertime()));
                closingModel.setTotalCicilan(Main.getTransaksiService().sumCicilan(new Integer(data[i].toString()), bulanClosing.getDate(), bulanClosing.getDate()).add(Main.getTransaksiService().findClosingByRefNoLambung(new Integer(data[i].toString()), monthClosing).getTotalCicilan()));
                closingModel.setPeriodeBulan(bulanClosing.getDate());
                closingModel.setTglClosing(Main.getSistemService().sistemRecord().getTglKerja());
                closingModel.setClosedFor(isClosedFor.CLOSING_SALDO_BULAN_LALU);
                closingModel.setActClosing(aksiClosing.NOPE);
                Main.getTransaksiService().save(closingModel);
            }
            closingState = true;
        } else {
            JOptionPane.showMessageDialog(this, "Maaf Anda Sudah Melakukan Closing Bulan Ini", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
            cmbJenisArmada.setEnabled(false);
            bulanClosing.setEnabled(false);
            checkBackUp.setEnabled(false);
            closingState = false;
        }
    }

    private void setClosingPutih() {
        if (closingBulananPutihModel == null) {
            closingBulananPutihModel = new closingBulananPutih();
        }
        totalSetoran = Main.getTransaksiService().sumSetoranPutih(bulanClosing.getDate());
        totalAngsuran = Main.getTransaksiService().sumAngsuranPutih(bulanClosing.getDate());
        totalTabungan = Main.getTransaksiService().sumTabunganPutih(bulanClosing.getDate());
        totalKasbon = Main.getTransaksiService().sumKasbonPutih(bulanClosing.getDate());
        totalBayarKas = Main.getTransaksiService().sumBayarKasbonPutih(bulanClosing.getDate());
        totalOvertime = Main.getTransaksiService().sumOvertimePutih(bulanClosing.getDate());
        totalCicilan = Main.getTransaksiService().sumCicilanPutih(bulanClosing.getDate());

        closingBulananPutihModel.setPeriodeBulan(bulanClosing.getDate());
        closingBulananPutihModel.setTglClosing(Main.getSistemService().sistemRecord().getTglKerja());
        closingBulananPutihModel.setTotalSetor(totalSetoran);
        closingBulananPutihModel.setTotalAngsuran(totalAngsuran);
        closingBulananPutihModel.setTotalTabungan(totalTabungan);
        closingBulananPutihModel.setTotalKas(totalKasbon);
        closingBulananPutihModel.setTotalBayarKas(totalBayarKas);
        closingBulananPutihModel.setTotalOvertime(totalOvertime);
        closingBulananPutihModel.setTotalCicilan(totalCicilan);
        closingBulananPutihModel.setClosedFor(isClosedFor.CLOSING_BULAN);
    }

    private void doBackupMysql(String host, String dbUsername, String dbPassword, String dbName, String path) {
        String backupCommand = "mysqldump -h" + host + " -u" + dbUsername + " -p" + dbPassword + " --add-drop-database -B " + dbName + " -r " + path;
        System.out.println("backup command = " + backupCommand);
        Process runtimeProcess;
        try {
            runtimeProcess = Runtime.getRuntime().exec(backupCommand);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                JOptionPane.showMessageDialog(this, "Proses Backup Database Sukses Dilakukan", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Proses Backup Database Gagal Dilakukan", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bulanClosing = new com.toedter.calendar.JDateChooser();
        checkBackUp = new javax.swing.JCheckBox();
        btnProses = new javax.swing.JButton();
        mTitlePanel1 = new id.web.martinusadyh.panel.MTitlePanel();
        cmbJenisArmada = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Proses Akhir Bulan");

        jLabel1.setText("Periode Bulan");

        jLabel3.setText("Backup Data");

        bulanClosing.setDateFormatString("MMMM yyyy");

        checkBackUp.setFont(new java.awt.Font("Tahoma", 1, 11));
        checkBackUp.setSelected(true);
        checkBackUp.setText("Ya");
        checkBackUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBackUpMouseClicked(evt);
            }
        });

        btnProses.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnProses.setText("PROSES");
        btnProses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProsesActionPerformed(evt);
            }
        });

        mTitlePanel1.setBorder(null);
        mTitlePanel1.setFontTitle(new java.awt.Font("Tahoma", 1, 14));
        mTitlePanel1.setIconHelpVisible(false);
        mTitlePanel1.setTextTitlePanel("Closing Transaksi Setoran Bulanan");

        cmbJenisArmada.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PUTIH", "BIRU" }));

        jLabel2.setText("Jenis Armada");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbJenisArmada, 0, 174, Short.MAX_VALUE)
                    .addComponent(bulanClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBackUp))
                .addGap(170, 170, 170))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(btnProses, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addComponent(mTitlePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mTitlePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJenisArmada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bulanClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(checkBackUp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProses, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesActionPerformed
        // TODO add your handling code here:
        int maxDateOfMonth = new DateTime(sys.getTglKerja()).dayOfMonth().getMaximumValue();
        System.out.println("maxDateOfMonth = " + maxDateOfMonth);
        if (bulanClosing.getDate() != null && checkBackUp.isSelected()) {
            if (cmbJenisArmada.getSelectedIndex() == 0) {
                if (new DateTime(Main.getSistemService().sistemRecord().getTglKerja().getTime()).getDayOfMonth() == maxDateOfMonth) {
                    closingState = true;
                    setClosingPutih();
                    doBackupMysql("localhost", "root", "root", "papermandb", "src\\paperman\\backupDB\\backupPutih" + dateFormatter.format(bulanClosing.getDate()) + ".sql");
                    Main.getTransaksiService().save(closingBulananPutihModel);
                    List<setoranPutih> listSetoranPutih = Main.getTransaksiService().setoranPutihRecordInCustomRange(bulanClosing.getDate());
                    for (setoranPutih stPth : listSetoranPutih) {
                        stPth.setIdClosing(closingBulananPutihModel);
                        stPth.setClosedStatus(isClosedStatus.CLOSED);
                        Main.getTransaksiService().save(stPth);
                    }
                    Main.getTransaksiService().save(closingBulananPutihModel);
                    Main.getMainMenu().setTanggalKerja(new DateTime(bulanClosing.getDate().getTime()).plusMonths(1).toDate());
                    JOptionPane.showMessageDialog(this, "Transaksi Setoran Putih Closing Bulanan Telah Dilakukan Oleh 'Set User'", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Tanggal Ini Belum Mencapai Akhir Bulan\nClosing Bulanan Tidak Dapat Dilakukan", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
                    bulanClosing.setEnabled(false);
                    checkBackUp.setEnabled(false);
                    btnProses.setEnabled(false);
                    cmbJenisArmada.setEnabled(false);
                }
            } else if (cmbJenisArmada.getSelectedIndex() == 1) {
                if (OpenClosedTrans.isUpdatingClosing == true) {
                    List<setoranDetail> listSdt = Main.getTransaksiService().findAvailableSetoran(isClosedStatus.AVAILABLE, OpenClosedTrans.lastClosing, OpenClosedTrans.lastClosing, OpenClosedTrans.lastSelectedTagArmada);
                    for (int i = 0; i <= listSdt.size() - 1; i++) {
                        closingBulanan clb = Main.getTransaksiService().findClosingByRefNoLambung(listSdt.get(i).getKemudi().getKend().getNoLambung(), OpenClosedTrans.lastClosing);
                        setoran stor = listSdt.get(i).getSetor_map();
                        stor.setClosedStatus(isClosedStatus.CLOSED);
                        stor.setIdClosing(clb);
                        Main.getTransaksiService().save(stor);
                    }
                    OpenClosedTrans.isUpdatingClosing = false;
                    if(TransaksiSetoran.getTransaksiSetoran().isVisible()) {
                        TransaksiSetoran.destroy();
                    }
                }
                if (new DateTime(Main.getSistemService().sistemRecord().getTglKerja()).getDayOfMonth() == maxDateOfMonth) {
                    setClosing();
                    if (closingState == true) {
                        List<setoran> listSetoran = Main.getTransaksiService().setoranRecordInCustomRange(bulanClosing.getDate());
                        for (setoran st : listSetoran) {
                            st.setIdClosing(Main.getTransaksiService().findClosingByRefNoLambung(st.getDetail().get(0).getKemudi().getKend().getNoLambung(), bulanClosing.getDate()));
                            st.setClosedStatus(isClosedStatus.CLOSED);
                            Main.getTransaksiService().save(st);
                        }
                        Main.getMainMenu().setTanggalKerja(new DateTime(bulanClosing.getDate().getTime()).plusMonths(1).toDate());
                        JOptionPane.showMessageDialog(this, "Transaksi Setoran Biru Closing Bulanan Telah Dilakukan Oleh 'Set User'", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
                        if (OsUtils.isWindows()) {
                            doBackupMysql("localhost", "root", "root", "papermandb", "src\\paperman\\backupDB\\backup" + dateFormatter.format(bulanClosing.getDate()) + ".sql");
                        } else if (OsUtils.isUnix()) {
                            doBackupMysql("localhost", "root", "root", "papermandb", "src/paperman/backupDB/backup" + dateFormatter.format(bulanClosing.getDate()) + ".sql");
                        }
                        destroy();
                    } else {
                        JOptionPane.showMessageDialog(this, "Transaksi Setoran Biru GAGAL Closing Bulanan", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                        closingModel = null;
                        sys = null;
                        destroy();
                    }
                } else {
                    if (OpenClosedTrans.isUpdatingClosing == false) {
                        JOptionPane.showMessageDialog(this, "Proses Closing Transaksi Yang Dibuka Sukses Dilakukan", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Tanggal Ini Belum Mencapai Akhir Bulan\nClosing Bulanan Tidak Dapat Dilakukan", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
                    }
                    bulanClosing.setEnabled(false);
                    checkBackUp.setEnabled(false);
                    btnProses.setEnabled(false);
                    cmbJenisArmada.setEnabled(false);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Harap Isi Periode Bulan Dan Pilih Backup Database", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }//GEN-LAST:event_btnProsesActionPerformed

    private void checkBackUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBackUpMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_checkBackUpMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProses;
    private com.toedter.calendar.JDateChooser bulanClosing;
    private javax.swing.JCheckBox checkBackUp;
    private javax.swing.JComboBox cmbJenisArmada;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private id.web.martinusadyh.panel.MTitlePanel mTitlePanel1;
    // End of variables declaration//GEN-END:variables
}
