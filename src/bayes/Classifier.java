package bayes;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import database.DatabaseModule;


public abstract class Classifier implements IFeatureProbability {
	
	public static final String CLASSIFICATION_POSITIVE = "positive";
	
	public static final String CLASSIFICATION_NEGATIVE = "negative";
	

	int totalNumberOfFeatures = 0;

 
    private Dictionary<String, Dictionary<String, Integer>> featureCountPerCategory;

   
    public Dictionary<String, Integer> totalFeatureCount;

    
    private Dictionary<String, Integer> totalCategoryCount;

    
    public Classifier() {
        this.reset();
    }

   
    public void reset() {
        this.featureCountPerCategory = new Hashtable<String, Dictionary<String,Integer>>();
        this.featureCountPerCategory.put(this.CLASSIFICATION_NEGATIVE, new Hashtable<String, Integer>());
        this.featureCountPerCategory.put(this.CLASSIFICATION_POSITIVE, new Hashtable<String, Integer>());
        this.totalFeatureCount =
                new Hashtable<String, Integer>();
        this.totalCategoryCount =
                new Hashtable<String, Integer>();
    }

   
    public Set<String> getFeatures() {
        return ((Hashtable<String, Integer>) this.totalFeatureCount).keySet();
    }

  
    public Set<String> getCategories() {
        return ((Hashtable<String, Integer>) this.totalCategoryCount).keySet();
    }

    /**
     * Retrieves the total number of categories the classifier knows about.
     *
     * @return The total category count.
     */
    public int getCategoriesTotal() {
        int toReturn = 0;
        for (Enumeration<Integer> e = this.totalCategoryCount.elements();
                e.hasMoreElements();) {
            toReturn += e.nextElement();
        }
        return toReturn;
    }

   




    public int featureCount(String feature, String category) {
        Dictionary<String, Integer> features =
                this.featureCountPerCategory.get(category);
        if (features == null)
            return 0;
        Integer count = features.get(feature);
        return (count == null) ? 0 : count.intValue();
    }

    /**
     * Retrieves the number of occurrences of the given category.
     * 
     * @param category The category, which count should be retrieved.
     * @return The number of occurrences.
     */
    public int categoryCount(String category) {
        Integer count = this.totalCategoryCount.get(category);
        return (count == null) ? 0 : count.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double featureProbability(String feature, String category) {
        if (this.categoryCount(category) == 0)
            return 0;
        return (double) this.featureCount(feature, category)
                / (double) this.categoryCount(category);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with
     * overall weight of <code>1.0</code> and an assumed probability of
     * <code>0.5</code>. The probability defaults to the overall feature
     * probability.
     *
     * @see de.daslaboratorium.machinelearning.classifier.Classifier#featureProbability(Object, Object)
     * @see de.daslaboratorium.machinelearning.classifier.Classifier#featureWeighedAverage(Object, Object, IFeatureProbability, double, double)
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @return The weighed average probability.
     */
    public double featureWeighedAverage(String feature, String category) {
        return this.featureWeighedAverage(feature, category,
                null, 1.0f, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with
     * overall weight of <code>1.0</code>, an assumed probability of
     * <code>0.5</code> and the given object to use for probability calculation.
     *
     * @see de.daslaboratorium.machinelearning.classifier.Classifier#featureWeighedAverage(Object, Object, IFeatureProbability, double, double)
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @return The weighed average probability.
     */
    public double featureWeighedAverage(String feature, String category,
            IFeatureProbability calculator) {
        return this.featureWeighedAverage(feature, category,
                calculator, 1.0f, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with
     * the given weight and an assumed probability of <code>0.5</code> and the
     * given object to use for probability calculation.
     *
     * @see de.daslaboratorium.machinelearning.classifier.Classifier#featureWeighedAverage(Object, Object, IFeatureProbability, double, double)
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @param weight The feature weight.
     * @return The weighed average probability.
     */
    public double featureWeighedAverage(String feature, String category,
            IFeatureProbability calculator, double weight) {
        return this.featureWeighedAverage(feature, category,
                calculator, weight, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with
     * the given weight, the given assumed probability and the given object to
     * use for probability calculation.
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @param weight The feature weight.
     * @param assumedProbability The assumed probability.
     * @return The weighed average probability.
     */
    public double featureWeighedAverage(String feature, String category,
            IFeatureProbability calculator, double weight,
            double assumedProbability) {

        /*
         * use the given calculating object or the default method to calculate
         * the probability that the given feature occurred in the given
         * category.
         */
        final double basicProbability =
                (calculator == null)
                    ? this.featureProbability(feature, category) //i think apriori 
                            : calculator.featureProbability(feature, category);

        Integer totals = this.totalFeatureCount.get(feature);
        if (totals == null)
            totals = 0;
        return (weight * assumedProbability + totals  * basicProbability)
                / (weight + totals);
    }

    /**
     * Train the classifier by telling it that the given features resulted in
     * the given category.
     *
     * @param category The category the features belong to.
     * @param features The features that resulted in the given category.
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public void learn() throws ClassNotFoundException, SQLException {
    	DatabaseModule db = new DatabaseModule();
    	
    	db.tryToConnect();
    	
    	totalNumberOfFeatures = db.loadData(featureCountPerCategory, totalFeatureCount, totalCategoryCount);
    	
    	
    	for (String key : ((Hashtable<String, Integer>) featureCountPerCategory.get(this.CLASSIFICATION_POSITIVE)).keySet()) {
    		System.out.println(key +" " + ((Hashtable<String, Integer>) featureCountPerCategory.get(this.CLASSIFICATION_POSITIVE)).get(key));
    	}
   
    	
    }



    /**
     * The classify method.  It will retrieve the most likely category for the
     * features given and depends on the concrete classifier implementation.
     *
     * @param features The features to classify.
     * @return The category most likely.
     */
    public abstract Classification classify(Collection<String> features);

}