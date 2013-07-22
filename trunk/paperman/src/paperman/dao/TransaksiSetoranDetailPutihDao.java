/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

import java.util.Date;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import paperman.model.setoranDetailPutih;

/**
 *
 * @author i1440ns
 */
@Repository
public class TransaksiSetoranDetailPutihDao extends BaseDaoHibernate<setoranDetailPutih>{
    public List<setoranDetailPutih> setoranPutihDetailRecord() {
        List<setoranDetailPutih> listSetoranPutihDetail = sessionFactory.getCurrentSession()
                .createQuery("from setoranDetailPutih stDetail").list();

        for(setoranDetailPutih stDet : listSetoranPutihDetail) {
            Hibernate.initialize(stDet.getSetor_map_putih().getDetailPutih());
        }
        return listSetoranPutihDetail;
    }

    public List<setoranDetailPutih> findLastSetoranDetailPutihByLambung(Integer lambung) {
        List<setoranDetailPutih> listStd = sessionFactory.getCurrentSession()
                .createQuery("from setoranDetailPutih sdp where sdp.kemudiPutih.kendPutih.noLambung= :lb order by sdp.setor_map_pth.counter_setoran desc")
                .setInteger("lb", lambung)
                .setMaxResults(1)
                .list();
        for(setoranDetailPutih stDetPutih : listStd) {
            Hibernate.initialize(stDetPutih.getSetor_map_putih().getDetailPutih());
        }
        return listStd;
    }

    public List<setoranDetailPutih> findSetoranDetailPutihByLambung(Integer lambung, Date tglSetoran) {
        List<setoranDetailPutih> listStdPth = sessionFactory.getCurrentSession()
                .createQuery("from setoranDetailPutih sdp where sdp.kemudiPutih.kendPutih.noLambung= :lb and day(sdp.setor_map_pth.tglSPO)= day(:tgl) order by sdp.setor_map_pth.counter_setoran desc")
                .setInteger("lb", lambung)
                .setDate("tgl", tglSetoran)
                .setMaxResults(1)
                .list();
        for(setoranDetailPutih stDetPth : listStdPth) {
            Hibernate.initialize(stDetPth.getSetor_map_putih().getDetailPutih());
        }
        return listStdPth;
    }
}
