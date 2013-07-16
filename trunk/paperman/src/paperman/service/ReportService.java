/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.service;

import java.util.Date;
import net.sf.jasperreports.engine.JasperPrint;
import paperman.model.isClosedFor;
import paperman.model.komposisiSetoran;

/**
 *
 * @author Nurul Chusna
 */
public interface ReportService {
    // ---- Armada BIRU ---- //
    public JasperPrint getDailyTransactionReport(Date dateSPO, komposisiSetoran kompos);
    public JasperPrint getTandaTerimaReport(Integer lambung, Integer setKe);
    public JasperPrint getMonthlyTransactionReport(Date dateSPO, komposisiSetoran kompos);
    public JasperPrint getKartuPembayaranReport(Integer lambung, Date dateAwal, Date dateAkhir, isClosedFor optionalClosingStatus, Integer monthClosing);
    public JasperPrint getDataLambungReport(Date awalSPO, Date dateSPO, komposisiSetoran kompos);

    // ---- Armada PUTIH ---- //
    public JasperPrint getDailyTransactionWhiteReport(Date dateSPO);
    public JasperPrint getTandaTerimaWhiteReport(Integer lambung, Integer setKe);
    public JasperPrint getMonthlyTransactionWhiteReport(Date dateSPO);
    public JasperPrint getKartuPembayaranWhiteReport(Integer lambung, Date dateAwal, Date dateAkhir);
    public JasperPrint getDataLambungWhiteReport(Date dateAwal, Date dateSPO);

    // ---- MANAJEMEN PIUTANG ---- //
    public JasperPrint getDataLambungForPiutangReport(Integer lambung, Date dateAwal, Date dateSPO);
    public JasperPrint getDataLambungForPiutangWhiteReport(Integer lambung, Date dateAwal, Date dateSPO);
}
