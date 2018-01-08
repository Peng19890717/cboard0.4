package org.cboard.services;

import org.cboard.cache.CacheManager;
import org.cboard.dto.DataProviderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存的service
 * Created by yfyuan on 2016/8/24.
 */
@Repository
public class CachedDataProviderService extends DataProviderService {
    //结果集（String[][]）缓存（key：数据源+sql；value：结果集）
    @Autowired
    private CacheManager<DataProviderResult> cacheManager;

    public DataProviderResult getData(Long datasourceId, Map<String, String> query, Long datasetId, boolean reload) {
        String keys = null;
        if (datasetId != null) {
            keys = "dataset_" + datasetId.toString();
        } else {
            keys = "" + datasourceId + "_" + query.toString();
        }
        DataProviderResult o = null;
        if (reload || ((o = cacheManager.get(keys)) == null)) {
            synchronized (keys) {
                if (reload || ((o = cacheManager.get(keys)) == null)) {
                    DataProviderResult d = super.getData(datasourceId, query, datasetId);//获取数据
                    long expire = 12 * 60 * 60 * 1000;
                    if (datasetId != null) {
                        Dataset dataset = super.getDataset(datasetId);
                        if (dataset.getInterval() != null && dataset.getInterval() > 0) {
                            expire = dataset.getInterval() * 1000;
                        }
                    }
                    cacheManager.put(keys, d, expire);
                    return d;
                } else {
                    return o;
                }
            }
        } else {
            return o;
        }
    }
}
