/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paperman.service.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paperman.OsUtils;
import paperman.model.DailyTransactionReport;
import paperman.model.DataLambungBulanan;
import paperman.model.KartuBayarReport;
import paperman.model.TandaTerimaReport;
import paperman.model.isClosedFor;
import paperman.model.komposisiSetoran;
import paperman.service.ReportService;

/**
 *
 * @author Nurul Chusna
 */
@Service("reportService")
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    @Autowired
    protected SessionFactory sessionFactory;

    public JasperPrint getDailyTransactionReport(Date dateSPO, komposisiSetoran kompos) {
        try {
            List<DailyTransactionReport> dailyTransactionReport = sessionFactory.getCurrentSession().createQuery("select std.kemudi.kend.noLambung as nLambung , std.kemudi.nrp as nrp, std.kemudi.nama as nama, std.setor_map.counter_setoran as setKe, "
                    + "std.setor_map.tglJatuhTempo as tglJatuhTempo, std.setor_map.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetail std "
                    + "where std.setor_map.tglSPO = :date and std.setor_map.idKomposisi=:kompos order by std.kemudi.kend.noLambung ASC")
                    .setParameter("date", dateSPO)
                    .setParameter("kompos", kompos)
                    .setResultTransformer(Transformers.aliasToBean(DailyTransactionReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanTransaksiSetoran.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            System.out.println("classReport= " + reportClassURL);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dailyTransactionReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getDailyTransactionWhiteReport(Date dateSPO) {
        try {
            List<DailyTransactionReport> dailyTransactionReport = sessionFactory.getCurrentSession().createQuery("select std.kemudiPutih.kendPutih.noLambung as nLambung , std.kemudiPutih.nrp as nrp, std.kemudiPutih.nama as nama, std.setor_map_pth.counter_setoran as setKe, "
                    + "std.setor_map_pth.tglJatuhTempo as tglJatuhTempo, std.setor_map_pth.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map_pth.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetailPutih std "
                    + "where std.setor_map_pth.tglSPO = :date order by std.setor_map_pth.counter_setoran")
                    .setParameter("date", dateSPO)
                    .setResultTransformer(Transformers.aliasToBean(DailyTransactionReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanTransaksiSetoranPutih.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dailyTransactionReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getKartuPembayaranReport(Integer lambung, Date dateAwal, Date dateAkhir, isClosedFor optionalClosingStatus, Integer monthClosing) {
        String subReportPath = "";
        try {
            List<KartuBayarReport> ListKartuBayarReport = sessionFactory.getCurrentSession().createQuery("select std.setor_map.counter_setoran as setKe, "
                    + "std.setor_map.tglJatuhTempo as tglJatuhTempo, std.setor_map.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetail std "
                    + "where std.kemudi.kend.noLambung=:lb and std.setor_map.tglSPO >=:start and std.setor_map.tglSPO <=:end order by std.setor_map.counter_setoran ASC")
                    .setInteger("lb", lambung)
                    .setDate("start", dateAwal)
                    .setDate("end", dateAkhir)
                    .setResultTransformer(Transformers.aliasToBean(KartuBayarReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanKartuPembayaran.jasper");
//            InputStream subReportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/SaldoAwalKartuBayar.jasper");
//            String path = System.getProperty("user.dir") + "\\src\\paperman\\report\\LaporanKartuPembayaran.jasper";
            if (OsUtils.isUnix()) {
                subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalKartuBayar.jasper";
                System.out.println("OS = " + OsUtils.getOsName());
            }
            if (OsUtils.isWindows()) {
                subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalKartuBayar.jasper";
                System.out.println("OS = " + OsUtils.getOsName());
            }
//            System.out.println("path = "+path);
            System.out.println("reportClass = " + reportClassURL.toString());
            System.out.println("subReportClass = " + subReportPath);
            Connection conn = sessionFactory.getCurrentSession().connection();
            try {
                System.out.println("Connection : " + conn.getMetaData().getURL().toString());
            } catch (SQLException ex) {
                Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("lambung", lambung);
            parameters.put("dateAwal", dateAwal);
            parameters.put("dateAkhir", dateAkhir);
            parameters.put("JDBC_CONNECTION", conn);
            parameters.put("SUBREPORT_DIR", subReportPath);
            parameters.put("closedfor", optionalClosingStatus);
            parameters.put("monthClosing", monthClosing);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(ListKartuBayarReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getKartuPembayaranWhiteReport(Integer lambung, Date dateAwal, Date dateAkhir) {
        String subReportPath = "";
        try {
            List<KartuBayarReport> ListKartuBayarWhiteReport = sessionFactory.getCurrentSession().createQuery("select std.setor_map_pth.counter_setoran as setKe, "
                    + "std.setor_map_pth.tglJatuhTempo as tglJatuhTempo, std.setor_map_pth.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map_pth.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetailPutih std "
                    + "where std.kemudiPutih.kendPutih.noLambung=:lb and std.setor_map_pth.tglSPO>=:start and std.setor_map_pth.tglSPO<=:end order by std.setor_map_pth.counter_setoran ASC")
                    .setInteger("lb", lambung)
                    .setDate("start", dateAwal)
                    .setDate("end", dateAkhir)
                    .setResultTransformer(Transformers.aliasToBean(KartuBayarReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanKartuPembayaranPutih.jasper");
//            InputStream subReportURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanKartuPembayaranPutih.jasper");
//            String subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalKartuBayarPutih.jasper";
            if (OsUtils.isUnix()) {
                subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalKartuBayarPutih.jasper";
            }
            if (OsUtils.isWindows()) {
                subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalKartuBayarPutih.jasper";
            }
            Connection conn = sessionFactory.getCurrentSession().connection();

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("lambung", lambung);
            parameters.put("dateAwal", dateAwal);
            parameters.put("dateAkhir", dateAkhir);
            parameters.put("JDBC_CONNECTION", conn);
            parameters.put("SUBREPORT_DIR", subReportPath);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(ListKartuBayarWhiteReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getTandaTerimaReport(Integer lambung, Integer setKe) {
        try {
            List<TandaTerimaReport> tandaTerimaReport = sessionFactory.getCurrentSession().createQuery("select std.kemudi.kend.noLambung as nLambung, std.setor_map.kode as kode, std.kemudi.nrp as nrp, std.kemudi.nama as nama, std.setor_map.counter_setoran as setoranKe, "
                    + "std.setor_map.tglJatuhTempo as jatuhTempo, std.setor_map.tglSPO as spo, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanLain, std.ovtime as overtime, std.setor_map.totalSetoran as totalAngsuran, std.setor_map.totalHutang as totalHutang, std.ket as ket "
                    + "from setoranDetail std "
                    + "where std.kemudi.kend.noLambung = :lambung and std.setor_map.counter_setoran = :setKe order by std.kemudi.kend.noLambung")
                    .setInteger("lambung", lambung)
                    .setInteger("setKe", setKe)
                    .setResultTransformer(Transformers.aliasToBean(TandaTerimaReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/TandaTerimaSetoran.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("lambung", lambung);
            parameters.put("setKe", setKe);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(tandaTerimaReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getTandaTerimaWhiteReport(Integer lambung, Integer setKe) {
        try {
            List<TandaTerimaReport> tandaTerimaReport = sessionFactory.getCurrentSession().createQuery("select std.kemudiPutih.kendPutih.noLambung as nLambung, std.setor_map_pth.kode as kode, std.kemudiPutih.nrp as nrp, std.kemudiPutih.nama as nama, std.setor_map_pth.counter_setoran as setoranKe, "
                    + "std.setor_map_pth.tglJatuhTempo as jatuhTempo, std.setor_map_pth.tglSPO as spo, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanLain, std.ovtime as overtime, std.setor_map_pth.totalSetoran as totalAngsuran, std.setor_map_pth.totalHutang as totalHutang, std.ket as ket "
                    + "from setoranDetailPutih std "
                    + "where std.kemudiPutih.kendPutih.noLambung = :lambung and std.setor_map_pth.counter_setoran = :setKe order by std.kemudiPutih.kendPutih.noLambung")
                    .setInteger("lambung", lambung)
                    .setInteger("setKe", setKe)
                    .setResultTransformer(Transformers.aliasToBean(TandaTerimaReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/TandaTerimaSetoranPutih.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("lambung", lambung);
            parameters.put("setKe", setKe);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(tandaTerimaReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getMonthlyTransactionReport(Date dateSPO, komposisiSetoran kompos) {
        try {
            List<DailyTransactionReport> monthlyTransactionReport = sessionFactory.getCurrentSession().createQuery("select std.kemudi.kend.noLambung as nLambung , std.kemudi.nrp as nrp, std.kemudi.nama as nama, std.setor_map.counter_setoran as setKe, "
                    + "std.setor_map.tglJatuhTempo as tglJatuhTempo, std.setor_map.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetail std "
                    + "where month(std.setor_map.tglSPO) = month(:date) and std.setor_map.idKomposisi=:kompos order by std.kemudi.kend.noLambung ASC")
                    .setDate("date", dateSPO)
                    .setParameter("kompos", kompos)
                    .setResultTransformer(Transformers.aliasToBean(DailyTransactionReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanTransaksiSetoranBulanan.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            System.out.println("Report classURL= " + reportClassURL);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(monthlyTransactionReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getMonthlyTransactionWhiteReport(Date dateSPO) {
        try {
            List<DailyTransactionReport> monthlyTransactionReport = sessionFactory.getCurrentSession().createQuery("select std.kemudiPutih.kendPutih.noLambung as nLambung , std.kemudiPutih.nrp as nrp, std.kemudiPutih.nama as nama, std.setor_map_pth.counter_setoran as setKe, "
                    + "std.setor_map_pth.tglJatuhTempo as tglJatuhTempo, std.setor_map_pth.tglSPO as tglSPO, std.angsuran as angsuran, std.tabungan as tabungan, std.kasbon as kasbon, "
                    + "std.bayar as bayarKasbon, std.KS as cicilanKas, std.ovtime as overtime, std.setor_map_pth.totalSetoran as totalAngsur, std.ket as keterangan "
                    + "from setoranDetailPutih std "
                    + "where month(std.setor_map_pth.tglSPO) = month(:date) order by std.setor_map_pth.counter_setoran")
                    .setDate("date", dateSPO)
                    .setResultTransformer(Transformers.aliasToBean(DailyTransactionReport.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanTransaksiSetoranPutihBulanan.jasper");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(monthlyTransactionReport));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getDataLambungReport(Date awalSPO, Date dateSPO, komposisiSetoran kompos) {
        String subReportPath = "";
        try {
            List<DataLambungBulanan> dataLambungBulanan = sessionFactory.getCurrentSession().createQuery("select std.kemudi.kend.noLambung as nLambung , std.kemudi.kend.noPolisi as nopol, (select max(stdet.setor_map.counter_setoran) from setoranDetail stdet where stdet.kemudi.kend.noLambung=std.kemudi.kend.noLambung) as setKe, "
                    + "std.kemudi.kend.tglJatuhTempo as tglAngsuranAwal, std.setor_map.tglJatuhTempo as tglJatuhTempo, std.setor_map.tglSPO as tglSPO, sum(std.angsuran) as angsuran, sum(std.tabungan) as tabungan, sum(std.kasbon) as kasbon, "
                    + "sum(std.bayar) as bayarKasbon, sum(std.KS) as cicilanKas, sum(std.ovtime) as overtime, sum(std.setor_map.totalSetoran) as totalSetoran, std.setor_map.totalHutang as totalhutang "
                    + "from setoranDetail std "
                    + "where month(std.setor_map.tglSPO) >= month(:dateAwal) and month(std.setor_map.tglSPO) <= month(:dateSPO) and std.setor_map.idKomposisi=:kompos group by std.kemudi.kend.noLambung order by std.kemudi.kend.noLambung ASC")
                    .setDate("dateAwal", awalSPO)
                    .setDate("dateSPO", dateSPO)
                    .setParameter("kompos", kompos)
                    .setResultTransformer(Transformers.aliasToBean(DataLambungBulanan.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanDataLambungBulanan.jasper");
//            String subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambung.jasper";
            if (OsUtils.isUnix()) {
                subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalDataLambung.jasper";
            }
            if (OsUtils.isWindows()) {
                subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambung.jasper";
            }
            Connection conn = sessionFactory.getCurrentSession().connection();

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            parameters.put("dateAwal", dateSPO);
            parameters.put("SUBREPORT_DIR", subReportPath);
            parameters.put("REPORT_CONNECTION", conn);
            System.out.println("report classURL= " + reportClassURL);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dataLambungBulanan));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getDataLambungWhiteReport(Date dateAwal, Date dateSPO) {
        String subReportPath = "";
        try {
            List<DataLambungBulanan> dataLambungBulanan = sessionFactory.getCurrentSession().createQuery("select std.kemudiPutih.kendPutih.noLambung as nLambung , std.kemudiPutih.kendPutih.noPolisi as nopol, std.setor_map_pth.counter_setoran as setKe, "
                    + "std.kemudiPutih.kendPutih.tglJatuhTempo as tglAngsuranAwal, std.setor_map_pth.tglJatuhTempo as tglJatuhTempo, std.setor_map_pth.tglSPO as tglSPO, sum(std.angsuran) as angsuran, sum(std.tabungan) as tabungan, sum(std.kasbon) as kasbon, "
                    + "sum(std.bayar) as bayarKasbon, sum(std.KS) as cicilanKas, sum(std.ovtime) as overtime "
                    + "from setoranDetailPutih std "
                    + "where month(std.setor_map_pth.tglSPO) >= month(:awal) and month(std.setor_map_pth.tglSPO)<= month(:akhir) group by std.kemudiPutih.kendPutih.noLambung order by std.kemudiPutih.kendPutih.noLambung")
                    .setDate("awal", dateAwal)
                    .setDate("akhir", dateSPO)
                    .setResultTransformer(Transformers.aliasToBean(DataLambungBulanan.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanDataLambungPutihBulanan.jasper");
//            String subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambungPutih.jasper";
            if (OsUtils.isUnix()) {
                subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalDataLambungPutih.jasper";
            }
            if (OsUtils.isWindows()) {
                subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambungPutih.jasper";
            }
            Connection conn = sessionFactory.getCurrentSession().connection();

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            parameters.put("dateAwal", dateSPO);
            parameters.put("SUBREPORT_DIR", subReportPath);
            parameters.put("REPORT_CONNECTION", conn);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dataLambungBulanan));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getDataLambungForPiutangReport(Integer lambung, Date dateAwal, Date dateSPO) {
        String subReportPath = "";
        try {
            List<DataLambungBulanan> dataLambungBulanan = sessionFactory.getCurrentSession().createQuery("select std.kemudi.kend.noLambung as nLambung , std.kemudi.kend.noPolisi as nopol, std.setor_map.counter_setoran as setKe, "
                    + "std.kemudi.kend.tglJatuhTempo as tglAngsuranAwal, std.setor_map.tglJatuhTempo as tglJatuhTempo, std.setor_map.tglSPO as tglSPO, std.angsuran as angsuran, sum(std.tabungan) as tabungan, sum(std.kasbon) as kasbon, "
                    + "sum(std.bayar) as bayarKasbon, sum(std.KS) as cicilanKas, sum(std.ovtime) as overtime "
                    + "from setoranDetail std "
                    + "where std.kemudi.kend.noLambung=:lb and month(std.setor_map.tglSPO) >= month(:awal) and month(std.setor_map.tglSPO) <= month(:akhir) group by std.kemudi.kend.noLambung order by std.kemudi.kend.noLambung")
                    .setInteger("lb", lambung)
                    .setDate("awal", dateAwal)
                    .setDate("akhir", dateSPO)
                    .setResultTransformer(Transformers.aliasToBean(DataLambungBulanan.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanDataLambungBulanan.jasper");
//            String subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambung.jasper";
            if (OsUtils.isUnix()) {
                subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalDataLambung.jasper";
            }
            if (OsUtils.isWindows()) {
                subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambung.jasper";
            }
            Connection conn = sessionFactory.getCurrentSession().connection();

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            parameters.put("dateAwal", dateSPO);
            parameters.put("REPORT_CONNECTION", conn);
            parameters.put("SUBREPORT_DIR", subReportPath);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dataLambungBulanan));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JasperPrint getDataLambungForPiutangWhiteReport(Integer lambung, Date dateAwal, Date dateSPO) {
        try {
            List<DataLambungBulanan> dataLambungBulanan = sessionFactory.getCurrentSession().createQuery("select std.kemudiPutih.kendPutih.noLambung as nLambung , std.kemudiPutih.kendPutih.noPolisi as nopol, std.setor_map_pth.counter_setoran as setKe, "
                    + "std.kemudiPutih.kendPutih.tglJatuhTempo as tglAngsuranAwal, std.setor_map_pth.tglJatuhTempo as tglJatuhTempo, std.setor_map_pth.tglSPO as tglSPO, sum(std.angsuran) as angsuran, sum(std.tabungan) as tabungan, sum(std.kasbon) as kasbon, "
                    + "sum(std.bayar) as bayarKasbon, sum(std.KS) as cicilanKas, sum(std.ovtime) as overtime "
                    + "from setoranDetailPutih std "
                    + "where std.kemudiPutih.kendPutih.noLambung=:lb and month(std.setor_map_pth.tglSPO) >= month(:date) and month(std.setor_map_pth.tglSPO) <= month(:akhir) group by std.kemudiPutih.kendPutih.noLambung order by std.kemudiPutih.kendPutih.noLambung")
                    .setInteger("lb", lambung)
                    .setDate("date", dateAwal)
                    .setDate("akhir", dateSPO)
                    .setResultTransformer(Transformers.aliasToBean(DataLambungBulanan.class))
                    .list();

            InputStream reportClassURL = this.getClass().getClassLoader().getResourceAsStream("paperman/report/LaporanDataLambungPutihBulanan.jasper");
//            String subReportPath = System.getProperty("user.dir") + "\\src\\paperman\\report\\SaldoAwalDataLambungPutih.jasper";
            String subReportPath = System.getProperty("user.dir") + "/src/paperman/report/SaldoAwalDataLambungPutih.jasper";
            Connection conn = sessionFactory.getCurrentSession().connection();

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("date", dateSPO);
            parameters.put("dateAwal", dateSPO);
            parameters.put("SUBREPORT_DIR", subReportPath);
            parameters.put("REPORT_CONNECTION", conn);

            return JasperFillManager.fillReport(reportClassURL, parameters, new JRBeanCollectionDataSource(dataLambungBulanan));
        } catch (JRException ex) {
            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
