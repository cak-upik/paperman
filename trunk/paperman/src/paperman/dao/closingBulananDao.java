/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import paperman.model.aksiClosing;
import paperman.model.closingBulanan;
import paperman.model.isClosedFor;
import paperman.model.isClosedStatus;

/**
 *
 * @author Nurul Chusna
 */
@Repository
public class closingBulananDao extends BaseDaoHibernate<closingBulanan> {
    public List<closingBulanan> closingBulananRecord() {
        List<closingBulanan> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb")
                .list();
        return listClosingBulanan;
    }

    public List<closingBulanan> closingBulananRecordForSaldoAwal() {
        List<closingBulanan> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where clb.closedFor= :closedType order by clb.refNoLambung asc")
                .setParameter("closedType", isClosedFor.CLOSING_SALDO_AWAL)
                .list();
        return listClosingBulanan;
    }

    public List<closingBulanan> closingBulananRecordForSaldoAwal(Date thisMonth) {
        List<closingBulanan> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where clb.closedFor= :closedType and month(clb.periodeBulan)= month(:thisMonth) order by clb.refNoLambung asc")
                .setParameter("closedType", isClosedFor.CLOSING_SALDO_AWAL)
                .setDate("thisMonth", thisMonth)
                .list();
        return listClosingBulanan;
    }

    public List<closingBulanan> closingBulananInCustomRange(Date thisMonth) {
        List<closingBulanan> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where month(clb.periodeBulan)= month(:thisMonth) order by clb.refNoLambung asc")
                .setDate("thisMonth", thisMonth)
                .list();
        return listClosingBulanan;
    }

    public List<closingBulanan> closingBulananRecordForSaldoAwal(aksiClosing act) {
        List<closingBulanan> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where clb.closedFor= :closedType and actClosing= :whatAct order by id desc")
                .setParameter("closedType", isClosedFor.CLOSING_SALDO_AWAL)
                .setParameter("whatAct", act)
                .setMaxResults(1)
                .list();
        return listClosingBulanan;
    }

    public closingBulanan findAnyClosingBulananValid(Date thisMonth, isClosedFor closedFor) {
        closingBulanan clb = (closingBulanan) sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where month(clb.tglClosing)=month(:dateClosing) and clb.closedFor=:closingParam")
                .setDate("dateClosing", thisMonth)
                .setParameter("closingParam", closedFor)
                .uniqueResult();
        return clb;

    }

    public closingBulanan findClosingByRefNoLambung(Integer refLambung) {
        closingBulanan clb = (closingBulanan) sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where clb.refNoLambung=:no")
                .setInteger("no", refLambung)
                .uniqueResult();
        return clb;
    }

    public closingBulanan findClosingByRefNoLambung(Integer refLambung, Date whenMonth) {
        closingBulanan clb = (closingBulanan) sessionFactory.getCurrentSession()
                .createQuery("from closingBulanan clb where clb.refNoLambung=:no and month(clb.periodeBulan)=month(:whenMonth)")
                .setInteger("no", refLambung)
                .setDate("whenMonth", whenMonth)
                .uniqueResult();
        return clb;
    }
}
