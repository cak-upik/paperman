/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TransaksiSetoran.java
 *
 * Created on Apr 28, 2012, 3:15:51 PM
 */
package paperman.transaksi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.JDBCException;
import org.joda.time.DateTime;
import org.springframework.transaction.TransactionException;
import paperman.Main;
import paperman.TextComponentUtils;
import paperman.dialog.PilihKomposisi;
import paperman.model.JalanStatus;
import paperman.model.TransaksiStatus;
import paperman.model.closingBulanan;
import paperman.model.codeGenerator;
import paperman.model.isClosedFor;
import paperman.model.isClosedStatus;
import paperman.model.kendaraan;
import paperman.model.komposisiSetoran;
import paperman.model.pengemudi;
import paperman.model.setoran;
import paperman.model.setoranDetail;
import paperman.model.sistem;
import paperman.tablemodel.TransaksiSetoranTableModel;

/**
 *
 * @author i1440ns
 */
public class TransaksiSetoran extends javax.swing.JInternalFrame implements ListSelectionListener {

    private static TransaksiSetoran transaksiSetoran;
    private List<setoran> listSetoran = new ArrayList<setoran>();
    private List<setoranDetail> listSetoranDetail = new ArrayList<setoranDetail>();
    private List<kendaraan> listKendaraan = new ArrayList<kendaraan>();
    private List<pengemudi> listPengemudi = new ArrayList<pengemudi>();
    private List<sistem> listSistem;
    private List<closingBulanan> listClosingBulananSaldoAwal;
    private kendaraan kend;
    private pengemudi drive;
    private sistem sys;
    private setoran sto;
    private setoranDetail stoDet;
    private List<codeGenerator> generateCode;
    private codeGenerator genCode;
    private komposisiSetoran komposisisetoran;
    private static final Integer option = JOptionPane.OK_CANCEL_OPTION;
    private int setoranCounter = 0;
    private int setoranPrevious = 0;
    private DecimalFormat rupiahFormatter = new DecimalFormat("Rp ###,###.00");
    private DecimalFormat totalFormatter = new DecimalFormat("###.00");
    private static final int MIN_FONT_SIZE = 3;
    private static final int MAX_FONT_SIZE = 3;
    private BigDecimal total = new BigDecimal(0);
    private BigDecimal hutang = new BigDecimal(0);
    private BigDecimal sumSetoran = new BigDecimal(0);
    private BigDecimal hutangToday = new BigDecimal(0);
    private SimpleDateFormat formatDate = new SimpleDateFormat("ddMMyy");
    private Date current_date;
    private boolean isEditFire = false;
    Dimension ukuranFrame;
    Dimension ukuranLayar;

    public static void inisialisasi() {
        transaksiSetoran = new TransaksiSetoran();
    }

    public static void destroy() {
        transaksiSetoran.dispose();
        transaksiSetoran = null;
        System.gc();
    }

    public static TransaksiSetoran getTransaksiSetoran() {
        return transaksiSetoran;
    }

    /** Creates new form TransaksiSetoran */
    public TransaksiSetoran() {
        initComponents();
        initButtonListener();
        sys = Main.getSistemService().sistemRecord();
        int maxDay = new DateTime(new Date()).dayOfMonth().getMaximumValue();
        int currentDay = new DateTime(sys.getTglKerja()).getDayOfMonth();
        if (currentDay == maxDay) {
            if (Main.getSistemService().findBonusBulanan() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal Hari Ini Menggunakan Komposisi Bonus Bulanan\nKomposisi Bonus Bulanan Kosong, Periksa Lagi", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                InputBonusBulanan.inisialisasi();
                Main.getMainMenu().getDesktopPane().add(InputBonusBulanan.getFormInputBonusBulanan());
                ukuranLayar = Toolkit.getDefaultToolkit().getScreenSize();
                ukuranFrame = InputBonusBulanan.getFormInputBonusBulanan().getSize();
                InputBonusBulanan.getFormInputBonusBulanan().setLocation((ukuranLayar.width - ukuranFrame.width) / 2, (ukuranLayar.height - ukuranFrame.height) / 4);
                InputBonusBulanan.getFormInputBonusBulanan().setVisible(true);
                aksiKeluar();
            } else {
                komposisisetoran = Main.getSistemService().findBonusBulanan();
            }
        } else {
            komposisisetoran = new PilihKomposisi().showDialog();
        }
        listPengemudi = Main.getMasterService().kemudiRecord();
        listClosingBulananSaldoAwal = Main.getTransaksiService().closingBulananRecordForSaldoAwal();
        toolbarButtonTransaksi.defaultMode();
        enableForm(false);
        tblSetoran.setAutoCreateColumnsFromModel(false);
        TextComponentUtils.setNumericTextOnly(txtAngsuran);
        TextComponentUtils.setNumericTextOnly(txtTabungan);
        TextComponentUtils.setNumericTextOnly(txtKasbon);
        TextComponentUtils.setNumericTextOnly(txtBayarKasbon);
        TextComponentUtils.setNumericTextOnly(txtOvertime);
        TextComponentUtils.setNumericTextOnly(txtCicilan);
        TextComponentUtils.setCurrency(txtAngsuran);
        TextComponentUtils.setCurrency(txtTabungan);
        TextComponentUtils.setCurrency(txtKasbon);
        TextComponentUtils.setCurrency(txtBayarKasbon);
        TextComponentUtils.setCurrency(txtOvertime);
        TextComponentUtils.setCurrency(txtCicilan);
        LoadDatabaseToTable();
        tblSetoran.getSelectionModel().addListSelectionListener(this);
        lblJalanStatus.setVisible(false);
        checkJalanStatus.setVisible(false);
    }

