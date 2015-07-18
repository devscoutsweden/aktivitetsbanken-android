package se.devscout.server.api.activityfilter;

import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.ActivityKey;

/**
 * Returns the activities which are related to the specified activity. Since
 * relations are uni-directional, this means that the filter returns activities
 * which are the "child" of the specified "parent".
 *
 * Sample relations:
 * <ul>
 *     <li>A -&gt; B</li>
 *     <li>A -&gt; C</li>
 *     <li>D -&gt; A</li>
 * </ul>
 *
 * Filtering for "A" would return activities "B" and "C", not "D".
 */
public interface RelatedToFilter extends ActivityFilter {
    ActivityKey getActivityKey();
}
