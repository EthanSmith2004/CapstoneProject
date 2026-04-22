package com.jel.spys.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassExtractor {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) {
        try {
            String basePackage = "com.jel.spys";
            if (args.length > 0) {
                basePackage = args[0];
            }
            
            Set<Class<?>> classes = findClassesInPackage(basePackage);
            ObjectNode result = extractClassInformation(classes);
            
            System.out.println(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            System.err.println("Error extracting classes: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Set<Class<?>> findClassesInPackage(String basePackage) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = basePackage.replace('.', '/');
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.getFile());
                classes.addAll(findClassesInDirectory(directory, basePackage));
            } else if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                classes.addAll(findClassesInJar(jarPath, basePackage));
            }
        }
        
        return classes;
    }
    
    private static Set<Class<?>> findClassesInDirectory(File directory, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isRelevantClass(clazz)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Skip classes that can't be loaded
                    }
                }
            }
        }
        
        return classes;
    }
    
    private static Set<Class<?>> findClassesInJar(String jarPath, String basePackage) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = basePackage.replace('.', '/');
        
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isRelevantClass(clazz)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Skip classes that can't be loaded
                    }
                }
            }
        }
        
        return classes;
    }
    
    private static boolean isRelevantClass(Class<?> clazz) {
        // Check if class has relevant annotations or is in relevant packages
        return hasRelevantAnnotation(clazz) || 
               clazz.getPackage().getName().contains("controller") ||
               clazz.getPackage().getName().contains("service") ||
               clazz.getPackage().getName().contains("facade") ||
               clazz.getPackage().getName().contains("security") ||
               clazz.getPackage().getName().contains("repository") ||
               clazz.getPackage().getName().contains("entity");
    }
    
    private static boolean hasRelevantAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(Controller.class) ||
               clazz.isAnnotationPresent(RestController.class) ||
               clazz.isAnnotationPresent(Service.class) ||
               clazz.isAnnotationPresent(Component.class) ||
               clazz.isAnnotationPresent(jakarta.persistence.Entity.class) ||
               clazz.isAnnotationPresent(jakarta.persistence.MappedSuperclass.class);
    }    private static ObjectNode extractClassInformation(Set<Class<?>> classes) {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode classesArray = objectMapper.createArrayNode();
        ArrayNode entitiesArray = objectMapper.createArrayNode();
        ArrayNode relationshipsArray = objectMapper.createArrayNode();
        
        for (Class<?> clazz : classes) {
            try {
                if (isEntityClass(clazz)) {
                    ObjectNode entityInfo = extractEntityInformation(clazz);
                    if (entityInfo != null) {
                        entitiesArray.add(entityInfo);
                    }
                } else {
                    ObjectNode classInfo = extractClassInfo(clazz);
                    if (classInfo != null) {
                        classesArray.add(classInfo);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing class " + clazz.getName() + ": " + e.getMessage());
            }        }
          // Extract relationships between entities
        for (Class<?> clazz : classes) {
            if (isEntityClass(clazz)) {
                try {
                    ArrayNode entityRelationships = extractEntityRelationships(clazz, classes);
                    for (int i = 0; i < entityRelationships.size(); i++) {
                        relationshipsArray.add(entityRelationships.get(i));
                    }
                } catch (Exception e) {
                    System.err.println("Error extracting relationships for " + clazz.getName() + ": " + e.getMessage());
                }
            }
        }
          root.set("classes", classesArray);
        root.set("entities", entitiesArray);
        root.set("relationships", relationshipsArray);
        
        return root;
    }
    
    private static boolean isRepositoryClass(Class<?> clazz) {
        return clazz.getPackage().getName().contains("repository") && 
               clazz.isInterface() &&
               (clazz.isAnnotationPresent(org.springframework.stereotype.Repository.class) ||
                implementsJpaRepository(clazz));
    }
    
    private static boolean implementsJpaRepository(Class<?> clazz) {
        if (!clazz.isInterface()) {
            return false;
        }
        
        // Check if it extends JpaRepository, CrudRepository, or PagingAndSortingRepository
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            String interfaceName = interfaceClass.getName();
            if (interfaceName.contains("JpaRepository") || 
                interfaceName.contains("CrudRepository") || 
                interfaceName.contains("PagingAndSortingRepository")) {
                return true;
            }
        }
        
        return false;
    }
      private static String extractEntityTypeFromRepository(Class<?> repositoryClass) {
        try {
            java.lang.reflect.Type[] genericInterfaces = repositoryClass.getGenericInterfaces();
            for (java.lang.reflect.Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof java.lang.reflect.ParameterizedType) {
                    java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericInterface;
                    java.lang.reflect.Type rawType = paramType.getRawType();
                    
                    if (rawType instanceof Class) {
                        Class<?> rawClass = (Class<?>) rawType;
                        String className = rawClass.getName();
                        if (className.contains("JpaRepository") || 
                            className.contains("CrudRepository") || 
                            className.contains("PagingAndSortingRepository")) {
                            
                            java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                                Class<?> entityClass = (Class<?>) typeArgs[0];
                                return entityClass.getSimpleName();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
        return null;
    }
      private static void addStandardCrudMethods(ArrayNode methodsArray) {
        // Standard JpaRepository methods
        String[] standardMethods = {
            "save(entity)",
            "saveAll(entities)",
            "findById(id)",
            "existsById(id)",
            "findAll()",
            "findAllById(ids)",
            "count()",
            "deleteById(id)",
            "delete(entity)",
            "deleteAllById(ids)",
            "deleteAll(entities)",
            "deleteAll()",
            "flush()",
            "saveAndFlush(entity)",
            "saveAllAndFlush(entities)",
            "deleteAllInBatch(entities)",
            "deleteAllByIdInBatch(ids)",
            "deleteAllInBatch()",
            "getOne(id)",
            "getById(id)",
            "getReferenceById(id)",
            "findAll(sort: SpringSort)",
            "findAll(pageable: SpringPageable)",
        };
        
        for (String methodSig : standardMethods) {
            ObjectNode methodInfo = objectMapper.createObjectNode();
            String methodName = methodSig.substring(0, methodSig.indexOf('('));
            methodInfo.put("name", methodSig);
            methodInfo.put("visibility", "public");
            methodInfo.put("method_type", "inherited");
            methodInfo.put("category", categorizeRepositoryMethod(methodName));
            methodsArray.add(methodInfo);
        }
    }
      private static String categorizeRepositoryMethod(String methodName) {
        if (methodName.startsWith("save") || methodName.equals("flush")) {
            return "create/update";
        } else if (methodName.startsWith("find") || methodName.startsWith("get") || 
                   methodName.startsWith("exists") || methodName.equals("count")) {
            return "read";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "delete";
        } else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
            return "update";
        } else {
            return "other";
        }
    }
    
    private static boolean isEntityClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(jakarta.persistence.Entity.class) ||
               clazz.isAnnotationPresent(jakarta.persistence.MappedSuperclass.class);
    }
      private static ObjectNode extractClassInfo(Class<?> clazz) {
        ObjectNode classInfo = objectMapper.createObjectNode();
        
        classInfo.put("name", clazz.getSimpleName());
        classInfo.put("type", determineClassType(clazz));
        
        // Extract fields
        ArrayNode fieldsArray = objectMapper.createArrayNode();
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                ObjectNode fieldInfo = objectMapper.createObjectNode();
                fieldInfo.put("name", field.getName());
                fieldInfo.put("visibility", getVisibility(field.getModifiers()));
                fieldInfo.put("type", getSimpleTypeName(field.getType()));
                fieldsArray.add(fieldInfo);
            }
        }
        classInfo.set("fields", fieldsArray);
        
        // Extract methods
        ArrayNode methodsArray = objectMapper.createArrayNode();
        
        // For repository classes, add standard CRUD methods first
        if (isRepositoryClass(clazz)) {
            addStandardCrudMethods(methodsArray);
            
            // Extract entity type from generic parameters
            String entityType = extractEntityTypeFromRepository(clazz);
            if (entityType != null) {
                classInfo.put("entity_type", entityType);
            }
        }
        
        // Add declared methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic() && !method.getName().startsWith("lambda$")) {
                ObjectNode methodInfo = objectMapper.createObjectNode();
                methodInfo.put("name", formatMethodSignature(method));
                methodInfo.put("visibility", getVisibility(method.getModifiers()));
                
                // For repository methods, add additional information
                if (isRepositoryClass(clazz) && !method.isDefault()) {
                    methodInfo.put("return_type", getSimpleTypeName(method.getReturnType()));
                    methodInfo.put("method_type", "custom");
                    
                    // Determine method category based on name
                    String category = categorizeRepositoryMethod(method.getName());
                    methodInfo.put("category", category);
                    
                    // Check for annotations
                    ArrayNode annotations = objectMapper.createArrayNode();
                    if (method.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class)) {
                        annotations.add("@Query");
                    }
                    if (method.isAnnotationPresent(org.springframework.data.jpa.repository.Modifying.class)) {
                        annotations.add("@Modifying");
                    }
                    if (annotations.size() > 0) {
                        methodInfo.set("annotations", annotations);
                    }
                }
                
                methodsArray.add(methodInfo);
            }
        }
        classInfo.set("methods", methodsArray);
        
        return classInfo;
    }
    
    private static ObjectNode extractEntityInformation(Class<?> clazz) {
        ObjectNode entityInfo = objectMapper.createObjectNode();
        
        // Get table name
        String tableName = clazz.getSimpleName().toLowerCase();
        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
            if (!tableAnnotation.name().isEmpty()) {
                tableName = tableAnnotation.name();
            }
        }
        
        entityInfo.put("name", tableName);
        entityInfo.put("class", clazz.getSimpleName());
        
        ArrayNode primaryKeys = objectMapper.createArrayNode();
        ArrayNode columns = objectMapper.createArrayNode();
        ArrayNode foreignKeys = objectMapper.createArrayNode();
        
        // Extract fields
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                primaryKeys.add(field.getName());
            }
            
            if (isColumnField(field)) {
                String columnName = field.getName();
                if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
                    jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);
                    if (!columnAnnotation.name().isEmpty()) {
                        columnName = columnAnnotation.name();
                    }
                }
                columns.add(columnName);
            }
            
            if (field.isAnnotationPresent(jakarta.persistence.JoinColumn.class)) {
                jakarta.persistence.JoinColumn joinColumn = field.getAnnotation(jakarta.persistence.JoinColumn.class);
                foreignKeys.add(joinColumn.name());
            }
        }
        
        entityInfo.set("primary_key", primaryKeys);
        entityInfo.set("columns", columns);
        entityInfo.set("foreign_keys", foreignKeys);
        
        // Check for inheritance
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            entityInfo.put("inherits", clazz.getSuperclass().getSimpleName());
        }
        
        entityInfo.put("is_mapped_superclass", clazz.isAnnotationPresent(jakarta.persistence.MappedSuperclass.class));
        
        return entityInfo;
    }
    
    private static boolean isColumnField(Field field) {
        // Skip relationship fields
        return !field.isAnnotationPresent(jakarta.persistence.OneToMany.class) &&
               !field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) &&
               !field.isAnnotationPresent(jakarta.persistence.OneToOne.class) &&
               !field.isAnnotationPresent(jakarta.persistence.ManyToMany.class) &&
               !field.isAnnotationPresent(jakarta.persistence.Transient.class) &&
               !Modifier.isStatic(field.getModifiers());
    }
    
    private static String determineClassType(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)) {
            return "controller";
        } else if (clazz.isAnnotationPresent(Service.class)) {
            return "service";
        } else if (clazz.getSimpleName().toLowerCase().contains("facade")) {
            return "facade";
        } else if (clazz.getPackage().getName().contains("security")) {
            return "security";
        } else if (clazz.getPackage().getName().contains("repository")) {
            return "repository";
        } else if (clazz.isAnnotationPresent(jakarta.persistence.Entity.class) || clazz.isAnnotationPresent(jakarta.persistence.MappedSuperclass.class)) {
            return "entity";
        } else if (clazz.isAnnotationPresent(Component.class)) {
            return "component";
        }
        return "other";
    }
    
    private static String getVisibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) return "public";
        if (Modifier.isProtected(modifiers)) return "protected";
        if (Modifier.isPrivate(modifiers)) return "private";
        return "package";
    }
    
    private static String getSimpleTypeName(Class<?> type) {
        if (type.isArray()) {
            return getSimpleTypeName(type.getComponentType()) + "[]";
        }
        
        String name = type.getSimpleName();
        if (name.isEmpty()) {
            name = type.getName();
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(lastDot + 1);
            }
        }
        return name;
    }
    
    private static String formatMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getName()).append("(");
        
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) signature.append(", ");
            signature.append(parameters[i].getName());
        }
          signature.append(")");
        return signature.toString();
    }
    
    private static ArrayNode extractEntityRelationships(Class<?> entityClass, Set<Class<?>> allClasses) {
        ArrayNode relationships = objectMapper.createArrayNode();
        
        // Get the entity name for this class
        String fromEntityName = getEntityName(entityClass);
        
        // Examine all fields for relationship annotations
        for (Field field : entityClass.getDeclaredFields()) {
            try {
                ObjectNode relationship = null;
                
                if (field.isAnnotationPresent(jakarta.persistence.OneToMany.class)) {
                    relationship = createRelationship(fromEntityName, field, "1..1", "1..*", allClasses);
                } else if (field.isAnnotationPresent(jakarta.persistence.ManyToOne.class)) {
                    relationship = createRelationship(fromEntityName, field, "1..*", "1..1", allClasses);
                } else if (field.isAnnotationPresent(jakarta.persistence.OneToOne.class)) {
                    relationship = createRelationship(fromEntityName, field, "1..1", "1..1", allClasses);
                } else if (field.isAnnotationPresent(jakarta.persistence.ManyToMany.class)) {
                    relationship = createRelationship(fromEntityName, field, "1..*", "1..*", allClasses);
                }
                
                if (relationship != null) {
                    relationships.add(relationship);
                }
            } catch (Exception e) {
                System.err.println("Error processing field " + field.getName() + " in " + entityClass.getName() + ": " + e.getMessage());
            }
        }
        
        return relationships;
    }
    
    private static ObjectNode createRelationship(String fromEntityName, Field field, String fromCardinality, String toCardinality, Set<Class<?>> allClasses) {
        String toEntityName = determineTargetEntityName(field, allClasses);
        
        if (toEntityName == null) {
            return null;
        }
        
        ObjectNode relationship = objectMapper.createObjectNode();
        relationship.put("from", fromEntityName);
        relationship.put("to", toEntityName);
        
        ArrayNode cardinality = objectMapper.createArrayNode();
        cardinality.add(fromCardinality);
        cardinality.add(toCardinality);
        relationship.set("cardinality", cardinality);
        
        return relationship;
    }
    
    private static String determineTargetEntityName(Field field, Set<Class<?>> allClasses) {
        Class<?> targetType = null;
        
        // Handle collection types (OneToMany, ManyToMany)
        if (java.util.Collection.class.isAssignableFrom(field.getType())) {
            java.lang.reflect.Type genericType = field.getGenericType();
            if (genericType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
                java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                    targetType = (Class<?>) typeArgs[0];
                }
            }
        } else {
            // Handle single entity types (ManyToOne, OneToOne)
            targetType = field.getType();
        }
        
        if (targetType != null) {
            // Check if the target type is an entity class we know about
            for (Class<?> entityClass : allClasses) {
                if (entityClass.equals(targetType) && isEntityClass(entityClass)) {
                    return getEntityName(entityClass);
                }
            }
        }
        
        return null;
    }
    
    private static String getEntityName(Class<?> entityClass) {
        // Check for @Table annotation first
        if (entityClass.isAnnotationPresent(jakarta.persistence.Table.class)) {
            jakarta.persistence.Table tableAnnotation = entityClass.getAnnotation(jakarta.persistence.Table.class);
            if (!tableAnnotation.name().isEmpty()) {
                return tableAnnotation.name();
            }
        }
        
        // Default to lowercase class name without "Entity" suffix
        String className = entityClass.getSimpleName();
        if (className.endsWith("Entity")) {
            className = className.substring(0, className.length() - "Entity".length());
        }
        return className.toLowerCase();
    }
}
