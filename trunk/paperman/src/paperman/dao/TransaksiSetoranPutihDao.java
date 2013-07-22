/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paperman.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import paperman.model.JalanStatus;
import paperman.model.TransaksiStatus;
import paperman.model.isClosedStatus;
import paperman.model.setoranDetailPutih;
import paperman.model.setoranPutih;

/**
 *
 * @author i1440ns
 */
@Repository
public class TransaksiSetoranPutihDao extends BaseDaoHibernate<setoranPutih> {

    public setoranPutih findByCode(Integer id) {
        setoranPutih st = (setoranPutih) sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih st where st.idPth=:id")
                .setInteger("id", id)
                .uniqueResult();
        Hibernate.initialize(st.getId());
        Hibernate.initialize(st.getDetailPutih());
        return st;
    }

    public List<setoranPutih> findAllStatusSetoran(TransaksiStatus status) {
        List<setoranPutih> listStatusPutih = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih where payedStatus=:status")
                .setParameter("status", status)
                .list();
        return listStatusPutih;
    }

    public List<setoranPutih> findStatusSetoranByLambung(Integer lambung, TransaksiStatus status){
        List<setoranPutih> listStatusPutih = sessionFactory.getCurrentSession()
                .createCriteria(setoranPutih.class)
                .add(Restrictions.eq("payedStatus", status))
                .createCriteria("detailPutih", Criteria.INNER_JOIN)
                .createCriteria("kemudiPutih", Criteria.INNER_JOIN)
                .createCriteria("kendPutih", Criteria.INNER_JOIN)
                .add(Restrictions.eq("noLambung", lambung))
                .list();
        return listStatusPutih;
    }

    public List<setoranPutih> findAvailableSetoranPutih(isClosedStatus closedStatus){
        List<setoranPutih> listStatus = sessionFactory.getCurrentSession()
                .createCriteria(setoranPutih.class)
                .add(Restrictions.eq("closedStatus", closedStatus))
                .addOrder(Order.asc("closedStatus"))
                .addOrder(Order.asc("counter_setoran"))
                .list();
        return listStatus;
    }

    public List<setoranDetailPutih> findAvailableSetoranPutih(isClosedStatus closedStatus, Integer offset, Integer limit){
        List<setoranDetailPutih> listSetoranDetail = sessionFactory.getCurrentSession()
                .createQuery("from setoranDetailPutih stDetPth where stDetPth.setor_map_pth.closedStatus=:status order by stDetPth.kemudiPutih.kendPutih.noLambung, stDetPth.setor_map_pth.counter_setoran asc")
                .setParameter("status", closedStatus)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
        return listSetoranDetail;
    }

    public List<setoranPutih> findAllAvailableSetoranPutihByLambung(Integer lambung, Date tglSpo, isClosedStatus status) {
        List<setoranPutih> listAvailableSetoran = sessionFactory.getCurrentSession()
                .createCriteria(setoranPutih.class)
                .add(Restrictions.eq("tglSPO", tglSpo))
                .add(Restrictions.eq("closedStatus", status))
                .createCriteria("detailPutih", Criteria.INNER_JOIN)
                .createCriteria("kemudiPutih", Criteria.INNER_JOIN)
                .createCriteria("kendPutih", Criteria.INNER_JOIN)
                .add(Restrictions.eq("noLambung", lambung))
                .list();
        for(setoranPutih st : listAvailableSetoran) {
            Hibernate.initialize(st.getDetailPutih());
        }
        return listAvailableSetoran;
    }

    public List<setoranPutih> findLastTglJatuhTempo(JalanStatus statusJalan) {
        List<setoranPutih> ListSetoranPutih = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih sp where sp.jalanStatus=:status order by sp.tglJatuhTempo desc")
                .setParameter("status", statusJalan)
                .list();
        return ListSetoranPutih;
    }

    public setoranPutih findBySpecific(Integer lambung, Date tglSetoran, Integer setoran) {
        setoranPutih st = (setoranPutih) sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih s where s.detailPutih.kemudiPutih.kendPutih.noLambung=:lambung and s.tglSetoran=:tglSetoran and s.counter_setoran=:setoran")
                .setInteger("lambung", lambung)
                .setDate("tglSetoran", tglSetoran)
                .setInteger("setoran", setoran)
                .uniqueResult();
        Hibernate.initialize(st.getDetailPutih());
        return st;
    }

    public setoranPutih findByHutangNoLambung(Integer lambung, TransaksiStatus status) {
        setoranPutih stPutih = (setoranPutih) sessionFactory.getCurrentSession()
                .createCriteria(setoranPutih.class)
                .add(Restrictions.eq("payedStatus", status))
                .createCriteria("detailPutih", Criteria.INNER_JOIN)
                .createCriteria("kemudiPutih", Criteria.INNER_JOIN)
                .createCriteria("kendPutih", Criteria.INNER_JOIN)
                .add(Restrictions.eq("noLambung", lambung))
                .uniqueResult();
        Hibernate.initialize(stPutih.getDetailPutih());
        return stPutih;
    }

    public List<setoranPutih> hutangRecordPutihForPiutang(TransaksiStatus status) {
        List<setoranPutih> listStoPth = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih sp where sp.payedStatus=:status")
                .setParameter("status", status)
                .list();
        for(setoranPutih stp : listStoPth) {
            Hibernate.initialize(stp.getDetailPutih());
        }
        return listStoPth;
    }

