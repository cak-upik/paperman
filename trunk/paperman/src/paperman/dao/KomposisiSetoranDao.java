/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import paperman.model.komposisiSetoran;

/**
 *
 * @author i1440ns
 */
@Repository
public class KomposisiSetoranDao extends BaseDaoHibernate<komposisiSetoran> {
    public List<komposisiSetoran> komposisiRecord() {
        List<komposisiSetoran> listKomposisiSetoran = sessionFactory.getCurrentSession()
                .createQuery("from komposisiSetoran ks where ks.idSistem is not null")
                .list();
        return listKomposisiSetoran;
    }

    public List<komposisiSetoran> findBonusBulanan() {
        List<komposisiSetoran> listBonus = sessionFactory.getCurrentSession()
                .createQuery("from komposisiSetoran ks where ks.genCodeRef is not null")
                .list();
        return listBonus;
    }

    public List<komposisiSetoran> findBonusBulanan(String komposisi) {
        List<komposisiSetoran> listBonus = sessionFactory.getCurrentSession()
                .createQuery("from komposisiSetoran ks where ks.namaKomposisi=:data and ks.genCodeRef is not null")
                .setString("data", komposisi)
                .list();
        return listBonus;
    }

    public komposisiSetoran findBonusBulananPutih() {
        komposisiSetoran bns = (komposisiSetoran) sessionFactory.getCurrentSession()
                .createQuery("from komposisiSetoran ks where ks.namaKomposisi=:data and ks.genCodeRef is not null")
                .setString("data", "PUTIH")
                .uniqueResult();
        return bns;
    }
}
