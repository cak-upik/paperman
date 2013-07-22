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
import paperman.model.aksiClosing;
import paperman.model.closingBulananPutih;
import paperman.model.codeGenerator;
import paperman.model.isClosedStatus;
import paperman.model.kendaraanPutih;
import paperman.model.komposisiSetoran;
import paperman.model.pengemudiPutih;
import paperman.model.setoranDetailPutih;
import paperman.model.setoranPutih;
import paperman.model.sistem;
import paperman.tablemodel.TransaksiSetoranPutihTableModel;

/**
 *
 * @author i1440ns
 */
public class TransaksiSetoranPutih extends javax.swing.JInternalFrame implements ListSelectionListener {

    private static TransaksiSetoranPutih transaksiSetoranPutih;
    private List<setoranPutih> listSetoranPutih = new ArrayList<setoranPutih>();
    private List<setoranDetailPutih> listSetoranDetailPutih = new ArrayList<setoranDetailPutih>();
    private List<setoranDetailPutih> listStoDetPutih = new ArrayList<setoranDetailPutih>();
    private List<kendaraanPutih> listKendaraanPutih = new ArrayList<kendaraanPutih>();
    private List<pengemudiPutih> listPengemudiPutih = new ArrayList<pengemudiPutih>();
    private List<sistem> listSistem;
    private List<closingBulananPutih> listClosingBulananSaldoAwalPutih;
    private closingBulananPutih refNoLambung;
    private kendaraanPutih kendPutih;
    private pengemudiPutih drivePutih;
    private sistem sys;
    private setoranPutih stoPutih;
    private setoranDetailPutih stoDetPutih;
    private List<codeGenerator> generateCode;
    private codeGenerator genCode;
    private komposisiSetoran komposisiPutih;
    private List<komposisiSetoran> listKomposisi;
    private closingBulananPutih clbPutih;
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
    private boolean foundKomposisi = false;
    private int maxDay = 0;
    private int currentDay = 0;
    private int offset = 0;
    private int limit = 0;
    private int totalPage = 0;
    private int currentPos = 0;
    private int lastPos = 0;
    Dimension ukuranFrame;
    Dimension ukuranLayar;

    public static void inisialisasi() {
        transaksiSetoranPutih = new TransaksiSetoranPutih();
    }

    public static void destroy() {
        transaksiSetoranPutih.dispose();
        transaksiSetoranPutih = null;
        System.gc();
    }

    public static TransaksiSetoranPutih getTransaksiSetoranPutih() {
        return transaksiSetoranPutih;
    }

