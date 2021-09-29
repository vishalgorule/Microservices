package com.microservices.demo.common.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionsUtil {

    private CollectionsUtil(){}

    private static class CollectionUtilsHolder{
        static final CollectionsUtil INSTANCE = new CollectionsUtil();
    }

    public static CollectionsUtil getInstance(){
        return CollectionUtilsHolder.INSTANCE;
    }

    public <T> List<T> getListFromIterable(Iterable<T> iterable){
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

}

