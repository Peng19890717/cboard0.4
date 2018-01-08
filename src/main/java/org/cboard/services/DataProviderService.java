package org.cboard.services;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.cboard.dao.DatasetDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dataprovider.config.AggConfig;
import org.cboard.dataprovider.config.DimensionConfig;
import org.cboard.dataprovider.result.AggregateResult;
import org.cboard.dto.DataProviderResult;
import org.cboard.exception.CBoardException;
import org.cboard.pojo.DashboardDataset;
import org.cboard.pojo.DashboardDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by yfyuan on 2016/8/15.
 */
@Repository
public class DataProviderService {

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasetDao datasetDao;

    private int resultLimit=300000;//设置为30w，否则前段界面渲染时会崩溃

    //根据数据源，sql获取相应的数据（返回的结果其实是二维数组）
    public DataProviderResult getData(Long datasourceId, Map<String, String> query, Long datasetId) {
        String[][] dataArray = null;
        int resultCount = 0;
        String msg = "1";

        if (datasetId != null) {
            Dataset dataset = getDataset(datasetId);//数据源+sql封装类
            datasourceId = dataset.getDatasourceId();//获取数据源id
            query = dataset.getQuery();//获取sql
        }
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        try {
            JSONObject config = JSONObject.parseObject(datasource.getConfig());//获取数据库的配置信息
            DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType());
            Map<String, String> parameterMap = Maps.transformValues(config, Functions.toStringFunction());
            resultCount = dataProvider.resultCount(parameterMap, query);//先判断需要的数据的个数
            if (resultCount > resultLimit) {//数据上线30w条，否则不从数据加载数据
                msg = "Cube result count is " + resultCount + ", greater than limit " + resultLimit;
            } else {
                dataArray = dataProvider.getData(parameterMap, query);
            }
        } catch (Exception e) {
            msg =  e.getMessage();
        }
        return new DataProviderResult(dataArray, msg);
    }

    private DataProvider getDataProvider(Long datasourceId, Map<String, String> query, Dataset dataset) throws Exception {
        if (dataset != null) {
            datasourceId = dataset.getDatasourceId();
            query = dataset.getQuery();
        }
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        JSONObject datasourceConfig = JSONObject.parseObject(datasource.getConfig());
        Map<String, String> dataSource = Maps.transformValues(datasourceConfig, Functions.toStringFunction());
        DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType(), dataSource, query);
        if (dataset != null && dataset.getInterval() != null && dataset.getInterval() > 0) {
            dataProvider.setInterval(dataset.getInterval());
        }
        return dataProvider;
    }

    public AggregateResult queryAggData(Long datasourceId, Map<String, String> query, Long datasetId, AggConfig config, boolean reload) {
        try {
            Dataset dataset = getDataset(datasetId);
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);
            return dataProvider.getAggData(config, reload);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CBoardException(e.getMessage());
        }
    }

    public DataProviderResult getColumns(Long datasourceId, Map<String, String> query, Long datasetId, boolean reload) {
        DataProviderResult dps = new DataProviderResult();
        try {
            Dataset dataset = getDataset(datasetId);
            DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);
            String[] result = dataProvider.getColumn(reload);
            dps.setColumns(result);
            dps.setMsg("1");
        } catch (Exception e) {
            e.printStackTrace();
            dps.setMsg(e.getMessage());
        }
        return dps;
    }

    public String[] getDimensionValues(Long datasourceId, Map<String, String> query, Long datasetId, String columnName, AggConfig config, boolean reload) {
        try {
            Dataset dataset = getDataset(datasetId);//获取数据集信息（数据源+对应的sql）
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);//获取DataProvider，不同的连接方式(Jdbc连接)用的是不同的DataProvider
            String[] result = dataProvider.getDimVals(columnName, config, reload);//获取维度列（=查询条件）对应的去重后的值
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String viewAggDataQuery(Long datasourceId, Map<String, String> query, Long datasetId, AggConfig config) {
        try {
            Dataset dataset = getDataset(datasetId);
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);
            return dataProvider.getViewAggDataQuery(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CBoardException(e.getMessage());
        }
    }

    public ServiceStatus test(JSONObject dataSource, Map<String, String> query) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(dataSource.getString("type"),
                    Maps.transformValues(dataSource.getJSONObject("config"), Functions.toStringFunction()), query);
            dataProvider.getData();
            return new ServiceStatus(ServiceStatus.Status.Success, null);
        } catch (Exception e) {
            return new ServiceStatus(ServiceStatus.Status.Fail, e.getMessage());
        }
    }

    private void attachCustom(Dataset dataset, AggConfig aggConfig) {
        if (dataset == null || aggConfig == null) {
            return;
        }
        Consumer<DimensionConfig> predicate = (config) -> {
            if (StringUtils.isNotEmpty(config.getId())) {
                String custom = (String) JSONPath.eval(dataset.getSchema(), "$.dimension[id='" + config.getId() + "'][0].custom");
                if (custom == null) {
                    custom = (String) JSONPath.eval(dataset.getSchema(), "$.dimension[type='level'].columns[id='" + config.getId() + "'][0].custom");
                }
                config.setCustom(custom);
            }
        };
        aggConfig.getRows().forEach(predicate);
        aggConfig.getColumns().forEach(predicate);
    }

    protected Dataset getDataset(Long datasetId) {
        if (datasetId == null) {
            return null;
        }
        return new Dataset(datasetDao.getDataset(datasetId));
    }

    protected class Dataset {
        private Long datasourceId;
        private Map<String, String> query;
        private Long interval;
        private JSONObject schema;

        public Dataset(DashboardDataset dataset) {
            JSONObject data = JSONObject.parseObject(dataset.getData());
            this.query = Maps.transformValues(data.getJSONObject("query"), Functions.toStringFunction());
            this.datasourceId = data.getLong("datasource");
            this.interval = data.getLong("interval");
            this.schema = data.getJSONObject("schema");
        }

        public JSONObject getSchema() {
            return schema;
        }

        public void setSchema(JSONObject schema) {
            this.schema = schema;
        }

        public Long getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }

        public Map<String, String> getQuery() {
            return query;
        }

        public void setQuery(Map<String, String> query) {
            this.query = query;
        }

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }
    }
}