    /** Creates new form TransaksiSetoran */
    public TransaksiSetoranPutih() {
        initComponents();
        initButtonListener();
        sys = Main.getSistemService().sistemRecord();
        listKomposisi = Main.getSistemService().komposisiRecord();
        maxDay = new DateTime(sys.getTglKerja()).dayOfMonth().getMaximumValue();
        currentDay = new DateTime(sys.getTglKerja()).getDayOfMonth();
        if (currentDay == maxDay || currentDay == maxDay - 1) {
            if (Main.getSistemService().findBonusBulananPutih() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal Hari Ini Menggunakan Komposisi Bonus Bulanan\nKomposisi Bonus Bulanan Kosong, Periksa Lagi", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                InputBonusBulanan.inisialisasi();
                Main.getMainMenu().getDesktopPane().add(InputBonusBulanan.getFormInputBonusBulanan());
                ukuranLayar = Toolkit.getDefaultToolkit().getScreenSize();
                ukuranFrame = InputBonusBulanan.getFormInputBonusBulanan().getSize();
                InputBonusBulanan.getFormInputBonusBulanan().setLocation((ukuranLayar.width - ukuranFrame.width) / 2, (ukuranLayar.height - ukuranFrame.height) / 4);
                InputBonusBulanan.getFormInputBonusBulanan().setVisible(true);
                aksiKeluar();
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal Hari Ini Masuk Komposisi BONUS BULANAN\nSegala Jenis Transaksi Yang Bukan Termasuk Komposisi Bonus Tidak Ditampilkan\nTekan F12 Untuk Melihat Atau Dari Laporan");
                komposisiPutih = new PilihKomposisi().showDialogDataBonus();
            }
        } else {
            for (komposisiSetoran ks : listKomposisi) {
                if (ks.getStatus().booleanValue() == Boolean.TRUE) {
                    komposisiPutih = ks;
                    foundKomposisi = true;
                    break;
                }
                if (ks.getStatus().booleanValue() == Boolean.FALSE) {
                    komposisiPutih = new PilihKomposisi().showDialog();
                    foundKomposisi = true;
                    break;
                }
            }

            if (!foundKomposisi) {
                komposisiPutih = new PilihKomposisi().showDialog();
            }
        }
        listPengemudiPutih = Main.getMasterService().kemudiPutihRecord();
        listClosingBulananSaldoAwalPutih = Main.getTransaksiService().closingBulananPutihRecordForSaldoAwal();
        if (!listClosingBulananSaldoAwalPutih.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input Saldo Awal Telah Dilakukan\nTotal Hutang Akan Otomatis Di Sinkronisasi Dengan Kartu Pembayaran", "Pesan Sistem", JOptionPane.INFORMATION_MESSAGE);
        }
        toolbarButtonTransaksi.defaultMode();
        enableForm(false);
        tblSetoran.setAutoCreateColumnsFromModel(false);
        offset = 0;
        limit = 100;
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
        listSetoranPutih = Main.getTransaksiService().getLatestSetoranPutihCount();
        if (!listClosingBulananSaldoAwalPutih.isEmpty()) {
            listClosingBulananSaldoAwalPutih = Main.getTransaksiService().closingBulananPutihRecordForSaldoAwal();
        }
        genCode = Main.getSistemService().findBySpecific("SETORAN", "PUTIH");
        if (genCode != null && !listSetoranPutih.isEmpty()) {
            generateCode = Main.getSistemService().generateCode(genCode.getTagTransaction(), genCode.getTagArmada(), sys.getTglKerja());
            txtKodeSetoran.setText(formatDate.format(generateCode.get(0).getTanggal()) + "-" + generateCode.get(0).getTagTransaction().substring(0, 3) + "-" + generateCode.get(0).getTagArmada().substring(0, 1) + "-" + new Integer(generateCode.get(0).getLastnum() + 1));
            txtAngsuran.setText(TextComponentUtils.formatNumber(komposisiPutih.getAngsuran()));
            txtTabungan.setText(TextComponentUtils.formatNumber(komposisiPutih.getTabungan()));
            enableForm(true);
            enableSearchEngine(false);
            toolbarButtonTransaksi.editMode();
        }
        if (genCode == null && listSetoranPutih.isEmpty()) {
            JOptionPane.showMessageDialog(Main.getMainMenu(), "Nomor Kode Belum Di Konfigurasi\nHarap Setting Nomor Kode Pada Menu File > Konfigurasi");
            enableForm(false);
            enableSearchEngine(false);
            tblSetoran.setEnabled(false);
            toolbarButtonTransaksi.defaultMode();
        }
        if (genCode != null && listSetoranPutih.isEmpty()) {
            setoranCounter = 1;
            generateCode = Main.getSistemService().generateCode(genCode.getTagTransaction(), genCode.getTagArmada(), sys.getTglKerja());
            enableForm(true);
            enableSearchEngine(false);
            txtKodeSetoran.setText(formatDate.format(generateCode.get(0).getTanggal()) + "-" + generateCode.get(0).getTagTransaction().substring(0, 3) + "-" + generateCode.get(0).getTagArmada().substring(0, 1) + "-" + new Integer(generateCode.get(0).getLastnum() + 1));
            txtAngsuran.setText(TextComponentUtils.formatNumber(komposisiPutih.getAngsuran()));
            txtTabungan.setText(TextComponentUtils.formatNumber(komposisiPutih.getTabungan()));
            List<closingBulananPutih> listClosingPutih = Main.getTransaksiService().closingBulananPutihRecord();
            if (listClosingPutih.isEmpty()) {
                closingBulananPutih closingPutih = new closingBulananPutih();
                closingPutih.setPeriodeBulan(sys.getTglKerja());
                closingPutih.setTglClosing(sys.getTglKerja());
                closingPutih.setTotalAngsuran(new BigDecimal(0));
                closingPutih.setTotalTabungan(new BigDecimal(0));
                closingPutih.setTotalKas(new BigDecimal(0));
                closingPutih.setTotalBayarKas(new BigDecimal(0));
                closingPutih.setTotalOvertime(new BigDecimal(0));
                closingPutih.setTotalCicilan(new BigDecimal(0));
                closingPutih.setTotalSetor(new BigDecimal(0));
                Main.getTransaksiService().save(closingPutih);
            }
            toolbarButtonTransaksi.firstTransaksiMode();
        }
        if (genCode == null && !listSetoranPutih.isEmpty()) {
            JOptionPane.showMessageDialog(Main.getMainMenu(), "Terjadi Kesalahan Pada Form Transaksi\nPeriksa Lagi Apakah Nomor Kode Sudah Dikonfigurasi Atau Belum? ?", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
            enableForm(false);
            enableSearchEngine(false);
            toolbarButtonTransaksi.defaultMode();
        }
        if (!listSetoranPutih.isEmpty() && listSetoranPutih.get(0).getIdClosing() != null && listSetoranPutih.get(0).getClosedStatus().equals(isClosedStatus.CLOSED)) {
            if (new DateTime(listSetoranPutih.get(0).getIdClosing().getPeriodeBulan().getTime()).getMonthOfYear() == new DateTime(Main.getSistemService().sistemRecord().getTglKerja().getTime()).getMonthOfYear()) {
                JOptionPane.showMessageDialog(this, "Transaksi Sudah Di Closing\nTidak Bisa Melakukan Transaksi Bila Telah Di Closing", "Pesan Sistem", JOptionPane.ERROR_MESSAGE);
                houseKeeping();
                enableForm(false);
                enableSearchEngine(false);
                toolbarButtonTransaksi.defaultMode();
            }
        } else {
            txtNoLambung.requestFocus();
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
        listClosingBulananSaldoAwalPutih = Main.getTransaksiService().closingBulananPutihRecordForSaldoAwal();
        enableForm(true);
        enableSearchEngine(false);
//        setoranPrevious = Integer.parseInt(txtSetoranCounter.getText());
//        System.out.println("stp = " + setoranPrevious);
        this.isEditFire = true;
        txtNoLambung.setEnabled(false);
        txtSetoranCounter.setEnabled(false);
        toolbarButtonTransaksi.editMode();
        tblSetoran.setEnabled(false);
        lblJalanStatus.setEnabled(true);
        checkJalanStatus.setEnabled(true);
    }

    private void aksiHapus() {
//        setoranPrevious = Integer.parseInt(txtSetoranCounter.getText());
//        System.out.println("stp = " + setoranPrevious);
        String noLambung = stoDetPutih.getKendPutih().getNoLambung().toString();
        String nrp = stoDetPutih.getKemudiPutih().getNrp();
        String nama = stoDetPutih.getKemudiPutih().getNama();
        String setoranCount = stoPutih.getCounter_setoran().toString();
        String jatuhTempo = stoPutih.getTglJatuhTempo().toString();
        String tglSPO = stoPutih.getTglSPO().toString();
        String kodeSetoran = stoPutih.getKode();
        String angsuran = stoDetPutih.getAngsuran().toString();
        String tabungan = stoDetPutih.getTabungan().toString();
        String kasbon = stoDetPutih.getKasbon().toString();
        String bayarKasbon = stoDetPutih.getBayar().toString();
        String overtime = stoDetPutih.getOvtime().toString();
        String cicilan = stoDetPutih.getKS().toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Anda Yakin Akan Menghapus Data Ini ? ?\n"
                + "Rincian :\n"
                + "Kode Setoran = " + kodeSetoran + "\n"
                + "No.Lambung = " + noLambung + "\n"
                + "NRP =  " + nrp + "\n"
                + "Nama = " + nama + "\n"
                + "Setoran Ke = " + setoranCount + "\n"
                + "Tgl. Jatuh Tempo =  " + jatuhTempo + "\n"
                + "Tgl. SPO = " + tglSPO + "\n"
                + "Angsuran = " + angsuran + "\n"
                + "Tabungan = " + tabungan + "\n"
                + "Kasbon = " + kasbon + "\n"
                + "Bayar Kasbon = " + bayarKasbon + "\n"
                + "Over Time= " + overtime + "\n"
                + "Cicilan= " + cicilan,
                "Konfirmasi Hapus Data", option, JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi == JOptionPane.OK_OPTION) {
            Main.getTransaksiService().delete(stoPutih);
            LoadDatabaseToTable();
            houseKeeping();
            listSetoranPutih = Main.getTransaksiService().getLatestSetoranPutihCount();
            if (listSetoranPutih.isEmpty()) {
                setoranCounter = 0;
            } else {
                if (listSetoranPutih.get(0).getTotalHutang().compareTo(BigDecimal.ZERO) != 0) {
                    stoPutih = listSetoranPutih.get(0);
                    stoPutih.setPayedStatus(TransaksiStatus.U);
                    Main.getTransaksiService().save(stoPutih);
                }
                setoranCounter = listSetoranPutih.get(0).getCounter_setoran();
            }
            stoPutih = null;
            stoDetPutih = null;
        }
        if (konfirmasi == JOptionPane.CANCEL_OPTION) {
            houseKeeping();
            stoPutih = null;
            stoDetPutih = null;
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
            stoPutih = null;
            stoDetPutih = null;
        }
    }

    private void cekDuplicate() {
        if (validateForm()) {
            LoadFormToDatabase();
            Main.getTransaksiService().save(stoPutih);
            Main.getSistemService().save(genCode);
            LoadDatabaseToTable();
            houseKeeping();
            enableForm(false);
            enableSearchEngine(true);
            tblSetoran.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Proses Penyimpanan Telah Berhasil", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            tblSetoran.getSelectionModel().clearSelection();
            toolbarButtonTransaksi.defaultMode();
            stoPutih = null;
            stoDetPutih = null;
            listStoDetPutih = null;
            listClosingBulananSaldoAwalPutih = null;
            setoranCounter = 0;
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
//        listSetoranPutih = Main.getTransaksiService().getLatestSetoranPutihCount();
//        setoranCounter = listSetoranPutih.get(0).getCounter_setoran();
//        System.out.println("stc = " + setoranCounter);
//        System.out.println("stp = " + setoranPrevious);
        houseKeeping();
        enableForm(false);
        enableSearchEngine(true);
        tblSetoran.getSelectionModel().clearSelection();
        tblSetoran.setEnabled(true);
        stoPutih = null;
        stoDetPutih = null;
        listStoDetPutih = null;
        listClosingBulananSaldoAwalPutih = null;
        setoranCounter = 0;
//        listSetoranPutih = null;
//        listSetoranDetailPutih = null;
        toolbarButtonTransaksi.defaultMode();
    }

    private void aksiCetak() {
        int pil = JOptionPane.YES_NO_OPTION;
        int konfirm = JOptionPane.showConfirmDialog(this, "Pastikan Kertas Dan Printer Telah Terpasang Dengan Baik\nLanjutkan? ?", "Peringatan Sistem", pil, JOptionPane.QUESTION_MESSAGE);
        if (konfirm == JOptionPane.YES_OPTION) {
            JasperPrint loadReport = Main.getReportService().getTandaTerimaWhiteReport(new Integer(txtNoLambung.getText()), new Integer(txtSetoranCounter.getText()));
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
        stoPutih = null;
        stoDetPutih = null;
        listSetoranPutih = null;
        listSetoranDetailPutih = null;
        listStoDetPutih = null;
        listClosingBulananSaldoAwalPutih = null;
        setoranCounter = 0;
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
        txtCariKodeSetoran.setEnabled(es);
        dateCariSetoran.setEnabled(es);
        tblSetoran.setEnabled(es);
        txtCariKodeSetoran.setText("");
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
        if (stoPutih == null) {
            stoPutih = new setoranPutih();
            stoDetPutih = new setoranDetailPutih();
        }
        stoPutih.setKode(txtKodeSetoran.getText());
        stoPutih.setTglJatuhTempo(dateJatuhTempo.getDate());
        stoPutih.setTglSPO(dateSPO.getDate());
        stoPutih.setTglSetoran(sys.getTglKerja());
        stoPutih.setCounter_setoran(new Integer(txtSetoranCounter.getText()));
        stoPutih.setTotalSetoran(TextComponentUtils.parseNumberToBigDecimal(lblNominal.getText().substring(3)));
        stoPutih.setUser(sys.getLastLoginUser().getNamaLogin());
        stoDetPutih.setKemudiPutih(drivePutih);
        stoDetPutih.setKendPutih(kendPutih);
        stoDetPutih.setAngsuran(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText()));
        stoDetPutih.setTabungan(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
        stoDetPutih.setKasbon(TextComponentUtils.parseNumberToBigDecimal(txtKasbon.getText()));
        stoDetPutih.setBayar(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText()));
        stoDetPutih.setOvtime(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText()));
        stoDetPutih.setKS(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
        stoDetPutih.setKet(txtKeterangan.getText());
        stoDetPutih.setSetor_map_putih(stoPutih);
        listSetoranDetailPutih.add(stoDetPutih);
        stoPutih.setDetailPutih(listSetoranDetailPutih);
        stoPutih.setClosedStatus(isClosedStatus.AVAILABLE);
        if (stoPutih.getPayedStatus() == TransaksiStatus.L) {
            List<setoranPutih> listStats = Main.getTransaksiService().findStatusSetoranPutihByLambung(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
            for (setoranPutih stPutih : listStats) {
                stPutih.setPayedStatus(TransaksiStatus.L);
                Main.getTransaksiService().save(stPutih);
                System.out.println("id= " + stPutih.getId());
                System.out.println("status = " + stPutih.getPayedStatus());
            }
        }
        if (stoPutih.getPayedStatus() == TransaksiStatus.U) {
            List<setoranPutih> listSetor = Main.getTransaksiService().findStatusSetoranPutihByLambung(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
            for (setoranPutih stp : listSetor) {
                stp.setPayedStatus(TransaksiStatus.L);
                Main.getTransaksiService().save(stp);
            }
            stoPutih.setPayedStatus(TransaksiStatus.U);
        }
        if (stoPutih.getJalanStatus() == JalanStatus.J) {
            stoPutih.setJalanStatus(JalanStatus.J);
        } else {
            stoPutih.setJalanStatus(JalanStatus.M);
        }
        if (!lblHutang.getText().isEmpty() && !lblHutang.getText().equalsIgnoreCase("Belum Ada Hutang")) {
            stoPutih.setTotalHutang(TextComponentUtils.parseNumberToBigDecimal(lblHutang.getText().substring(3)));
        } else {
            stoPutih.setTotalHutang(BigDecimal.ZERO);
        }
        if (isEditFire) {
            genCode.setId(genCode.getId());
            genCode.setLastnum(genCode.getLastnum());
        } else {
            genCode.setId(genCode.getId());
            genCode.setLastnum(genCode.getLastnum() + 1);
        }
        if (!listClosingBulananSaldoAwalPutih.isEmpty()) {
            closingBulananPutih clbPth = Main.getTransaksiService().findClosingPutihByRefNoLambung(new Integer(txtNoLambung.getText()));
            if (clbPth != null && clbPth.getActClosing().equals(aksiClosing.DO_CLOSING_SALDO_AWAL)) {
                clbPth.setActClosing(aksiClosing.NOPE);
                Main.getTransaksiService().save(clbPth);
            }
        }
    }

    private void LoadDataListSetoranDetail() {
        for (int i = 0; i < listSetoranDetailPutih.size(); i++) {
            listSetoranDetailPutih.get(i).setAngsuran(new BigDecimal(txtAngsuran.getText()));
            listSetoranDetailPutih.get(i).setTabungan(new BigDecimal(txtTabungan.getText()));
            listSetoranDetailPutih.get(i).setKasbon(new BigDecimal(txtKasbon.getText()));
            listSetoranDetailPutih.get(i).setBayar(new BigDecimal(txtBayarKasbon.getText()));
            listSetoranDetailPutih.get(i).setOvtime(new BigDecimal(txtOvertime.getText()));
            listSetoranDetailPutih.get(i).setKS(new BigDecimal(txtCicilan.getText()));
        }
    }

    private void LoadDatabaseToForm() {
        kendPutih.setId(stoDetPutih.getKemudiPutih().getKendPutih().getId());
        drivePutih.setId(stoDetPutih.getKemudiPutih().getId());
        txtNoLambung.setText(stoDetPutih.getKemudiPutih().getKendPutih().getNoLambung().toString());
        txtNRP.setText(stoDetPutih.getKemudiPutih().getNrp());
        txtNama.setText(stoDetPutih.getKemudiPutih().getNama());
        txtSetoranCounter.setText(stoDetPutih.getSetor_map_putih().getCounter_setoran().toString());
        dateJatuhTempo.setDate(stoDetPutih.getSetor_map_putih().getTglJatuhTempo());
        dateSPO.setDate(stoDetPutih.getSetor_map_putih().getTglSPO());
        txtKeterangan.setText(stoDetPutih.getKet());
        txtKodeSetoran.setText(stoDetPutih.getSetor_map_putih().getKode());
        txtAngsuran.setText(TextComponentUtils.formatNumber(stoDetPutih.getAngsuran()));
        txtTabungan.setText(TextComponentUtils.formatNumber(stoDetPutih.getTabungan()));
        txtKasbon.setText(TextComponentUtils.formatNumber(stoDetPutih.getKasbon()));
        txtBayarKasbon.setText(TextComponentUtils.formatNumber(stoDetPutih.getBayar()));
        txtOvertime.setText(TextComponentUtils.formatNumber(stoDetPutih.getOvtime()));
        txtCicilan.setText(TextComponentUtils.formatNumber(stoDetPutih.getKS()));
        lblNominal.setText(rupiahFormatter.format(stoDetPutih.getSetor_map_putih().getTotalSetoran()));
        lblHutang.setText(rupiahFormatter.format(stoDetPutih.getSetor_map_putih().getTotalHutang()));
        lblJalanStatus.setVisible(true);
        lblJalanStatus.setEnabled(false);
        if (stoDetPutih.getSetor_map_putih().getJalanStatus() == JalanStatus.J) {
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
        listSetoranDetailPutih = Main.getTransaksiService().findAvailableSetoranPutih(isClosedStatus.AVAILABLE, offset, limit);
        tblSetoran.setModel(new TransaksiSetoranPutihTableModel(listSetoranDetailPutih));
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
        if (!txtBayarKasbon.getText().isEmpty()
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
        tblSetoran.getColumnModel().getColumn(2).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(2).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(3).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(3).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(4).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(4).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(5).setMinWidth(300);
        tblSetoran.getColumnModel().getColumn(5).setMaxWidth(300);
        tblSetoran.getColumnModel().getColumn(6).setMinWidth(300);
        tblSetoran.getColumnModel().getColumn(6).setMaxWidth(300);
    }

    public void valueChanged(ListSelectionEvent lse) {
        if (tblSetoran.getSelectedRow() >= 0) {
            stoDetPutih = listSetoranDetailPutih.get(tblSetoran.getSelectedRow());
            stoPutih = stoDetPutih.getSetor_map_putih();
            kendPutih = new kendaraanPutih();
            drivePutih = new pengemudiPutih();
            genCode = Main.getSistemService().findBySpecific("SETORAN", "PUTIH");
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
        txtCariKodeSetoran = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dateCariSetoran = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSetoran = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
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
        setTitle("Transaksi Setoran - Armada Putih");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel1.setText("Cari Kode Setoran");

        txtCariKodeSetoran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKodeSetoranKeyReleased(evt);
            }
        });

        jLabel2.setText("Tgl Setoran");

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
        tblSetoran.getColumnModel().getColumn(2).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(2).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(3).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(3).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(4).setMinWidth(200);
        tblSetoran.getColumnModel().getColumn(4).setMaxWidth(200);
        tblSetoran.getColumnModel().getColumn(5).setMinWidth(300);
        tblSetoran.getColumnModel().getColumn(5).setMaxWidth(300);
        tblSetoran.getColumnModel().getColumn(6).setMinWidth(300);
        tblSetoran.getColumnModel().getColumn(6).setMaxWidth(300);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("BPG Chveulebrivi", 1, 13));
        jLabel12.setText("  Data Setoran");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addContainerGap(1022, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCariKodeSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateCariSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(321, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCariKodeSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1))
                    .addComponent(dateCariSetoran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("BPG Chveulebrivi", 1, 13));
        jLabel13.setText("  Input Setoran");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addContainerGap(1019, Short.MAX_VALUE))
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

        lblNominal.setFont(new java.awt.Font("BPG Chveulebrivi", 1, 24)); // NOI18N
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
                    .addComponent(lblNominal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(lblRupiahKasir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
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

        lblHutang.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblHutang.setText("Nominal");

        lblRupiahHutang.setText("Rupiah");

        javax.swing.GroupLayout pnlHutangLayout = new javax.swing.GroupLayout(pnlHutang);
        pnlHutang.setLayout(pnlHutangLayout);
        pnlHutangLayout.setHorizontalGroup(
            pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHutangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRupiahHutang, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHutangLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblHutang, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlHutangLayout.setVerticalGroup(
            pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHutangLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblRupiahHutang, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(pnlHutangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHutangLayout.createSequentialGroup()
                    .addComponent(lblHutang, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(60, Short.MAX_VALUE)))
        );

        pnlHutangLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblHutang, lblRupiahHutang});

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
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNoLambung, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblJalanStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkJalanStatus))
                    .addComponent(txtNRP, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSetoranCounter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateJatuhTempo, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(dateSPO, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlTerimaKasir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHutang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                        .addComponent(pnlTerimaKasir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlHutang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(dateSPO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtSetoranCounter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dateJatuhTempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)))
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
                            .addComponent(jLabel17))))
                .addContainerGap(37, Short.MAX_VALUE))
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
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNoLambungKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoLambungKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyChar() == '\n') {
            drivePutih = new pengemudiPutih();
            kendPutih = new kendaraanPutih();
            Date spo = new Date();
            int i = 0;
            int a = 0;
            checkJalanStatus.setVisible(true);
            checkJalanStatus.setEnabled(true);
            lblJalanStatus.setVisible(true);
            lblJalanStatus.setEnabled(true);
            for (; i < listPengemudiPutih.size(); i++) {
                if (listPengemudiPutih.get(i).getKendPutih().getNoLambung().toString().startsWith(txtNoLambung.getText())) {
                    txtNRP.setText(listPengemudiPutih.get(i).getNrp());
                    txtNama.setText(listPengemudiPutih.get(i).getNama());
                    txtKeterangan.setText(listPengemudiPutih.get(i).getKendPutih().getKeterangan());
                    drivePutih.setId(listPengemudiPutih.get(i).getId());
                    kendPutih.setId(listPengemudiPutih.get(i).getKendPutih().getId());
                    if (!listSetoranPutih.isEmpty()) {
                        if (Main.getSistemService().findBonusBulanan(komposisiPutih.getNamaKomposisi() + "BONUS BULANAN") != null) {
                            listStoDetPutih = Main.getTransaksiService().findLastSetoranDetailPutihByLambung(new Integer(txtNoLambung.getText()));
                        } else {
                            listStoDetPutih = Main.getTransaksiService().findSetoranDetailPutihByLambung(new Integer(txtNoLambung.getText()), sys.getTglKerja());
                        }
                        spo = Main.getTransaksiService().getLatestSetoranPutihCount().get(0).getTglSPO();
                        List<setoranPutih> listSTOPutih = Main.getTransaksiService().findLastTglJatuhTempoPutih(JalanStatus.J);
                        if (!listSTOPutih.isEmpty()) {
                            dateJatuhTempo.setDate(new DateTime(listSTOPutih.get(0).getTglJatuhTempo().getTime()).plusDays(1).toDate());
                            dateSPO.setDate(sys.getTglKerja());
                        } else {
                            if (listSetoranPutih.isEmpty()) {
                                dateJatuhTempo.setDate(listPengemudiPutih.get(i).getKendPutih().getTglJatuhTempo());
                            } else {
                                dateJatuhTempo.setDate(Main.getTransaksiService().findLastTglJatuhTempoPutih(JalanStatus.M).get(0).getTglJatuhTempo());
                            }
                            dateSPO.setDate(sys.getTglKerja());
                        }
                    } else {
                        dateJatuhTempo.setDate(listPengemudiPutih.get(i).getKendPutih().getTglJatuhTempo());
                        dateSPO.setDate(sys.getTglKerja());
                    }
                    if(listClosingBulananSaldoAwalPutih.isEmpty()) {
                        if(!listSetoranPutih.isEmpty()) {
                            if(!listStoDetPutih.isEmpty()) {
                                setoranCounter = listStoDetPutih.get(0).getSetor_map_putih().getCounter_setoran();
                                setoranCounter++;
                                txtSetoranCounter.setText(String.valueOf(setoranCounter));
                            }else {
                                setoranCounter = 1;
                                txtSetoranCounter.setText(String.valueOf(setoranCounter));
                            }
                        } else {
                            setoranCounter = 1;
                            txtSetoranCounter.setText(String.valueOf(setoranCounter));
                        }
                    }else {
                        for(; a < listClosingBulananSaldoAwalPutih.size(); a++) {
                            if(listClosingBulananSaldoAwalPutih.get(a).getActClosing().equals(aksiClosing.DO_CLOSING_SALDO_AWAL)) {
                                if(listClosingBulananSaldoAwalPutih.get(a).getRefNoLambung().compareTo(new Integer(txtNoLambung.getText()))==0) {
                                    refNoLambung = Main.getTransaksiService().findClosingPutihByRefNoLambung(new Integer(txtNoLambung.getText()));
                                    if(refNoLambung !=null) {
                                        setoranCounter = refNoLambung.getRefSetoranKe();
                                        setoranCounter++;
                                        txtSetoranCounter.setText(String.valueOf(setoranCounter));
                                        break;
                                    }else {
                                        if(!listSetoranPutih.isEmpty()) {
                                            if(!listStoDetPutih.isEmpty()) {
                                                setoranCounter = listStoDetPutih.get(0).getSetor_map_putih().getCounter_setoran();
                                                setoranCounter++;
                                                txtSetoranCounter.setText(String.valueOf(setoranCounter));
                                            }else {
                                                setoranCounter = 1;
                                                txtSetoranCounter.setText(String.valueOf(setoranCounter));
                                            }
                                        }else {
                                            txtSetoranCounter.setText("1");
                                        }
                                        break;
                                    }
                                }
                            }else {
                                if(!listSetoranPutih.isEmpty()) {
                                    if(!listStoDetPutih.isEmpty()) {
                                        setoranCounter = listStoDetPutih.get(0).getSetor_map_putih().getCounter_setoran();
                                        setoranCounter++;
                                        txtSetoranCounter.setText(String.valueOf(setoranCounter));
                                    }else {
                                        setoranCounter = 1;
                                        txtSetoranCounter.setText(String.valueOf(setoranCounter));
                                    }
                                }else {
                                    txtSetoranCounter.setText("1");
                                }
                            }
                        }
                    }
                }
            }
            if (!listSetoranPutih.isEmpty() && !listClosingBulananSaldoAwalPutih.isEmpty()) {
                BigDecimal tKasbon = BigDecimal.ZERO;
                BigDecimal tBayar = BigDecimal.ZERO;
                BigDecimal tCicilan = BigDecimal.ZERO;
                tKasbon = Main.getTransaksiService().sumKasbonPutih(new Integer(txtNoLambung.getText()), spo, spo).add(Main.getTransaksiService().findClosingPutihByRefNoLambung(new Integer(txtNoLambung.getText())).getTotalKas());
                tBayar = Main.getTransaksiService().sumBayarKasbonPutih(new Integer(txtNoLambung.getText()), spo, spo).add(Main.getTransaksiService().findClosingPutihByRefNoLambung(new Integer(txtNoLambung.getText())).getTotalBayarKas());
                tCicilan = Main.getTransaksiService().sumCicilanPutih(new Integer(txtNoLambung.getText()), spo, spo).add(Main.getTransaksiService().findClosingPutihByRefNoLambung(new Integer(txtNoLambung.getText())).getTotalCicilan());
//                hutang = Main.getTransaksiService().sumHutangPutih(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
                hutang = tKasbon.subtract(tBayar).subtract(tCicilan);
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
                lblRupiahHutang.setText("Total Yang Harus Di Bayar = " + rupiahFormatter.format(hutangToday.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()))));
    /**/        } else if (!listSetoranPutih.isEmpty() && listClosingBulananSaldoAwalPutih.isEmpty()) {
                hutang = Main.getTransaksiService().sumHutangPutih(new Integer(txtNoLambung.getText()), TransaksiStatus.U);
                if (hutang != null && hutang.compareTo(BigDecimal.ZERO) <= 0) {
                    hutangToday = BigDecimal.ZERO;
                    System.out.println("Hutang Compare = " + hutang);
                } else if (hutang != null && hutang.compareTo(BigDecimal.ZERO) > 0) {
                    hutangToday = hutang;
                    if (hutangToday.signum() == -1) {
                        hutangToday = hutangToday.negate();
                        System.out.println("hutang today = " + hutangToday.signum());
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
            if (stoPutih == null) {
                stoPutih = new setoranPutih();
                stoDetPutih = new setoranDetailPutih();
            }

            // ------- SAAT JUMLAH TRANSAKSI TIDAK KOSONG DAN HUTANG HARI INI LEBIH DARI 0 ------ //
            if (!listSetoranPutih.isEmpty() || !listSetoranPutih.isEmpty() && hutangToday.compareTo(BigDecimal.ZERO) > 0) {

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
                    totalKasbon = totalKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
                    totalHutang = totalHutang.add(totalKasbon.subtract(mustPaid));
                    if (totalHutang.signum() == -1) {
                        totalHutang = totalHutang.negate();
                    }
                    if (totalHutang.compareTo(BigDecimal.ZERO) == 0) {
                        lblNominal.setText(rupiahFormatter.format(totalKasbon));
                        lblHutang.setText(rupiahFormatter.format(totalHutang));
                        txtKasbon.setText("0");
                        stoPutih.setPayedStatus(TransaksiStatus.L);
                        System.out.println("payed = " + stoPutih.getPayedStatus());
                    } else {
//                        if (totalKasbon.compareTo(mustPaid) > 0) {
//                            JOptionPane.showMessageDialog(this, "Pembayaran Tidak Boleh Lebih Besar Dari Jumlah Yang Harus Dibayar = " + mustPaid, "Pesan Sistem", JOptionPane.WARNING_MESSAGE);
//                            txtBayarKasbon.requestFocus();
//                        } else {
                        lblNominal.setText(rupiahFormatter.format(totalKasbon));
                        lblHutang.setText(rupiahFormatter.format(totalHutang));
                        txtKasbon.setText(totalAngsuran.toString());
                        stoPutih.setPayedStatus(TransaksiStatus.U);
//                        }
                    }
//                    stoPutih.setJalanStatus(JalanStatus.J);
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
                    stoPutih.setPayedStatus(TransaksiStatus.U);
//                    stoPutih.setJalanStatus(JalanStatus.M);
                    System.out.println("payed status = " + stoPutih.getPayedStatus());
                    txtBayarKasbon.setText("0");
                    txtOvertime.setText("0");
                    txtCicilan.setText("0");
                }
                // ------- SAAT JUMLAH TRANSAKSI KOSONG DAN HUTANG HARI INI KURANG DARI ATAU SAMA DENGAN 0 ------ //
            } else {
                totalAngsuran = totalAngsuran.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                totalKasbon = totalKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtBayarKasbon.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtOvertime.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtCicilan.getText()));
//                totalHutang = totalHutang.add(totalAngsuran.subtract(totalKasbon));
                if (validateHutang() && totalKasbon.compareTo(totalAngsuran) >= 0) {
                    txtKasbon.setText("0");
                    lblNominal.setText(rupiahFormatter.format(totalKasbon));
                    lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
                    stoPutih.setPayedStatus(TransaksiStatus.L);
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
                    stoPutih.setPayedStatus(TransaksiStatus.U);
                }

//                stoPutih.setPayedStatus(TransaksiStatus.L);
//                stoPutih.setJalanStatus(JalanStatus.J);
//                System.out.println("payed = " + stoPutih.getPayedStatus());
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
        BigDecimal totalNonKasbon = new BigDecimal(0);
        BigDecimal totalKasbon = new BigDecimal(0);
        BigDecimal mustPaid = new BigDecimal(0);

        if (stoPutih == null) {
            stoPutih = new setoranPutih();
            stoDetPutih = new setoranDetailPutih();
        }
        // ----- SAAT JUMLAH TRANSAKSI TIDAK KOSONG DAN HUTANG HARI INI LEBIH DARI 0 ----- //
        if (!listSetoranPutih.isEmpty() && hutangToday.compareTo(BigDecimal.ZERO) > 0) {
            // ----- CEK APABILA PENULISAN DIMULAI DENGAN ANGKA 0, YANG ARTINYA PEMBAYARAN PENUH ----- //
            totalNonKasbon = totalNonKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
            System.out.println("total angsuran = " + totalNonKasbon);
            if (txtKasbon.getText().startsWith("0") && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                mustPaid = hutangToday.add(totalNonKasbon);
                totalKasbon = totalKasbon.add(mustPaid);
                if (totalKasbon.signum() == -1) {
                    totalKasbon = totalKasbon.negate();
                }
//                if (totalNonKasbon.compareTo(totalKasbon) > 0) {
//                    stoPutih.setPayedStatus(TransaksiStatus.L);
//                    lblNominal.setText(rupiahFormatter.format(totalKasbon));
//                    lblHutang.setText(TextComponentUtils.formatNumber(BigDecimal.ZERO));
//                } else if (totalNonKasbon.compareTo(totalKasbon) < 0) {
//                    stoPutih.setPayedStatus(TransaksiStatus.U);
//                    lblNominal.setText(rupiahFormatter.format(totalNonKasbon));
//                    lblHutang.setText(totalKasbon.toString());
//                }
                System.out.println("hutangToday = " + hutangToday);
                System.out.println("Total kasbon = " + totalKasbon);
                txtKasbon.setText("0");
                txtOvertime.setText("0");
                txtBayarKasbon.setText(TextComponentUtils.formatNumber(hutangToday));
                txtCicilan.setText("0");
                lblNominal.setText(rupiahFormatter.format(mustPaid));
                lblHutang.setText(rupiahFormatter.format(BigDecimal.ZERO));
                stoPutih.setPayedStatus(TransaksiStatus.L);
                // ----- CEK APABILA KASBON KOSONG MAKA SELURUH NILAI AKAN DIHAPUS ----- //
            } else if (txtKasbon.getText().startsWith(TextComponentUtils.formatNumber(totalNonKasbon)) && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                mustPaid = hutangToday;
                txtBayarKasbon.setText("0");
                txtOvertime.setText("0");
                txtCicilan.setText("0");
                lblNominal.setText(rupiahFormatter.format(totalNonKasbon));
                lblHutang.setText(rupiahFormatter.format(mustPaid));
                stoPutih.setPayedStatus(TransaksiStatus.U);
            }
            // ----- SAAT JUMLAH TRANSAKSI KOSONG DAN HUTANG HARI INI KURANG DARI 0 ----- //
        } else {
            // ----- CEK APABILA PENULISAN DIMULAI DENGAN ANGKA 0, YANG ARTINYA PEMBAYARAN PENUH ----- //
            if (txtKasbon.getText().startsWith("0") && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                totalNonKasbon = totalNonKasbon.add(TextComponentUtils.parseNumberToBigDecimal(txtAngsuran.getText())).add(TextComponentUtils.parseNumberToBigDecimal(txtTabungan.getText()));
                lblNominal.setText(rupiahFormatter.format(totalNonKasbon));
                txtBayarKasbon.setText("0");
                txtOvertime.setText("0");
                txtCicilan.setText("0");
                hutangToday = BigDecimal.ZERO;
                stoPutih.setPayedStatus(TransaksiStatus.L);
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
        if (stoPutih == null) {
            stoPutih = new setoranPutih();
            stoDetPutih = new setoranDetailPutih();
        }
        if (checkJalanStatus.isSelected()) {
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.BOLD, 13));
            checkJalanStatus.setText("Jalan");
            stoPutih.setJalanStatus(JalanStatus.J);
            System.out.println("jalan status = " + stoPutih.getJalanStatus());
        } else {
            checkJalanStatus.setFont(new Font("BPG Chveulebrivi", Font.PLAIN, 13));
            checkJalanStatus.setText("Tidak/Rusak");
            stoPutih.setJalanStatus(JalanStatus.M);
            System.out.println("jalan status = " + stoPutih.getJalanStatus());
        }
    }//GEN-LAST:event_checkJalanStatusActionPerformed

    private void dateCariSetoranPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateCariSetoranPropertyChange
        // TODO add your handling code here:
        if (dateCariSetoran.getDate() != null) {
            if (stoPutih == null) {
                stoPutih = new setoranPutih();
                stoDetPutih = new setoranDetailPutih();
            }
            listSetoranPutih = Main.getTransaksiService().findAllAvailableSetoranPutihByLambung(new Integer(txtCariKodeSetoran.getText()), dateCariSetoran.getDate(), isClosedStatus.AVAILABLE);
            for (int i = 0; i <= listSetoranPutih.size(); i++) {
                if (txtCariKodeSetoran.getText().startsWith(listSetoranPutih.get(i).getDetailPutih().get(0).getKemudiPutih().getKendPutih().getNoLambung().toString())) {
                    tblSetoran.removeAll();
                    stoPutih = listSetoranPutih.get(i);
                    stoDetPutih.setSetor_map_putih(stoPutih);
                    listSetoranDetailPutih.add(stoDetPutih);
                    tblSetoran.setModel(new TransaksiSetoranPutihTableModel(listSetoranDetailPutih));
                    initColumnSize();
                }
            }
        }
    }//GEN-LAST:event_dateCariSetoranPropertyChange

    private void txtCariKodeSetoranKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKodeSetoranKeyReleased
        // TODO add your handling code here:
        if (txtCariKodeSetoran.getText().isEmpty() && evt.getKeyChar() == '\n') {
            dateCariSetoran.setDate(null);
            LoadDatabaseToTable();
        }
    }//GEN-LAST:event_txtCariKodeSetoranKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JTextField txtCariKodeSetoran;
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
