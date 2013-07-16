/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.dao;

import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import paperman.model.sistem;

/**
 *
 * @author i1440ns
 */
@Repository
public class SistemDao extends BaseDaoHibernate<sistem> {
    public void update(sistem sys) {
        sessionFactory.getCurrentSession().saveOrUpdate(sys);
    }

    public sistem sistemRecord() {
        List<sistem> listSistem = sessionFactory.getCurrentSession().createQuery("from sistem")
                .setFirstResult(0)
                .setMaxResults(1)
                .list();
        if(listSistem.size() == 1) {
            return listSistem.get(0);
        }
        return null;
    }
}