    private void initButtonListener() {
        toolbarButtonTransaksi.getBtnTambah().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiTambah();
            }
        });

        toolbarButtonTransaksi.getBtnUbah().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiUbah();
            }
        });

        toolbarButtonTransaksi.getBtnHapus().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiHapus();
            }
        });

        toolbarButtonTransaksi.getBtnSimpan().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiSimpan();
            }
        });

        toolbarButtonTransaksi.getBtnCetak().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiCetak();
            }
        });

        toolbarButtonTransaksi.getBtnBatal().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiBatal();
            }
        });

        toolbarButtonTransaksi.getBtnKeluar().addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                aksiKeluar();
            }
        });
    }

    private void aksiTambah() {
        listSetoran = Main.getTransaksiService().getLatestSetoranCount();
        genCode = Main.getSistemService().findBySpecific("SETORAN", "BIRU");
        if (genCode != null && !listSetoran.isEmpty()) {
            if (!listClosingBulananSaldoAwal.isEmpty() && listClosingBulananSaldoAwal.get(0).getClosedFor() == isClosedFor.CLOSING_SALDO_AWAL) {
                setoranCounter = listClosingBulananSaldoAwal.get(0).getRefSetoranKe();
            } else {
                setoranCounter = listSetoran.get(0).getCounter_setoran();
            }
            generateCode = Main.getSistemService().generateCode(genCode.getTagTransaction(), genCode.getTagArmada(), sys.getTglKerja());
//            setoranCounter = listSetoran.get(0).getCounter_setoran();
            setoranCounter++;
            txtKodeSetoran.setText(formatDate.format(generateCode.get(0).getTanggal()) + "-" + generateCode.get(0).getTagTransaction().substring(0, 3) + "-" + generateCode.get(0).getTagArmada().substring(0, 1) + "-" + new Integer(generateCode.get(0).getLastnum() + 1));
            txtSetoranCounter.setText(String.valueOf(setoranCounter));
            txtAngsuran.setText(TextComponentUtils.formatNumber(komposisisetoran.getAngsuran()));
            txtTabungan.setText(TextComponentUtils.formatNumber(komposisisetoran.getTabungan()));
            enableForm(true);
            enableSearchEngine(false);
            toolbarButtonTransaksi.editMode();
        }
        if (genCode == null && listSetoran.isEmpty()) {
            JOptionPane.showMessageDialog(Main.getMainMenu(), "Nomor Kode Belum Di Konfigurasi\nHarap Setting Nomor Kode Pada Menu File > Konfigurasi");
            enableForm(false);
            enableSearchEngine(false);
            tblSetoran.setEnabled(false);
            toolbarButtonTransaksi.defaultMode();
        }
        if (genCode != null && listSetoran.isEmpty()) {
            if (!listClosingBulananSaldoAwal.isEmpty() && listClosingBulananSaldoAwal.get(0).getClosedFor() == isClosedFor.CLOSING_SALDO_AWAL) {
                setoranCounter = listClosingBulananSaldoAwal.get(0).getRefSetoranKe();
            } else {
                setoranCounter = 1;
            }
            generateCode = Main.getSistemService().generateCode(genCode.getTagTransaction(), genCode.getTagArmada(), sys.getTglKerja());
//            setoranCounter = 1;
            enableForm(true);
            enableSearchEngine(false);
            txtSetoranCounter.setText(String.valueOf(setoranCounter));
            txtKodeSetoran.setText(formatDate.format(generateCode.get(0).getTanggal()) + "-" + generateCode.get(0).getTagTransaction().substring(0, 3) + "-" + generateCode.get(0).getTagArmada().substring(0, 1) + "-" + new Integer(generateCode.get(0).getLastnum() + 1));
            txtAngsuran.setText(TextComponentUtils.formatNumber(komposisisetoran.getAngsuran()));
            txtTabungan.setText(TextComponentUtils.formatNumber(komposisisetoran.getTabungan()));
            List<closingBulanan> listClosing = Main.getTransaksiService().closingBulananRecord();
            if (listClosing.isEmpty()) {
                closingBulanan clb = new closingBulanan();
                clb.setPeriodeBulan(sys.getTglKerja());
                clb.setTglClosing(sys.getTglKerja());
                clb.setTotalAngsuran(new BigDecimal(0));
                clb.setTotalTabungan(new BigDecimal(0));
                clb.setTotalKas(new BigDecimal(0));
                clb.setTotalBayarKas(new BigDecimal(0));
                clb.setTotalOvertime(new BigDecimal(0));
                clb.setTotalCicilan(new BigDecimal(0));
                clb.setTotalSetor(new BigDecimal(0));
                Main.getTransaksiService().save(clb);
            }
            toolbarButtonTransaksi.firstTransaksiMode();
        }
        if (genCode == null && !listSetoran.isEmpty()) {
            JOptionPane.showMessageDialog(Main.getMainMenu(), "Terjadi Kesalahan Pada Form Transaksi\nPeriksa Lagi Apakah Nomor Kode Sudah Dikonfigurasi Atau Belum? ?", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
            enableForm(false);
            enableSearchEngine(false);
            toolbarButtonTransaksi.defaultMode();
        }
        if (!listSetoran.isEmpty() && listSetoran.get(0).getIdClosing() != null) {
            if (new DateTime(listSetoran.get(0).getIdClosing().getPeriodeBulan().getTime()).getMonthOfYear() == new DateTime(Main.getSistemService().sistemRecord().getTglKerja().getTime()).getMonthOfYear()) {
                JOptionPane.showMessageDialog(this, "Transaksi Sudah Di Closing\nTidak Bisa Melakukan Transaksi Bila Telah Di Closing", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                houseKeeping();
                enableForm(false);
                enableSearchEngine(false);
                toolbarButtonTransaksi.defaultMode();
            }
        } else {
            return;
//            enableForm(true);
//            enableSearchEngine(false);
        }
        System.out.println("stc = " + setoranCounter);
        System.out.println("stp = " + setoranPrevious);
        txtNoLambung.requestFocus();
        tblSetoran.setEnabled(false);
    }

    private void aksiUbah() {
        enableForm(true);
        enableSearchEngine(false);
//        setoranPrevious = Integer.parseInt(txtSetoranCounter.getText());
//        System.out.println("stp = " + setoranPrevious);
        this.isEditFire = true;
        txtNoLambung.requestFocus();
        txtSetoranCounter.setEnabled(false);
        toolbarButtonTransaksi.editMode();
        tblSetoran.setEnabled(false);
    }

    private void aksiHapus() {
//        setoranPrevious = Integer.parseInt(txtSetoranCounter.getText());
//        System.out.println("stp = " + setoranPrevious);
        String noLambung = stoDet.getKend().getNoLambung().toString();
        String nrp = stoDet.getKemudi().getNrp();
        String nama = stoDet.getKemudi().getNama();
        String setoranCount = sto.getCounter_setoran().toString();
        String jatuhTempo = sto.getTglJatuhTempo().toString();
        String tglSPO = sto.getTglSPO().toString();
        String kodeSetoran = sto.getKode();
        String angsuran = stoDet.getAngsuran().toString();
        String tabungan = stoDet.getTabungan().toString();
        String kasbon = stoDet.getKasbon().toString();
        String bayarKasbon = stoDet.getBayar().toString();
        String overtime = stoDet.getOvtime().toString();
        String cicilan = stoDet.getKS().toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Anda Yakin Akan Menghapus Data Ini ? ?\nRincian :\nKode Setoran = " + kodeSetoran + "\nNo.Lambung = " + noLambung + "\nNRP =  " + nrp + "\nNama = " + nama + "\nSetoran Ke = " + setoranCount + "\nTgl. Jatuh Tempo =  " + jatuhTempo + "\nTgl. SPO = " + tglSPO + "\nAngsuran = " + angsuran + "\nTabungan = " + tabungan + "\nKasbon = " + kasbon + "\nBayar Kasbon = " + bayarKasbon + "\nOver Time= " + overtime + "\nCicilan= " + cicilan, "Konfirmasi Hapus Data", option, JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi == JOptionPane.OK_OPTION) {
            Main.getTransaksiService().delete(sto);
            LoadDatabaseToTable();
            houseKeeping();
            listSetoran = Main.getTransaksiService().getLatestSetoranCount();
            if (listSetoran.isEmpty()) {
                setoranCounter = 0;
            } else {
                if (listSetoran.get(0).getTotalHutang().compareTo(BigDecimal.ZERO) != 0) {
                    sto = listSetoran.get(0);
                    sto.setPayedStatus(TransaksiStatus.U);
                    Main.getTransaksiService().save(sto);
                }
                setoranCounter = listSetoran.get(0).getCounter_setoran();
            }
            sto = null;
            stoDet = null;
        }
        if (konfirmasi == JOptionPane.CANCEL_OPTION) {
            houseKeeping();
            sto = null;
            stoDet = null;
        }
        enableForm(false);
        enableSearchEngine(true);
        houseKeeping();
        tblSetoran.getSelectionModel().clearSelection();
        tblSetoran.setEnabled(true);
        toolbarButtonTransaksi.defaultMode();
    }

    private void aksiSimpan() {
        try {
            cekDuplicate();
        } catch (JDBCException jde) {
            int konfirmasi = JOptionPane.showConfirmDialog(this, "Data Dengan Kode Sama Sudah Ada\nGanti Kode Data ? ? ?", "Gagal Menyimpan", option, JOptionPane.QUESTION_MESSAGE);
            if (konfirmasi == JOptionPane.OK_OPTION) {
                enableForm(true);
                enableSearchEngine(false);
                txtKodeSetoran.requestFocus();
                toolbarButtonTransaksi.editMode();
                tblSetoran.setEnabled(false);
            } else if (konfirmasi == JOptionPane.CANCEL_OPTION) {
                enableForm(false);
                enableSearchEngine(true);
                houseKeeping();
                toolbarButtonTransaksi.defaultMode();
                tblSetoran.setEnabled(true);
            }
        } catch (TransactionException te) {
            String error = te.getMessage();
            TextArea txtError = new TextArea();
            txtError.setText(error);
            Object messageOutput[] = {"Koneksi Database Terputus, Mohon Periksa Koneksi Dengan Server\n", "Rincian Kesalahan :", txtError};
            JOptionPane msg = new JOptionPane();
            msg.setMessage(messageOutput);
            msg.setMessageType(JOptionPane.ERROR_MESSAGE);
            JDialog dialog = msg.createDialog(this, "Terjadi Kesalahan Koneksi Database");
            dialog.setVisible(true);
            houseKeeping();
            enableForm(false);
            tblSetoran.getSelectionModel().clearSelection();
            tblSetoran.setEnabled(true);
            toolbarButtonTransaksi.defaultMode();
            sto = null;
            stoDet = null;
        }
    }

    private void cekDuplicate() {
        if (validateForm()) {
            LoadFormToDatabase();
            Main.getTransaksiService().save(sto);
            Main.getSistemService().save(genCode);
            LoadDatabaseToTable();
            houseKeeping();
            enableForm(false);
            enableSearchEngine(true);
            tblSetoran.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Proses Penyimpanan Telah Berhasil", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            tblSetoran.getSelectionModel().clearSelection();
            toolbarButtonTransaksi.defaultMode();
            sto = null;
            stoDet = null;
        } else {
            JOptionPane.showMessageDialog(this, "Terjadi Kesalahan\nGagal Menyimpan", "Pemberitahuan", JOptionPane.ERROR_MESSAGE);
            kenaTilang();
            enableForm(true);
            enableSearchEngine(false);
            tblSetoran.setEnabled(false);
            toolbarButtonTransaksi.editMode();
        }
    }

    private void aksiBatal() {
//        if (setoranCounter != listSetoran.get(0).getCounter_setoran()) {
//            setoranCounter = listSetoran.get(0).getCounter_setoran();
//        }
//        listSetoran = Main.getTransaksiService().getLatestSetoranCount();
//        setoranCounter = listSetoran.get(0).getCounter_setoran();
//        System.out.println("stc = " + setoranCounter);
//        System.out.println("stp = " + setoranPrevious);
        houseKeeping();
        enableForm(false);
        enableSearchEngine(true);
        tblSetoran.getSelectionModel().clearSelection();
        tblSetoran.setEnabled(true);
        sto = null;
        stoDet = null;
//        listSetoran = null;
//        listSetoranDetail = null;
        toolbarButtonTransaksi.defaultMode();
    }

    private void aksiCetak() {
        int pil = JOptionPane.YES_NO_OPTION;
        int konfirm = JOptionPane.showConfirmDialog(this, "Pastikan Kertas Dan Printer Telah Terpasang Dengan Baik\nLanjutkan? ?", "Peringatan Sistem", pil, JOptionPane.QUESTION_MESSAGE);
        if (konfirm == JOptionPane.YES_OPTION) {
            JasperPrint loadReport = Main.getReportService().getTandaTerimaReport(new Integer(txtNoLambung.getText()), new Integer(txtSetoranCounter.getText()));
            JasperViewer.setDefaultLookAndFeelDecorated(true);
            JasperViewer.viewReport(loadReport, false);
        }
        if (konfirm == JOptionPane.NO_OPTION) {
            toolbarButtonTransaksi.defaultMode();
            return;
        }
    }

    private void aksiKeluar() {
        enableForm(false);
        enableSearchEngine(true);
        houseKeeping();
        tblSetoran.getSelectionModel().clearSelection();
        tblSetoran.setEnabled(false);
        toolbarButtonTransaksi.keluarMode();
        destroy();
        sto = null;
        stoDet = null;
        listSetoran = null;
        listSetoranDetail = null;
    }

    private void enableForm(boolean en) {
        txtNoLambung.setEnabled(en);
        txtNRP.setEnabled(en);
        txtNama.setEnabled(en);
        txtSetoranCounter.setEnabled(en);
        dateJatuhTempo.setEnabled(en);
        dateSPO.setEnabled(en);
        txtKeterangan.setEnabled(en);
        txtAngsuran.setEnabled(en);
        txtKodeSetoran.setEnabled(en);
        txtTabungan.setEnabled(en);
        txtKasbon.setEnabled(en);
        txtBayarKasbon.setEnabled(en);
        txtOvertime.setEnabled(en);
        txtCicilan.setEnabled(en);
        pnlTerimaKasir.setEnabled(en);
        pnlHutang.setEnabled(en);
        lblNominal.setEnabled(en);
        lblHutang.setEnabled(en);
        lblRupiahHutang.setEnabled(en);
        lblRupiahKasir.setEnabled(en);
    }

    private void enableSearchEngine(boolean es) {
        txtCariSetoranLambung.setEnabled(es);
        dateCariSetoran.setEnabled(es);
        tblSetoran.setEnabled(es);
        txtCariSetoranLambung.setText("");
        dateCariSetoran.setDate(null);
    }

    private void houseKeeping() {
        txtNoLambung.setText("");
        txtNRP.setText("");
        txtNama.setText("");
        txtSetoranCounter.setText("");
        dateJatuhTempo.setDate(null);
        dateSPO.setDate(null);
        txtKeterangan.setText("");
        txtKodeSetoran.setText("");
        txtAngsuran.setText("");
        txtTabungan.setText("");
        txtKasbon.setText("");
        txtBayarKasbon.setText("");
        txtOvertime.setText("");
        txtCicilan.setText("");
        lblNominal.setText("");
        lblHutang.setText("");
        lblRupiahHutang.setText("");
        lblRupiahKasir.setText("");
        lblJalanStatus.setVisible(false);
        checkJalanStatus.setSelected(false);
        checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.PLAIN, 13));
        checkJalanStatus.setText("Tidak/Rusak");
        checkJalanStatus.setVisible(false);
    }

    private void kenaTilang() {
        if (txtNoLambung.getText().isEmpty()
                && txtKodeSetoran.getText().isEmpty()
                && txtKasbon.getText().isEmpty()
                && txtBayarKasbon.getText().isEmpty()
                && txtOvertime.getText().isEmpty()
                && txtCicilan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Tidak boleh ada field yang kosong \nPeriksa Lagi !!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtNoLambung.setForeground(Color.RED);
            txtNoLambung.setText("TIDAK BOLEH KOSONG...");
            txtKodeSetoran.setForeground(Color.RED);
            txtKodeSetoran.setText("TIDAK BOLEH KOSONG...");
            txtKasbon.setForeground(Color.RED);
            txtKasbon.setText("TIDAK BOLEH KOSONG...");
            txtBayarKasbon.setForeground(Color.RED);
            txtBayarKasbon.setText("TIDAK BOLEH KOSONG...");
            txtOvertime.setForeground(Color.RED);
            txtOvertime.setText("TIDAK BOLEH KOSONG...");
            txtCicilan.setForeground(Color.RED);
            txtCicilan.setText("TIDAK BOLEH KOSONG...");
        } else if (txtNoLambung.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi No. Lambung !!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtNoLambung.requestFocus();
            txtNoLambung.setForeground(Color.RED);
            txtNoLambung.setText("TIDAK BOLEH KOSONG...");
        } else if (txtKodeSetoran.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Kode Setoran !!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtKodeSetoran.requestFocus();
            txtKodeSetoran.setForeground(Color.RED);
            txtKodeSetoran.setText("TIDAK BOLEH KOSONG...");
        } else if (txtAngsuran.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Nilai Angsuran!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtAngsuran.requestFocus();
            txtAngsuran.setForeground(Color.RED);
            txtAngsuran.setText("TIDAK BOLEH KOSONG...");
        } else if (txtTabungan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Nilai Tabungan !!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtTabungan.requestFocus();
            txtTabungan.setForeground(Color.RED);
            txtTabungan.setText("TIDAK BOLEH KOSONG...");
        } else if (txtKasbon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Nilai Kasbon!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtKasbon.requestFocus();
            txtKasbon.setForeground(Color.RED);
            txtKasbon.setText("TIDAK BOLEH KOSONG...");
        } else if (txtBayarKasbon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Pembayaran Kasbon!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtBayarKasbon.requestFocus();
            txtBayarKasbon.setForeground(Color.RED);
            txtBayarKasbon.setText("TIDAK BOLEH KOSONG...");
        } else if (txtOvertime.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Nilai Overtime!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtOvertime.requestFocus();
            txtOvertime.setForeground(Color.RED);
            txtOvertime.setText("TIDAK BOLEH KOSONG...");
        } else if (txtCicilan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi Nilai Cicilan!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            txtCicilan.requestFocus();
            txtCicilan.setForeground(Color.RED);
            txtCicilan.setText("TIDAK BOLEH KOSONG...");
        } else if (dateJatuhTempo.getDate() == null) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi tanggal jatuh tempo !!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            dateJatuhTempo.setBackground(Color.RED);
            dateJatuhTempo.requestFocusInWindow();
        } else if (dateSPO.getDate() == null) {
            JOptionPane.showMessageDialog(this, "!!! Harap isi tanggal SPO!!!", "Terjadi Error", JOptionPane.ERROR_MESSAGE);
            dateSPO.setBackground(Color.RED);
            dateSPO.requestFocusInWindow();
        }
    }

    private void LoadFormToDatabase() {
        if (sto == null) {
            sto = new setoran();
            stoDet = new setoranDetail();
        }
        sto.setKode(txtKodeSetoran.getText());
        sto.setTglJatuhTempo(dateJatuhTempo.getDate());
        sto.setTglSPO(dateSPO.getDate());
        sto.setTglSetoran(sys.getTglKerja());
        sto.setCounter_setoran(new Integer(txtSetoranCounter.getText()));
        sto.setTotalSetoran(TextComponentUtils.parseNumberToBigDecimal(lblNominal.getText().substring(3)));
        stoDet.setKemudi(drive);
        stoDet.setKend(kend);
        stoDet.setAngsuran(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText()));
        stoDet.setTabungan(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
        stoDet.setKasbon(TextComponentUtils.parseNumberToBigDecimal(txtKasbon.getText()));
        stoDet.setBayar(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText()));
        stoDet.setOvtime(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText()));
        stoDet.setKS(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
        stoDet.setKet(txtKeterangan.getText());
        stoDet.setSetor_map(sto);
        listSetoranDetail.add(stoDet);
        sto.setDetail(listSetoranDetail);
        sto.setClosedStatus(isClosedStatus.AVAILABLE);
        if (sto.getPayedStatus() == TransaksiStatus.L) {
            List<setoran> listStats = Main.getTransaksiService().findStatusSetoranByLambung(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
            for (setoran st : listStats) {
                st.setPayedStatus(TransaksiStatus.L);
                Main.getTransaksiService().save(st);
                System.out.println("id= " + st.getId());
                System.out.println("status = " + st.getPayedStatus());
            }
        }
        if (sto.getPayedStatus() == TransaksiStatus.U) {
            List<setoran> listStats = Main.getTransaksiService().findStatusSetoranByLambung(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
            for (setoran st : listStats) {
                st.setPayedStatus(TransaksiStatus.L);
                Main.getTransaksiService().save(st);
                System.out.println("id= " + st.getId());
                System.out.println("status = " + st.getPayedStatus());
            }
            sto.setPayedStatus(TransaksiStatus.U);
        }
        if (sto.getJalanStatus() == JalanStatus.J) {
            sto.setJalanStatus(JalanStatus.J);
        } else {
            sto.setJalanStatus(JalanStatus.M);
        }
        System.out.println("jalan status now = " + sto.getJalanStatus());
        if (!lblHutang.getText().isEmpty() && !lblHutang.getText().equalsIgnoreCase("Belum Ada Hutang")) {
            sto.setTotalHutang(TextComponentUtils.parseNumberToBigDecimal(lblHutang.getText().substring(3)));
        } else {
            sto.setTotalHutang(BigDecimal.ZERO);
        }
        if (isEditFire) {
            genCode.setId(genCode.getId());
            genCode.setLastnum(genCode.getLastnum());
        } else {
            genCode.setId(genCode.getId());
            genCode.setLastnum(genCode.getLastnum() + 1);
        }
        if (!listClosingBulananSaldoAwal.isEmpty()) {
            closingBulanan clb = listClosingBulananSaldoAwal.get(0);
            clb.setRefSetoranKe(new Integer(txtSetoranCounter.getText()));
            Main.getTransaksiService().save(clb);
        }
    }

    private void LoadDataListSetoranDetail() {
        for (int i = 0; i < listSetoranDetail.size(); i++) {
            listSetoranDetail.get(i).setAngsuran(new BigDecimal(txtAngsuran.getText()));
            listSetoranDetail.get(i).setTabungan(new BigDecimal(txtTabungan.getText()));
            listSetoranDetail.get(i).setKasbon(new BigDecimal(txtKasbon.getText()));
            listSetoranDetail.get(i).setBayar(new BigDecimal(txtBayarKasbon.getText()));
            listSetoranDetail.get(i).setOvtime(new BigDecimal(txtOvertime.getText()));
            listSetoranDetail.get(i).setKS(new BigDecimal(txtCicilan.getText()));
        }
    }

    private void LoadDatabaseToForm() {
        kend.setId(stoDet.getKemudi().getKend().getId());
        drive.setId(stoDet.getKemudi().getId());
        txtNoLambung.setText(stoDet.getKemudi().getKend().getNoLambung().toString());
        txtNRP.setText(stoDet.getKemudi().getNrp());
        txtNama.setText(stoDet.getKemudi().getNama());
        txtSetoranCounter.setText(stoDet.getSetor_map().getCounter_setoran().toString());
        dateJatuhTempo.setDate(stoDet.getSetor_map().getTglJatuhTempo());
        dateSPO.setDate(stoDet.getSetor_map().getTglSPO());
        txtKeterangan.setText(stoDet.getKet());
        txtKodeSetoran.setText(stoDet.getSetor_map().getKode());
        txtAngsuran.setText(TextComponentUtils.formatNumber(stoDet.getAngsuran()));
        txtTabungan.setText(TextComponentUtils.formatNumber(stoDet.getTabungan()));
        txtKasbon.setText(TextComponentUtils.formatNumber(stoDet.getKasbon()));
        txtBayarKasbon.setText(TextComponentUtils.formatNumber(stoDet.getBayar()));
        txtOvertime.setText(TextComponentUtils.formatNumber(stoDet.getOvtime()));
        txtCicilan.setText(TextComponentUtils.formatNumber(stoDet.getKS()));
        lblNominal.setText(rupiahFormatter.format(stoDet.getSetor_map().getTotalSetoran()));
        lblHutang.setText(rupiahFormatter.format(stoDet.getSetor_map().getTotalHutang()));
        lblJalanStatus.setVisible(true);
        lblJalanStatus.setEnabled(false);
        if (stoDet.getSetor_map().getJalanStatus() == JalanStatus.J) {
            checkJalanStatus.setVisible(true);
            checkJalanStatus.setEnabled(false);
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.BOLD, 13));
            checkJalanStatus.setText("Jalan");
            checkJalanStatus.setSelected(true);
        } else {
            checkJalanStatus.setVisible(true);
            checkJalanStatus.setEnabled(false);
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.PLAIN, 13));
            checkJalanStatus.setText("Tidak/Rusak");
            checkJalanStatus.setSelected(false);
        }
    }

    private void LoadDatabaseToTable() {
        listSetoran = Main.getTransaksiService().findAvailableSetoran(isClosedStatus.AVAILABLE);
        listSetoranDetail = Main.getTransaksiService().setoranDetailRecord();
        tblSetoran.setModel(new TransaksiSetoranTableModel(listSetoran, listSetoranDetail));
        initColumnSize();
    }

    private boolean validateForm() {
        if (!txtNoLambung.getText().isEmpty() && !txtNoLambung.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtKodeSetoran.getText().isEmpty() && !txtKodeSetoran.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtAngsuran.getText().isEmpty() && !txtAngsuran.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtTabungan.getText().isEmpty() && !txtTabungan.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtKasbon.getText().isEmpty() && !txtKasbon.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtBayarKasbon.getText().isEmpty() && !txtBayarKasbon.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtOvertime.getText().isEmpty() && !txtOvertime.getText().equals("TIDAK BOLEH KOSONG...")
                && !txtCicilan.getText().isEmpty() && !txtCicilan.getText().equals("TIDAK BOLEH KOSONG...")
                && dateSPO.getDate() != null && dateJatuhTempo.getDate() != null) {
            return true;
        }
        return false;
    }

    private boolean validateHutang() {
        if (!txtBayarKasbon.getText().equalsIgnoreCase("0")
                && !txtBayarKasbon.getText().isEmpty()
                && !txtOvertime.getText().isEmpty()
                && !txtCicilan.getText().isEmpty()) {
            return true;
        } else if (txtBayarKasbon.getText().isEmpty()
                && txtOvertime.getText().isEmpty()
                && txtCicilan.getText().isEmpty()) {
            return false;
        } else {
            return false;
        }
    }

    private void initColumnSize() {
        tblSetoran.getColumnModel().getColumn(0).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(0).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(1).setMinWidth(100);
        tblSetoran.getColumnModel().getColumn(1).setMaxWidth(100);
        tblSetoran.getColumnModel().getColumn(2).setMinWidth(100);
        tblSetoran.getColumnModel().getColumn(2).setMaxWidth(100);
        tblSetoran.getColumnModel().getColumn(3).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(3).setMaxWidth(150);
        tblSetoran.getColumnModel().getColumn(4).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(4).setMaxWidth(150);
        tblSetoran.getColumnModel().getColumn(5).setMinWidth(180);
        tblSetoran.getColumnModel().getColumn(5).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(6).setMinWidth(180);
        tblSetoran.getColumnModel().getColumn(6).setMaxWidth(200);
    }

    public void valueChanged(ListSelectionEvent lse) {
        if (tblSetoran.getSelectedRow() >= 0) {
            stoDet = listSetoranDetail.get(tblSetoran.getSelectedRow());
            sto = stoDet.getSetor_map();
            kend = new kendaraan();
            drive = new pengemudi();
            genCode = Main.getSistemService().findBySpecific("SETORAN", "BIRU");
            LoadDatabaseToForm();
            toolbarButtonTransaksi.tableMode();
            enableForm(false);
            enableSearchEngine(true);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCariSetoranLambung = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dateCariSetoran = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSetoran = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        btnOpenClosedTransaction = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNoLambung = new javax.swing.JTextField();
        txtNRP = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        txtSetoranCounter = new javax.swing.JTextField();
        dateJatuhTempo = new com.toedter.calendar.JDateChooser();
        dateSPO = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAngsuran = new javax.swing.JTextField();
        txtTabungan = new javax.swing.JTextField();
        txtKasbon = new javax.swing.JTextField();
        txtBayarKasbon = new javax.swing.JTextField();
        txtOvertime = new javax.swing.JTextField();
        txtCicilan = new javax.swing.JTextField();
        pnlTerimaKasir = new javax.swing.JPanel();
        lblNominal = new javax.swing.JLabel();
        lblRupiahKasir = new javax.swing.JLabel();
        pnlHutang = new javax.swing.JPanel();
        lblHutang = new javax.swing.JLabel();
        lblRupiahHutang = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        txtKodeSetoran = new javax.swing.JTextField();
        lblJalanStatus = new javax.swing.JLabel();
        checkJalanStatus = new javax.swing.JCheckBox();
        toolbarButtonTransaksi = new paperman.transaksi.toolbarButtonTransaksi();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Transaksi Setoran - Armada Biru");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel1.setText("Cari Setoran Lambung");

        txtCariSetoranLambung.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariSetoranLambungKeyReleased(evt);
            }
        });

        jLabel2.setText("Tgl SPO");

        dateCariSetoran.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateCariSetoranPropertyChange(evt);
            }
        });

        tblSetoran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "  Kode Setoran", "  Setoran Ke", "  No. Lambung", "  Tgl. Jatuh Tempo", "  Tgl. SPO", "  Total Setoran", "  Total Hutang"
            }
        ));
        tblSetoran.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblSetoran);
        tblSetoran.getColumnModel().getColumn(0).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(0).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(1).setMinWidth(100);
        tblSetoran.getColumnModel().getColumn(1).setMaxWidth(100);
        tblSetoran.getColumnModel().getColumn(2).setMinWidth(100);
        tblSetoran.getColumnModel().getColumn(2).setMaxWidth(100);
        tblSetoran.getColumnModel().getColumn(3).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(3).setMaxWidth(150);
        tblSetoran.getColumnModel().getColumn(4).setMinWidth(150);
        tblSetoran.getColumnModel().getColumn(4).setMaxWidth(150);
        tblSetoran.getColumnModel().getColumn(5).setMinWidth(180);
        tblSetoran.getColumnModel().getColumn(5).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(6).setMinWidth(180);
        tblSetoran.getColumnModel().getColumn(6).setMaxWidth(200);

        jPanel4.setBackground(new java.awt.Color(0, 73, 255));

        jLabel12.setFont(new java.awt.Font("BPG Chveulebrivi", 1, 13));
        jLabel12.setForeground(new java.awt.Color(254, 254, 254));
        jLabel12.setText("  Data Setoran");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addContainerGap(1071, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        btnOpenClosedTransaction.setText("Buka Closed Transaksi");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCariSetoranLambung, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateCariSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOpenClosedTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(144, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1162, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtCariSetoranLambung)
                        .addComponent(dateCariSetoran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnOpenClosedTransaction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jPanel5.setBackground(new java.awt.Color(0, 73, 255));

        jLabel13.setFont(new java.awt.Font("BPG Chveulebrivi", 1, 13));
        jLabel13.setForeground(new java.awt.Color(254, 254, 254));
        jLabel13.setText("  Input Setoran");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addContainerGap(1068, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jLabel3.setText("No. Lambung");

        jLabel4.setText("NRP");

        jLabel5.setText("Nama");

        jLabel6.setText("Setoran Ke");

        jLabel7.setText("Jatuh Tempo");

        jLabel8.setText("Tgl. SPO");

        jLabel9.setText("Keterangan");

        txtNoLambung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtNoLambungMouseClicked(evt);
            }
        });
        txtNoLambung.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoLambungKeyReleased(evt);
            }
        });

        jLabel10.setText("Angsuran Setoran");

        jLabel11.setText("Tabungan");

        jLabel14.setText("Kas Bon");

        jLabel15.setText("Pembayaran Kas Bon");

        jLabel16.setText("Over Time");

        jLabel17.setText("Cicilan KS/BS/KB");

        txtAngsuran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtAngsuranMouseClicked(evt);
            }
        });

        txtTabungan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTabunganMouseClicked(evt);
            }
        });

        txtKasbon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtKasbonMouseClicked(evt);
            }
        });
        txtKasbon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKasbonKeyReleased(evt);
            }
        });

        txtBayarKasbon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBayarKasbonMouseClicked(evt);
            }
        });

        txtOvertime.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtOvertimeMouseClicked(evt);
            }
        });

        txtCicilan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCicilanMouseClicked(evt);
            }
        });
        txtCicilan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCicilanKeyReleased(evt);
            }
        });

        pnlTerimaKasir.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Terima Kasir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        lblNominal.setFont(new java.awt.Font("Tahoma", 1, 24));
        lblNominal.setText("Nominal");
        lblNominal.setAutoscrolls(true);

        lblRupiahKasir.setText("Rupiah");

        javax.swing.GroupLayout pnlTerimaKasirLayout = new javax.swing.GroupLayout(pnlTerimaKasir);
        pnlTerimaKasir.setLayout(pnlTerimaKasirLayout);
        pnlTerimaKasirLayout.setHorizontalGroup(
            pnlTerimaKasirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTerimaKasirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTerimaKasirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNominal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addComponent(lblRupiahKasir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTerimaKasirLayout.setVerticalGroup(
            pnlTerimaKasirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTerimaKasirLayout.createSequentialGroup()
                .addComponent(lblNominal, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRupiahKasir, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHutang.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Hutang Hari Ini", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        lblHutang.setFont(new java.awt.Font("Tahoma", 1, 24));
        lblHutang.setText("Nominal");

        lblRupiahHutang.setText("Rupiah");

        javax.swing.GroupLayout pnlHutangLayout = new javax.swing.GroupLayout(pnlHutang);
        pnlHutang.setLayout(pnlHutangLayout);
        pnlHutangLayout.setHorizontalGroup(
            pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHutangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRupiahHutang, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHutangLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblHutang, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlHutangLayout.setVerticalGroup(
            pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHutangLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblRupiahHutang, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHutangLayout.createSequentialGroup()
                    .addComponent(lblHutang, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(67, Short.MAX_VALUE)))
        );

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane3.setViewportView(txtKeterangan);

        jLabel18.setText("Kode Setoran");

        txtKodeSetoran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtKodeSetoranMouseClicked(evt);
            }
        });

        lblJalanStatus.setText("Jalan Status :");

        checkJalanStatus.setText("Tidak/Rusak");
        checkJalanStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkJalanStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNoLambung, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblJalanStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkJalanStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, Short.MAX_VALUE))
                    .addComponent(txtNRP, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(txtSetoranCounter, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(dateJatuhTempo, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(dateSPO, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAngsuran, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(txtTabungan, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(txtKasbon, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(txtBayarKasbon, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(txtOvertime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKodeSetoran, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(txtCicilan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlTerimaKasir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHutang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtNama, txtSetoranCounter});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAngsuran, txtBayarKasbon, txtCicilan, txtKasbon, txtKodeSetoran, txtOvertime, txtTabungan});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dateJatuhTempo, dateSPO});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pnlHutang, pnlTerimaKasir});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNoLambung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblJalanStatus)
                            .addComponent(checkJalanStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNRP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSetoranCounter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateJatuhTempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dateSPO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKodeSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtTabungan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtKasbon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtBayarKasbon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtOvertime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(pnlTerimaKasir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlHutang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtNRP, txtNama, txtNoLambung, txtSetoranCounter});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dateJatuhTempo, dateSPO});

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setRightComponent(jSplitPane2);

        toolbarButtonTransaksi.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jSplitPane1.setLeftComponent(toolbarButtonTransaksi);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNoLambungKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoLambungKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyChar() == '\n') {
            drive = new pengemudi();
            kend = new kendaraan();
            int i = 0;
            checkJalanStatus.setVisible(true);
            checkJalanStatus.setEnabled(true);
            lblJalanStatus.setVisible(true);
            lblJalanStatus.setEnabled(true);
            for (; i < listPengemudi.size(); i++) {
                if (listPengemudi.get(i).getKend().getNoLambung().toString().startsWith(txtNoLambung.getText())) {
                    txtNRP.setText(listPengemudi.get(i).getNrp());
                    txtNama.setText(listPengemudi.get(i).getNama());
                    List<setoran> listSTO = Main.getTransaksiService().findLastTglJatuhTempo(JalanStatus.J);
                    if (!listSTO.isEmpty()) {
                        dateJatuhTempo.setDate(new DateTime(listSTO.get(0).getTglJatuhTempo().getTime()).plusDays(1).toDate());
                        dateSPO.setDate(sys.getTglKerja());
                    } else {
                        if (listSetoran.isEmpty()) {
                            dateJatuhTempo.setDate(listPengemudi.get(i).getKend().getTglJatuhTempo());
                        } else {
                            dateJatuhTempo.setDate(Main.getTransaksiService().findLastTglJatuhTempo(JalanStatus.M).get(0).getTglJatuhTempo());
                        }
                        dateSPO.setDate(sys.getTglKerja());
                    }
                    txtKeterangan.setText(listPengemudi.get(i).getKend().getKeterangan());
                    drive.setId(listPengemudi.get(i).getId());
                    kend.setId(listPengemudi.get(i).getKend().getId());
                }
            }
            if (!listSetoran.isEmpty()) {
                hutang = Main.getTransaksiService().sumHutang(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
//                sumSetoran = Main.getTransaksiService().sumSetoran(new Integer(txtNoLambung.getText()));
                if (hutang != null && hutang.compareTo(BigDecimal.ZERO) <= 0) {
                    hutangToday = BigDecimal.ZERO;
                    System.out.println("Hutang compare = " + hutang);
                } else if (hutang != null && hutang.compareTo(BigDecimal.ZERO) > 0) {
//                    hutangToday = sumSetoran.subtract(hutang);
                    hutangToday = hutang;
                    if (hutangToday.signum() == -1) {
                        hutangToday = hutangToday.negate();
                        System.out.println("hutang today= " + hutangToday.signum());
                    }
                } else {
                    hutang = new BigDecimal(0);
                    hutangToday = new BigDecimal(0);
                }

                System.out.println("Hutang = " + hutang);
                System.out.println("SumSetoran = " + sumSetoran);
                System.out.println("Total Hutang Hari Ini = " + hutangToday);
                lblHutang.setText(rupiahFormatter.format(hutangToday));
                lblRupiahHutang.setText("Total Yang Harus Di Bayar = " + hutangToday.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText())));
            } else {
                hutangToday = BigDecimal.ZERO;
            }
        }
        if (txtNoLambung.getText().isEmpty()) {
            txtNRP.setText("");
            txtNama.setText("");
            dateJatuhTempo.setDate(null);
            dateSPO.setDate(null);
            txtKeterangan.setText("");
            return;
        }
    }//GEN-LAST:event_txtNoLambungKeyReleased

    private void txtCicilanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCicilanKeyReleased
        // TODO add your handling code here:

        // ---------- KEADAAN BERHUTANG ---------------//
        int labelOverSize = 245;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BigDecimal totalKasbon = new BigDecimal(0);
            BigDecimal totalAngsuran = new BigDecimal(0);
            BigDecimal totalHutang = new BigDecimal(0);
            BigDecimal mustPaid = new BigDecimal(0);
            if (sto == null) {
                sto = new setoran();
                stoDet = new setoranDetail();
            }

            // ------- SAAT JUMLAH TRANSAKSI TIDAK KOSONG DAN HUTANG HARI INI LEBIH DARI 0 ------ //
            if (!listSetoran.isEmpty() || !listSetoran.isEmpty() && hutangToday.compareTo(BigDecimal.ZERO) > 0) {

                // ----- CEK APAKAH KOMPONEN HUTANG(KASBON, BAYAR KASBON, OVERTIME, CICILAN) TIDAK KOSONG -------- //
                if (validateHutang()) {
//            if (lblNominal.getSize().width > labelOverSize) {
//                lblNominal.setFont(new Font("BPG Chveulebrivi", Font.BOLD, 18));
//                lblNominal.repaint();
//                pnlTerimaKasir.updateUI();
//
//            } else {
//                lblNominal.setFont(new Font("BPG Chveulebrivi", Font.BOLD, 24));
//                lblNominal.repaint();
//                pnlTerimaKasir.updateUI();
//            }
//            sto = Main.getTransaksiService().findByNoLambung(new Integer(txtNoLambung.getText()));
                    totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                    mustPaid = hutangToday.add(totalAngsuran);
                    System.out.println("must Paid=" + mustPaid);
                    totalKasbon = totalKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
                    totalHutang = totalHutang.add(totalKasbon.subtract(mustPaid));
                    if (totalHutang.signum() == -1) {
                        totalHutang = totalHutang.negate();
                    }
                    if (totalHutang.compareTo(BigDecimal.ZERO) == 0) {
                        lblNominal.setText(rupiahFormatter.format(totalKasbon));
                        lblHutang.setText(rupiahFormatter.format(totalHutang));
                        txtKasbon.setText("0");
                        sto.setPayedStatus(TransaksiStatus.L);
                        System.out.println("payed = " + sto.getPayedStatus());
                    } else {
//                        if (totalKasbon.compareTo(mustPaid) > 0) {
//                            JOptionPane.showMessageDialog(this, "Pembayaran Tidak Boleh Lebih Besar Dari Jumlah Yang Harus Dibayar = " + mustPaid, "Pesan Sistem", JOptionPane.WARNING_MESSAGE);
//                            txtBayarKasbon.requestFocus();
//                        } else {
                            lblNominal.setText(rupiahFormatter.format(totalKasbon));
                            lblHutang.setText(rupiahFormatter.format(totalHutang));
                            txtKasbon.setText(totalAngsuran.toString());
                            sto.setPayedStatus(TransaksiStatus.U);
                            System.out.println("payed = " + sto.getPayedStatus());
//                        }
                    }
//                    sto.setJalanStatus(JalanStatus.J);
                    System.out.println("total angsuran = " + totalAngsuran);
                    System.out.println("total kasbon = " + totalKasbon);
                    System.out.println("hutang hari ini = " + hutangToday);
                    System.out.println("total hutang =" + totalHutang);

                    // ----- APABILA KOMPONEN HUTANG KOSONG ----- //
                } else {
                    totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                    txtKasbon.setText(TextComponentUtils.formatNumber(totalAngsuran));
                    lblNominal.setText(rupiahFormatter.format(BigDecimal.ZERO));
                    totalKasbon = totalAngsuran;
                    totalHutang = totalHutang.add(totalKasbon.add(hutangToday));
                    lblHutang.setText(rupiahFormatter.format(totalHutang));
                    sto.setPayedStatus(TransaksiStatus.U);
//                    sto.setJalanStatus(JalanStatus.M);
                    System.out.println("payed status = " + sto.getPayedStatus());
                    txtBayarKasbon.setText("0");
                    txtOvertime.setText("0");
                    txtCicilan.setText("0");
                }
                // ------- SAAT JUMLAH TRANSAKSI KOSONG DAN HUTANG HARI INI KURANG DARI ATAU SAMA DENGAN 0 ------ //
            } else {
                totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                totalKasbon = totalKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
                if (validateHutang() && totalKasbon.compareTo(totalAngsuran) >= 0) {
                    txtKasbon.setText("0");
                    lblNominal.setText(rupiahFormatter.format(totalKasbon));
                    lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
                    sto.setPayedStatus(TransaksiStatus.L);
                } else if (validateHutang() && totalKasbon.compareTo(totalAngsuran) < 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah Pembayaran Yang Anda Masukkan Tidak Boleh Kurang Dari " + totalAngsuran, "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                    txtBayarKasbon.requestFocus();
                } else {
                    txtKasbon.setText(TextComponentUtils.formatNumber(totalAngsuran));
                    txtBayarKasbon.setText("0");
                    txtOvertime.setText("0");
                    txtCicilan.setText("0");
                    lblNominal.setText(rupiahFormatter.format(BigDecimal.ZERO));
                    lblHutang.setText(rupiahFormatter.format(totalAngsuran));
                    sto.setPayedStatus(TransaksiStatus.U);
                }
//            totalKasbon = totalKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
//            totalHutang = totalHutang.add(totalAngsuran.subtract(totalKasbon));
//                sto.setPayedStatus(TransaksiStatus.L);
//                sto.setJalanStatus(JalanStatus.J);
//                System.out.println("payed = " + sto.getPayedStatus());
//                txtKasbon.setText("0");
//                txtBayarKasbon.setText("0");
//                txtOvertime.setText("0");
//                txtCicilan.setText("0");
//                lblNominal.setText(rupiahFormatter.format(totalAngsuran));
//                lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
//                System.out.println("total angsuran = " + totalAngsuran);
//                System.out.println("total kasbon = " + totalKasbon);
//                System.out.println("hutang hari ini = " + hutangToday);
//                System.out.println("total hutang =" + totalHutang);
            }
        }
    }//GEN-LAST:event_txtCicilanKeyReleased

    private void txtNoLambungMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNoLambungMouseClicked
        // TODO add your handling code here:
        if (txtNoLambung.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtNoLambung.setForeground(Color.BLACK);
            txtNoLambung.setText("");
        }
    }//GEN-LAST:event_txtNoLambungMouseClicked

    private void txtKodeSetoranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtKodeSetoranMouseClicked
        // TODO add your handling code here:
        if (txtKodeSetoran.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtKodeSetoran.setForeground(Color.BLACK);
            txtKodeSetoran.setText("");
        }
    }//GEN-LAST:event_txtKodeSetoranMouseClicked

    private void txtAngsuranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAngsuranMouseClicked
        // TODO add your handling code here:
        if (txtAngsuran.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtAngsuran.setForeground(Color.BLACK);
            txtAngsuran.setText("");
        }
    }//GEN-LAST:event_txtAngsuranMouseClicked

    private void txtTabunganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTabunganMouseClicked
        // TODO add your handling code here:
        if (txtTabungan.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtTabungan.setForeground(Color.BLACK);
            txtTabungan.setText("");
        }
    }//GEN-LAST:event_txtTabunganMouseClicked

    private void txtKasbonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtKasbonMouseClicked
        // TODO add your handling code here:
        if (txtKasbon.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtKasbon.setForeground(Color.BLACK);
            txtKasbon.setText("");
        }
    }//GEN-LAST:event_txtKasbonMouseClicked

    private void txtBayarKasbonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBayarKasbonMouseClicked
        // TODO add your handling code here:
        if (txtBayarKasbon.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtBayarKasbon.setForeground(Color.BLACK);
            txtBayarKasbon.setText("");
        }
    }//GEN-LAST:event_txtBayarKasbonMouseClicked

    private void txtOvertimeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtOvertimeMouseClicked
        // TODO add your handling code here:
        if (txtOvertime.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtOvertime.setForeground(Color.BLACK);
            txtOvertime.setText("");
        }
    }//GEN-LAST:event_txtOvertimeMouseClicked

    private void txtCicilanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCicilanMouseClicked
        // TODO add your handling code here:
        if (txtCicilan.getText().equals("TIDAK BOLEH KOSONG...")) {
            txtCicilan.setForeground(Color.BLACK);
            txtCicilan.setText("");
        }
    }//GEN-LAST:event_txtCicilanMouseClicked

    private void txtKasbonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKasbonKeyReleased
        // TODO add your handling code here:
        // ----- KEADAAN PELUNASAN ----- //
        BigDecimal totalAngsuran = new BigDecimal(0);
        BigDecimal totalKasbon = new BigDecimal(0);
        BigDecimal mustPaid = new BigDecimal(0);

        if (sto == null) {
            sto = new setoran();
            stoDet = new setoranDetail();
        }
