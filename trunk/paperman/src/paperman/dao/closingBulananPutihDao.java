/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import paperman.model.closingBulananPutih;
import paperman.model.isClosedFor;

/**
 *
 * @author Nurul Chusna
 */
@Repository
public class closingBulananPutihDao extends BaseDaoHibernate<closingBulananPutih> {
    public List<closingBulananPutih> closingBulananPutihRecord() {
        List<closingBulananPutih> listClosingBulanan = sessionFactory.getCurrentSession()
                .createQuery("from closingBulananPutih clb")
                .list();
        return listClosingBulanan;
    }

    public List<closingBulananPutih> closingBulananPutihRecordForSaldoAwal() {
        List<closingBulananPutih> listClosingBulananPutih = sessionFactory.getCurrentSession()
                .createQuery("from closingBulananPutih clb where clb.closedFor= :closedType order by clb.refNoLambung asc")
                .setParameter("closedType", isClosedFor.CLOSING_SALDO_AWAL)
                .list();
        return listClosingBulananPutih;
    }

    public closingBulananPutih findClosingPutihByRefNoLambung(Integer refLambung) {
        closingBulananPutih clbPth = (closingBulananPutih) sessionFactory.getCurrentSession()
                .createQuery("from closingBulananPutih clb where clb.refNoLambung=:no")
                .setInteger("no", refLambung)
                .uniqueResult();
        return clbPth;
    }
}
