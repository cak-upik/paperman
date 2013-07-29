/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

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
}
