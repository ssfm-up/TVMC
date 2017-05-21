package za.ac.up.cs;

import java.util.List;

/**
 * Created by Matthias on 2016/04/15.
 * Project: Spotlight
 * See definition 2.2 [TGH-Draft-2016]
 */
public class KState {
    final List<Integer> predVals;
    final int locationVal;

    public KState(List<Integer> predValues, int locationValue) {
        this.predVals = predValues;
        this.locationVal = locationValue;
    }
}
