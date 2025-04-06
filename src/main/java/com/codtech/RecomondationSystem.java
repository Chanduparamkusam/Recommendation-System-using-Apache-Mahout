package com.codtech;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.io.*;
import java.util.List;

public class RecomondationSystem {
    public static void main(String[] args) {
        try {
            System.out.println("Program started ✅");
            System.out.println("📁 Checking resource file...");

            InputStream inputStream = RecomondationSystem.class.getClassLoader().getResourceAsStream("data.csv");

            if (inputStream == null) {
                System.out.println("❌ data.csv not found in resources folder!");
                return;
            }

            // Write to a temporary file for Mahout
            File tempFile = File.createTempFile("mahout-data", ".csv");
            tempFile.deleteOnExit();

            try (OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            System.out.println("✅ data.csv successfully loaded!");
            System.out.println("Temp file size: " + tempFile.length() + " bytes");

            // Load data model
            DataModel model = new FileDataModel(tempFile);

            System.out.println("Number of users: " + model.getNumUsers());
            System.out.println("Number of items: " + model.getNumItems());

            // Item-based similarity
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            Recommender recommender = new GenericItemBasedRecommender(model, similarity);

            long userId = 3L;
            List<RecommendedItem> recommendations = recommender.recommend(userId, 3);

            System.out.println("Total Recommendations: " + recommendations.size());

            if (recommendations.isEmpty()) {
                System.out.println("⚠️ No recommendations found for user ID: " + userId);
            } else {
                for (RecommendedItem recommendation : recommendations) {
                    System.out.println("💡 Recommend Item ID: " + recommendation.getItemID() +
                            " | Score: " + recommendation.getValue());
                }
            }

            System.out.println("Program ended ✅");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