//         ----- SAAT JUMLAH TRANSAKSI TIDAK KOSONG DAN HUTANG HARI INI LEBIH DARI 0 ----- //
        if (!listSetoran.isEmpty() && hutangToday.compareTo(BigDecimal.ZERO) > 0) {
            // ----- CEK APABILA PENULISAN DIMULAI DENGAN ANGKA 0, YANG ARTINYA PEMBAYARAN PENUH ----- //
            totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
            if (txtKasbon.getText().startsWith("0") && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                mustPaid = hutangToday.add(totalAngsuran);
                totalKasbon = totalKasbon.add(mustPaid);
                if (totalKasbon.signum() == -1) {
                    totalKasbon = totalKasbon.negate();
                }
//                if (totalAngsuran.compareTo(totalKasbon) > 0) {
//                    sto.setPayedStatus(TransaksiStatus.L);
//                    lblNominal.setText(rupiahFormatter.format(totalKasbon));
//                    lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
//                }else if (totalAngsuran.compareTo(totalKasbon) == 0) {
//                    sto.setPayedStatus(TransaksiStatus.L);
//                    lblNominal.setText(rupiahFormatter.format(totalAngsuran));
//                    lblHutang.setText(rupiahFormatter.format(totalKasbon));
//                }
//
//                else if (totalAngsuran.compareTo(totalKasbon) < 0) {
//                    sto.setPayedStatus(TransaksiStatus.U);
//                    lblNominal.setText(rupiahFormatter.format(totalAngsuran));
//                    lblHutang.setText(rupiahFormatter.format(totalKasbon));
//                }
//                if (totalKasbon.compareTo(BigDecimal.ZERO) != 0) {
//                    lblNominal.setText(rupiahFormatter.format(totalAngsuran));
//                    lblHutang.setText(rupiahFormatter.format(totalKasbon));
//                    sto.setPayedStatus(TransaksiStatus.U);
//                } else {
//                    sto.setPayedStatus(TransaksiStatus.L);
//                    lblNominal.setText(rupiahFormatter.format(totalAngsuran));
//                    lblHutang.setText(rupiahFormatter.format(totalKasbon));
//                }
                txtKasbon.setText("0");
                txtOvertime.setText("0");
                txtBayarKasbon.setText(TextComponentUtils.formatNumber(hutangToday));
                txtCicilan.setText("0");
                lblNominal.setText(rupiahFormatter.format(mustPaid));
                lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
                sto.setPayedStatus(TransaksiStatus.L);
                System.out.println("Total angsuran = " + totalAngsuran);
                System.out.println("hutangToday = " + hutangToday);
                System.out.println("Total kasbon = " + totalKasbon);
                // ----- CEK APABILA KASBON KOSONG MAKA SELURUH NILAI AKAN DIHAPUS ----- //
//            } else if (txtKasbon.getText().isEmpty()) {
//                txtKasbon.setText("");
//                txtBayarKasbon.setText("");
//                txtOvertime.setText("");
//                txtCicilan.setText("");
            } else if (txtKasbon.getText().startsWith(TextComponentUtils.formatNumber(totalAngsuran)) && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                mustPaid = hutangToday;
                txtOvertime.setText("0");
                txtBayarKasbon.setText("0");
                txtCicilan.setText("0");
                lblNominal.setText(rupiahFormatter.format(totalAngsuran));
                lblHutang.setText(rupiahFormatter.format(mustPaid));
                sto.setPayedStatus(TransaksiStatus.U);
            }
//         ----- SAAT JUMLAH TRANSAKSI KOSONG DAN HUTANG HARI INI KURANG DARI 0 ----- //
        } else {
//         ----- CEK APABILA PENULISAN DIMULAI DENGAN ANGKA 0 DAN ENTER, YANG ARTINYA PEMBAYARAN SETORAN TANPA HUTANG ----- //
            if (txtKasbon.getText().startsWith("0") && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                lblNominal.setText(rupiahFormatter.format(totalAngsuran));
                txtBayarKasbon.setText("0");
                txtOvertime.setText("0");
                txtCicilan.setText("0");
                hutangToday = BigDecimal.ZERO;
                sto.setPayedStatus(TransaksiStatus.L);
                lblHutang.setText("Belum Ada Hutang");
            } else if (txtKasbon.getText().isEmpty()) {
                txtKasbon.setText("");
                txtBayarKasbon.setText("");
                txtOvertime.setText("");
                txtCicilan.setText("");
                lblNominal.setText("");
                lblHutang.setText("");
            }
        }
    }//GEN-LAST:event_txtKasbonKeyReleased

    private void checkJalanStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkJalanStatusActionPerformed
        // TODO add your handling code here:
        if (sto == null) {
            sto = new setoran();
            stoDet = new setoranDetail();
        }
        if (checkJalanStatus.isSelected()) {
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.BOLD, 13));
            checkJalanStatus.setText("Jalan");
            sto.setJalanStatus(JalanStatus.J);
            System.out.println("jalan status =" + sto.getJalanStatus());
        } else {
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.PLAIN, 13));
            checkJalanStatus.setText("Tidak/Rusak");
            sto.setJalanStatus(JalanStatus.M);
            System.out.println("jalan status =" + sto.getJalanStatus());
        }
}//GEN-LAST:event_checkJalanStatusActionPerformed

    private void dateCariSetoranPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateCariSetoranPropertyChange
        // TODO add your handling code here:
        if (dateCariSetoran.getDate() != null) {
            if (sto == null) {
                sto = new setoran();
                stoDet = new setoranDetail();
            }
            listSetoran = Main.getTransaksiService().findAllAvailableSetoranByLambung(new Integer(txtCariSetoranLambung.getText()), dateCariSetoran.getDate(), isClosedStatus.AVAILABLE);
            for (int i = 0; i <= listSetoran.size(); i++) {
                if (txtCariSetoranLambung.getText().startsWith(listSetoran.get(i).getDetail().get(0).getKemudi().getKend().getNoLambung().toString())) {
                    tblSetoran.removeAll();
                    sto = listSetoran.get(i);
                    stoDet.setSetor_map(sto);
                    listSetoranDetail.add(stoDet);
                    tblSetoran.setModel(new TransaksiSetoranTableModel(listSetoran, listSetoranDetail));
                    initColumnSize();
                }
            }
        }
    }//GEN-LAST:event_dateCariSetoranPropertyChange

    private void txtCariSetoranLambungKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariSetoranLambungKeyReleased
        // TODO add your handling code here:
        if (txtCariSetoranLambung.getText().isEmpty() && evt.getKeyChar() == '\n') {
            dateCariSetoran.setDate(null);
            LoadDatabaseToTable();
        }
    }//GEN-LAST:event_txtCariSetoranLambungKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpenClosedTransaction;
    private javax.swing.JCheckBox checkJalanStatus;
    private com.toedter.calendar.JDateChooser dateCariSetoran;
    private com.toedter.calendar.JDateChooser dateJatuhTempo;
    private com.toedter.calendar.JDateChooser dateSPO;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblHutang;
    private javax.swing.JLabel lblJalanStatus;
    private javax.swing.JLabel lblNominal;
    private javax.swing.JLabel lblRupiahHutang;
    private javax.swing.JLabel lblRupiahKasir;
    private javax.swing.JPanel pnlHutang;
    private javax.swing.JPanel pnlTerimaKasir;
    private javax.swing.JTable tblSetoran;
    private paperman.transaksi.toolbarButtonTransaksi toolbarButtonTransaksi;
    private javax.swing.JTextField txtAngsuran;
    private javax.swing.JTextField txtBayarKasbon;
    private javax.swing.JTextField txtCariSetoranLambung;
    private javax.swing.JTextField txtCicilan;
    private javax.swing.JTextField txtKasbon;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtKodeSetoran;
    private javax.swing.JTextField txtNRP;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoLambung;
    private javax.swing.JTextField txtOvertime;
    private javax.swing.JTextField txtSetoranCounter;
    private javax.swing.JTextField txtTabungan;
    // End of variables declaration//GEN-END:variables
}