    public BigDecimal sumAngsuran(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.angsuran) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumAngsuran(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.angsuran) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumTabungan(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.tabungan) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumTabungan(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.tabungan) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumKasbon(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.kasbon) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumKasbon(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.kasbon) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumKasbon(Integer lambung, Date startDate, Date endDate) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.kasbon) from setoranDetailPutih stdPth where stdPth.kemudiPutih.kendPutih.noLambung=:lb and month(stdPth.setor_map_pth.tglSPO)= month(:start) and year(stdPth.setor_map_pth.tglSPO) = year(:end)")
                .setInteger("lb", lambung)
                .setDate("start", startDate)
                .setDate("end", endDate)
                .uniqueResult();
    }

    public BigDecimal sumBayarKasbon(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.bayar) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumBayarKasbon(Integer lambung, Date startDate, Date endDate) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.bayar) from setoranDetailPutih stdPth where stdPth.kemudiPutih.kendPutih.noLambung=:lb and month(stdPth.setor_map_pth.tglSPO)= month(:start) and year(stdPth.setor_map_pth.tglSPO) = year(:end)")
                .setInteger("lb", lambung)
                .setDate("start", startDate)
                .setDate("end", endDate)
                .uniqueResult();
    }

    public BigDecimal sumBayarKasbon(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.bayar) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumOvertime(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.ovtime) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumOvertime(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.ovtime) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumCicilan(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.KS) from setoranDetailPutih stdPth where month(stdPth.setor_map_pth.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumCicilan(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.KS) from setoranDetailPutih stdPth where year(stdPth.setor_map_pth.tglSPO)>=year(:startPeriod) and year(stdPth.setor_map_pth.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumCicilan(Integer lambung, Date startDate, Date endDate) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(stdPth.KS) from setoranDetailPutih stdPth where stdPth.kemudiPutih.kendPutih.noLambung=:lb and month(stdPth.setor_map_pth.tglSPO)= month(:start) and year(stdPth.setor_map_pth.tglSPO) = year(:end)")
                .setInteger("lb", lambung)
                .setDate("start", startDate)
                .setDate("end", endDate)
                .uniqueResult();
    }

    public BigDecimal sumHutang(Integer lambung, TransaksiStatus status) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(s.totalHutang) from setoranPutih s inner join s.detailPutih det where det.kemudiPutih.kendPutih.noLambung=:lambung and det.setor_map_pth.payedStatus=:status")
                .setInteger("lambung", lambung)
                .setParameter("status", status)
                .uniqueResult();
    }

    public BigDecimal sumSetoran(Date periodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(sp.totalSetoran) from setoranPutih sp where month(sp.tglSPO)=month(:datePeriod)")
                .setDate("datePeriod", periodTime)
                .uniqueResult();
    }

    public BigDecimal sumSetoran(Date startPeriodTime, Date endPeriodTime) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(sp.totalSetoran) from setoranPutih sp where year(sp.tglSPO)>=year(:startPeriod) and year(sp.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .uniqueResult();
    }

    public BigDecimal sumSetoran(Integer lambung) {
        return (BigDecimal) sessionFactory.getCurrentSession()
                .createQuery("select sum(s.totalSetoran) from setoranPutih s inner join s.detailPutih det where det.kemudiPutih.kendPutih.noLambung=:lambung")
                .setInteger("lambung", lambung)
                .uniqueResult();
    }

    public setoranPutih findByNoLambung(Integer lambung) {
        setoranPutih st = (setoranPutih) sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih s where s.detailPutih.kemudiPutih.kendPutih.noLambung=:lambung")
                .setInteger("lambung", lambung)
                .uniqueResult();
        Hibernate.initialize(st.getDetailPutih());
        return st;
    }

    public List<setoranPutih> setoranPutihRecord() {
        List<setoranPutih> listSetoranPutih = sessionFactory.getCurrentSession()
                .createCriteria(setoranPutih.class)
                .list();

        for (setoranPutih str : listSetoranPutih) {
            Hibernate.initialize(str.getDetailPutih());
        }
        return listSetoranPutih;
    }

    public List<setoranPutih> setoranPutihRecordInCustomRange(Date periodTime) {
        List<setoranPutih> listSetoranPutih = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih str where month(str.tglSPO)=month(:period)")
                .setDate("period", periodTime)
                .list();

        for (setoranPutih str : listSetoranPutih) {
            Hibernate.initialize(str.getDetailPutih());
        }
        return listSetoranPutih;
    }

    public List<setoranPutih> setoranPutihRecordInCustomRange(Date startPeriodTime, Date endPeriodTime) {
        List<setoranPutih> listSetoranPutih = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih str where year(str.tglSPO)>=year(:startPeriod) and year(str.tglSPO)<=year(:endPeriod)")
                .setDate("startPeriod", startPeriodTime)
                .setDate("endPeriod", endPeriodTime)
                .list();

        for (setoranPutih str : listSetoranPutih) {
            Hibernate.initialize(str.getDetailPutih());
        }
        return listSetoranPutih;
    }

    public List<setoranPutih> getLatestSetoranPutihCount() {
        List<setoranPutih> listSetoranPutih = sessionFactory.getCurrentSession()
                .createQuery("from setoranPutih order by counter_setoran desc")
                .setMaxResults(1)
                .list();
        return listSetoranPutih;
    }
}
