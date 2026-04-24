package com.smartcampus.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Register Resources
        classes.add(com.smartcampus.resources.DiscoveryResource.class);
        classes.add(com.smartcampus.resources.RoomResource.class);
        classes.add(com.smartcampus.resources.SensorResource.class);
        
        // Register Exception Mappers
        classes.add(com.smartcampus.mappers.GlobalExceptionMapper.class);
        classes.add(com.smartcampus.mappers.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.mappers.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.mappers.SensorUnavailableExceptionMapper.class);
        
        // Register Filters
        classes.add(com.smartcampus.filters.LoggingFilter.class);
        
        return classes;
    }
}