package com.example.project.utils;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.List;

/**
 * 协同过滤推荐算法工具类
 */
public class RecommendUtils {

    /**
     * 使用协同过滤算法推荐商品（基于用户）
     * @param userId
     * @param size
     * @return
     */
    public static List<RecommendedItem> recommendUserCF(Integer userId, Integer size) {
        List<RecommendedItem> recommendations = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName("localhost");//本地为localhost
            dataSource.setPort(3306);
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setDatabaseName("sport-appointment");//数据库名
            DataModel dataModel=new MySQLJDBCDataModel(dataSource,"comments","user_id","area_id","score","create_time");

            System.out.println("基于用户的协同过滤推荐算法开始计算...");

            //基于余弦相似性的相似度
            UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            //基于皮尔逊相关系数的相似度
            //UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            //基于欧式距离的相似度
            //UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);

            LongPrimitiveIterator iterator = dataModel.getUserIDs();
            while(iterator.hasNext()){
                long id = iterator.next();
                double sim = similarity.userSimilarity(userId, id);
                System.out.println("目标用户:"+userId+" 与用户："+id+" 相似度="+sim);
            }

            //计算最近的邻居，邻居有两种算法，基于固定数量的邻居和基于相似度的邻居，这里使用基于固定数量的邻居
            //基于用户相似度算法
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);//计算用户的“邻居”，这里将与该用户最近距离为 3 的用户设置为该用户的“邻居”。

            //获取最近邻列表
            long[] userNeighborhood = neighborhood.getUserNeighborhood(userId);
            System.out.println("最近邻用户为：");
            for(long neighboarId:userNeighborhood){
                System.out.println("近邻用户:"+neighboarId);
            }

            Recommender recommender = new CachingRecommender(new GenericUserBasedRecommender(dataModel, neighborhood, similarity));//采用 CachingRecommender 为 RecommendationItem 进行缓存

            recommendations = recommender.recommend(userId, size);

            System.out.println("推荐结果：");
            //打印推荐的结果
            if(recommendations!=null) {
                for (RecommendedItem recommendedItem : recommendations) {
                    System.out.println("推荐物品："+recommendedItem.getItemID()+"，预测评分："+recommendedItem.getValue());
                }
            }

            return recommendations;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<RecommendedItem> recommendItemCF(Integer userId,Integer goodId, Integer size) {
        List<RecommendedItem> recommendations = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName("localhost");//本地为localhost
            dataSource.setPort(3306);
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setDatabaseName("sport-appointment");//数据库名
            DataModel dataModel=new MySQLJDBCDataModel(dataSource,"comments","user_id","area_id","score","create_time");

            System.out.println("基于物品的协同过滤推荐算法开始计算...");

            //基于余弦相似性的相似度
            ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            //基于皮尔逊相关系数的相似度
            //ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            //基于欧式距离的相似度
            //ItemSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);

            LongPrimitiveIterator iterator = dataModel.getItemIDs();
            while(iterator.hasNext()){
                long id = iterator.next();
                double sim = similarity.itemSimilarity(goodId, id);
                System.out.println("目标物品:"+goodId+" 与物品："+id+" 相似度="+sim);
            }

            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel,similarity);//采用 CachingRecommender 为 RecommendationItem 进行缓存

            recommendations = recommender.recommendedBecause(userId,goodId, size);

            System.out.println("推荐结果：");
            //打印推荐的结果
            if(recommendations!=null) {
                for (RecommendedItem recommendedItem : recommendations) {
                    System.out.println("推荐物品："+recommendedItem.getItemID()+"，预测评分："+recommendedItem.getValue());
                }
            }

            return recommendations;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
