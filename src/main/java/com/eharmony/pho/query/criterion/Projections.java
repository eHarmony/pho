package com.eharmony.pho.query.criterion;

import com.eharmony.pho.query.criterion.projection.CountProjection;
import com.eharmony.pho.query.criterion.projection.GroupProjection;
import com.eharmony.pho.query.criterion.projection.MaxProjection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Projections {

    private Projections() {
    }

    public static GroupProjection groupBy(String... propertyNames) {
        return new GroupProjection(propertyNames);
    }

    public static CountProjection count(String propertyName) {
        return new CountProjection(propertyName);
    }

    public static MaxProjection max(String propertyName) {
        return new MaxProjection(propertyName);
    }
}
