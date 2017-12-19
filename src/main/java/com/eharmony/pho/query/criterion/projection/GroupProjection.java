package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

import java.util.ArrayList;
import java.util.List;

public class GroupProjection extends Projection{

    public GroupProjection(String... propertyNames) {
        super(Aggregate.GROUP_BY, propertyNames);
    }

    @Override
    public String toString() {
        return "GroupProjection{" + getPropertyNames() + "}";
    }
}
