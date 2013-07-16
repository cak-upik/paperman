/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paperman.service;

import java.util.List;
import paperman.model.kendaraan;
import paperman.model.kendaraanPutih;
import paperman.model.pengemudi;
import paperman.model.pengemudiPutih;

/**
 *
 * @author i1440ns
 */
public interface MasterService {
    public void save(kendaraan kend);
    public void save(kendaraanPutih kendPutih);
    public void delete(kendaraan kend);
    public void delete(kendaraanPutih kendPutih);
    public List<kendaraan> kendaraanRecord();
    public List<kendaraanPutih> kendaraanPutihRecord();
    public List<kendaraan> findKendaraanByLambung(Integer lambung);
    public List<kendaraanPutih> findKendaraanPutihByLambung(Integer lambung);

    public void save(pengemudi kemudi);
    public void save(pengemudiPutih kemudiPutih);
    public void delete(pengemudi kemudi);
    public void delete(pengemudiPutih kemudPutih);
    public List<pengemudi> kemudiRecord();
    public List<pengemudiPutih> kemudiPutihRecord();
}
